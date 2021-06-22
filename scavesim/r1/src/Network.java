import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Network extends Canvas{

	protected Vector<Node> nodeList;
	protected Hashtable<Node,NodeInfo> nodeInfoTable;
	protected boolean graphPictureValid = false;
	protected Vector<Agent> agentList;
	protected Color nodeColor;
	protected Color nodeColor1;
	protected Color nodeColor2;
	protected Color txRangeColor;
	protected Color linkColor;
	protected Color agentColor;
	protected Color specialAgentColor;
	protected final int nodeSize = 15;
	protected final int agentSize = 10;
	protected int xSize;
	protected int ySize;
	protected Image imageBuffer;
	protected Dimension imageBufferSize;

	public Network(Vector<Node> paramVector1, Hashtable paramHashtable, Vector<Agent> paramVector2, int paramInt1, int paramInt2){
		this.nodeList = paramVector1;
		this.nodeInfoTable = paramHashtable;
		this.agentList = paramVector2;
		this.xSize = paramInt1;
		this.ySize = paramInt2;
		setBackground(Color.white);
		setForeground(Color.black);
		this.nodeColor1 = Color.black;
		this.nodeColor2 = Color.orange; //new Color(0.3F, 0.3F, 0.3F);
		this.linkColor = Color.black; //new Color(0.0F, 0.6F, 0.0F);
		this.txRangeColor = new Color(0.2F, 0.2F, 0.2F);
		this.agentColor = Color.green;
		this.specialAgentColor = Color.blue; //this.agentColor;
		setSize(paramInt1, paramInt2);
	}

	public void invalidateGraphPicture(){
		this.graphPictureValid = false;
	}

	public void renderState(Graphics paramGraphics){
		Enumeration<Node> localEnumeration = this.nodeList.elements();

		while (localEnumeration.hasMoreElements()){

			Node localObject = (Node)localEnumeration.nextElement();

			NodeInfo localNodeInfo = (NodeInfo)this.nodeInfoTable.get(localObject);

			float f = (this.agentList.size() - localNodeInfo.knownBy) / this.agentList.size();

			paramGraphics.setColor(new Color(new Float(this.nodeColor1.getRed() * f + this.nodeColor2.getRed() * (1.0F - f)).intValue(), new Float(this.nodeColor1.getGreen() * f + this.nodeColor2.getGreen() * (1.0F - f)).intValue(), new Float(this.nodeColor1.getBlue() * f + this.nodeColor2.getBlue() * (1.0F - f)).intValue()));

			//nodes
			paramGraphics.fillRect((int)(localNodeInfo.x - 6.0D), (int)(localNodeInfo.y - 6.0D), 13, 13);
		}

		paramGraphics.setColor(this.agentColor);

		Object localObject = this.agentList.elements();

		while (((Enumeration)localObject).hasMoreElements()){
			drawAgent(paramGraphics, (Agent)((Enumeration)localObject).nextElement());
		}

		paramGraphics.setColor(this.specialAgentColor);
		drawAgent(paramGraphics, (Agent)this.agentList.firstElement());

	}

	public void renderRangeAndLinks(Graphics paramGraphics){

		Node localNode1;
		NodeInfo localNodeInfo1;
		Enumeration<Node> localEnumeration1 = this.nodeList.elements();

		while (localEnumeration1.hasMoreElements()){
			localNode1 = (Node)localEnumeration1.nextElement();
			localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
			paramGraphics.setColor(this.txRangeColor);

			//TODO:show/hide range from GUI
			//paramGraphics.drawOval((int)(localNodeInfo1.x - 85.0D), (int)(localNodeInfo1.y - 85.0D), 170, 170);
		}

		localEnumeration1 = this.nodeList.elements();
		while (localEnumeration1.hasMoreElements()){
			localNode1 = (Node)localEnumeration1.nextElement();
			localNodeInfo1 = (NodeInfo)this.nodeInfoTable.get(localNode1);
			paramGraphics.setColor(this.linkColor);
			Enumeration<Node> localEnumeration2 = localNode1.getReachableNodes().elements();
			while (localEnumeration2.hasMoreElements()){
				Node localNode2 = (Node)localEnumeration2.nextElement();
				NodeInfo localNodeInfo2 = (NodeInfo)this.nodeInfoTable.get(localNode2);
				drawArrow(paramGraphics, (int)localNodeInfo1.x, (int)localNodeInfo1.y, (int)localNodeInfo2.x, (int)localNodeInfo2.y);
			}
		}
	}

	protected void drawAgent(Graphics paramGraphics, Agent paramAgent){
		NodeInfo localNodeInfo = (NodeInfo)this.nodeInfoTable.get(paramAgent.getCurrentNode());
		paramGraphics.fillOval((int)(localNodeInfo.x - 5.0D), (int)(localNodeInfo.y - 5.0D), 10, 10);
	}

	protected void drawArrow(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4){
		double d = Math.atan2(paramInt4 - paramInt2, paramInt3 - paramInt1);
		int i = paramInt1 + (int)Math.round(14.0D * Math.cos(d));//TODO:parametrize from GUI
		int k = paramInt2 + (int)Math.round(14.0D * Math.sin(d));//TODO:parametrize from GUI
		int j = paramInt3 - (int)Math.round(14.0D * Math.cos(d));//TODO:parametrize from GUI
		int l = paramInt4 - (int)Math.round(14.0D * Math.sin(d));//TODO:parametrize from GUI
		paramGraphics.drawLine(i, k, j, l);
	}

	public Dimension getPreferredSize(){
		return new Dimension(this.xSize, this.ySize);
	}

	public Dimension getMinimumSize(){
		return getPreferredSize();
	}

	public Dimension minimumSize(){
		return getMinimumSize();
	}

	public Dimension preferredSize(){
		return getPreferredSize();
	}

	public void paint(Graphics paramGraphics){
		Dimension localDimension = getSize();
		if ((this.imageBuffer == null) ||
				(localDimension.width != this.imageBufferSize.width) ||
				(localDimension.height != this.imageBufferSize.height)
				){
			this.imageBuffer = createImage(localDimension.width, localDimension.height);
			this.imageBufferSize = localDimension;
			clearImageBuffer();
		}
		if (!(this.graphPictureValid)){
			clearImageBuffer();
			
			this.graphPictureValid = true;
		}
		renderState(this.imageBuffer.getGraphics());
		renderRangeAndLinks(this.imageBuffer.getGraphics());
		paramGraphics.drawImage(this.imageBuffer, 0, 0, this);
	}

	public void update(Graphics paramGraphics){
		paint(paramGraphics);

		
	}

	public void clearImageBuffer(){
		Graphics localGraphics = this.imageBuffer.getGraphics();
		Color localColor = localGraphics.getColor();
		localGraphics.setColor(getBackground());
		localGraphics.fillRect(0, 0, this.imageBufferSize.width, this.imageBufferSize.height);
		localGraphics.setColor(localColor);
	}
}
