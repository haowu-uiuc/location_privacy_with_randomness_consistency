package defenseExp;

import java.io.File;
import java.util.List;

import utils.Point;

public interface BasicSingleExperiment extends Experiment{
	
	/**
	 * set the grid range
	 * recommend: minX = -maxX, minY = -maxY
	 * @param minX integer
	 * @param maxX integer
	 * @param minY integer
	 * @param maxY integer
	 */
	public void setRange(double minX, double maxX, double minY, double maxY);
	
	public void setBaseMapRadius(int baseMapRadius);
	
	public void setPfuncThreshold(List<Double> threList);
	
	public void setNumOfStep(int numOfStep);
	
	public void setTweetPoint(double x_t, double y_t);
	
	public void setSearchStrategy(String strategy);
	
	public void setPFunctionSerie(PFunctionSerie pFuncSerie);
	
	public Double getUserMSE();
	
	public Double getUserMAE();
	
	public Double getUserPAE();
	
	public List<Double> getHList();
	
	public List<Double> getAttMSEList();
	
	public List<Double> getAttMAEList();
	
	public List<Double> getAttMAESquareList();
	
	public List<List<Double>> getProbXList();
	
	public List<Point> getAllPoints();
	
	public void disableStdout();
	
	public void enableStdout();
	
	public PFunctionSerie getPFunctionSerie();
	
	/**
	 * if the exp set the realDataFile, then the new one also has the same file set
	 * @return
	 */
	public BasicSingleExperiment createNewOne();
	
	public void disableWriteFile();
	
	public void setSquareSize(double squareSize);
	
	public void useRealData(File realDataFile);

	public void useFlatData();
	
	public void setNumOfDuplicatedPosts(int numOfPosts);
	
}
