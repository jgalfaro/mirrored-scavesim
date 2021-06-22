package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Font;

import javax.swing.JComponent;

import java.util.Enumeration;
import java.util.Vector;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.*;
import javax.vecmath.*;

///////////////////////////////////////////////
//import javax.media.j3d.Appearance;
//import javax.media.j3d.BoundingSphere;
//import javax.media.j3d.BranchGroup;
//import javax.media.j3d.Canvas3D;
//import javax.media.j3d.ColoringAttributes;
//import javax.media.j3d.Group;
//import javax.media.j3d.LineArray;
//import javax.media.j3d.Shape3D;
//import javax.media.j3d.Transform3D;
//import javax.media.j3d.TransformGroup;

//import javax.vecmath.Color3f;
//import javax.vecmath.Point3d;
//import javax.vecmath.Vector3d;
//import javax.vecmath.Vector3f;
///////////////////////////////////////////////

import src.behavior.AgentBehavior;
import src.behavior.NodeBehavior;


public class Network3D extends JComponent {

    protected Vector<Agent> agentList;
    protected Record dataCollector;
    protected Color3f nodeColor;
    protected Color3f nodeColor1;
    protected Color3f nodeColor2;
    protected Color3f nodeColor3;
    protected Color3f txRangeColor;
    protected Color3f linkColor;
    protected Color3f agentColor;
    protected Color3f agentColor2;
    protected Color3f specialAgentColor;
    protected Color3f agentDead;
    protected final int agentSize = 10;
    protected int xSize;
    protected int ySize;

    protected final int nodeSize = 15;
    Appearance agentAppear1 = new Appearance();
    Appearance agentAppear2 = new Appearance();
    Appearance agentAppear3 = new Appearance();
    Appearance agentAppearBH = new Appearance();

    Canvas3D canvas3D;
    TransformGroup publicationNode;
    BranchGroup objRoot;

    public void updateAgentList(Vector<Agent> agentVector){
        this.agentList = agentVector;
    }

    public Network3D(Vector<Node> nodesVector, Vector<Agent> agentVector, int sizeX, int sizeY){


        this.agentList = agentVector;

        this.nodeColor1 = new Color3f(0.5F, 0.5F, 0.5F); // GREY
        this.nodeColor2 = new Color3f(1, 0.5F, 0);// Orange
        this.nodeColor3 = new Color3f(1, 0, 0); // RED
        this.linkColor = new Color3f(1, 1, 1); // WHITE;
        this.txRangeColor = new Color3f(0.2F, 0.2F, 0.2F);// GREY
        this.agentColor2 = new Color3f(1, 1, 0);// YELLOW
        this.agentColor = new Color3f(0, 1, 0);// GREEN
        this.specialAgentColor = new Color3f(0, 0, 1); // BLUE
        this.agentDead = new Color3f(1, 0, 0);// GREEN


        agentAppear1.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        agentAppear2.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        agentAppear3.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        agentAppear1.setColoringAttributes(new ColoringAttributes(specialAgentColor, ColoringAttributes.FASTEST));
        agentAppear2.setColoringAttributes(new ColoringAttributes(agentColor, ColoringAttributes.FASTEST));
        agentAppear3.setColoringAttributes(new ColoringAttributes(agentColor2, ColoringAttributes.FASTEST));
        agentAppearBH.setColoringAttributes(new ColoringAttributes(agentDead, ColoringAttributes.FASTEST));


        // setSize(sizeX, sizeY);

        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        // 1-Crear canvas3D
        canvas3D = new Canvas3D(config);
        this.setSize(sizeX, sizeY);
        canvas3D.setSize(sizeX, sizeY);
        add("Center", canvas3D);

        // 2-Build simpleUniverse and link to canvas3D
        // SimpleUniverse is a Convenience Utility class
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        // 3-Build content branch
        BranchGroup scene = createSceneGraph();

        // 4-Compile
        scene.compile();

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        simpleU.getViewingPlatform().setNominalViewingTransform();

        //Move the camera
        TransformGroup VpTG = simpleU.getViewingPlatform().getViewPlatformTransform();
        Transform3D Trfcamera = new Transform3D();
        Trfcamera.setTranslation(new Vector3f(0.0f, 0.0f, 3f)); // initial 3D position (0f,0f,3f)
        VpTG.setTransform( Trfcamera );

        simpleU.addBranchGraph(scene);
    }

    /** Set nodes in initial position
     * @param paramGraphics
     */
    public void renderNodes(Graphics paramGraphics) {
        if(this.agentList.size()>0){//if there are still agents
            for (Node node : Simulator.nodeList) {
                drawNode(node);
            }
        }
    }

    public void renderState(Graphics paramGraphics) {


        Enumeration<Node> localEnumeration = Simulator.nodeList.elements();

        if(this.agentList.size()>0){//if there are still agents

            while (localEnumeration.hasMoreElements()) {

                Node localNode = localEnumeration.nextElement();


                float f = (this.agentList.size() - localNode.getInfo().knownBy) / this.agentList.size();

                if(localNode.getBH()){
                    //System.out.print("Node "+localNode.getName()+" is BH!!!\n");
                    //don't change the color of a BH node
                    localNode.changeColorNode(new Color3f(
                                                          this.nodeColor3.x * 1,
                                                          this.nodeColor3.y * 1,
                                                          this.nodeColor3.z * 1));
                }else if(!localNode.getToken()){
                    //change color of visited nodes
                    localNode.changeColorNode(new Color3f(
                                                     this.nodeColor1.x * f + this.nodeColor2.x * (1.0F - f),//change to Color2
                                                     this.nodeColor1.y * f + this.nodeColor2.y * (1.0F - f),//change to Color2
                                                     this.nodeColor1.z * f + this.nodeColor2.z * (1.0F - f)));//change to Color2
                }else{
                    localNode.changeColorNode(new Color3f(
                                                          this.nodeColor1.x * 1,
                                                          this.nodeColor1.y * 1,
                                                          this.nodeColor1.z * 1));
                }
            }//while

        }

        Enumeration<Agent> agentes = this.agentList.elements();

        while (agentes.hasMoreElements()) {
            Agent a = agentes.nextElement();

            a.getBehaviour().setPosition(a.getCurrentNode().getPosition());
            a.getBehaviour().postId(AgentBehavior.ID);
        }


    }

    public void renderRangeAndLinks(Graphics paramGraphics) {
        Node localNode1;
        //		NodeInfo localNodeInfo1;
        Enumeration<Node> localEnumeration1 = Simulator.nodeList.elements();

        while (localEnumeration1.hasMoreElements()) {
            localNode1 = (Node) localEnumeration1.nextElement();
            // localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
        }

        localEnumeration1 = Simulator.nodeList.elements();
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

        //Default Node Color
        Appearance nodeAppear=new Appearance();
        nodeAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        nodeAppear.setColoringAttributes(new ColoringAttributes(nodeColor1, ColoringAttributes.FASTEST));

        Sphere sphere = new Sphere(0.02f, nodeAppear);
        TransformGroup translateNode = translate(node.getPosition());


        sphere.getAppearance().getColoringAttributes().setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

        translateNode.addChild(sphere);


        NodeBehavior behavior = new NodeBehavior(sphere);
        behavior.setSchedulingBounds(new BoundingSphere());

        node.setBehavior(behavior);

        translateNode.addChild(behavior);

        publicationNode.addChild(translateNode);

        if(Simulator.showLabels==1){
            Font3D f3d = new Font3D(new Font("Arial", Font.PLAIN, 1),new FontExtrusion());
            Text3D label3D = new Text3D(f3d, node.getName(), new Point3f(+.25f,0.0f,0.0f));//avoid overlaping node's shape
            Point3d pos = node.getPosition();
            Vector3d site = new Vector3d(pos.x, pos.y, pos.z);
            Transform3D  t3d = new Transform3D();
            t3d.setScale(0.05);
            t3d.setTranslation(site);
            TransformGroup subTg = new TransformGroup(t3d);

            subTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            Appearance app = new Appearance();
            Color3f white = new Color3f(1f, 1f, 1f);
            Color3f objColor = new Color3f(0f, 1f, 0f);
            app.setMaterial(new Material(objColor, white, objColor, white, 1.0f));
            Shape3D sh = new Shape3D(label3D, app);
            subTg.addChild(sh);
            publicationNode.addChild(subTg);
        }

    }

    protected void drawAgent(Agent agent) {

        //Set position
        Vector3f pos;
        Sphere sphere=null;
        String agentName=agent.getName();

        if(agent.getCurrentNode()!=null){
            pos= new Vector3f((float)agent.getCurrentNode().getPosition().x,(float)agent.getCurrentNode().getPosition().y,(float)agent.getCurrentNode().getPosition().z);
        }else{
            pos= new Vector3f(0f,0f,0f);
        }


        //Set Shape3D
        if(agent.alive){
            //System.out.print(agent.getName()+" is alive!\n");
            if(agentName.equals("A1")){
                sphere = new Sphere(0.04f, agentAppear1);
            }else if(agentName.equals("A2")){
                sphere = new Sphere(0.04f, agentAppear2);
            }else{
                sphere = new Sphere(0.04f, agentAppear3);
            }
        }else{
            sphere = new Sphere(0.02f, agentAppearBH);
        }

        //moveAgent and local root node
        TransformGroup moveAgent = new TransformGroup();
        moveAgent.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        //Add Shape to node
        moveAgent.addChild(sphere);


        AgentBehavior myBehaviour = new AgentBehavior(moveAgent,pos);
        myBehaviour.setSchedulingBounds(new BoundingSphere());
        moveAgent.addChild(myBehaviour);

        agent.setBehaviour(myBehaviour);


        publicationNode.addChild(moveAgent);
    }

    protected void drawArrow(Point3d p0, Point3d p1) {

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
        //Dimension localDimension = getSize();
        /*if ((canvas3D == null) || (localDimension.width != canvas3D.getSize().width) || (localDimension.height != canvas3D.getSize().height)) {
          this.canvas3D = createImage(localDimension.width,localDimension.height);

          clearImageBuffer(localDimension);
          }*/

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

    protected TransformGroup translate(Point3d pos) {
        Transform3D tras = new Transform3D();

        Vector3d site = new Vector3d(pos.x, pos.y, pos.z);
        tras.setTranslation(site);
        TransformGroup objTras = new TransformGroup(tras);
        return objTras;
    }

    private void crear(BranchGroup objRoot) {

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

        MouseTranslate myMouseTranslate = new MouseTranslate();
        myMouseTranslate.setTransformGroup(publicationNode);
        myMouseTranslate.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myMouseTranslate);

//         // return objSpin;

    }

    public BranchGroup createSceneGraph() {
        // Create the root of the branch graph
        objRoot = new BranchGroup();
        crear(objRoot);

        return objRoot;
    } // end of CreateSceneGraph method of HelloJava3Dd


    public void reset(){
        for (Node node : Simulator.nodeList) {
            node.getInfo().knownBy=0;
        }
        for (Agent agent : Simulator.agentList) {
            //			Enumeration<Node> key = agent.knownNodes.keys();
            agent.knownNodes.clear();
            //			while(key.hasMoreElements()){
            //				Node n=key.nextElement();
            //				agent.knownNodes.remove(n);
            //				agent.knownNodes.put(n,0);
            //			}
            System.out.println(agent.knownNodes);
        }
    }

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

    public void setRecord(Record paramRecord){
        this.dataCollector = paramRecord;
    }


} // end of class HelloJava3Dd
