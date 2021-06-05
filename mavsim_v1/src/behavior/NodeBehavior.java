package src.behavior;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Sphere;

public class NodeBehavior extends Behavior {
	
	public final static int ID=2;
	ColoringAttributes coloringAtr= new ColoringAttributes();
	Color3f color;
	private Sphere sphere;

	public NodeBehavior(Sphere sphere) {
		this.sphere=sphere;
		this.color=new Color3f((float)Math.random(),(float)Math.random(),(float)Math.random());
	}
	
	public NodeBehavior(Sphere sphere,Color3f color) {
		this.sphere=sphere;
		this.color=color;
	}

	@Override
	public void initialize() {
		this.wakeupOn(new WakeupOnBehaviorPost(this,ID));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processStimulus(Enumeration arg0) {
		sphere.getAppearance().getColoringAttributes().setColor(color);
		this.wakeupOn(new WakeupOnBehaviorPost(this,ID));
	}
	
	public void setColor(Color3f color){
		this.color=color;
	}
		
}