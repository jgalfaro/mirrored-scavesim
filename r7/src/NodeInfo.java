package src;
import javax.vecmath.Point3d;


public class NodeInfo{
	Point3d position;

	public static final double txRadius = 25.0D;//TODO: next versions must add this value from GUI
	public static final double rxRadius = 25.0D;//TODO: next versions must add this value from GUI
	public double xMove;
	public double yMove;
	public double zMove;
	public int knownBy;

	public NodeInfo(double pX, double pY,double pZ){
		position= new Point3d(pX, pY, pZ);
	}

	public Point3d getPosition() {
		return position;
	}

	public void setPosition(Point3d position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Move("+xMove+","+yMove+","+zMove+");knownBy="+knownBy;
	}
	
	
}
