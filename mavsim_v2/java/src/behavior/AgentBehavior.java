package src.behavior;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public class AgentBehavior extends Behavior {

	public final static int ID=1;
	private TransformGroup targetTG;
	private Transform3D translation= new Transform3D();
	private Vector3f point;
	Color3f color;


	public AgentBehavior(TransformGroup targetTG,Vector3f point) {
		this.targetTG = targetTG;
		this.color=new Color3f((float)Math.random(),(float)Math.random(),(float)Math.random());
	}

	@Override
	public void initialize() {
		this.wakeupOn(new WakeupOnBehaviorPost(this,ID));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processStimulus(Enumeration arg0) {

		translation.setTranslation(point);
		targetTG.setTransform(translation);
		this.wakeupOn(new WakeupOnBehaviorPost(this,ID));

	}

	public void setPosition(Point3d point){
		this.point=new Vector3f((float)point.x,(float)point.y,(float)point.z);
	}
	
	public void setColor(Color3f color){
		this.color=color;
	}
	
}
