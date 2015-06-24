import java.awt.Graphics2D;
import java.util.ArrayList;


/**
 * Represents a group of Target Objects
 * @author Nathan_Schilling
 *
 */
public class TargetGroup extends Target{
	private ArrayList<Target> targets;
	
	public TargetGroup(){
		targets=new ArrayList<Target>();
	}
	
	public TargetGroup(ArrayList<Target> t){
		targets=t;
		cacluateCenter();
	}
	
	public boolean add(Target t){
		boolean b=targets.add(t);
		cacluateCenter();
		return b;
		
	}
	
	public boolean remove(Target t){
		boolean b=targets.remove(t);
		cacluateCenter();
		return b;
		
	}
	
	public Target remove(int index){
		Target b=targets.remove(index);
		cacluateCenter();
		return b;
		
	}
	
	public Target get(int index){
		return targets.get(index);
	}
	
	/**
	 * Calculates the center of this Target group based on an average the centers of each target
	 */
	public void cacluateCenter(){
		for(Target t:targets){
			xCenterPosition+=t.xCenterPosition/targets.size();
			yCenterPosition+=t.yCenterPosition/targets.size();
		}
	}
	
	public void move(long delta,int screenWidth,int screenHeight){
		
	}
	
	public void draw(Graphics2D g){
		
	}
}