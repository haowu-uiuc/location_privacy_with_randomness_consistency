package utils;
import java.util.ArrayList;
import java.util.List;


public class GeoUtils {

	public static List<Point> generatePointList(double minX, double maxX, double minY, double maxY, double eps){
		List<Point> pointList = new ArrayList<>();
		for(double x = minX; x <= maxX; x += eps){
			for(double y = minY; y <= maxY; y += eps){
				Point curPoint = new Point(x, y);
				pointList.add(curPoint);
			}
		}
		
		return pointList;
	}
	
	public static List<Point> getPointListInDounut(Donut donut, List<Point> candidatePoints){
		
		List<Point> outPointList = new ArrayList<>();
		for(Point X : candidatePoints){
			if(X.isInDonut(donut)){
				outPointList.add(X);
			}
		}
		
		return outPointList;
	}
	
	public static List<Point> getPointListOutCircle(Circle circle, List<Point> candidatePoints){
		List<Point> outPointList = new ArrayList<>();
		for(Point X : candidatePoints){
			if(!X.isInCircle(circle)){
				outPointList.add(X);
			}
		}
		
		return outPointList;
	}
	
	public static double getAreaOfPointList(List<Point> pointList, double eps){
		return pointList.size() * eps * eps;
	}
	
	public static int getResultFromSearch(Point target, Point searchPoint){
		return (int)(target.getDistance(searchPoint));
	}
	
	public static List<Point> generatePointsOnCircle(Circle circle, int numOfPoints){
		
		List<Point> pointList = new ArrayList<Point>();
		for(int i = 0; i < numOfPoints; i++){
			double theta = 2. * Math.PI * (double)i / (double)numOfPoints;
			double x = Math.cos(theta) * circle.getR();
			double y = Math.sin(theta) * circle.getR();
			Point curPoint = new Point(x, y);
			pointList.add(curPoint);
		}
		
		return pointList;
	}
	
	/**
	 * return the geo distance between to geo points (lon, lat)
	 * the result is approximate distance between to close points.
	 * Unit is km.
	 * @param geo_x1
	 * @param geo_x2
	 * @return
	 */
	public static double getDistanceBetweenTwoGeos(Point geo_x1, Point geo_x2) {
		double R = 6371;
		double Rx = R * Math.cos(geo_x1.getY() / 180. * Math.PI);
		return R * (geo_x1.getY() - geo_x2.getY()) * (geo_x1.getY() - geo_x2.getY())
				+ Rx * (geo_x1.getX() - geo_x2.getX()) * (geo_x1.getX() - geo_x2.getX());
	}
	
}
