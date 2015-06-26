import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import joystick.JInputJoystick;
import net.java.games.input.Controller;

public class DanProgramCopy extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8042350635458523909L;
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
	protected long lastDrawTime;
	/**True if stick and program are calibrated properly*/
	protected boolean isCalibrated=false;
	/**All the things you would ever want to know about the state of the control stick*/
	protected JInputJoystick stick;
	/**True if the experiment is running, false otherwise*/
	protected boolean isRunning=true;
	/**Array of spatial frequencies to test*/
	protected static final double[] SPATIAL_FREQUENCIES={1000,24,60};
	/**Array of corresponding pixel widths. A pixel width is the width(diameter, whatever) of an on-screen object in screen pixels*/
	protected static int[] pixelWidths;
	/**Test subject distance from screen in inches*/
	protected static final double DISTANCE_FROM_SCREEN_IN_INCHES=48;
	/**Array of modulation levels to test. 1.0 is full, sharp, 0.0 is fully invisible*/
	protected static final double[] MODULATIONS={1.0,0.5,0.1};
	/**The time delay, in milliseconds, between frame updates*/
	protected static final int DELAY_TIME=30;
	/**Number of objects on screen that are part of the scenery*/
	protected static final int NUMBER_OF_SCENERY_OBJECTS=4;
	/**Delay between iterations (an iteration is composed of a modulation-spatial frequency combination) in milliseconds*/
	protected static final int ITERATIONS_DELAY=8000;
	/**First thread; handles drawing objects on-screen*/
	protected Thread thread;
	/**Seconds thread; handles moving on-screen objects*/
	protected Thread thread2;
	/**Locking object; used to prevent threads from potentially accessing the same data at the same time*/
	protected Object lock1=new Object();
	/**Graphics object that encapsulate the graphics of the canvas*/
	protected Graphics2D canvasGraphics;
	/**Index of current position in pixelWidths array*/
	protected int pixelWidthIndex=0;
	/**Index of current position in modulations array*/
	protected int modIndex=0;
	/**Target Position Index*/
	protected int targetPositionIndex=0;
	/**Array holding all possible positions of the target. Program iterates over this and draws the target at these x-locations*/
	protected int[] targetPositions;	
	/**Holds the last time an iteration was run*/
	protected long lastIterationTime;
	/**PrinterWriter used to write the results to a CSV file*/
	protected PrintWriter outputFile;
	/**The time according to the internal clock that the program starts at*/
	protected long startTime;
	protected boolean flag;
	protected static int XGain=2;
	
	
	public DanProgramCopy(){
		super();
		
		//Set this to true to enable multi-screening
		boolean IS_MULTI_SCREEN=false;
		if(IS_MULTI_SCREEN)
			XGain=3*XGain;
		
		//Creates the file-writer
		try {
			outputFile = new PrintWriter("Data_Output.csv");
			outputFile.println("Spatial Frequency(Hz),Modulation(0-1),System time(milliseconds),Target Position(pixels),Cursor Position(pixels),Error(pixels),x-axis gain");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Output file could not be created-Fatal error. Program closing...");
			System.exit(0);
		}
		
		startTime=System.currentTimeMillis();
		
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
		
		 
		//Possible position arrays
		 int [] targetPossiblePossitionsArray1 =
			{96,98,100,102,104,106,109,112,114,117,120,123,126,130,133,136,139,142,145,148,151,153,156,158,160,162,164,166,167,169,170,171,173,174,175,176,177,178,179,180,181,183,184,185,187,188,190,191,193,194,196,197,198,200,201,202,203,204,205,206,207,208,208,209,210,210,211,212,212,213,214,215,216,217,218,220,221,223,225,226,228,230,232,234,236,237,239,241,242,243,244,245,246,246,246,246,246,245,244,243,242,241,239,237,236,234,232,230,228,226,224,223,221,220,218,217,215,214,213,212,211,209,208,207,206,205,203,202,200,199,197,195,193,191,188,186,184,181,179,176,173,171,168,166,163,161,159,157,154,152,150,149,147,145,143,142,140,138,137,135,133,132,130,128,126,124,122,120,118,115,113,111,108,106,104,101,99,97,96,94,92,91,90,89,88,87,86,86,86,85,85,85,85,85,85,84,84,84,83,82,82,81,79,78,76,74,72,70,68,66,63,61,58,56,53,51,48,46,43,41,39,37,35,34,32,31,30,28,27,26,25,24,23,22,21,20,19,18,16,15,14,12,11,10,8,7,5,4,3,1,0,-1,-2,-3,-4,-5,-6,-6,-7,-7,-8,-8,-9,-9,-10,-10,-11,-12,-13,-14,-15,-16,-18,-19,-21,-23,-25,-27,-29,-32,-34,-37,-39,-42,-44,-46,-48,-51,-53,-54,-56,-57,-59,-60,-61,-62,-63,-63,-64,-64,-64,-65,-65,-65,-66,-66,-67,-67,-68,-69,-70,-71,-72,-73,-75,-76,-78,-79,-81,-83,-84,-86,-87,-89,-90,-91,-92,-93,-94,-94,-95,-95,-95,-95,-95,-95,-95,-94,-94,-93,-93,-92,-92,-91,-91,-91,-90,-90,-90,-89,-89,-89,-88,-88,-88,-87,-87,-86,-85,-85,-84,-83,-82,-81,-80,-78,-77,-75,-74,-73,-71,-70,-68,-67,-66,-65,-64,-64,-63,-63,-63,-63,-64,-64,-65,-66,-67,-68,-69,-70,-72,-73,-74,-76,-77,-78,-79,-80,-80,-81,-81,-82,-82,-81,-81,-81,-80,-80,-79,-78,-77,-77,-76,-75,-75,-74,-74,-73,-73,-73,-73,-73,-73,-73,-74,-74,-74,-75,-75,-75,-76,-76,-76,-76,-76,-76,-76,-76,-76,-76,-75,-75,-74,-74,-74,-73,-73,-72,-72,-72,-72,-72,-72,-73,-73,-73,-74,-74,-75,-76,-77,-77,-78,-79,-79,-80,-80,-81,-81,-81,-81,-81,-81,-81,-81,-80,-80,-79,-79,-79,-78,-78,-78,-78,-78,-78,-79,-79,-80,-81,-82,-84,-85,-87,-89,-91,-93,-95,-97,-99,-102,-104,-106,-108,-109,-111,-113,-114,-115,-116,-117,-118,-118,-119,-119,-120,-120,-120,-120,-121,-121,-121,-122,-122,-123,-123,-124,-125,-126,-127,-128,-129,-131,-132,-133,-135,-136,-138,-139,-141,-142,-144,-145,-146,-147,-149,-150,-151,-152,-154,-155,-156,-157,-159,-160,-162,-164,-166,-168,-170,-172,-174,-177,-179,-182,-185,-187,-190,-193,-195,-198,-200,-203,-205,-207,-209,-210,-212,-213,-214,-215,-215,-216,-216,-216,-216,-216,-215,-215,-215,-214,-214,-213,-213,-213,-213,-212,-212,-213,-213,-213,-213,-214,-214,-214,-215,-215,-215,-215,-215,-215,-215,-215,-214,-214,-213,-212,-211,-209,-208,-206,-204,-202,-200,-198,-196,-193,-191,-189,-186,-184,-182,-180,-178,-176,-174,-172,-170,-169,-167,-165,-163,-162,-160,-158,-156,-154,-152,-150,-148,-146,-144,-142,-139,-137,-135,-132,-130,-128,-125,-123,-121,-119,-117,-115,-114,-112,-111,-110,-109,-108,-107,-106,-105,-105,-104,-104,-103,-102,-101};	
		 int [] targetPossiblePossitionsArray2 =
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,2,3,3,3,4,4,4,5,5,5,6,6,6,6,5,4,3,1,-1,-1,-1,-3,-3,-5,-5,-6,-9,-14,-18,-21,-24,-26,-26,-26,-26,-26,-26,-28,-29,-29,-29,-30,-30,-27,-24,-18,-15,-15,-12,-12,-11,-10,-8,-8,-8,-8,-8,-8,-8,-9,-9,-12,-17,-20,-20,-20,-21,-23,-27,-32,-35,-36,-38,-39,-42,-47,-51,-54,-55,-58,-61,-67,-75,-75,-79,-79,-85,-94,-97,-102,-106,-110,-114,-117,-120,-124,-127,-130,-134,-136,-136,-136,-136,-136,-136,-136,-136,-136,-136,-135,-134,-133,-131,-130,-130,-128,-128,-128,-129,-129,-131,-131,-131,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-132,-133,-135,-137,-140,-142,-145,-149,-152,-155,-158,-162,-166,-169,-172,-175,-178,-181,-181,-184,-186,-189,-191,-192,-195,-198,-199,-201,-202,-204,-204,-206,-207,-207,-209,-210,-211,-211,-211,-212,-214,-215,-217,-218,-218,-219,-219,-220,-221,-221,-223,-223,-223,-225,-226,-228,-228,-229,-231,-231,-233,-233,-235,-237,-240,-242,-244,-244,-246,-247,-249,-250,-253,-255,-256,-256,-258,-259,-261,-263,-265,-267,-270,-275,-279,-282,-284,-285,-287,-287,-289,-289,-289,-289,-289,-289,-289,-288,-284,-281,-278,-276,-275,-272,-271,-268,-265,-262,-261,-259,-259,-258,-255,-253,-251,-249,-249,-248,-247,-245,-244,-242,-241,-239,-236,-234,-231,-228,-224,-222,-219,-216,-216,-213,-210,-207,-204,-202,-199,-197,-194,-190,-185,-181,-176,-173,-170,-169,-169,-167,-167,-167,-169,-169,-170,-172,-173,-173,-175,-181,-183,-185,-186,-189,-192,-195,-198,-198,-198,-198,-198,-198,-198,-198,-198,-197,-194,-192,-189,-188,-185,-180,-176,-170,-166,-166,-162,-158,-152,-147,-141,-136,-133,-131,-128,-126,-123,-120,-119,-116,-113,-113,-112,-110,-108,-106,-104,-104,-102,-97,-95,-94,-91,-88,-83,-79,-79,-76,-74,-73,-73,-71,-68,-63,-61,-58,-55,-51,-46,-43,-40,-37,-34,-34,-30,-26,-23,-20,-18,-17,-17,-17,-17,-17,-17,-17,-17,-19,-23,-24,-24,-24,-25,-26,-28,-28,-30,-31,-32,-32,-33,-34,-35,-35,-35,-36,-36,-36,-37,-37,-37,-37,-36,-36,-35,-31,-25,-20,-15,-9,-5,1,5,14,23,23,31,35,40,47,54,60,64,67,69,74,78,80,84,87,87,90,93,96,99,100,100,101,101,103,106,109,112,115,119,119,127,127,130,134,138,141,144,146,147,149,150,150,152,154,155,156,157,158,159,159,160,160,160,162,163,163,163,164,165,166,168,170,175,175,177,180,183,184,185,188,188,191,192,194,195,197,198,200,200,200,200,200,200,200,200,200,200,200,199,199,198,196,195,193,190,187,185,184,182,181,179,177,176,176,175,175,174,174,173,173,172,172,172,172,172,173,174,177,180,181,182,184,187,189,192,193,193,194,194,195,197,199,200,200,200,200,200,200,200,200,200,199,198,196,193,190,187,187,185,181,175,167,160,151,142,135,129,121,113,103,96,92,87,84,84,80,78,74,71,69,69,68,66,66,66,66,66,66,67,70,70,73,73,82,84,88,90,93,94,96,99,102,103,105,105,105,104,98,92,86,77,71,65,63,61,61,61,59,58,57,54,51,48,44,40,36,30,20,10,3,-3,-11,-21,-29,-29,-33,-38,-43,-49,-54,-58,-60,-61,-66,-69,-72};

		 int [] targetPossiblePossitionsArray3 = 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,-3,-6,-12,-20,-29,-34,-37,-38,-40,-43,-47,-51,-51,-55,-58,-62,-67,-71,-77,-84,-93,-99,-104,-105,-107,-107,-107,-107,-107,-106,-103,-102,-102,-101,-99,-98,-96,-94,-93,-93,-92,-92,-91,-91,-91,-90,-89,-87,-87,-86,-84,-83,-81,-78,-77,-77,-78,-81,-84,-87,-91,-95,-100,-106,-114,-119,-122,-122,-126,-130,-134,-138,-143,-147,-152,-156,-161,-165,-170,-176,-179,-182,-182,-182,-182,-182,-182,-181,-180,-178,-175,-172,-169,-165,-162,-159,-156,-154,-150,-147,-142,-139,-136,-132,-129,-126,-123,-123,-119,-116,-113,-112,-110,-107,-106,-104,-104,-104,-104,-104,-105,-108,-113,-120,-125,-132,-132,-140,-140,-145,-149,-152,-154,-157,-160,-163,-168,-171,-174,-177,-180,-183,-188,-188,-194,-194,-197,-197,-199,-202,-205,-208,-210,-213,-215,-215,-215,-215,-215,-215,-215,-215,-215,-215,-214,-214,-212,-211,-209,-206,-202,-197,-194,-191,-188,-185,-180,-174,-168,-162,-158,-153,-150,-145,-142,-137,-132,-132,-127,-122,-118,-113,-109,-104,-99,-93,-87,-81,-77,-69,-63,-58,-53,-50,-50,-47,-46,-46,-45,-45,-45,-45,-45,-46,-47,-50,-54,-60,-65,-70,-73,-76,-79,-79,-79,-79,-79,-77,-74,-72,-70,-70,-70,-70,-74,-80,-86,-89,-90,-90,-90,-90,-89,-86,-83,-83,-80,-78,-76,-74,-73,-71,-70,-68,-65,-63,-61,-58,-55,-54,-51,-50,-50,-48,-46,-43,-42,-40,-37,-34,-33,-31,-30,-28,-25,-23,-20,-17,-13,-9,-6,-3,-3,0,3,5,7,9,10,14,20,24,28,32,38,44,48,53,56,58,61,61,64,65,67,69,70,70,70,70,70,69,68,66,63,62,60,57,55,51,49,49,48,46,45,42,40,37,35,34,32,29,27,26,25,25,25,25,26,29,32,32,35,38,41,44,47,50,53,56,59,63,66,70,75,81,90,99,99,109,119,130,140,148,154,158,164,168,173,177,181,185,190,195,201,206,209,209,211,216,220,224,227,229,231,234,236,237,237,237,237,237,236,234,231,231,227,224,219,216,213,210,204,198,194,190,186,180,175,170,164,159,153,146,146,141,135,130,124,118,111,105,98,90,81,73,67,60,51,42,37,36,36,36,36,37,38,42,45,48,51,56,58,61,64,67,70,73,76,78,79,82,83,85,87,87,87,89,89,89,89,89,89,89,89,89,90,91,93,94,96,97,97,98,98};
		
		 int [] targetPossiblePossitionsArray4 =
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-2,-3,-3,-4,-4,-5,-5,-7,-7,-7,-7,-8,-9,-11,-12,-14,-15,-15,-15,-15,-15,-14,-14,-12,-11,-9,-8,-8,-8,-8,-8,-8,-10,-11,-11,-12,-14,-16,-17,-18,-20,-20,-22,-23,-24,-25,-26,-28,-28,-29,-32,-34,-36,-39,-42,-45,-46,-48,-49,-52,-54,-57,-60,-63,-66,-69,-72,-72,-75,-75,-78,-78,-80,-82,-85,-86,-87,-87,-88,-88,-89,-89,-90,-90,-90,-91,-91,-93,-93,-96,-97,-97,-97,-97,-97,-95,-92,-89,-86,-83,-80,-78,-77,-75,-75,-75,-75,-75,-76,-77,-78,-80,-81,-83,-84,-86,-86,-88,-89,-90,-92,-93,-95,-98,-100,-100,-103,-106,-110,-112,-115,-119,-123,-126,-127,-129,-132,-135,-139,-142,-142,-142,-142,-142,-142,-142,-142,-142,-142,-141,-140,-137,-135,-135,-133,-133,-132,-132,-132,-132,-132,-132,-133,-138,-141,-141,-144,-147,-149,-150,-153,-158,-159,-161,-162,-165,-168,-170,-172,-173,-176,-179,-180,-180,-185,-185,-187,-190,-191,-194,-195,-195,-196,-196,-196,-196,-196,-196,-195,-193,-193,-191,-189,-186,-183,-180,-177,-177,-174,-170,-167,-164,-162,-160,-157,-154,-151,-148,-146,-145,-144,-143,-143,-142,-142,-141,-140,-137,-135,-133,-128,-125,-122,-118,-113,-111,-109,-108,-106,-103,-97,-92,-92,-87,-85,-80,-77,-73,-70,-67,-67,-65,-65,-65,-65,-65,-65,-64,-61,-56,-47,-38,-30,-18,-18,-9,-3,0,2,5,7,7,7,7,7,7,6,6,6,6,6,6,6,6,6,8,11,15,18,19,21,24,27,28,31,32,38,46,55,62,73,83,83,93,98,102,104,105,105,105,106,106,106,106,106,107,108,110,111,111,111,113,114,116,117,117,118,118,118,122,128,133,142,150,156,159,159,161,164,167,170,174,177,180,182,183,185,188,190,193,196,200,205,209,209,214,217,219,219,220,223,228,233,236,239,241,242,244,248,251,252,252,252,252,251,248,245,242,239,236,232,229,226,224,221,218,217,216,215,214,214,213,212,212,211,210,210,210,209,208,207,206,205,205,204,204,204,204,204,204,204,204,204,206,211,218,224,227,230,232,234,235,237,238,239,240,241,243,243,245,245,245,247,249,250,252,253,253,253,253,253,253,252,251,248,244,241,238,235,232,227,227,223,219,215,210,206,200,193,186,179,173,166,159,153,146,139,134,129,129,125,121,116,111,107,104,101,96,92,89,86,83,80,78,76,74,71,71,70,67,65,62,60,59,59,59,59,59,59,59,60,63,68,74,77,82,82,82,82,82,82,82,82,82,82,82,81,79,77,74,71,67,63,60,56,53,50,47,43,39,36,31,31,28,24,21,20,18,15,12,7,3,-2,-5,-8,-10,-13,-13,-17,-20,-23,-28,-34,-36,-38,-38,-39,-40,-43,-45,-46,-46,-46,-46,-46,-46,-45,-45,-40,-35,-30,-27,-24,-21,-17,-12,-9,-7,-4,-1,2,6,9,9,9,14,21,24,28,33,35,36,39,40,42,45,45,45,45,45,45,45,44,41,38,35,27,20,14,10,6,2,-3,-7,-11,-14,-16,-19,-23,-31,-39,-46,-49,-49,-54,-58,-64,-69,-77,-83,-88,-91,-92,-94,-95,-98,-102,-107,-111,-113,-115,-117,-121,-121,-126,-130,-134,-135,-139,-141,-146,-153,-163,-169,-173,-176};
		
		 int [] targetPossiblePossitionsArray5 =
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-2,-3,-3,-3,-4,-4,-4,-5,-5,-5,-6,-6,-6,-6,-5,-4,-3,-1,1,1,1,3,3,5,5,6,9,14,18,21,24,26,26,26,26,26,26,28,29,29,29,30,30,27,24,18,15,15,12,12,11,10,8,8,8,8,8,8,8,9,9,12,17,20,20,20,21,23,27,32,35,36,38,39,42,47,51,54,55,58,61,67,75,75,79,79,85,94,97,102,106,110,114,117,120,124,127,130,134,136,136,136,136,136,136,136,136,136,136,135,134,133,131,130,130,128,128,128,129,129,131,131,131,132,132,132,132,132,132,132,132,132,132,132,132,132,132,132,132,132,132,132,133,135,137,140,142,145,149,152,155,158,162,166,169,172,175,178,181,181,184,186,189,191,192,195,198,199,201,202,204,204,206,207,207,209,210,211,211,211,212,214,215,217,218,218,219,219,220,221,221,223,223,223,225,226,228,228,229,231,231,233,233,235,237,240,242,244,244,246,247,249,250,253,255,256,256,258,259,261,263,265,267,270,275,279,282,284,285,287,287,289,289,289,289,289,289,289,288,284,281,278,276,275,272,271,268,265,262,261,259,259,258,255,253,251,249,249,248,247,245,244,242,241,239,236,234,231,228,224,222,219,216,216,213,210,207,204,202,199,197,194,190,185,181,176,173,170,169,169,167,167,167,169,169,170,172,173,173,175,181,183,185,186,189,192,195,198,198,198,198,198,198,198,198,198,197,194,192,189,188,185,180,176,170,166,166,162,158,152,147,141,136,133,131,128,126,123,120,119,116,113,113,112,110,108,106,104,104,102,97,95,94,91,88,83,79,79,76,74,73,73,71,68,63,61,58,55,51,46,43,40,37,34,34,30,26,23,20,18,17,17,17,17,17,17,17,17,19,23,24,24,24,25,26,28,28,30,31,32,32,33,34,35,35,35,36,36,36,37,37,37,37,36,36,35,31,25,20,15,9,5,-1,-5,-14,-23,-23,-31,-35,-40,-47,-54,-60,-64,-67,-69,-74,-78,-80,-84,-87,-87,-90,-93,-96,-99,-100,-100,-101,-101,-103,-106,-109,-112,-115,-119,-119,-127,-127,-130,-134,-138,-141,-144,-146,-147,-149,-150,-150,-152,-154,-155,-156,-157,-158,-159,-159,-160,-160,-160,-162,-163,-163,-163,-164,-165,-166,-168,-170,-175,-175,-177,-180,-183,-184,-185,-188,-188,-191,-192,-194,-195,-197,-198,-200,-200,-200,-200,-200,-200,-200,-200,-200,-200,-200,-199,-199,-198,-196,-195,-193,-190,-187,-185,-184,-182,-181,-179,-177,-176,-176,-175,-175,-174,-174,-173,-173,-172,-172,-172,-172,-172,-173,-174,-177,-180,-181,-182,-184,-187,-189,-192,-193,-193,-194,-194,-195,-197,-199,-200,-200,-200,-200,-200,-200,-200,-200,-200,-199,-198,-196,-193,-190,-187,-187,-185,-181,-175,-167,-160,-151,-142,-135,-129,-121,-113,-103,-96,-92,-87,-84,-84,-80,-78,-74,-71,-69,-69,-68,-66,-66,-66,-66,-66,-66,-67,-70,-70,-73,-73,-82,-84,-88,-90,-93,-94,-96,-99,-102,-103,-105,-105,-105,-104,-98,-92,-86,-77,-71,-65,-63,-61,-61,-61,-59,-58,-57,-54,-51,-48,-44,-40,-36,-30,-20,-10,-3,3,11,21,29,29,33,38,43,49,54,58,60,61,66,69,72};
		
		 int [] targetPossiblePossitionsArray6 = 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,0,3,6,12,20,29,34,37,38,40,43,47,51,51,55,58,62,67,71,77,84,93,99,104,105,107,107,107,107,107,106,103,102,102,101,99,98,96,94,93,93,92,92,91,91,91,90,89,87,87,86,84,83,81,78,77,77,78,81,84,87,91,95,100,106,114,119,122,122,126,130,134,138,143,147,152,156,161,165,170,176,179,182,182,182,182,182,182,181,180,178,175,172,169,165,162,159,156,154,150,147,142,139,136,132,129,126,123,123,119,116,113,112,110,107,106,104,104,104,104,104,105,108,113,120,125,132,132,140,140,145,149,152,154,157,160,163,168,171,174,177,180,183,188,188,194,194,197,197,199,202,205,208,210,213,215,215,215,215,215,215,215,215,215,215,214,214,212,211,209,206,202,197,194,191,188,185,180,174,168,162,158,153,150,145,142,137,132,132,127,122,118,113,109,104,99,93,87,81,77,69,63,58,53,50,50,47,46,46,45,45,45,45,45,46,47,50,54,60,65,70,73,76,79,79,79,79,79,77,74,72,70,70,70,70,74,80,86,89,90,90,90,90,89,86,83,83,80,78,76,74,73,71,70,68,65,63,61,58,55,54,51,50,50,48,46,43,42,40,37,34,33,31,30,28,25,23,20,17,13,9,6,3,3,0,-3,-5,-7,-9,-10,-14,-20,-24,-28,-32,-38,-44,-48,-53,-56,-58,-61,-61,-64,-65,-67,-69,-70,-70,-70,-70,-70,-69,-68,-66,-63,-62,-60,-57,-55,-51,-49,-49,-48,-46,-45,-42,-40,-37,-35,-34,-32,-29,-27,-26,-25,-25,-25,-25,-26,-29,-32,-32,-35,-38,-41,-44,-47,-50,-53,-56,-59,-63,-66,-70,-75,-81,-90,-99,-99,-109,-119,-130,-140,-148,-154,-158,-164,-168,-173,-177,-181,-185,-190,-195,-201,-206,-209,-209,-211,-216,-220,-224,-227,-229,-231,-234,-236,-237,-237,-237,-237,-237,-236,-234,-231,-231,-227,-224,-219,-216,-213,-210,-204,-198,-194,-190,-186,-180,-175,-170,-164,-159,-153,-146,-146,-141,-135,-130,-124,-118,-111,-105,-98,-90,-81,-73,-67,-60,-51,-42,-37,-36,-36,-36,-36,-37,-38,-42,-45,-48,-51,-56,-58,-61,-64,-67,-70,-73,-76,-78,-79,-82,-83,-85,-87,-87,-87,-89,-89,-89,-89,-89,-89,-89,-89,-89,-90,-91,-93,-94,-96,-97,-97,-98,-98};

		 boolean readFromFile=true;
		 if(readFromFile){
			 try {
				//Scanner reader = new Scanner(new FileReader("InputTable.txt"));
				Scanner reader = new Scanner(new FileReader("NewSoS.txt"));
				reader.nextLine();
//				String str=reader.nextLine();
//				String[] strs=str.split(",");
//				str.
				for(int i=0;i<targetPossiblePossitionsArray1.length;i++)
					if(reader.hasNextInt())
						targetPossiblePossitionsArray1[i]=reader.nextInt();
				for(int i=0;i<targetPossiblePossitionsArray2.length;i++)
					if(reader.hasNextInt())
						targetPossiblePossitionsArray2[i]=reader.nextInt();
				for(int i=0;i<targetPossiblePossitionsArray3.length;i++)
					if(reader.hasNextInt())
						targetPossiblePossitionsArray3[i]=reader.nextInt();
				for(int i=0;i<targetPossiblePossitionsArray4.length;i++)
					if(reader.hasNextInt())
						targetPossiblePossitionsArray4[i]=reader.nextInt();
				for(int i=0;i<targetPossiblePossitionsArray5.length;i++)
					if(reader.hasNextInt())
						targetPossiblePossitionsArray5[i]=reader.nextInt();
				for(int i=0;i<targetPossiblePossitionsArray6.length;i++)
					if(reader.hasNextInt())
						targetPossiblePossitionsArray6[i]=reader.nextInt();
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		//Randomly chooses a target position array
		//switch((int)Math.random()*6){
		 switch(0){
			case 0:targetPositions=targetPossiblePossitionsArray1;
				   break;
			case 1:targetPositions=targetPossiblePossitionsArray2;
				   break;
			case 2:targetPositions=targetPossiblePossitionsArray3;
				   break;
			case 3:targetPositions=targetPossiblePossitionsArray4;
				   break;
			case 4:targetPositions=targetPossiblePossitionsArray5;
				   break;
			default:targetPositions=targetPossiblePossitionsArray6;
				   break;
		}
		
		
		//Sets the screen dimensions to the size of the window. 
		screenDimentions=new Dimension(virtualBounds.width,virtualBounds.height);
		
		//Creates new frame to hold and display our game object
		JFrame jiff=new JFrame("IMPORTANT RESEARCH PROGRAME");
		if(!IS_MULTI_SCREEN){
			//If the user wants the experiment to take up a single screen, this allows for that
			virtualBounds=new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		}
		//Sets the bounds of the experiment display to take up the amount of screens the user wants to take up
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

		//Gets the current time off the processor and store it 
		lastDrawTime = System.currentTimeMillis();
		lastIterationTime=lastDrawTime;

		//Ties the joystick to the program so information can be pulled from the joystick
		stick=new JInputJoystick(Controller.Type.STICK);

		//Initializes all on-screen objects
		initObjects();

		//Allows us to see the frame we just made so we can actually see our experiment
		jiff.setVisible(true);

		//Does the arithmetic to convert the array of spatial frequencies at the top to an array for pixel widths that allows for easier drawing
		convertToSpacialFrequencies();

		

		//Creates the first thread that will handle the displaying of the objects on-screen
		thread=new Thread(this);
		
		//Creates the second thread that will handle moving the objects 
		thread2=new Thread(){
			public void run(){
				//Runs while thread is active and the experiment is running 
				while(Thread.currentThread()==thread2 && isRunning){
					//The syncronized tag prevents the program from both moving and drawing everything on the screen at the same time
					synchronized(lock1){
						moveObjects();
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
		flag=false;
	}

	public void SFAndModulationUpdater(){
		lastIterationTime=System.currentTimeMillis();
		pixelWidthIndex++;
		System.out.println("Frequency Iteration "+pixelWidthIndex+" done");
		if(pixelWidthIndex>=pixelWidths.length){
			pixelWidthIndex=0;
			modIndex++;
			System.out.println("Modulation Iteration "+modIndex+" done");
		}
		if(modIndex>=MODULATIONS.length && isRunning){
			isRunning=false;
			System.out.println("Experiment trial completed!!\nWriting to file...");
			outputFile.close();
		}
	}
	
	public void writeErrorToFile(){
		if(pixelWidthIndex<SPATIAL_FREQUENCIES.length && modIndex<MODULATIONS.length){
		outputFile.println(SPATIAL_FREQUENCIES[pixelWidthIndex] + "," + MODULATIONS[modIndex] + "," 
				+ (System.currentTimeMillis()-startTime)+"," + target.xCenterPosition + "," + warfighter.xCenterPosition + ","
				+ warfighter.getError(target) + "," 
				//+ stick.getXAxisValue() +"," 
				);
		if(warfighter.getDynamicMod()!=null)
			outputFile.println(warfighter.getDynamicMod().getxGain() + "," );
		}		
	}
	
	public void moveObjects(){
		//Gets the change in milliseconds since the last update
		long delta=System.currentTimeMillis()-lastDrawTime;
		//Updates the holder variable with the current time
		lastDrawTime=System.currentTimeMillis();
		//Sets the size of the target to the current size we want
		target.setPixelDiameter(pixelWidths[pixelWidthIndex]);
		//Sets the size of the Cursor to be 20% bigger than the target
		warfighter.setPixelDiameter(1.2*pixelWidths[pixelWidthIndex]);
		target.modulation=MODULATIONS[modIndex];
		target.yCenterPosition=(int) Math.round(screenDimentions.getHeight()/2);
		long delta2=System.currentTimeMillis()-lastIterationTime;
		if(delta2>=ITERATIONS_DELAY){
			SFAndModulationUpdater();
			flag=true;
			return;
		}
		else if(delta2>=ITERATIONS_DELAY/2){
				target.xCenterPosition=(int) Math.round(targetPositions[targetPositionIndex]+3*screenDimentions.getWidth()/4);
				targetPositionIndex++;
			 }
			 else {
				 target.xCenterPosition=(int) Math.round(targetPositions[targetPositionIndex]+screenDimentions.getWidth()/4);
				 targetPositionIndex++;
			 }
		
		if(targetPositionIndex>=targetPositions.length){
			targetPositionIndex=0;
		}
		warfighter.yCenterPosition=(int) Math.round(screenDimentions.getHeight()/2);
		//Updates the positions of (moves) all the objects on the screen depending on how much time has passed since the last redraw, and the objects position (if the object is near the side of the screen it bounces off it)
		for(ScreenObject o:allOnScreenObjects){
			//prevents the null pointer exception throw
			if(o!=null){
				o.move(delta, screenDimentions.width, screenDimentions.height);
			}
		}
		if(flag){
			warfighter.xCenterPosition=target.xCenterPosition;
			warfighter.yCenterPosition=target.yCenterPosition;
			flag=false;
		}
	}
	
	public void initObjects(){
		//Initializes Curseor
		warfighter=new Curseor(screenDimentions.width/8,screenDimentions.height/2);
		
		//Allows the Curseor to take in joystick input
		warfighter.setDynamicsModel(new DynamicsModel(stick,XGain,0));

		System.out.println(warfighter.dx);
		//Initializes the Target
		target=new Target(screenDimentions.width/8,screenDimentions.height/2);

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
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
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
					if(stick.getXAxisPercentage()==50 && stick.getYAxisPercentage()==50){
						message2="Calibration complete";
						isCalibrated=true;
					}

				}
			
			//Prints messages to the screen in the upper-left
			g.drawString(message,0,g.getFontMetrics().getMaxAscent());
			g.drawString(message2,screenDimentions.width/2-g.getFontMetrics().stringWidth(message2)/2,screenDimentions.height/2-g.getFontMetrics().getMaxAscent());			
		}
	}

	public void convertToSpacialFrequencies(){
		//Makes the array of pixel widths the same size as the array of spatial frequencies-this allows the user to input in a array of spatial frequencies of any length
		pixelWidths=new int[SPATIAL_FREQUENCIES.length];

		//Gets the (approximate) resolution of the screen 
		int dpi=Toolkit.getDefaultToolkit().getScreenResolution();
		//Fills the array of pixel widths based on the input array spatial frequencies
		for(int i=0;i<SPATIAL_FREQUENCIES.length;i++){
			pixelWidths[i]=(int) (DISTANCE_FROM_SCREEN_IN_INCHES*dpi*Math.tan(2/SPATIAL_FREQUENCIES[i]));
			
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
			Color backgroundColor = Color.black;
			//this clears the offscreen image, not the onscreen one
			if(modIndex<MODULATIONS.length)
				backgroundColor=new Color((int) (125*MODULATIONS[modIndex]+125),(int) (125*MODULATIONS[modIndex]+125),(int) (125*MODULATIONS[modIndex]+125));
			canvasGraphics.setColor(backgroundColor);
			writeErrorToFile();
			canvasGraphics.fillRect(0,0,screenDimentions.width,screenDimentions.height);

			//calls the paintbuffer method with 
			//the offscreen graphics as a param
			updateFrame(canvasGraphics);

			//we finally paint the offscreen image onto the onscreen image
			g.drawImage(canvas,0,0,this);
		}
	}

	public void drawObjectsOnScreen(Graphics2D g){
		if(!isCalibrated){
			calibration(g);
		}
		else {
			g.setColor(Color.black);
			target.draw(g);
			g.setColor(Color.red);
			warfighter.draw(g);
		}
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
		
		//Makes movement look smoother 
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

		while(Thread.currentThread()==thread && isRunning){
			try{ Thread.sleep(DELAY_TIME);} catch(InterruptedException e) {}
			if(!isRunning)
				break;
			repaint();
		}
	}

	public static void main(String[] args){

		//Creates the frame we will display everything on and titles it (text at the top) "IMPORTANT RESEARCH PROGRAME"
		//Creates an instance of this class. Basically this line creates a 'canvas' (think painting canvas) that has a 
		//picture of the way everything looks. This picture changes over time; this is the point of a flight simulator. 
		//If it was a still picture it would be boring
		DanProgramCopy m=new DanProgramCopy();
		while(m.isRunning){
			try{Thread.sleep(DanProgramCopy.DELAY_TIME);} catch(InterruptedException e) {}
		}
		System.out.println("Writing complete! Closing...");
		System.exit(0);
	}
	
	public int stringToInt(String str){
		str=str.trim();
		int out=0;
		for(int i=0;i<str.length();i++){
			out+=Math.pow(10,(str.length()-1-i))*((int)str.charAt(i)-48);
		}
		return out;
	}
}
