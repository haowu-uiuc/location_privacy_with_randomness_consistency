package defenseExp;

import java.util.ArrayList;
import java.util.List;

import utils.Point;

public class HierarchicalSubdivision {

	int maxX;
	int minX;
	int maxY;
	int minY;
	int baseMapScale; // side length = baseMapScale * 2 + 1
	int numOfHier;
	Point[][][] centers;

	public HierarchicalSubdivision(int maxX, int minX, int maxY, int minY,
			int baseMapRadius) {
		this.maxX = maxX;
		this.minX = minX;
		this.maxY = maxY;
		this.minY = minY;
		int maxRadius = Math.max(Math.max(Math.abs(maxX), Math.abs(minX)),
				Math.max(Math.abs(maxY), Math.abs(minY)));
		this.baseMapScale = baseMapRadius * 2 + 1;
		numOfHier = 1;
		int maxSquareSize = maxRadius * 2 + 1;
		int squareSize = 1;
		
		while (squareSize < maxSquareSize) {
			numOfHier ++;
			squareSize *= baseMapScale;
		}
		
		centers = new Point[maxX - minX + 1][maxY - minY + 1][numOfHier];
		for (int i = 0; i < maxX - minX + 1; i ++) {
			for (int j = 0; j < maxY - minY + 1; j ++) {
				for (int k = 0; k < numOfHier; k ++ ) {
					centers[i][j][k] = null;
				}
			}
		}
		
	}

	public Point[] getCenters(Point X) {
		int x = (int)Math.round(X.getX());
		int y = (int)Math.round(X.getY());
		if (centers[getXIndex(x)][getYIndex(y)][0] != null) {
			return centers[getXIndex(x)][getYIndex(y)];
		}		
		
		// add centers of squares in each hierachies
		int squareSize = 1;
		for (int hierIndex = 0; hierIndex < numOfHier; hierIndex++) {
			double centerX = Math.round(X.getX() / (double)squareSize) * squareSize;
			double centerY = Math.round(X.getY() / (double)squareSize) * squareSize;
			Point center = new Point(centerX, centerY);
			centers[getXIndex(x)][getYIndex(y)][hierIndex] = center;
			squareSize *= baseMapScale;
		}
		
		return centers[getXIndex(x)][getYIndex(y)];
	}
	
//	public List<Point> getCenters(Point X) {
//		List<Point> centerList = new ArrayList<>();
//		
//		// add centers of squares in each hierachies
//		int squareSize = 1;
//		for (int hierIndex = 0; hierIndex < numOfHier; hierIndex++) {
//			double centerX = Math.round(X.getX() / (double)squareSize) * squareSize;
//			double centerY = Math.round(X.getY() / (double)squareSize) * squareSize;
//			Point center = new Point(centerX, centerY);
//			centerList.add(center);
//			squareSize *= baseMapScale;
//		}
//		
//		return centerList;
//	}

	public Double getCenterDist(Point X1, Point X2) {		
		Point[] centerListX1 = getCenters(X1);
		Point[] centerListX2 = getCenters(X2);
		
		Point centerX1 = centerListX1[0];
		Point centerX2 = centerListX2[0];
		
		for (int i = 1; i < numOfHier; i++) {
			Point curCenterX1 = centerListX1[i];
			Point curCenterX2 = centerListX2[i];
			if (curCenterX1.getX() == curCenterX2.getX()
				&& curCenterX1.getY() == curCenterX2.getY()) {
				break;
			}
			centerX1 = curCenterX1;
			centerX2 = curCenterX2;
		}
		
		return centerX1.getDistance(centerX2);
	}
	
	private int getXIndex(int x) {
		if (x < minX || x > maxX) {
			System.out.println("X index is out of bound");
		}
		return x - minX;
	}
	
	private int getYIndex(int y) {
		if (y < minY || y > maxY) {
			System.out.println("Y index is out of bound");
		}
		return y - minY;
	}

}
