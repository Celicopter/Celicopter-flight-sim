import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Test extends Canvas{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Rectangle virtualBounds;

	public static void main(String []args){
		Test t=new Test();
		JFrame f=new JFrame();

		/*
		//Gets information related to the display screen. This only works for single-monitor devices
		Toolkit tk = Toolkit.getDefaultToolkit();  

		//Gets the width of the screen and stores it in variable xSize
	    int xSize = ((int) tk.getScreenSize().getWidth()); 

	    //Gets the height of the screen and stores it in variable ySize
	    int ySize = ((int) tk.getScreenSize().getHeight());  

	    //Sets the screen to it's maximum allowable size (maximizes screen)
	    f.setSize(xSize,ySize);
		 */
		
		f.setSize((int)t.virtualBounds.getWidth(),(int)t.virtualBounds.getWidth());
		f.add(t);
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}//closes window listener that closes the program when we exit out of the window
		}//closes patch
		);
	}

	public Test() {
		super();
		/**This code works for devices with multiple screens*/
		//Defines a variable to hold the maximum bounds of the screen we will find shortly
		virtualBounds = new Rectangle();
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
	}
	
	public void paint(Graphics g){
		g.fillRect(0,0,100,100);
	}
	
}