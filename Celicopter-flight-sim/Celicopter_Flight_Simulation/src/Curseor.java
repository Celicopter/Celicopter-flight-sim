import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;


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
	
	/**
	 * Next-level basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, from rest, 
	 * with spatial frequency 182 and modulation 1
	 * @param startX X-coordinate of the top right-hand corner where the circle starts
	 * @param startY Y-coordinate of the top right-hand corner where the circle starts
	 */
	public Curseor(double startX, double startY){
		super(startX,startY);
		wind=null;
		dynamicMod=null;
	}
	
	public Curseor(double startX, double startY, double startDx, double startDy,double SF,double mod){
		super(startX,startY,startDx,startDy,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(double startX, double startY, double startDx, double startDy){
		super(startX,startY,startDx,startDy);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(int numberOfSides){
		super(0,0,numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(double startX, double startY, int numberOfSides){
		super(startX,startY,numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(double startX, double startY, double startDx, double startDy, int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(double startX, double startY, double startDx, double startDy, double SF, double mod,int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	public Curseor(double startX, double startY, double startDx, double startDy, double SF,int numberOfSides){
		super( startX,  startY,  startDx,  startDy, SF,numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public void setWind(WindFunction forcingFunction){
		wind=forcingFunction;
	}
	public void setDynamicsModel(DynamicsModel dynamicsModel){
		dynamicMod=dynamicsModel;
	}
	
	public void move(long time, int screenWidth, int screenHeight){
		setDx(time);
		setDy(time);
		super.move(time, screenWidth, screenHeight);
	}
	public void setDx(long time){
		if(dynamicMod!=null)
			dx+=dynamicMod.solveDx(time,dx);
		if(wind!=null)
			dx+=wind.solveDx(time,dx);
	}
	public void setDy(long time){
		if(dynamicMod!=null)
			dy+=dynamicMod.solveDx(time,dy);
		if(wind!=null)
			dy+=wind.solveDx(time,dy);
	}
	public void draw(Graphics g){
		Color c=new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),(int) (255*modulation));
		g.setColor(c);
		if(shape==null && sprite==null){
			g.drawOval((int)xPosition, (int)yPosition, (int)pixelDiameter, (int)pixelDiameter);
			return;
		}
		if(shape!=null){
			Polygon sharpie=new Polygon(shape.xpoints,shape.ypoints,shape.npoints);
			sharpie.translate((int) (xPosition), (int) (yPosition));
			g.drawPolygon(sharpie);
			sharpie=null;
		}	
	}
}
