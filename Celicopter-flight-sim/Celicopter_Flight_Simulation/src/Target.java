import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

public class Target extends ScreenObject{
	private WindFunction wind;
	private DynamicsModel dynamicMod;
	
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
	
	/**
	 * Next-level basic constructor; represents on-screen a filled-in circle that starts 
	 * with it's top right-hand corner at coordinates specified by the user, from rest, 
	 * with spatial frequency 182 and modulation 1
	 * @param startX X-coordinate of the top right-hand corner where the circle starts
	 * @param startY Y-coordinate of the top right-hand corner where the circle starts
	 */
	public Target(double startX, double startY){
		super(startX,startY);
		wind=null;
		dynamicMod=null;
	}
	
	public Target(double startX, double startY, double startDx, double startDy,double SF,double mod){
		super(startX,startY,startDx,startDy,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, double startDx, double startDy){
		super(startX,startY,startDx,startDy);
		wind=null;
		dynamicMod=null;
	}
	public Target(int numberOfSides){
		super(0,0,numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, int numberOfSides){
		super(startX,startY,numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, double startDx, double startDy, int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, double startDx, double startDy, double SF, double mod,int numberOfSides){
		super( startX,  startY,  startDx,  startDy,  numberOfSides,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, double startDx, double startDy, double SF,int numberOfSides){
		super( startX,  startY,  startDx,  startDy, SF,numberOfSides);
		wind=null;
		dynamicMod=null;
	}
	public Target(BufferedImage i){
		super();
		sprite=i;
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX,double startY,BufferedImage image){
		super(startX,startY,image);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, double startDx, double startDy, BufferedImage image){
		super(startX,startY,startDx,startDy,image);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, BufferedImage image,double SF, double mod){
		super(startX,startY,image,SF,mod);
		wind=null;
		dynamicMod=null;
	}
	public Target(double startX, double startY, double startDx, double startDy, double SF, double mod,BufferedImage image){
		super(startX,startY,startDx,startDy,SF,mod,image);
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
		Color d=g.getColor();
		Color c=Color.getHSBColor(0f,0f,(float) (0.5f*(1-modulation)));
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
}