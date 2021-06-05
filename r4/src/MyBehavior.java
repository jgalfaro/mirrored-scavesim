package src;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.NodeReferenceTable;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Vector3d;

public class MyBehavior extends Behavior{
	
	private TransformGroup targetTG;
	private Transform3D translation = new Transform3D();
	
	public static final int id_behavior = (int) (Math.random()*1000);
	
	@Override
	public void updateNodeReferences(NodeReferenceTable arg0) {
		System.out.println("updateNodeReferences");
		// TODO Auto-generated method stub
		super.updateNodeReferences(arg0);

	
	}

	@Override
	protected void wakeupOn(WakeupCondition arg0) {


		System.out.println("wakeupOn");
		// TODO Auto-generated method stub
		super.wakeupOn(arg0);
	}

	public static final int behavior_id=512; 

	MyBehavior(TransformGroup targetTG) {
		this.targetTG=targetTG;
		
		System.out.println("Constructor");
	}

	public void initialize(){

		System.out.println("Initialize");
		// set initial wakeup condition
		this.wakeupOn(new WakeupOnBehaviorPost(null, behavior_id));
		// set someIntegerValue to your specific value
		// null can be replaced by an specific Behavior Object to send this value
	}

	// called by Java 3D when appropriate stimulus occures
	public void processStimulus(Enumeration criteria){
		// do what is necessary

		System.out.println("processStimulus");
		translation.setTranslation(new Vector3d(0d,0d,0d));
		
		targetTG.setTransform(translation);
		// resetup Behavior
		this.wakeupOn(new WakeupOnBehaviorPost(null, id_behavior));
		
	}
}