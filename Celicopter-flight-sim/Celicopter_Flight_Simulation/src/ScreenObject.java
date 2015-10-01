import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * Represents an object on the screen-either a circle, oval with height two-thirds it's width, n-sided regular polygon, or image
 * @author NathanS
 *
 */
public class ScreenObject{
	/**X axis Position of the center of the object. 0 corresponds to right side of screen*/
	protected int xCenterPosition;
	/**Y axis Position of the center of the object. 0 corresponds to top of screen*/
	protected int yCenterPosition;
	/**Velocity along x-axis in pixels/sec*/
	protected double dx;
	/**Velocity along y-axis in pixels/sec*/
	protected double dy;
	/**Object can be filled-in polygon shape. This variable encapsulates said polygon. 
	 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/awt/Polygon.html">Polygon</a>*/
	protected Polygon shape;
	/**Object can be an image. This variable holds that image. Image is reformatted to a square of length and height pixelDiamter */
	protected BufferedImage sprite;
	/**The diameter of the circle, representing the object, in pixels OR if the object is a polygon, the diameter of the circumscribed circle of the polygon in pixels, OR if the object is an image, the width and height of said image. Images are displayed as perfect squares*/
	private double pixelDiameter;
	/**The modulation represents how visible the object is. Modulation of 1 represents a sharp, completely visible object, whereas 0 represents a completely invisible object*/
	protected double modulation;
	/**Keeps track of whether the on-screen object is actually a circle or an oval*/
	protected boolean isOval;
	/**Color of on-screen object. This color has it's alpha set by modulation*/
	private Color color;

	/**Holds the minimum y-coordinate of the vertices of polygon (if the object is represented by a polygon). Used exclusively for determining the bounds of the object with respect to the y-axis (proper collision-handling/bounds checking). If the object isn't a polygon, this value isn't used.*/
	protected int minY=0;
	/**Holds the maximum y-coordinate of the vertices of polygon (if the object is represented by a polygon). Used exclusively for determining the bounds of the object with respect to the y-axis (proper collision-handling/bounds checking). If the object isn't a polygon, this value isn't used.*/
	protected int maxY=0;

	/**
	 * Most basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's center in the top right hand corner of the window 
	 * [coordinates (0,0)], from rest, with diameter 10 pixels and modulation 1.0 (fully dark)
	 */
	public ScreenObject(){
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
	public ScreenObject(int startX, int startY){
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
	public ScreenObject(int startX, int startY, int numberOfSides){
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
	public ScreenObject(int startX, int startY, double startDx, double startDy){
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
	public ScreenObject(int startX, int startY, double startDx, double startDy, int numberOfSides){
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
	public ScreenObject(int startX, int startY, double startDx, double startDy, double startSF,int numberOfSides){
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
	public ScreenObject(int startX, int startY, double startDx, double startDy, int numberOfSides, double SF, double mod){
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
	public ScreenObject(int startX,int startY, BufferedImage image){
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
	public ScreenObject(int startX, int startY, double startDx, double startDy, BufferedImage image){
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
	public ScreenObject(int startX,int startY, BufferedImage image,double startSF, double startMod){
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
	public ScreenObject(int startX, int startY, double startDx, double startDy, double startSF,double startMod,BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=image;
		pixelDiameter=startSF;
		modulation=startMod;

	}

	/**
	 * Accesses and returns the width/height (the object is approximated to be a square with the same width and height) of the on-screen object.
	 * <p>
	 * The pixel diameter is the diameter of the circle, representing the object, in pixels 
	 * OR if the object is a polygon, the diameter of the circumscribed circle of the polygon in pixels, 
	 * OR if the object is an image, the width and height of said image. Images are displayed as perfect squares.
	 * @return the width/height of the object in pixels
	 */
	public double getPixelDiameter() {
		return pixelDiameter;
	}

	/**
	 * Changes the width/height of the object to a user-specified value (the object is approximated to be a square with the same width and height).
	 * <p>
	 * The pixel diameter is the diameter of the circle, representing the object, in pixels 
	 * OR if the object is a polygon, the diameter of the circumscribed circle of the polygon in pixels, 
	 * OR if the object is an image, the width and height of said image. Images are displayed as perfect squares.
	 * @param pixelDiameter new width/height the object will be set to
	 */
	public void setPixelDiameter(double pixelDiameter) {
		if(shape!=null){
			shape=createPoly(shape.npoints,pixelDiameter);
		}
		this.pixelDiameter = pixelDiameter;
	}

	/**
	 * Gets the color of this object
	 * @return the color of this object as a Color object
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color of this object
	 * @param color the new Color object that will be associated with this ScreenObject
	 */
	public void setColor(Color color) {
		this.color = new Color(color.getRed(),color.getGreen(),color.getBlue(),(int) (255*modulation));
	}

	/**
	 * Draws the object in the specified graphics context
	 * <p>
	 * If this object isn't a polygon or an image, a circle is drawn
	 * If the object is an oval (isOval field is true), an ellipse is drawn with it's height equal to two-thirds it's width 
	 * If this object is a polygon, it is drawn
	 * If this object is an image, the image is drawn
	 * @param g The Graphics2D context to draw the object in
	 */
	public void draw(Graphics2D g){
		Color d=g.getColor();
		setColor(color);
		g.setColor(color);
		drawObject(g);
		g.setColor(d);	
	}

	/**
	 * Actually draws the object in the specified context g
	 * @param g Graphics2D context to draw the object in
	 */
	protected void drawObject(Graphics2D g){
		if(shape==null && sprite==null){
			if(isOval)
				g.fillOval((int)xCenterPosition-(int)pixelDiameter/2, (int)yCenterPosition-(int)pixelDiameter/2, (int)pixelDiameter, (int) (2*pixelDiameter/3));
			else
				g.fillOval((int)xCenterPosition-(int)pixelDiameter/2, (int)yCenterPosition-(int)pixelDiameter/2, (int)pixelDiameter, (int)pixelDiameter);
			return;
		}
		if(shape!=null){
			Polygon sharpie=new Polygon(shape.xpoints,shape.ypoints,shape.npoints);
			sharpie.translate((int) (xCenterPosition), (int) (yCenterPosition));
			g.fillPolygon(sharpie);
			sharpie=null;
		}
		if(sprite!=null){
			g.drawImage(sprite, (int)xCenterPosition-(int)pixelDiameter/2, (int)yCenterPosition-(int)pixelDiameter/2, (int)pixelDiameter, (int)pixelDiameter, null);
		}
	}
	/**
	 * Method assumes the speeds set are in pixels/update and simply adds the speeds to the object's x and y Positions 
	 * This method also bounces the object off the sides of the Frame it's in.
	 * Frame size, in pixels is assumed to be screenWidth by screenHeight
	 * @param screenWidth width of the screen/Frame the object is being drawn in pixels
	 * @param screenHeight height of the screen/Frame the object is being drawn in pixels
	 */
	public void move(int screenWidth,int screenHeight){
		int newX=(int) Math.round(xCenterPosition+dx);
		int newY=(int) Math.round(yCenterPosition+dy);
		if(shape==null){
			if(distanceFromRightSide(newX,screenWidth)<0 || distanceFromLeftSide(newX)<0){
				dx=-dx;
			}
			else
				xCenterPosition=newX;
			if(distanceFromBottom(newY,screenHeight)<0 || distanceFromTop(newY)<0){
				dy=-dy;
			}
			else
				yCenterPosition=newY;
		}
		if(distanceFromLeftSide(xCenterPosition)<0)
			xCenterPosition=0;
		if(distanceFromTop(yCenterPosition)<0)
			yCenterPosition=0;
		if(distanceFromRightSide(yCenterPosition,screenWidth)<0)
			yCenterPosition=(int) (screenWidth-pixelDiameter/2);
		if(distanceFromBottom(yCenterPosition,screenHeight)<0)
			yCenterPosition=(int) (screenHeight-pixelDiameter/2);
	}

	/**
	 * Moves the ScreenObject based on it's speed, time elapses since it was last moved, the screen width and screen height.
	 * If the object, will go past or thru the sides of the frame it is in (frame size depends on the input screenWidth and screenHeight),
	 * it will instead bounce off the side
	 * @param time elapsed since object was last moved
	 * @param screenWidth Frame width, in pixels of the Frame/window the object is in
	 * @param screenHeight Frame width, in pixels of the Frame/window the object is in 
	 * @return True if the object has hit a wall and bounced off it, false otherwise
	 */
	public void move(long time, int screenWidth, int screenHeight){
		int newX=(int) Math.round(xCenterPosition+dx*time);
		int newY=(int) Math.round(yCenterPosition+dy*time);
		
		//Makes the object 'bounce' off walls
		if(distanceFromRightSide(newX,screenWidth)<0 || distanceFromLeftSide(newX)<0){
			dx=-dx;
		}
		else
			xCenterPosition=newX;
		if(distanceFromBottom(newY,screenHeight)<0 || distanceFromTop(newY)<0){
			dy=-dy;
		}
		else
			yCenterPosition=newY;
		
		/*If the user simply does xCenterPosotion=some value or yCenterPosition=some value, 
		 * these lines ensure that even if the user specifies an off-screen value, 
		 * the object is moved so it is barely on-screen, thereby staying on screen.
		 */
		if(distanceFromLeftSide(xCenterPosition)<0){
			if(shape!=null)
				xCenterPosition=(int) (shape.getBounds2D().getWidth()/2);
			else 
				xCenterPosition=(int) (pixelDiameter/2);

		}
		if(distanceFromTop(yCenterPosition)<0){
			if(shape!=null)
				yCenterPosition=(int) (shape.getBounds2D().getHeight()/2);
			else if(isOval)
				yCenterPosition=(int) (pixelDiameter/3);
				else
					yCenterPosition=(int) (pixelDiameter/2);
		}
		if(distanceFromRightSide(yCenterPosition,screenWidth)<0){
			if(shape!=null)
				xCenterPosition=(int) (screenWidth-shape.getBounds2D().getWidth()/2);
			else 
				xCenterPosition=(int) (screenWidth-pixelDiameter/2);				
		}

		if(distanceFromBottom(yCenterPosition,screenHeight)<0){
			if(shape!=null)
				yCenterPosition=(int) (screenHeight-shape.getBounds2D().getHeight()/2);
			else if(isOval)
				yCenterPosition=(int) (screenHeight-pixelDiameter/3);
				else
					yCenterPosition=(int) (screenHeight-pixelDiameter/2);
		}
	}

	/**Calculates the distance from the left-most part of the object to the left side of the screen in pixels. Negative if the object is partially obscured by, or completely off the left side of the screen.
	 * @param x current x-position in pixels*/
	public double distanceFromLeftSide(int x){
		if(shape==null)
			return x-pixelDiameter/2;
		else
			return x-shape.getBounds2D().getWidth()/2;
	}
	/**Calculates the distance from the top-most part of the object to the top of the screen in pixels. Negative if the object is partially obscured by, or completely off the top of the screen.
	 * @param y current y-position in pixels*/
	public double distanceFromTop(int y){
		if(isOval)
			return y-pixelDiameter/3;
		if(shape==null)
			return y-pixelDiameter/2;
		else
			return y-minY;
	}

	/**Calculates the distance from the bottom-most part of the object to the bottom of the screen in pixels. Negative if the object is partially obscured by, or completely off the bottom of the screen.
	 * @param y current y-position in pixels
	 * @param screenHeigth Current height of the screen in pixels*/
	public double distanceFromBottom(int y,int screenHeight){
		if(isOval)
			return screenHeight-(y+pixelDiameter/3);
		if(shape==null)
			return screenHeight-(y+pixelDiameter/2);
		else
			return screenHeight-(y+maxY);
	}

	/**Calculates the distance from the right-most part of the object to the right-side of the screen in pixels. Negative if the object is partially obscured by, or completely off the right-side of the screen.
	 * @param x current x-position in pixels
	 * @param screenWidth Current width of the screen in pixels*/
	public double distanceFromRightSide(int x,int screenWidth){
		if(shape==null)
			return screenWidth-(x+pixelDiameter/2);
		else
			return screenWidth-(x+shape.getBounds2D().getWidth()/2);
	}

	/**
	 * Calculates and returns true if the object is at or past the bounds of the screen
	 * @param x Current x-position of the object in pixels
	 * @param y Current y-position of the object in pixels
	 * @param screenWidth Current width of the screen in pixels
	 * @param screenHeight Current height of the screen in pixels
	 * @return true if the object is at or past the bounds of the screen, false otherwise
	 */
	public boolean isAtBoundary(int x,int y, int screenWidth,int screenHeight){
		return distanceFromTop(y)<0 ||distanceFromLeftSide(x)<0 
				|| distanceFromRightSide(x,screenWidth)<0 || distanceFromBottom(y,screenHeight)<0;
	}

	/**
	 * Inner method that creates a regular polygon by creating a circumscribed circle around the polygon and filling in the same number of evenly-space vertices as sides
	 * @param numberOfSides number of sides of regular polygon
	 * @param SF pixel diameter. More specifically this is the diameter of the circumscribed circle of the regular polygon, ie the distance from the circumcenter to a given vertex 
	 * @return a regular polygon with the specified number of sides and  pixel width
	 */
	protected Polygon createPoly(int numberOfSides,double SF){
		if(numberOfSides==2)
			isOval=true;
		else
			isOval=false;
		if(numberOfSides>2){
			int[] xPts=new int[numberOfSides];
			int[] yPts=new int[numberOfSides];
			double intAngle=2*Math.PI/numberOfSides;
			//double r=SF/2;
			double r;
			if(numberOfSides%2==0)
				r=SF/(2*Math.cos(intAngle/2));
			else
				r=SF/(1+Math.cos(intAngle/2));
			//double r=sideLength/(2*Math.sin(intAngle/2));
			double theta=(3*Math.PI/2)-intAngle/2;
			for(int i=0;i<numberOfSides;i++){
				xPts[i]=(int) Math.round((r*Math.cos(theta+i*intAngle)));
				yPts[i]=(int) Math.round((r*Math.sin(theta+i*intAngle)));
			}
			Polygon p= new Polygon(xPts,yPts,numberOfSides);
			//p.translate(-1*min(xPts), -1*min(yPts));
			minY=-min(yPts);
			maxY=max(xPts);
			return p;
		}
		else
			return null;
	}

	/**
	 * Finds and returns the numerical minimum value (value closest to negative infinity) in an array of ints. Used exclusively to calculate minY, but can be used by other programs to calculate other minimums.
	 * @param ints array of ints 
	 * @return value in array closest to negative infinity
	 */
	public static int min(int[] ints){
		int min=ints[0];
		for(int i=1;i<ints.length;i++)
			if(ints[i]<min)
				min=ints[i];
		return min;
	}

	/**
	 * Finds and returns the numerical maximum value (value closest to positive infinity) in an array of ints. Used exclusively to calculate maxY, but can be used by other programs to calculate other maximums.
	 * @param ints array of ints 
	 * @return value in array closest to positive infinity
	 */
	public static int max(int[] ints){
		int max=ints[0];
		for(int i=1;i<ints.length;i++)
			if(ints[i]>max)
				max=ints[i];
		return max;
	}
	
	/**
	 * Returns a clone of the current object. Used for copying 
	 * @return an object with the exact same properties as this object but a different address in memory 
	 */
	public ScreenObject clone(){
		ScreenObject o=new ScreenObject();
		o.xCenterPosition=this.xCenterPosition;
		o.yCenterPosition=this.yCenterPosition;
		o.dx=this.dx;
		o.dy=this.dy;
		o.shape=this.shape;
		o.sprite=this.sprite;
		o.setPixelDiameter(this.pixelDiameter);
		o.modulation=this.modulation;
		o.isOval=this.isOval;
		Color c;
		if(color!=null){
			c=new Color(this.color.getRed(),this.color.getGreen(),this.color.getBlue());
		o.setColor(c);
		}
		o.minY=this.minY;
		o.maxY=this.maxY;
		return o;
	}
	/**
	 * Prints out information about the object when the object has System.out.println called on it
	 */
	public String toString(){
		String str="";
		str="This "+this.getClass()+" is centered at "+xCenterPosition+","+yCenterPosition+"\ntravels with x-direction speed "+dx+" and y-direction speed "+dy+".\n";
		str="This object has a pixel dimater of "+pixelDiameter+".\n";
		str+="This object is ";
		if(!isOval)
			str+="NOT ";
		str+="an oval.\n";
		if(shape!=null)
			str+="This object has "+shape.npoints+" sides.";
		return str;
	}
}