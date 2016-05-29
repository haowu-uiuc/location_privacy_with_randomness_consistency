package AttackDemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Entropy.PFunctionSerie;
import Utils.Point;

public class LocationHacker {

	private MapQuantizer mapQuantizer;
	private AttackToNaiveDefense attack;
	
	public LocationHacker(Point geo_center, double radius, double squareSize,
			PostQuerier postQuerier) throws Exception {
		
		mapQuantizer = new MapQuantizer(geo_center, radius, squareSize);
		postQuerier.setMapQuantizer(mapQuantizer);
		attack = new AttackToNaiveDefense();
		attack.setRange((int)mapQuantizer.getGridMinX(), 
				(int)mapQuantizer.getGridMaxX(), 
				(int)mapQuantizer.getGridMinY(), 
				(int)mapQuantizer.getGridMaxY());
		attack.setPostQuerier(postQuerier);
		attack.setNumOfStep(10);
	}
	
	public void diableWriteFile() {
		attack.disableWriteFile();
	}
	
	public void setPostToHack(String postId) {
		attack.setPostId(postId);
	}
	
	public void setPFuncSerie(PFunctionSerie pFuncSerie) {
		PFunctionSerie gridPFuncSerie = pFuncSerie.clone();
		gridPFuncSerie.scaleBy(1. / mapQuantizer.getSquareSize());
		attack.setPFunctionSerie(gridPFuncSerie);
	}
	
	public void setNumOfStep(int numOfStep) {
		attack.setNumOfStep(numOfStep);
	}
	
	public Point run() throws IOException {
		attack.run();
		
		double estX = 0.;
		double estY = 0.;
		
		List<Double> probX = attack.getProbX();
		List<Point> pointList = attack.getAllPoints();
		
		
		for (int i = 0; i < pointList.size(); i++) {
			double prob = probX.get(i);
			Point point = pointList.get(i);
			estX += prob * point.getX();
			estY += prob * point.getY();
		}
		
		return mapQuantizer.getGeoPoint(new Point(estX, estY));
	}
	
}
