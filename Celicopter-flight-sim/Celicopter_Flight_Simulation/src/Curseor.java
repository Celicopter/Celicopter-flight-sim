import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import joystick.JInputJoystick;


/**
 * Represents of the on-screen object that the user controls
 * @author Nathan_Schilling
 *
 */
public class Curseor extends ScreenObject{

	private WindFunction wind;
	private DynamicsModel dynamicMod;
	/**
	 * Most basic constructor; represents on-screen a filled-in Target Object that starts 
	 * with it's top right-hand corner in the top right hand corner of the window 
	 * [coordinates (0,0)], from rest, with spatial frequency 182 and modulation 1
	 */
	public Curseor(){
		super();
		wind=null;
		dynamicMod=null;
	}
	
	public Curseor(int numberOfSides){
		super(0,0,numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	/**
	 * Next-level basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, from rest, 
	 * with spatial frequency 182 and modulation 1
	 * @param startX X-coordinate of the top right-hand corner where the circle starts
	 * @param startY Y-coordinate of the top right-hand corner where the circle starts
	 */
	public Curseor(int startX, int startY){
		super(startX,startY);
		wind=null;
		dynamicMod=null;
	}
	
	public Curseor(int startX, int startY, int numberOfSides){
		super(startX,startY,numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	public Curseor(int startX, int startY, double startDx, double startDy){
		super(startX,startY,startDx,startDy);
		wind=null;
		dynamicMod=null;
	}

	public Curseor(int startX, int startY, double startDx, double startDy, int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	public Curseor(int startX, int startY, double startDx, double startDy, double SF,int numberOfSides){
		super( startX,  startY,  startDx,  startDy, SF,numberOfSides);
		wind=null;
		dynamicMod=null;
	}

	public Curseor(int startX, int startY, double startDx, double startDy,int numberOfSides, double sF, double mod) {
		super(startX,startY,startDx,startDy,numberOfSides,sF,mod);
		wind=null;
		dynamicMod=null;
	}

	public Curseor(int startX, int startY, double startDx, double startDy, double SF, double mod,int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(ScreenObject o){
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
	
	public Curseor(ScreenObject o,WindFunction windF,DynamicsModel dynamod){
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
		wind=windF;
		dynamicMod=dynamod;
	}
	
	public WindFunction getWind() {
		return wind;
	}

	public void setWind(WindFunction forcingFunction){
		wind=forcingFunction;
	}
	public void setDynamicsModel(DynamicsModel dynamicsModel){
		dynamicMod=dynamicsModel;
	}
	public DynamicsModel getDynamicMod() {
		return dynamicMod;
	}

	public void move(long time, int screenWidth, int screenHeight){
		setDx(time);
		setDy(time);
		super.move(time, screenWidth, screenHeight);
	}

	/**
	 * Sets the velocity along the x-axis according to the amount of time that has passed
	 * @param time Amount of time that has passed (dt)
	 */
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
	public void draw(Graphics2D g){
		Color d=g.getColor();
		Color c=new Color(this.getColor().getRed(),this.getColor().getGreen(),this.getColor().getBlue(),(int) (255*modulation));
		BasicStroke b=(BasicStroke) g.getStroke();
		g.setColor(c);
		g.setStroke(new BasicStroke(2));
		if(shape==null && sprite==null){
			g.drawOval((int)xCenterPosition-(int)getPixelDiameter()/2, (int)yCenterPosition-(int)getPixelDiameter()/2, (int)getPixelDiameter(), (int)getPixelDiameter());
			g.setStroke(b);
			g.setColor(d);
			return;
		}
		if(shape!=null){
			Polygon sharpie=new Polygon(shape.xpoints,shape.ypoints,shape.npoints);
			sharpie.translate((int) (xCenterPosition), (int) (yCenterPosition));
			g.drawPolygon(sharpie);
			sharpie=null;
			g.setStroke(b);
			g.setColor(d);
		}	
	}
	public double getError(ScreenObject o){
		return Math.sqrt(Math.pow(xCenterPosition-o.xCenterPosition,2)+Math.pow(yCenterPosition-o.yCenterPosition,2));
	}
}
