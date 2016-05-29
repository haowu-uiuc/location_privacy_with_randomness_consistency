package Utils;

public class Point {
	private Double x;
	private Double y;
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public Point(Double x, Double y){
		this.x = x;
		this.y = y;
	}
	
	public boolean isInCircle(Circle circle){
		
		if(getDistance(circle.getCenter()) <= circle.getR()){
			return true;
		}
		
		return false;
	}
	
	public boolean isInDonut(Donut donut){
		double dist = getDistance(donut.getCenter());
		if(dist <= donut.get_R() && dist >= donut.get_r()){
			return true;
		}
		
		return false;
	}
	
	public double getDistance(Point point){
		return Math.sqrt(Math.pow(x - point.getX(), 2) + Math.pow(y - point.getY(), 2));
	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof Point 
		   && ((Point)object).getX() == this.getX()
		   && ((Point)object).getY() == this.getY()){
		    return true;
		} else {
		    return false;
		}
	}
	
	@Override
	public int hashCode() {
		String coordinate = x + "," + y;
	      return coordinate.hashCode();
	}
	
	@Override
	public String toString(){
		return "(" + x + "," + y + ")";
	}
}
