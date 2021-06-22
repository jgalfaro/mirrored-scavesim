package src;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JComponent;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Network3D extends JComponent {

	protected Vector<Node> nodeList;
	protected Vector<Agent> agentList;
	protected Color3f nodeColor;
	protected Color3f nodeColor1;
	protected Color3f nodeColor2;
	protected Color3f txRangeColor;
	protected Color3f linkColor;
	protected Color3f agentColor;
	protected Color3f specialAgentColor;
	protected final int agentSize = 10;
	protected int xSize;
	protected int ySize;

	Appearance nodeAppear = new Appearance();
	protected final int nodeSize = 15;
	Appearance agentAppear = new Appearance();
	
	Canvas3D canvas3D;
	TransformGroup publicationNode;
	BranchGroup objRoot;

	// protected Image imageBuffer;
	// protected Dimension imageBufferSize;

	public Network3D(Vector<Node> nodesVector, Vector<Agent> agentVector, int sizeX, int sizeY) {

		this.nodeList = nodesVector;
		this.agentList = agentVector;

		this.nodeColor1 = new Color3f(0.5F, 0.5F, 0.5F); // GREY
		this.nodeColor2 = new Color3f(1, 0.5F, 0);// Orange
		this.linkColor = new Color3f(1, 1, 1); // WHITE;
		this.txRangeColor = new Color3f(0.2F, 0.2F, 0.2F);// GREY
		this.agentColor = new Color3f(0, 1, 0);// GREEN;
		this.specialAgentColor = new Color3f(0, 0, 1); // BLUE

		
		
		//Default Node Color
		nodeAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		nodeAppear.setColoringAttributes(new ColoringAttributes(nodeColor1, ColoringAttributes.FASTEST));
		
		agentAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		agentAppear.setColoringAttributes(new ColoringAttributes(agentColor, ColoringAttributes.FASTEST));
		
		
		// setSize(sizeX, sizeY);

		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		// 1-Crear canvas3D
		canvas3D = new Canvas3D(config);
		this.setSize(sizeX, sizeY);
		canvas3D.setSize(sizeX, sizeY);
		add("Center", canvas3D);

		// 2-Crear simpleUniverse y referenciar a canvar3D
		// SimpleUniverse is a Convenience Utility class
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

		// 3-Construir rama de contenido
		BranchGroup scene = createSceneGraph();

		// 4-Compilar rama
		scene.compile();

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		simpleU.getViewingPlatform().setNominalViewingTransform();

		simpleU.addBranchGraph(scene);
	}
	
	/** Set nodes in initial position
	 * @param paramGraphics
	 */
	public void renderNodes(Graphics paramGraphics) {
		for (Node node : nodeList) {
			drawNode(node);
		}
	}
	
	public void renderState(Graphics paramGraphics) {

//		System.out.println("renderState");
		Enumeration<Node> localEnumeration = this.nodeList.elements();

		System.out.print("[");
		while (localEnumeration.hasMoreElements()) {

			Node localNode = localEnumeration.nextElement();

			float f = (this.agentList.size() - localNode.getInfo().knownBy) / this.agentList.size();

			System.out.print(f+",");
			// nodes
			localNode.changeColorNode(new Color3f(this.nodeColor1.x * f + this.nodeColor2.x * (1.0F - f), this.nodeColor1.y * f + this.nodeColor2.y * (1.0F - f),
					this.nodeColor1.z * f + this.nodeColor2.z * (1.0F - f)));
		}
		System.out.println("]");
		
		/*TODO: Se debe actualizar los agentes aqu�*/
		Enumeration<Agent> agentes = this.agentList.elements();

		while (agentes.hasMoreElements()) {
			Agent a = agentes.nextElement();
//			System.out.println("Current node="+a.getCurrentNode().getPosition());
			a.getBehaviorNode().postId(MyBehavior.id_behavior);
			Transform3D translate=a.getTranslateAgent();
			
			
			translate.setTranslation(new Vector3d(0d,0d,0.0d));
//agent.get
//			translate.get
//			objTras.setTransform(translate);
		}


	}

	public void renderRangeAndLinks(Graphics paramGraphics) {
		Node localNode1;
//		NodeInfo localNodeInfo1;
		Enumeration<Node> localEnumeration1 = this.nodeList.elements();

		while (localEnumeration1.hasMoreElements()) {
			localNode1 = (Node) localEnumeration1.nextElement();
			// localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
		}

		localEnumeration1 = this.nodeList.elements();
		while (localEnumeration1.hasMoreElements()) {
			localNode1 = (Node) localEnumeration1.nextElement();
			// localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
			Enumeration<Node> localEnumeration2 = localNode1.getReachableNodes().elements();
			while (localEnumeration2.hasMoreElements()) {
				Node localNode2 = (Node) localEnumeration2.nextElement();
				// NodeInfo localNodeInfo2 =
				// (NodeInfo)this.nodeInfoTable.get(localNode2);
				drawArrow(localNode1.getPosition(), localNode2.getPosition());
			}
		}
		
		Enumeration<Agent> agentes = this.agentList.elements();

		while (agentes.hasMoreElements()) {			
			drawAgent(agentes.nextElement());
		}

	}
	protected void drawNode(Node node){
//		Color3f color= nodeColor;
		
		node.setGraphicNode(addNode(node.getPosition()));
		publicationNode.addChild(node.getGraphicNode());	
	}
	
	protected void drawAgent(Agent agent) {		
		Point3d pos;
		if(agent.getCurrentNode()!=null)
			pos=agent.getCurrentNode().getPosition();
		else
			pos= new Point3d(0d,0d,0d);
		
		
		System.out.println("Add agent in "+pos);
		
		Sphere sphere = new Sphere(0.04f, agentAppear);

		Transform3D tras = new Transform3D();
		Vector3d site = new Vector3d(pos.x, pos.y, pos.z);
		tras.setTranslation(site);
		TransformGroup objTras = new TransformGroup(tras);
		
		objTras.addChild(sphere);

		
		agent.setBehaviorNode(new MyBehavior(objTras));
			
			
		agent.setTranslateAgent(tras);

		
		publicationNode.addChild(objTras);
		objRoot.addChild(agent.getBehaviorNode());
	}

	protected void drawArrow(Point3d p0, Point3d p1) {

		//		System.out.println("Link de:" + p0 + " a " + p1);
//		Enumeration e= publicationNode.getAllChildren();
		
		BranchGroup line = new BranchGroup();
		publicationNode.addChild(line);
		line.addChild(addLink(p0, p1));
	}
	
	

	public Dimension getPreferredSize() {
		return new Dimension(this.xSize, this.ySize);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension minimumSize() {
		return getMinimumSize();
	}

	public Dimension preferredSize() {
		return getPreferredSize();
	}

	public void paint(Graphics paramGraphics) {
		Dimension localDimension = getSize();
		if ((canvas3D == null) || (localDimension.width != canvas3D.getSize().width) || (localDimension.height != canvas3D.getSize().height)) {
			// this.canvas3D = createImage(localDimension.width,
			// localDimension.height);

			clearImageBuffer(localDimension);
		}

		renderState(canvas3D.getGraphics());
		//renderRangeAndLinks(canvas3D.getGraphics());
		//paramGraphics.drawImage(this.canvas3D, 0, 0, this);
	}

	public void clearImageBuffer(Dimension localDimension) {
		Graphics localGraphics = canvas3D.getGraphics();
		Color localColor = localGraphics.getColor();
		localGraphics.setColor(getBackground());
		localGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
		localGraphics.setColor(localColor);
	}

	@Override
	public void update(Graphics paramGraphics) {
		paint(paramGraphics);
		
	}

	private Group addLink(Point3d p0, Point3d p1) {

		//System.out.println("Add link ("+p0.x+","+p0.y+","+p0.z+")->("+p1.x+","+p1.y+","+p1.z+")");
		Group lineGroup = new Group();



		// Plain line
		Point3d[] plaPts = new Point3d[2];
		plaPts[0] = p0;
		plaPts[1] = p1;
		LineArray pla = new LineArray(2, LineArray.COORDINATES);
		pla.setCoordinates(0, plaPts);
		
		/*BEGIN Code to change line format */
//		Appearance app = new Appearance();
//		Color3f white = new Color3f(1f, 1f, 1f);
//		ColoringAttributes ca = new ColoringAttributes(white, ColoringAttributes.SHADE_FLAT);
//		app.setColoringAttributes(ca);
//		Shape3D plShape = new Shape3D(pla, app);
		/*END Code to change line format */
		
		Shape3D plShape = new Shape3D(pla);
		lineGroup.addChild(plShape);

		return lineGroup;

	}

	private TransformGroup addNode(Point3d pos) {

		Sphere sphere = new Sphere(0.02f, nodeAppear);

		TransformGroup translateNode = translate(pos);
		translateNode.addChild(sphere);

		return translateNode;
	}
	
	protected TransformGroup translate(Point3d pos) {
		Transform3D tras = new Transform3D();

		Vector3d site = new Vector3d(pos.x, pos.y, pos.z);
		tras.setTranslation(site);
		TransformGroup objTras = new TransformGroup(tras);
		return objTras;
	}

	private void crear(BranchGroup objRoot) {

		// Create the transform group node and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at runtime. Add it to the
		// root of the subgraph.
		publicationNode = new TransformGroup();
		publicationNode.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		publicationNode.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		publicationNode.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		publicationNode.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

		objRoot.addChild(publicationNode);
		
		renderNodes(canvas3D.getGraphics());
		renderRangeAndLinks(canvas3D.getGraphics());
		renderState(canvas3D.getGraphics());
		

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into
		// the scene graph.
		// Transform3D yAxis = new Transform3D();
		// Alpha rotationAlpha = new Alpha(-1, 40000);
		// RotationInterpolator rotator = new
		// RotationInterpolator(rotationAlpha, publicationNode, yAxis, 0.0f,
		// (float) Math.PI*2f);

		MouseRotate myMouseRotate = new MouseRotate();
		myMouseRotate.setTransformGroup(publicationNode);
		myMouseRotate.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseRotate);
		
		MouseZoom myMouseZoom = new MouseZoom();
		myMouseZoom.setTransformGroup(publicationNode);
		myMouseZoom.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseZoom);

		

		// return objSpin;

	}

	public BranchGroup createSceneGraph() {
		// Create the root of the branch graph
		objRoot = new BranchGroup();
		crear(objRoot);

//		imprimir_nodos("",objRoot.getAllChildren());
		return objRoot;
	} // end of CreateSceneGraph method of HelloJava3Dd
	
	public void imprimir_nodos(String tab,Enumeration components){
		while(components.hasMoreElements()){
			Object o=components.nextElement();
			System.out.println(tab+"->"+o.getClass());
			
			if(o instanceof BranchGroup )
				imprimir_nodos(tab+"\t",((BranchGroup) o).getAllChildren());
			else if (o instanceof TransformGroup)
				imprimir_nodos(tab+"\t",((TransformGroup) o).getAllChildren());
			else if (o instanceof Group)
				imprimir_nodos(tab+"\t",((Group) o).getAllChildren());
			else if (o instanceof Shape3D)
				imprimir_nodos(tab+"\t",((Shape3D) o).getAllGeometries());
			/*else if (o instanceof Behavior)
				imprimir_nodos(tab+"\t",((Behavior) o).get());*/
		}
			
		
		
	}

} // end of class HelloJava3Dd
