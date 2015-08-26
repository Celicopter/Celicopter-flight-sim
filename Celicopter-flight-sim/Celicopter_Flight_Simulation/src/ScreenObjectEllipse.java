import java.awt.image.BufferedImage;


public class ScreenObjectEllipse extends ScreenObject{
	/**Amount, in degrees, the ellipse is rotated about it's center*/
	protected double theta=0;
	/**Height of the ellipse. The pixel diameter field is used to determine the width of the ellipse*/
	protected double pixelHeight=1;
	
	public ScreenObjectEllipse(){
		xCenterPosition=0;
		yCenterPosition=0;
		dx=0;
		dy=0;
		shape=null;
		sprite=null;
		pixelDiameter=10;
		modulation=1.0;
	}

	/**
	 * Next-level basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, from rest, 
	 * with diameter 10 pixels and modulation 1.0
	 * @param startX X-coordinate of the center where the circle starts
	 * @param startY Y-coordinate of the center where the circle starts
	 */
	public ScreenObjectEllipse(int startX, int startY){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		shape=null;
		sprite=null;
		pixelDiameter=10;
		modulation=1;
	}

	/**
	 * Constructs an on-screen object meant to represent a regular polygon. 
	 * This constructor assumes the diameter of the circumscribed circle of this polygon is 10 pixels, modulation is 1.0, and the shape initially starts from rest. 
	 * @param startX X-coordinate of the center where the object starts
	 * @param startY Y-coordinate of the center where the object starts
	 * @param numberOfSides Number of sides of polygon.<p>
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height two-thirds it's width. 
	 * All other numberOfSides less than two will result in a circle of diameter 10 pixels (the default pixel width) being displayed. 
	 */
	public ScreenObjectEllipse(int startX, int startY, int numberOfSides){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		sprite=null;
		pixelDiameter=10;
		modulation=1;
		shape=createPoly(numberOfSides,pixelDiameter);
	}

	/**
	 * Represents on-screen a filled-in circle that starts 
	 * with it's center at x,y coordinates specified by the user, 
	 * and speed along the x and y axes specified by the user.
	 * This constructor assumes the circle diameter is 10 pixels, modulation is 1.0.
	 * @param startX X-coordinate of the center where the circle starts
	 * @param startY Y-coordinate of the center where the circle starts
	 * @param startDx start velocity along x-axis (positive to right, negative to left)
	 * @param startDy start velocity along y-axis (positive down, negative up)
	 */
	public ScreenObjectEllipse(int startX, int startY, double startDx, double startDy){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=null;
		pixelDiameter=10;
		modulation=1;
	}

	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor assumes spatial frequency is 182, modulation is 1, and the shape 
	 * initially starts with speed in the x and y direction specified by the user
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the center where the object starts
	 * @param startY Y-coordinate of the center where the object starts
	 * @param startDx The starting speed of the polygon in the x-direction 
	 * @param startDy The starting speed of the polygon in the y-direction (up and down)
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 */
	public ScreenObjectEllipse(int startX, int startY, double startDx, double startDy, int numberOfSides){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		sprite=null;
		pixelDiameter=10;
		modulation=1;
		shape=createPoly(numberOfSides,pixelDiameter);
	}

	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor assumes each side is to be 10 pixels long and the shape 
	 * initially starts with speed in the x and y direction specified by the user
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the center where the object starts
	 * @param startY Y-coordinate of the center where the object starts
	 * @param startDx The starting speed of the polygon in the x-direction 
	 * @param startDy The starting speed of the polygon in the y-direction (up and down)
	 * @param startSF The user-specified starting width/height of the object in pixels
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 */
	public ScreenObjectEllipse(int startX, int startY, double startDx, double startDy, double startSF,int numberOfSides){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		sprite=null;
		pixelDiameter=startSF;
		modulation=1;
		shape=createPoly(numberOfSides,pixelDiameter);
	}
	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor allows the user to specify a side-length, spatial frequency, modulation, and the shape 
	 * initially starts with speed in the x and y direction specified by the user
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the center where the object starts
	 * @param startY Y-coordinate of the center where the object starts
	 * @param startDx The starting speed of the polygon in the x-direction 
	 * @param startDy The starting speed of the polygon in the y-direction (up and down)
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 * @param SF starting width/height of the object in pixels
	 * @param mod Modulation
	 */
	public ScreenObjectEllipse(int startX, int startY, double startDx, double startDy, int numberOfSides, double SF, double mod){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		sprite=null;
		pixelDiameter=SF;
		modulation=mod;
		shape=createPoly(numberOfSides,pixelDiameter);
	}

	/**
	 * Creates an on-screen object,represented at a user-specified image
	 * with center at user-specified coordinates that starts from rest
	 * @param startX starting x-coordinate 
	 * @param startY starting y-coordinate
	 * @param image Image that represents on screen object
	 */
	public ScreenObjectEllipse(int startX,int startY, BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		shape=null;
		sprite=image;
		pixelDiameter=10;
		modulation=1;
	}

	/**
	 * Creates an on-screen object,represented at a user-specified image
	 * with it's center at user-specified coordinates and 
	 * user-specified x and y velocities
	 * @param startX starting center x-coordinate 
	 * @param startY starting center y-coordinate
	 * @param startDx starting x-axis velocity
	 * @param startDy staring y-axis velocity
	 * @param image Image that represents on screen object
	 */
	public ScreenObjectEllipse(int startX, int startY, double startDx, double startDy, BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=image;
		pixelDiameter=10;
		modulation=1;
	}

	/**
	 * Constructs a ScreenObject meant to represent an on-screen image rather than a shape.
	 * The image starts from rest
	 * @param startX X-coordinate of the center where the object starts
	 * @param startY Y-coordinate of the center where the object starts
	 * @param startSF The user-specified starting width/height of the object in pixels
	 * @param startMod The user-specified starting modulation as a decimal 0-1 inclusive
	 * @param image The BufferedImage meant to represent the object on-screen. Similar to a sprite in video-games
	 */
	public ScreenObjectEllipse(int startX,int startY, BufferedImage image,double startSF, double startMod){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		shape=null;
		sprite=image;
		pixelDiameter=startSF;
		modulation=startMod;
	}

	/**
	 * Super-expanded constructor for a ScreenObject that is represented on-screen by an Image. Has maximum functionality
	 * with the user being able to specify where the center starts in the x,y coordinate space, velocities along both 
	 * the x and y axes, the starting width/height of the object in pixels, the starting modulation (0-1.0), 
	 * as well as the image the object will be represented on-screen by.
	 * @param startXX-coordinate of the center where the object starts
	 * @param startY Y-coordinate of the center where the object starts
	 * @param startDx starting x-axis velocity
	 * @param startDy staring y-axis velocity
	 * @param startSF User-specified starting width/height of the object in pixels
	 * @param startMod The user-specified starting modulation as a decimal 0-1 inclusive
	 * @param image The BufferedImage meant to represent the object on-screen. Similar to a sprite in video-games
	 */
	public ScreenObjectEllipse(int startX, int startY, double startDx, double startDy, double startSF,double startMod,BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=image;
		pixelDiameter=startSF;
		modulation=startMod;

	}
}
