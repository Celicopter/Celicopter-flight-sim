import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Represents a one-dimensional user-controlled, red, centered, line on screen,
 * If xCenterPosition is negative, the line is defined by it's yCenterPosition and is assumed to be horizontal
 * If yCenterPosition is negative, the line is defined by it's xCenterPosition and is assumed to be vertical
 * If both are negative, the line is assumed to be vertical
 * Since this object extends Curseor, it can be subject to the same Dynamics and wind models a Curseor is subject to
 * Represents a line on screen, 2 pixels wide
 * The pixelDiamter field, in this case, is used to represent the length of the line 
 * @author NathanS
 *
 */
public class CursorLine extends Curseor{

	/**Holds a dimension of the window this CursorLine is currently existing in
	 * If the line is horizontal, it holds the window width
	 * If the line is vertical, it holds the window height*/
	private int windowDimention;
	public static final int MAX_LENGTH=1000000000;
	
	/**
	 * Base-level constructor
	 * Makes vertical line starting at x-coordinate 0 (left side of screen), with maximized length */
	public CursorLine(){
		super(0,-100,0,0,MAX_LENGTH,1);
		setColor(Color.red);
		setWindowDimention(500);
	}
	
	/**
	 * Makes vertical line with maximized length
	 * @param startX the x-coordinate the line will start at
	 */
	public CursorLine(int startX){
		super(startX,-100,0,0,MAX_LENGTH,1);
		setColor(Color.red);
		setWindowDimention(500);
	}
	/**
	 * Makes vertical line with maximized length
	 * @param startX x-coordinate the line will start at
	 * @param sH starting height the window this line exists in starts in
	 */
	public CursorLine(int startX,int sH){
		super(startX,-100,0,0,MAX_LENGTH,1);
		setColor(Color.red);
		setWindowDimention(sH);
	}
	/**
	 * Makes horizontal line with maximized length
	 * This constructor is essentially used as a cheat to allow the user to instantiate a horizontal line
	 * @param startY y-coordinate the line starts at
	 * @param startDx (meaningless)
	 * @param startDy starting velocity along the y-axis
	 */
	public CursorLine(int startY, double startDx, double startDy){
		super(-100,startY,0,startDy,MAX_LENGTH,1);
		setColor(Color.red);
		setWindowDimention(500);
	}

	/**
	 * Makes a horizontal line of maximized length
	 * The startDx parameter is not used; it is there simply to cheat the complier into allowing us to construct the line to be horizontal
	 * @param startY y-coordinate the line starts at
	 * @param sW screen Width
	 * @param startDy starting velocity along the y-axis
	 * @param startDx (meaningless)
	 */
	public CursorLine(int startY,int sW,double startDy,double startDx){
		super(-100,startY,0,startDy);
		setPixelDiameter(MAX_LENGTH);
		setColor(Color.red);
		windowDimention=sW;
	}
	
	/**
	 * Draws line in the specified Graphics2D context g.
	 * <p>
	 * If the xCenterPosition is negative, the line is defined by it's yCenterPosition and is horizontal.
	 * If the yCenterPosition is negative, the line is defined by it's xCenterPosition and is vertical.
	 * If both are negative, the line is assumed to be vertical.
	 */
	public void draw(Graphics2D g){
		Color c=g.getColor();
		g.setColor(getColor());
		g.setStroke(new BasicStroke(2));
		if(xCenterPosition<0){
			if(getPixelDiameter()==MAX_LENGTH || getPixelDiameter()>windowDimention-20)
				g.drawLine(10, yCenterPosition, windowDimention-10,yCenterPosition);
			else
				g.drawLine((int) ((windowDimention-getPixelDiameter())/2), yCenterPosition, (int) ((windowDimention-getPixelDiameter())/2),yCenterPosition);
			g.setColor(c);
			return;
		}
		if(getPixelDiameter()==MAX_LENGTH || getPixelDiameter()>windowDimention-20)
			g.drawLine(xCenterPosition,10,xCenterPosition, windowDimention-10);
		else
			g.drawLine(xCenterPosition, (int) ((windowDimention-getPixelDiameter())/2), xCenterPosition,(int) ((windowDimention-getPixelDiameter())/2));
		g.setColor(c);
	}
	
	public void move(long delta,int screenWidth,int screenHeight){
		int tempX,tempY;
		double tempPB=getPixelDiameter();
		setPixelDiameter(0);
		if(xCenterPosition<0){
			setWindowDimention(screenWidth);
			dx=0;
			tempX=xCenterPosition;
			if(getDynamicMod()!=null && getDynamicMod().getxGain()!=0)
				getDynamicMod().setxGain(0);
			super.move(delta, screenWidth, screenHeight);
			xCenterPosition=tempX;
		}
		else{
			setWindowDimention(screenHeight);
			dy=0;
			tempY=yCenterPosition;
			if(getDynamicMod()!=null && getDynamicMod().getyGain()!=0)
				getDynamicMod().setyGain(0);
			super.move(delta, screenWidth, screenHeight);
			yCenterPosition=tempY;
		}	
		setPixelDiameter(tempPB);
	}
	/**Returns the current screen height, according to this object*/
	public int getWindowDimention() {
		return windowDimention;
	}
	/**
	 * Sets the screenHeight to the specified value
	 * @param wd int to set the windowDimention to
	 */
	public void setWindowDimention(int wd) {
		this.windowDimention = wd;
	}
	
	public double getError(ScreenObject o){
		return Math.abs(o.xCenterPosition-xCenterPosition);
	}
}
