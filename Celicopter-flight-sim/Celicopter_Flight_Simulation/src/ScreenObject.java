import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.math.*;

/**
 * Represents an object on the screen
 * Object can be defined by the x and y coordinates of it's topmost corner
 * @author NathanS
 *
 */
public class ScreenObject{
	/**X axis Position of the center of the object. 0 corresponds to right side of screen*/
	protected double xCenterPosition;
	/**Y axis Position of the center of the object. 0 corresponds to top of screen*/
	protected double yCenterPosition;
	/**Velocity along x-axis in pixels/sec*/
	protected double dx;
	/**Velocity along y-axis in pixels/sec*/
	protected double dy;
	/**Object can be filled-in polygon shape. This variable encapsulates specifics about the polygon like the number of sides, etc.*/
	protected Polygon shape;
	/**Object can be an image. This variable holds that image*/
	protected BufferedImage sprite;
	/**The special frequency of the object corresponds to it's size on screen. The actual size of the object will depend not only on the special frequency but also the size on screen*/
	protected double pixelDiameter;
	/**The modulation represents how visible the object is. Modulation of 1 represents a sharp, completely visible object, whereas 0 represents a completely invisible object*/
	protected double modulation;
	/**Keeps track of wheather the on-screen object is actually a circle or an oval*/
	protected boolean isOval;
	/**Color of on-screen object*/
	private Color color;
	
	/**
	 * Most basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner in the top right hand corner of the window 
	 * [coordinates (0,0)], from rest, with spatial frequency 182 and modulation 1
	 */
	public ScreenObject(){
		xCenterPosition=0;
		yCenterPosition=0;
		dx=0;
		dy=0;
		shape=null;
		sprite=null;
		pixelDiameter=182;
		modulation=1;
	}
	
	/**
	 * Next-level basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, from rest, 
	 * with spatial frequency 182 and modulation 1
	 * @param startX X-coordinate of the top right-hand corner where the circle starts
	 * @param startY Y-coordinate of the top right-hand corner where the circle starts
	 */
	public ScreenObject(double startX, double startY){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		shape=null;
		sprite=null;
		pixelDiameter=182;
		modulation=1;
	}

	/**
	 * Fairly basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, x and y speed specified by the user
	 * with spatial frequency 182 and modulation 1
	 * @param startX X-coordinate of the top right-hand corner where the circle starts
	 * @param startY Y-coordinate of the top right-hand corner where the circle starts
	 * @param startDx start velocity along x-axis
	 * @param startDy start velocity along y-axis
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=null;
		pixelDiameter=182;
		modulation=1;
	}

	/**
	 * Represents a fill-in circle on screen
	 * @param startX X-coordinate of the top right-hand corner where the circle starts
	 * @param startY Y-coordinate of the top right-hand corner where the circle starts
	 * @param startDx The starting velocity of the object in the x-direction 
	 * @param startDy The starting velocity of the object in the y-direction (up and down)
	 * @param SF spatial frequency, dictates object's size on screen
	 * @param mod modulation, dictates contrast of object on screen
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy,double SF,double mod){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=null;
		pixelDiameter=SF;
		modulation=mod;
	}
	
	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor assumes the pixel width is 182, modulation is 1, and the shape initially starts from rest
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the top right-hand corner where the object starts
	 * @param startY Y-coordinate of the top right-hand corner where the object starts
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 */
	public ScreenObject(double startX, double startY, int numberOfSides){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		sprite=null;
		pixelDiameter=182;
		modulation=1;
		shape=createPoly(numberOfSides,pixelDiameter);
	}
	
	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor assumes spatial frequency is 182, modulation is 1, and the shape 
	 * initially starts with speed in the x and y direction specified by the user
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the top right-hand corner where the object starts
	 * @param startY Y-coordinate of the top right-hand corner where the object starts
	 * @param startDx The starting speed of the polygon in the x-direction 
	 * @param startDy The starting speed of the polygon in the y-direction (up and down)
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy, int numberOfSides){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		sprite=null;
		pixelDiameter=182;
		modulation=1;
		shape=createPoly(numberOfSides,pixelDiameter);
	}
	
	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor allows the user to specify a side-length, spatial frequency, modulation, and the shape 
	 * initially starts with speed in the x and y direction specified by the user
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the top right-hand corner where the object starts
	 * @param startY Y-coordinate of the top right-hand corner where the object starts
	 * @param startDx The starting speed of the polygon in the x-direction 
	 * @param startDy The starting speed of the polygon in the y-direction (up and down)
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 * @param SF Spatial frequency
	 * @param mod Modulation
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy, int numberOfSides, double SF, double mod){
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
	 * with it's top lefthand corner at user-specified coordinates 
	 * that starts from rest
	 * @param startX starting x-coordinate 
	 * @param startY starting y-coordinate
	 * @param image Image that represents on screen object
	 */
	public ScreenObject(double startX,double startY, BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		shape=null;
		sprite=image;
		pixelDiameter=182;
		modulation=1;
	}
	
	/**
	 * Creates an on-screen object,represented at a user-specified image
	 * with it's top lefthand corner at user-specified coordinates and 
	 * user-specified x and y velocities
	 * @param startX starting x-coordinate 
	 * @param startY starting y-coordinate
	 * @param startDx starting x-axis velocity
	 * @param startDy staring y-axis velocity
	 * @param image Image that represents on screen object
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy, BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=image;
		pixelDiameter=182;
		modulation=1;
	}
	
	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor assumes spatial frequency is 182, modulation is 1, and the shape initially starts from rest
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the top right-hand corner where the object starts
	 * @param startY Y-coordinate of the top right-hand corner where the object starts
	 * @param startSF The user-specified starting spatial-frequency
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 */
	public ScreenObject(double startX, double startY, double startSF,int numberOfSides){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=0;
		dy=0;
		sprite=null;
		pixelDiameter=startSF;
		modulation=1;
		shape=createPoly(numberOfSides,pixelDiameter);
	}
	
	/**
	 * Constructs an on-screen object meant to represent a regular polygon
	 * This constructor assumes each side is to be 10 pixels long and the shape 
	 * initially starts with speed in the x and y direction specified by the user
	 * Inputing a numberOfSides equal to 2 will result in an oval being displayed, with it's height half it's width
	 * All other numberOfSides less than two will result in a circle of diameter pixelWidth being displayed
	 * @param startX X-coordinate of the top right-hand corner where the object starts
	 * @param startY Y-coordinate of the top right-hand corner where the object starts
	 * @param startDx The starting speed of the polygon in the x-direction 
	 * @param startDy The starting speed of the polygon in the y-direction (up and down)
	 * @param startSF The user-specified starting spatial-frequency
	 * @param numberOfSides The number of sides the polygon to be rendered on the screen has
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy, double startSF,int numberOfSides){
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
	 * Constructs a ScreenObject meant to represent an on-screen image rather than a shape.
	 * The image starts from rest
	 * @param startX X-coordinate of the top right-hand corner where the object starts
	 * @param startY Y-coordinate of the top right-hand corner where the object starts
	 * @param startSF The user-specified starting spatial-frequency
	 * @param startMod The user-specified starting modulation as a decimal 0-1 inclusive
	 * @param image The BufferedImage meant to represent the object on-screen. Similar to a sprite in videogames
	 */
	public ScreenObject(double startX,double startY, BufferedImage image,double startSF, double startMod){
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
	 * 
	 * @param startX
	 * @param startY
	 * @param startDx
	 * @param startDy
	 * @param startSF
	 * @param startMod
	 * @param image
	 */
	public ScreenObject(double startX, double startY, double startDx, double startDy, double startSF,double startMod,BufferedImage image){
		xCenterPosition=startX;
		yCenterPosition=startY;
		dx=startDx;
		dy=startDy;
		shape=null;
		sprite=image;
		pixelDiameter=startSF;
		modulation=startMod;
	}
	
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = new Color(color.getRed(),color.getGreen(),color.getBlue(),(int) (255*modulation));
	}

	/**
	 * Draws the object in the specified graphics context
	 * If this object isn't a polygon or an image, a black circle is drawn
	 * If this object is a polygon, it is drawn in black
	 * If this object is an image, the image is drawn
	 * @param g The Graphics context to draw the object in
	 */
	public void draw(Graphics2D g){
		Color d=g.getColor();
		Color c=new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),(int) (125*modulation+125));
		g.setColor(c);
		if(shape==null && sprite==null){
			if(isOval)
				g.fillOval((int)xCenterPosition-(int)pixelDiameter/2, (int)yCenterPosition-(int)pixelDiameter/2, (int)pixelDiameter, (int)pixelDiameter/2);
			else
				g.fillOval((int)xCenterPosition-(int)pixelDiameter/2, (int)yCenterPosition-(int)pixelDiameter/2, (int)pixelDiameter, (int)pixelDiameter);
			g.setColor(d);
			return;
		}
		if(shape!=null){
			Polygon sharpie=new Polygon(shape.xpoints,shape.ypoints,shape.npoints);
			sharpie.translate((int) (xCenterPosition), (int) (yCenterPosition));
			g.fillPolygon(sharpie);
			sharpie=null;
		}
		if(sprite!=null){
			BufferedImage tmpImg = new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) tmpImg.getGraphics();
			g2d.setComposite(AlphaComposite.SrcOver.derive((float)modulation)); 
			g2d.drawImage(sprite, 0, 0, null);
			g.drawImage(tmpImg, (int)xCenterPosition-(int)pixelDiameter/2, (int)yCenterPosition-(int)pixelDiameter/2, (int)pixelDiameter, (int)pixelDiameter, null);
		}
		g.setColor(d);	
	}
	/**
	 * Method assumes the speeds set are in pixels/update and simply adds the speeds to the object's x and y Positions 
	 * This method also bounces the object off the sides of the Frame it's in.
	 * Frame size, in pixels is assumed to be screenWidth by screenHeight
	 * @param screenWidth width of the screen/Frame the object is being drawn in pixels
	 * @param screenHeight height of the screen/Frame the object is being drawn in pixels
	 */
	public void move(int screenWidth,int screenHeight){
		double newX=xCenterPosition+dx;
		double newY=yCenterPosition+dy;
		if(shape==null){
		if(newX+pixelDiameter/2>screenWidth || newX<pixelDiameter/2){
			dx=-dx;
		}
		else
			xCenterPosition=newX;
		if(newY+pixelDiameter/2>screenHeight || newY<pixelDiameter/2){
			dy=-dy;
		}
		else
			yCenterPosition=newY;
		}
		if(shape!=null){
			if(newX+shape.getBounds2D().getWidth()/2>screenWidth || newX<shape.getBounds2D().getWidth()/2){
				dx=-dx;
			}
			else
				xCenterPosition=newX;
			if(newY+shape.getBounds2D().getHeight()/2>screenHeight || newY<shape.getBounds2D().getHeight()/2){
				dy=-dy;
			}
			else
				yCenterPosition=newY;
			}
	}
	
	/**
	 * Moves the ScreenObject based on it's speed, time elapses since it was last moved, the screen width and screen height.
	 * If the object, will go past or thru the sides of the frame it is in (frame size depends on the input screenWidth and screenHeight),
	 * it will instead bounce off the side
	 * @param time elapsed since object was last moved
	 * @param screenWidth Frame width, in pixels of the Frame/window the object is in
	 * @param screenHeight Frame width, in pixels of the Frame/window the object is in 
	 */
	public void move(long time, int screenWidth, int screenHeight){
		double newX=xCenterPosition+dx*time;
		double newY=yCenterPosition+dy*time;
		if(shape==null){
		if(newX+pixelDiameter/2>screenWidth || newX<pixelDiameter/2){
			dx=-dx;
		}
		else
			xCenterPosition=newX;
		if(newY+pixelDiameter/2>screenHeight || newY<pixelDiameter/2){
			dy=-dy;
		}
		else
			yCenterPosition=newY;
		}
		if(shape!=null){
			if(newX+shape.getBounds2D().getWidth()/2>screenWidth || newX<shape.getBounds2D().getWidth()/2){
				dx=-dx;
			}
			else
				xCenterPosition=newX;
			if(newY+shape.getBounds2D().getHeight()/2>screenHeight || newY<shape.getBounds2D().getHeight()/2){
				dy=-dy;
			}
			else
				yCenterPosition=newY;
			}
	}

	/**
	 * Inner method that creates a regular polygon with number of sides equal to numberOfSides, and pixel diameter SF
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
		return p;
		}
		else
			return null;
	}
}