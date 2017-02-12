package utils;

public class PointSet {

	boolean[][] isPointInSet;
	int minX;
	int minY;
	int maxX;
	int maxY;

	public PointSet(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		isPointInSet = new boolean[maxX - minX + 1][maxY - minY + 1];
	}

	public boolean contains(Point X) {
		int x = (int) Math.round(X.getX());
		int y = (int) Math.round(X.getY());
		double e = 0.0001;
		if(Math.abs(x - X.getX()) > e || (Math.abs(y - X.getY()) > e)) {
			System.out.println("The PointSet cannot process float coordinates, please input interger");
			return false;
		}
		
		int xIndex = x - minX;
		int yIndex = y - minY;
		return isPointInSet[xIndex][yIndex];
	}
	
	public void add(Point X) {
		int x = (int) Math.round(X.getX());
		int y = (int) Math.round(X.getY());
		double e = 0.0001;
		if(Math.abs(x - X.getX()) > e || (Math.abs(y - X.getY()) > e)) {
			System.out.println("The PointSet cannot process float coordinates, please input interger");
			return;
		}
		
		int xIndex = x - minX;
		int yIndex = y - minY;
		isPointInSet[xIndex][yIndex] = true;
	}
	
}
