public class NodeInfo{
    double x;
    double y;
    public static final double txRadius = 25.0D;//TODO: next versions must add this value from GUI
    public static final double rxRadius = 25.0D;//TODO: next versions must add this value from GUI
    public double xMove;
    public double yMove;
    public int knownBy;

  public NodeInfo(double pX, double pY){
    this.x = pX;
    this.y = pY;
  }
}
