package Utils;

public class Donut {
	Double r;
	Double R;
	Point center;
	
	public Donut(double r, double R, Point center){
		this.r = r;
		this.R = R;
		this.center = center;
	}
	
	public double get_r(){
		return r;
	}
	
	public double get_R(){
		return R;
	}
	
	public Point getCenter(){
		return center;
	}
}
