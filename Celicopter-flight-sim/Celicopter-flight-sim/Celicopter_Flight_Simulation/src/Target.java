import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
/**
 * Represents a non-user controlled Target on-screen
 * @author NathanS
 *
 */
public class Target extends ScreenObject{
	/**Function that resolves this object's position. Usually this function is some sort of a model of how a helicopter behaves in a gust. It can be time-dependent, or not*/
	private WindFunction wind;
	/**Function that resolves this object's position. Usually this function is some sort of a model of how a helicopter responds to control inputs. It can be time-dependent, or not*/
	private DynamicsModel dynamicMod;
	/**Determines the neutral point of the modulation*/
	protected int midPoint=125;

	/**
	 * Most basic constructor; represents on-screen a filled-in Target Object that starts 
	 * with it's top right-hand corner in the top right hand corner of the window 
	 * [coordinates (0,0)], from rest, with spatial frequency 182 and modulation 1
	 */
	public Target(){
		super();
		wind=null;
		dynamicMod=null;
	}

	/**Creates a Target represented on-screen by a regular polygon
	 * @param numberOfSides Number of sides the regular polygon is to have*/
	public Target(int numberOfSides){
		super(0,0,numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Next-level basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, from rest, 
	 * with spatial frequency 182 and modulation 1
	 * @param startX X-coordinate of the center where the circle starts
	 * @param startY Y-coordinate of the center where the circle starts
	 */
	public Target(int startX, int startY){
		super(startX,startY);
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel
	 * @see ScreenObject#ScreenObject(int, int, int)
	 */
	public Target(int startX, int startY, int numberOfSides){
		super(startX,startY,numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel
	 * @see ScreenObject#ScreenObject(int, int, double,double)
	 */
	public Target(int startX, int startY, double startDx, double startDy){
		super(startX,startY,startDx,startDy);
		wind=null;
		dynamicMod=null;
	}
	
	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel
	 * @see ScreenObject#ScreenObject(int, int, double,double,int)
	 */
	public Target(int startX, int startY, double startDx, double startDy, int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel
	 * @see ScreenObject#ScreenObject(int, int, double,double,double,int)
	 */
	public Target(int startX, int startY, double startDx, double startDy, double SF,int numberOfSides){
		super( startX,  startY,  startDx,  startDy, SF,numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel 
	 * @see ScreenObject#ScreenObject(int, int, double,double,double,double,int)
	 */
	public Target(int startX, int startY, double startDx, double startDy, double SF, double mod,int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	
	/**
	 * Same as ScreenOject in See Also, but with null WindFunction and DynamicsModel, and the Image starts with it's top left corner at the top left corner of the screen
	 * @see ScreenObject#ScreenObject(int,int,BufferedImage)
	 */
	public Target(BufferedImage i){
		super();
		xCenterPosition=(int) (getPixelDiameter()/2);
		yCenterPosition=(int) (getPixelDiameter()/2);
		sprite=i;
		wind=null;
		dynamicMod=null;
	}
	
	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel 
	 * @see ScreenObject#ScreenObject(int,int,BufferedImage)
	 */
	public Target(int startX,int startY,BufferedImage image){
		super(startX,startY,image);
		wind=null;
		dynamicMod=null;
	}
	
	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel 
	 * @see ScreenObject#ScreenObject(int,int,double,double,BufferedImage)
	 */
	public Target(int startX, int startY, double startDx, double startDy, BufferedImage image){
		super(startX,startY,startDx,startDy,image);
		wind=null;
		dynamicMod=null;
	}
	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel 
	 * @see ScreenObject#ScreenObject(int,int,BufferedImage,double,double)
	 */
	public Target(int startX, int startY, BufferedImage image,double SF, double mod){
		super(startX,startY,image,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	/**
	 * Same as ScreenObject in See Also, but with null WindFunction and DynamicsModel 
	 * @see ScreenObject#ScreenObject(int,int,double,double,double,doubleBufferedImage)
	 */
	public Target(int startX, int startY, double startDx, double startDy, double SF, double mod,BufferedImage image){
		super(startX,startY,startDx,startDy,SF,mod,image);
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Creates a Target with the same display properties as a default ScreenObject (see See Also)
	 * but with null WindFunction and DynamicsModel and user-specified pixelDiamter and modulation
	 * @param sF user specified pixelDiameter
	 * @param mod user-specified modulation (0-1.0)
	 * @see ScreenObject#ScreenObject()
	 * @see ScreenObject#getPixelDiameter()
	 */
	public Target(double sF,double mod){
		super();
		setPixelDiameter(sF);
		modulation=mod;
		wind=null;
		dynamicMod=null;
	}
	/**
	 * Creates a Target with null WindFunction and DynamicsModel that displays and moves like the ScreenObject parameter passed to it
	 * @param o ScreenObject the Target bases itself off
	 * @see ScreenObject
	 */
	public Target(ScreenObject o){
		this.xCenterPosition=o.xCenterPosition;
		this.yCenterPosition=o.yCenterPosition;
		this.dx=o.dx;
		this.dy=o.dy;
		this.shape=o.shape;
		this.sprite=o.sprite;
		this.setPixelDiameter(o.getPixelDiameter());
		this.modulation=o.modulation;
		this.isOval=o.isOval;
		if(o.getColor()!=null){
			Color c=new Color(o.getColor().getRed(),o.getColor().getGreen(),o.getColor().getBlue());
			this.setColor(c);
		}
		this.minY=o.minY;
		this.maxY=o.maxY;
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Creates a Target with user-specified WindFunction and DynamicsModel that displays and moves like the ScreenObject parameter passed to it
	 * @param o ScreenObject the Target bases itself off
	 * @param wind WindFunction that controls how the Target moves on-screen
	 * @param dynamod DynamicsModel that controls how the Target moves on-screen
	 * @see ScreenObject
	 * @see WindFunction
	 * @see DynamicsModel
	 */
	public Target(ScreenObject o,WindFunction wind,DynamicsModel dynamod){
		this.xCenterPosition=o.xCenterPosition;
		this.yCenterPosition=o.yCenterPosition;
		this.dx=o.dx;
		this.dy=o.dy;
		this.shape=o.shape;
		this.sprite=o.sprite;
		this.setPixelDiameter(o.getPixelDiameter());
		this.modulation=o.modulation;
		this.isOval=o.isOval;
		if(o.getColor()!=null){
			Color c=new Color(o.getColor().getRed(),o.getColor().getGreen(),o.getColor().getBlue());
			this.setColor(c);
		}
		this.minY=o.minY;
		this.maxY=o.maxY;
		this.wind=wind;
		dynamicMod=dynamod;
	}

	/**
	 * Sets the Target's WindFunction to specified param
	 * @param forcingFunction this Target's WindFunction is set to this param
	 */
	public void setWind(WindFunction forcingFunction){
		wind=forcingFunction;
	}
	public void setDynamicsModel(DynamicsModel dynamicsModel){
		dynamicMod=dynamicsModel;
	}

	public WindFunction getWind() {
		return wind;
	}

	public DynamicsModel getDynamicMod() {
		return dynamicMod;
	}

	@Override
	public void move(long time, int screenWidth, int screenHeight){
		setDx(time);
		setDy(time);
		super.move(time, screenWidth, screenHeight);
	}
	public void setDx(long time){
		if(dynamicMod!=null)
			dx=dynamicMod.solveDx(time,dx);
		if(wind!=null)
			dx=wind.solveDx(time,dx);
	}
	public void setDy(long time){
		if(dynamicMod!=null)
			dy=dynamicMod.solveDy(time,dy);
		if(wind!=null)
			dy=wind.solveDy(time,dy);
	}
	
	@Override
	public Target clone(){
		return new Target(super.clone(),wind,dynamicMod);
	}
	
	public void setColor(Color c){
		int val=(int) (midPoint-125*modulation);
		if(val<0)
			val=0;
		if(val>255)
			val=255;
		Color newColor=new Color(val,val,val);
		double mod=modulation;
		modulation=1.0;
		super.setColor(newColor);
		modulation=mod;
	}
	
	public void draw(Graphics2D g, int screenWidth, int screenHeight){
		int val=(int) (midPoint+125*modulation);
		if(val<0)
			val=0;
		if(val>255)
			val=255;
		g.setColor(new Color(val,val,val));
		g.fillRect(0,0,screenWidth,screenHeight);
		this.setColor(Color.black);
		g.setColor(getColor());
		drawObject(g);
	}
}