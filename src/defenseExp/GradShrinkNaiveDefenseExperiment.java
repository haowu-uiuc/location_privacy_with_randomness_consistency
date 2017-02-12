package defenseExp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.GeoUtils;
import utils.Point;

public class GradShrinkNaiveDefenseExperiment implements BasicSingleExperiment{

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private List<Double> threList;
	private int numOfStep;
	private Point tweetPos;
	private String optXqStrategy;
	private PFunctionSerie pFuncSerie;
	private boolean disableStdout;
	private boolean writeFileDisabled = false;
	private double squareSize = 1.0;
	private File realDataFile = null;
	private int numOfDuplicatedPosts = 1;
	
	private Double userMSE;
	private Double userMAE;
	private Double userPAE;
	private List<Double> attMSEList;
	private List<Double> attMAEList;
	private List<Double> HList; 
	private List<List<Double>> probXList;
	private List<Point> allPoints;
	
	private double shrinkTimes = 1.0;
	private List<Double> xOffsetList = Arrays.asList(0.);
	private List<Double> yOffsetList = Arrays.asList(0.);
	
	public GradShrinkNaiveDefenseExperiment(){
		threList = new ArrayList<>();
		pFuncSerie = new HyperbolaPFunctionSerie();
		HList = new ArrayList<>();
		attMSEList = new ArrayList<>();
		attMAEList = new ArrayList<>();
		probXList = new ArrayList<List<Double>>();
		disableStdout = false;
	}
	
	@Override
	public void setRange(double minX, double maxX, double minY, double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public void setPfuncThreshold(List<Double> threList) {
		this.threList = threList;
		List<Double> new_threList = new ArrayList<>();
		for (int i = 0; i < threList.size(); i++) {
			new_threList.add(threList.get(i) * shrinkTimes);
		}
		pFuncSerie.setThreshold(new_threList);
	}

	@Override
	public void setNumOfStep(int numOfStep) {
		this.numOfStep = numOfStep;	
	}

	@Override
	public void setTweetPoint(double x_t, double y_t) {
		tweetPos = new Point(x_t, y_t);
	}

	@Override
	public void setSearchStrategy(String strategy) {
		this.optXqStrategy = strategy;
	}

	@Override
	public void setPFunctionSerie(PFunctionSerie pFuncSerie) {
		this.pFuncSerie = pFuncSerie;
		this.pFuncSerie.setThreshold(threList);
	}

	@Override
	public Double getUserMSE() {
		return userMSE;
	}

	@Override
	public Double getUserMAE() {
		return userMAE;
	}

	@Override
	public Double getUserPAE() {
		return userPAE;
	}
	
	@Override
	public List<Double> getHList() {
		return HList;
	}

	@Override
	public List<Double> getAttMSEList() {
		return attMSEList;
	}

	@Override
	public List<Double> getAttMAEList() {
		return attMAEList;
	}

	@Override
	public List<Double> getAttMAESquareList(){
		return attMAEList;
	}
	
	@Override
	public List<List<Double>> getProbXList() {
		return probXList;
	}

	@Override
	public List<Point> getAllPoints() {
		return allPoints;
	}

	@Override
	public void disableStdout() {
		disableStdout = true;
	}

	@Override
	public void enableStdout() {
		disableStdout = false;
	}

	@Override
	public PFunctionSerie getPFunctionSerie() {
		return pFuncSerie;
	}

	@Override
	public BasicSingleExperiment createNewOne() {
		GradShrinkNaiveDefenseExperiment exp = new GradShrinkNaiveDefenseExperiment();
		exp.setShrinkTimes(shrinkTimes);
		exp.setQueryOffset(xOffsetList, yOffsetList);
		if (realDataFile != null) {
			exp.useRealData(realDataFile);
		}
		return exp;
	}
	
	@Override
	public void setSquareSize(double squareSize) {
		if (squareSize > 0) {
			this.squareSize = squareSize;
		} else {
			System.out.println("Square Size has to be large than zero!");
		}
	}
	

	@Override
	public void run() throws IOException {
		HList.clear();
		attMSEList.clear();
		attMAEList.clear();
		probXList.clear();
		
		if (squareSize <= 0.0) {
			System.out.println("Square Size has to be large than zero!");
		}
		
		File outProbXDir = new File("./data/probX");
		File outEntropyDir = new File("./data/entropy");
		
		if(!outProbXDir.exists()){
			outProbXDir.mkdirs();
		}
		
		if(!outEntropyDir.exists()){
			outEntropyDir.mkdirs();
		}
		
		File outProbXFile = new File(outProbXDir.getAbsolutePath() + "/probX.txt");
		File outAllPointsFile = new File(outProbXDir.getAbsolutePath() + "/allPoints.txt");
		File outEntropyFile = new File(outEntropyDir.getAbsolutePath() + "/entropy.txt");
		File outAtackerMSEFile = new File(outEntropyDir.getAbsolutePath() + "/attackerMSE.txt");
		File outUserMSEFile = new File(outEntropyDir.getAbsolutePath() + "/userMSE.txt");
		BufferedWriter probXbw = new BufferedWriter(new FileWriter(outProbXFile));
		BufferedWriter entropybw = new BufferedWriter(new FileWriter(outEntropyFile));
		BufferedWriter allPointsbw = new BufferedWriter(new FileWriter(outAllPointsFile));
		BufferedWriter attackerMSEbw = new BufferedWriter(new FileWriter(outAtackerMSEFile));
		BufferedWriter userMSEbw = new BufferedWriter(new FileWriter(outUserMSEFile));
		BufferedWriter logBw = new BufferedWriter(new FileWriter(outEntropyDir.getAbsolutePath() + "/log.txt"));
		
		
		double eps = 1;			//fixed to 1, easy for matlab to process
		
		allPoints = GeoUtils.generatePointList(minX, maxX, minY, maxY, eps);
		if (!writeFileDisabled) {
			for(int k = 0; k < allPoints.size(); k++){
				Point curPoint = allPoints.get(k);
				allPointsbw.write(curPoint.getX() + "\t" + curPoint.getY() + "\n");
			}
		}

		List<Double> probX;
		if (realDataFile == null) {
			probX = ProbUtils.getFlatInitialProbX(allPoints);
		} else {
			probX = ProbUtils.getRealInitialProbX(allPoints, realDataFile); //???
		}
		
		//output user MSE to file
		// TODO how to calculate the errors in the case of multiple thresholds???
		// using the first PFunc for now.
		userPAE = ProbUtils.PerpostAbsErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX + maxX, minY + maxY), squareSize);
		userMSE = ProbUtils.MeanSquareErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX + maxX, minY + maxY), squareSize); 
		userMAE = ProbUtils.MeanAbsErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX + maxX, minY + maxY), squareSize);
		if (!writeFileDisabled) {
			userMSEbw.write(userMSE + "\t" + userMAE + '\t' + userPAE + "\n");
		}
		
		
		Double H = ProbUtils.entropy(probX);
		Double attMSE = ProbUtils.MeanSquareErrForAttacker(allPoints, probX, tweetPos, squareSize) / shrinkTimes / shrinkTimes;
		Double attMAE = ProbUtils.MeanAbsErrForAttacker(allPoints, probX, tweetPos, squareSize) / shrinkTimes;
		Point Xq = new Point(xOffsetList.get(0) * shrinkTimes, yOffsetList.get(0) * shrinkTimes);
		PFunction pFunc = pFuncSerie.getPFuncs().get(0);
		
		HList.add(H);
		attMSEList.add(attMSE);
		attMAEList.add(attMAE);
		probXList.add(probX);
		
		if(!disableStdout){
			System.out.println("Initialization: H = " + H + ", AttackerMAE = " + attMAE + ", UserPAE = " + userPAE );
		}
		if (!writeFileDisabled) {
			entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
			attackerMSEbw.write(attMSE + "\t" + attMAE + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
	
			for(int k = 0; k < probX.size(); k++){
				probXbw.write(probX.get(k) + "\t");
			}
			probXbw.newLine();
		}
		
		for(int i = 1; i <= numOfStep; i++){
			
			//calculate the new distribution for tweet
			if(i == 1){
				probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
				if(!disableStdout){
					System.out.println("Tweet_0 is shown");
				}
				
				for (int postId = 1; postId < numOfDuplicatedPosts; postId++) {
					double randomHash = Math.random();
					
					if(randomHash < pFunc.getValue(tweetPos.getDistance(Xq))){
						probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
						if(!disableStdout){
							System.out.println("Tweet_" + postId + " is shown");
						}
					} else{
						probX = ProbUtils.probX_nRx(allPoints, Xq, probX, pFunc);
						if(!disableStdout){
							System.out.println("Tweet_" + postId + " is not shown");
						}
					}
				}
				
			} else{
				for (int postId = 0; postId < numOfDuplicatedPosts; postId++) {
					double randomHash = Math.random();
					
					if(randomHash < pFunc.getValue(tweetPos.getDistance(Xq))){
						probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
						if(!disableStdout){
							System.out.println("Tweet_" + postId + " is shown");
						}
					} else{
						probX = ProbUtils.probX_nRx(allPoints, Xq, probX, pFunc);
						if(!disableStdout){
							System.out.println("Tweet_" + postId + " is not shown");
						}
					}
				}
			}
			
			H = ProbUtils.entropy(probX);
			attMSE = ProbUtils.MeanSquareErrForAttacker(allPoints, probX, tweetPos, squareSize) / shrinkTimes / shrinkTimes;
			attMAE = ProbUtils.MeanAbsErrForAttacker(allPoints, probX, tweetPos, squareSize) / shrinkTimes;
			
			HList.add(H);
			attMSEList.add(attMSE);
			attMAEList.add(attMAE);
			probXList.add(probX);

			if(!disableStdout){
				System.out.println(i + "th round: H = " + H + ", AttackerMAE = " + attMAE + ", Xq = (" + Xq.getX() + ", " + Xq.getY() + ")");
			}
			if (!writeFileDisabled) {
				entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
				attackerMSEbw.write(attMSE + "\t" + attMAE + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
			
				for(int k = 0; k < probX.size(); k++){
					probXbw.write(probX.get(k) + "\t");
				}
				probXbw.newLine();
			}
			
			//search for the optXq for next round;
			if(i >= numOfStep){
				continue;
			}
			
			Point optNextXq = new Point(xOffsetList.get(0) * shrinkTimes, yOffsetList.get(0) * shrinkTimes);
			PFunction optNextPFunc = pFuncSerie.getPFuncs().get(0);
			Double minNextExpH = Double.MAX_VALUE;
			for(int j = 0; j < allPoints.size(); j++){
				
				for (int offsetIndex = 0; offsetIndex < xOffsetList.size(); offsetIndex++){
					Point tmpNextXq = new Point(
							allPoints.get(j).getX() + xOffsetList.get(offsetIndex) * shrinkTimes, 
							allPoints.get(j).getY() + yOffsetList.get(offsetIndex) * shrinkTimes);
					
					for (PFunction tmpPFunc : pFuncSerie.getPFuncs()) {
						Double nextH;
						switch (optXqStrategy){
							case "exp": nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, tmpPFunc); 
										break;
							case "min": nextH = ProbUtils.minEntropy(allPoints, probX, tmpNextXq, tmpPFunc);
										break;
							case "max": nextH = ProbUtils.maxEntropy(allPoints, probX, tmpNextXq, tmpPFunc);
										break;
							default: 	nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, tmpPFunc);
										break;
						}
						
						if(nextH != Double.NaN && nextH <= minNextExpH){
							minNextExpH = nextH;
							optNextXq = tmpNextXq;
							optNextPFunc = tmpPFunc;
						}
					}
				}
			}

			Xq = optNextXq;
			pFunc = optNextPFunc;
			
		}
		
		////////
		logBw.close();
		////////
		probXbw.close();
		entropybw.close();
		allPointsbw.close();
		attackerMSEbw.close();
		userMSEbw.close();
	}

	@Override
	public void disableWriteFile() {
		writeFileDisabled = true;		
	}

	@Override
	public void setBaseMapRadius(int baseMapRadius) {
		return;
	}
	
	@Override
	public void useRealData(File realDataFile) {
		this.realDataFile = realDataFile;
	}

	@Override
	public void useFlatData() {
		realDataFile = null;
	}

	@Override
	public void setNumOfDuplicatedPosts(int numOfPosts) {
		numOfDuplicatedPosts = numOfPosts;
	}

	public void setShrinkTimes(double shrinkTimes) {
		this.shrinkTimes = shrinkTimes;
		List<Double> new_threList = new ArrayList<>();
		for (int i = 0; i < threList.size(); i++) {
			new_threList.add(threList.get(i) * shrinkTimes);
		}
		pFuncSerie.setThreshold(new_threList);
	}
	
	/**
	 * the offset is the coordinate before times the shrinkTimes
	 * @param x
	 * @param y
	 */
	public void setQueryOffset(List<Double> xOffsetList, List<Double> yOffsetList) {
		this.xOffsetList = xOffsetList;
		this.yOffsetList = yOffsetList;
	}
	
}
