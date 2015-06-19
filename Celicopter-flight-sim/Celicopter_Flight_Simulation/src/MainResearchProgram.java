import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;

import javax.swing.*;

import joystick.JInputJoystick;
import net.java.games.input.Controller;

public class MainResearchProgram extends JPanel implements Runnable{

	/**Represents the total bounds of the window in which our experiment happens. 
	 * Expands to occupy two monitors
	 * This variable is dynamic and changes depending on the size of the screen is*/
	protected Dimension screenDimentions;
	/**Holds the graphics object that stuff is drawn on*/
	protected BufferedImage canvas;
	/**Represents the target object to track*/
	protected Target target;
	/**Holds all the things to be displayed on the screen, including the Target, Curseor, and scenery objects (if there need be screen objects)*/
	protected ArrayList<ScreenObject> allOnScreenObjects=new ArrayList<ScreenObject>();
	/**Represents the on-screen object the user controls. The class name Cursor was already taken so for our class we simply misspelled the word*/
	protected Curseor warfighter;
	/**Holds the last time the loop was run. Used to calculate the speeds at which everything on-screen moves*/
	protected long lastLoopTime;
	/**True if stick and program are calibrated properly*/
	protected boolean isCalibrated;
	/**All the things you would ever want to know about the state of the control stick*/
	protected JInputJoystick stick;
	/**True if the experiment is running, false otherwise*/
	protected boolean isRunning;
	/**Array of spatial frequencies to test*/
	protected static final double[] SPATIAL_FREQUENCIES={1,10,100};
	/**Array of corresponding pixel widths. A pixel width is the width(diameter, whatever) of an on-screen object in screen pixels*/
	protected static int[] pixelWidths;
	/**Test subject distance from screen in inches*/
	protected static final double DISTANCE_FROM_SCREEN_IN_INCHES=48;
	/**Array of modulation levels to test. 1.0 is full, sharp, black, 0.0 is fully invisible*/
	protected static final double[] MODULATIONS={1.0,0.0,0.5,0.25,0.75};
	/**The time delay, in milliseconds, between frame updates*/
	protected static final int DELAY_TIME=30;
	/**Number of objects on screen that are part of the scenery*/
	protected static final int NUMBER_OF_SCENERY_OBJECTS=4;
	/**First thread; handles drawing objects on-screen*/
	protected Thread thread;
	/**Seconds thread; handles moving on-screen objects*/
	protected Thread thread2;
	/**Locking object; used to prevent threads from potentially accessing the same data at the same time*/
	protected Object lock1=new Object();
	/**Graphics object that encapualates the graphics of the canvas*/
	protected Graphics2D canvasGraphics;


	public MainResearchProgram(){
		super();
		/**This code should work for devices with multiple screens*/
		//Defines a variable to hold the maximum bounds of the screen we will find shortly
		Rectangle virtualBounds = new Rectangle();
		//Gets an object that encapsulates all periphery devices attached to this computer (screens, printers, etc.)
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//Unpacks this object and gets a list of all peripheries 
		GraphicsDevice[] gs = ge.getScreenDevices();
		//Iterates through this list, going to each device, getting the list of settings with each device, and parsing the maximum window size from these settings  
		for (int j = 0; j < gs.length; j++) {
			//Gets the list of settings associated with the device the for loop is currently on
			GraphicsConfiguration[] gc =gs[j].getConfigurations();
			//Iterates through the settings to get the bounds
			for (int i=0; i < gc.length; i++) {
				virtualBounds =virtualBounds.union(gc[i].getBounds());
			}
		}

		//Creates new frame to hold and display our game object
		JFrame jiff=new JFrame("IMPORTANT RESEARCH PROGRAME");
		//Sets the bounds of the experiment display to take up all the screens connected
		jiff.setBounds(virtualBounds);
		//Adds our canvas to the frame
		jiff.add(this);
		//Makes the program stop when we close the window. This is important for not making the computer excessively slow and preventing memory leaks
		jiff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Allows us to resize the window if need be. Gives our program flexibility
		jiff.setResizable(true);

		/*Used to make the experiment double-buffered (animation looks smoother)
		 * Double-buffering utilizes an offscreen image (that takes up the whole screen)
		 * to draw to first. In actuality when one calls the paint(Graphics g) or repaint() method
		 * on a canvas object in Java, Java will render to the screen, pixel row by pixcel row 
		 * (starting at the top of the screen), the object. If one has lots of objects on the screen,
		 * then this process takes long enough that oen can see the image forming, row by row, 
		 * which looks ugly. By using double-buffering, one draws to an image first, then the image is 
		 * effectively dumped onto the screen in one go, eliminating the lines and making the program run smoother*/
		canvas=new BufferedImage(virtualBounds.width,virtualBounds.height,BufferedImage.TYPE_INT_ARGB);

		//Holds the Graphics object of the image. Commands to draw stuff on the screen (like the Target and Curseor) are sent to this object
		canvasGraphics=(Graphics2D) canvas.getGraphics();

		//Allows the program to get input off the joystick somewhat faster
		requestFocus();

		//Gets the current time off the processor
		lastLoopTime = System.currentTimeMillis();


		//Initializes both boolean flag variables. They are used for control flow
		isRunning=true;
		isCalibrated=false;

		//Ties the joystick to the program so information can be pulled from the joystick
		stick=new JInputJoystick(Controller.Type.STICK);

		//Initializes all on-screen objects
		initObjects();

		//Allows us to see the frame we just made so we can actually see our experiment
		jiff.setVisible(true);

		//Does the arithmetic to convert the array of spatial frequencies at the top to an array for pixel widths that allows for easier drawing
		convertToSpacialFrequencies();

		//Sets the screen dimensions to the size of the window. 
		screenDimentions=new Dimension(virtualBounds.width,virtualBounds.height);

		//Creates the first thread that will handle the displaying of the objects on-screen
		thread=new Thread(this);
		
		//Creates the second thread that will handle moving the objects 
		thread2=new Thread(){
			public void run(){
				//Runs while thread is active and the experiment is running 
				while(Thread.currentThread()==thread2 && isRunning){
					//Gets the change in milliseconds since the last update
					long delta=System.currentTimeMillis()-lastLoopTime;
					//Updates the holder variable with the current time
					lastLoopTime=System.currentTimeMillis();
					
					//The syncronized tag prevents the program from both moving and drawing everything on the screen at the same time
					synchronized(lock1){
						//Updates the positions of (moves) all the objects on the screen depending on how much time has passed since the last redraw, and the objects position (if the object is near the side of the screen it bounces off it)
						for(ScreenObject o:allOnScreenObjects){
							//prevents the null pointer exception throw
							if(o!=null){
								o.move(delta, screenDimentions.width, screenDimentions.height);
							}
						}
					}
					//Pauses for delayTime milliseconds to make things smoother
					try{ Thread.sleep(DELAY_TIME);} catch(InterruptedException e) {e.printStackTrace();}
				}
			}
		};

		//Starts the drawing thread which begins executing what's in the run() method of this program
		thread.start();
		
		//Starts the thread that handles moving all on-screen objects
		thread2.start();
	}

	public void initObjects(){
		//Initializes Curseor
		warfighter=new Curseor();
		
		//Allows the Curseor to take in joystick input
		warfighter.setDynamicsModel(new DynamicsModel(stick,2,2));

		//Initializes the Target
		target=new Target();

		//		for(int i=0;i<numberOfSceneryObjects;i++){
		//			allOnScreenObjects.add(new ScreenObject());
		//		}
		allOnScreenObjects.add(target);
		allOnScreenObjects.add(warfighter);
	}

	public void calibration(Graphics2D g){
		String message="";
		String message2="";
		if(!isCalibrated){
			//Blanks out the current screen
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());

			//Sets the font our text will be displayed at to be 24pt Sans Serif (because I like Sans Serif font)
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
			//Sets the text color to be black
			g.setColor(Color.black);

			//Checks to ensure controller is connected
			if( !stick.isControllerConnected() )
			{
				//If not, print an error in annoying red
				g.setColor(Color.red);
				message2="No controller found!";
			} else

				// Get current state of joystick! And check, if joystick is disconnected.
				if( !stick.pollController() ) {
					//If not, print an error in annoying red
					g.setColor(Color.red);
					message2="Controller disconnected!";
				}
				else {
					/* Sets the text that will be displayed on screen, in this case a modified 
					 * value of the current position the joystick is in. A value of 50 from either 
					 * the .getXAxisPercentage() or getYAxisPercentage() on the stick indicates 
					 * the stick is in the exact center (not pushed) either way with those axes.
					 * For the calibration procedure and to make things simpler, we subtract 50 before display. 
					 * This makes the numbers the user sees on the screen either positive, negative, or zero.
					 * Zero is what the user should aim for. A positive number means the calibration wheel 
					 * associated with either axes is too far forward, a negative meaning it is too far back.
					 * 
					 */
					message=(stick.getXAxisPercentage()-50)+", "+(stick.getYAxisPercentage()-50);
					message2="Use the wheels by the joystick to make\nthe two numbers at the top zero!";

				}
			if(stick.getXAxisPercentage()==50 && stick.getYAxisPercentage()==50){
				message2="Calibration complete";
			}
			//Prints messages to the screen in the upper-left
			g.drawString(message,0,g.getFontMetrics().getMaxAscent());
			g.drawString(message2,getWidth()/2-g.getFontMetrics().stringWidth(message2),getHeight()/2-g.getFontMetrics().getMaxAscent());			
		}
	}

	public void convertToSpacialFrequencies(){
		//Makes the array of pixel widths the same size as the array of spatial frequencies-this allows the user to input in a array of spatial frequencies of any length
		pixelWidths=new int[SPATIAL_FREQUENCIES.length];

		//Gets the (approximate) resolution of the screen 
		int dpi=Toolkit.getDefaultToolkit().getScreenResolution();
		//Fills the array of pixel widths based on the input array spatial frequencies
		for(int i=0;i<SPATIAL_FREQUENCIES.length;i++){
			pixelWidths[i]=(int) (DISTANCE_FROM_SCREEN_IN_INCHES*dpi*Math.tan(1/SPATIAL_FREQUENCIES[i]));
		}
	}

	public void update(Graphics g){
		paint(g);
	}

	public void paint(Graphics g){

		//    checks the buffersize with the current panelsize
		//    or initialises the image with the first paint
		if(screenDimentions.width!=getWidth() || screenDimentions.height!=getHeight() || canvas==null || canvasGraphics==null)
			resetBuffer();

		if(canvas!=null){

			//this clears the offscreen image, not the onscreen one
			canvasGraphics.clearRect(0,0,screenDimentions.width,screenDimentions.height);

			//calls the paintbuffer method with 
			//the offscreen graphics as a param
			updateFrame(canvasGraphics);

			//we finally paint the offscreen image onto the onscreen image
			g.drawImage(canvas,0,0,this);
		}
	}

	public void drawObjectsOnScreen(Graphics2D g){
		g.setColor(Color.green);
		target.draw(g);
	}

	public void updateFrame(Graphics2D g) {

		synchronized(lock1){
			drawObjectsOnScreen(g);
		}

	}

	/** 
	 * Reinitialize double buffered graphics when canvas changes size
	 */
	public void resetBuffer(){
		// always keep track of the image size
		screenDimentions=getSize();

		//    clean up the previous image
		if(canvas!=null){
			canvas.flush();
			canvas=null;
		}
		if(canvasGraphics!=null){
			canvasGraphics.dispose();
			canvasGraphics=null;
		}
		System.gc();

		//    create the new image with the size of the panel
		canvas=new BufferedImage(screenDimentions.width,screenDimentions.height,BufferedImage.TYPE_INT_ARGB);
		canvasGraphics=(Graphics2D) canvas.getGraphics();
		canvasGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		canvasGraphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
	}

	@Override
	public void run() {

		while(Thread.currentThread()==thread){
			repaint(DELAY_TIME);
			//try{ Thread.sleep(delayTime);} catch(InterruptedException e) {}
		}

	}

	public static void main(String[] args){

		//Creates the frame we will display everything on and titles it (text at the top) "IMPORTANT RESEARCH PROGRAME"
		//Creates an instance of this class. Basically this line creates a 'canvas' (think painting canvas) that has a 
		//picture of the way everything looks. This picture changes over time; this is the point of a flight simulator. 
		//If it was a still picture it would be boring
		MainResearchProgram m=new MainResearchProgram();

	}
}
