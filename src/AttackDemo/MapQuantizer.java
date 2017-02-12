package attackDemo;

import utils.Point;

public class MapQuantizer {
	
	double R = 6371; 	// earth radius km
	double Rx;			// radius of the latitude circle
	double squareSize; 	// km
	Point geo_center; 	// (lon, lat)
	double radius; 		// km
	int grid_maxX; 		
	int grid_maxY;
	int grid_minX;
	int grid_minY;
	
	/**
	 * geo_center is a point with x-> longtitude, y-> latitude
	 * radius is search radius, by km
	 * squareSize is the length of the square in quantization, by km
	 * @param geo_center
	 * @param radius
	 * @throws Exception 
	 */
	public MapQuantizer(Point geo_center, double radius, double squareSize) throws Exception {
		this.squareSize = squareSize;
		this.radius = radius;
		this.geo_center = geo_center;
		Rx = R * Math.cos(geo_center.getY() / 180. * Math.PI);
		
		grid_maxX = (int) Math.round(radius / squareSize);
		grid_maxY = grid_maxX;
		grid_minX = -grid_maxX;
		grid_minY = grid_minX;
		
		if (grid_maxX <= 0) {
			throw new Exception("square size is too large.");
		}
	}
	
	public Point getGridPoint(Point geo_point) {
		double x = Math.round((geo_point.getX() - geo_center.getX()) / 180 * Math.PI * Rx / squareSize);
		double y = Math.round((geo_point.getY() - geo_center.getY()) / 180 * Math.PI * R / squareSize);

		return new Point(x, y);
	}
	
	public Point getGeoPoint(Point grid_point) { 
		return getGeoPointByDistance(geo_center, grid_point.getX() * squareSize, grid_point.getY() * squareSize);
	}
	
	/**
	 * get point from geo_init by moving x km in longtitude and y km in latitude
	 * @param geo_init
	 * @param x
	 * @param y
	 * @return
	 */
	private Point getGeoPointByDistance(Point geo_init, double x, double y) {
		double init_lon = geo_init.getX();
		double init_lat = geo_init.getY();
		
		return new Point(init_lon + x / Rx / Math.PI * 180., init_lat + y / R / Math.PI * 180.);
	}
	
	public double getGridMaxX() {
		return grid_maxX;
	}
	
	public double getGridMaxY() {
		return grid_maxY;
	}
	
	public double getGridMinX() {
		return grid_minX;
	}
	
	public double getGridMinY() {
		return grid_minY;
	}
	
	public double getSquareSize() {
		return squareSize;
	}
	
}
