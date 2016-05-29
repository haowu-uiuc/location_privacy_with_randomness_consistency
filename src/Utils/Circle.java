package Utils;

public class Circle {
	Double r;
	Point center;
	
	public Circle(double r, Point center){
		this.r = r;
		this.center = center;
	}
	
	public double getR(){
		return r;
	}
	
	public Point getCenter(){
		return center;
	}
}
