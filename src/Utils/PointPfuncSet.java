package Utils;

public class PointPfuncSet {

	boolean[][][] set;
	int minX;
	int minY;
	int maxX;
	int maxY;
	int numOfThres;

	public PointPfuncSet(int minX, int maxX, int minY, int maxY, int numOfThres) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.numOfThres = numOfThres;
		set = new boolean[maxX - minX + 1][maxY - minY + 1][numOfThres];
	}

	public boolean contains(Point X, int pFuncIndex) {
		int x = (int) Math.round(X.getX());
		int y = (int) Math.round(X.getY());
		double e = 0.0001;
		if(Math.abs(x - X.getX()) > e || (Math.abs(y - X.getY()) > e)) {
			System.out.println("The PointSet cannot process float coordinates, please input interger");
			return false;
		}
		
		int xIndex = x - minX;
		int yIndex = y - minY;
		return set[xIndex][yIndex][pFuncIndex];
	}
	
	public void add(Point X, int pFuncIndex) {
		int x = (int) Math.round(X.getX());
		int y = (int) Math.round(X.getY());
		double e = 0.0001;
		if(Math.abs(x - X.getX()) > e || (Math.abs(y - X.getY()) > e)) {
			System.out.println("The PointSet cannot process float coordinates, please input interger");
			return;
		}
		
		int xIndex = x - minX;
		int yIndex = y - minY;
		set[xIndex][yIndex][pFuncIndex] = true;
	}
	
}

