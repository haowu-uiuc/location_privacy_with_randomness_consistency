package defenseExp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.GeoUtils;
import utils.Point;
import utils.PointPfuncSet;
import utils.PointSet;

public class MonoDefenseExperiment implements BasicSingleExperiment{

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private List<Double> threList;
	private int numOfStep;
	private Point tweetPos;
	private Point tweetSquareCenter;
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
	private List<Double> attMAESquareList;
	
	private List<Double> HList; 
	private List<List<Double>> probXList;
	private List<Point> allPoints;
	
	public MonoDefenseExperiment(){
		threList = new ArrayList<>();
		pFuncSerie = new HyperbolaPFunctionSerie();
		HList = new ArrayList<>();
		attMSEList = new ArrayList<>();
		attMAEList = new ArrayList<>();
		attMAESquareList = new ArrayList<>();
		probXList = new ArrayList<List<Double>>();
		disableStdout = false;
		
	}
	
	/**
	 * if realDataFile is set the new one will have the same file set
	 */
	public MonoDefenseExperiment createNewOne(){
		MonoDefenseExperiment exp = new MonoDefenseExperiment();
		if (realDataFile != null) {
			exp.useRealData(realDataFile);
		}
		return exp;
	}
	
	public void setRange(double minX, double maxX, double minY, double maxY){
		int minX_int = (int) Math.round(minX);
		int maxX_int = (int) Math.round(maxX);
		int minY_int = (int) Math.round(minY);
		int maxY_int = (int) Math.round(maxY);
		double e = 0.000001;
		if(Math.abs(minX - minX_int) > e 
				|| (Math.abs(maxX - maxX_int) > e)
				|| (Math.abs(minY - minY_int) > e)
				|| (Math.abs(maxY - maxY_int) > e)
				) {
			System.out.println("The EntropyProcessExperiment cannot process float coordinates, please input interger");
		}
		
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void setPfuncThreshold(List<Double> threList){
		this.threList = threList;
		pFuncSerie.setThreshold(threList);
	}
	
	public void setNumOfStep(int numOfStep){
		this.numOfStep = numOfStep;
	}
	
	public void setTweetPoint(double x_t, double y_t){
		tweetPos = new Point(x_t, y_t);
		tweetSquareCenter = new Point((double)Math.round(x_t), (double)Math.round(y_t));
	}
	
	public void setSearchStrategy(String strategy){
		this.optXqStrategy = strategy;
	}
	
	@Override
	public void setPFunctionSerie(PFunctionSerie pFuncSerie){
		this.pFuncSerie = pFuncSerie;
		this.pFuncSerie.setThreshold(threList);
	}
	
	public Double getUserMSE(){
		return userMSE;
	}
	
	public Double getUserMAE(){
		return userMAE;
	}
	
	@Override
	public Double getUserPAE() {
		return userPAE;
	}
	
	@Override
	public List<Double> getHList(){
		return HList;
	}
	
	@Override
	public List<Double> getAttMSEList(){
		return attMSEList;
	}
	
	@Override
	public List<Double> getAttMAEList(){
		return attMAEList;
	}
	
	@Override
	public List<Double> getAttMAESquareList(){
		return attMAESquareList;
	}
	
	@Override
	public List<List<Double>> getProbXList(){
		return probXList;
	}
	
	@Override
	public List<Point> getAllPoints(){
		return allPoints;
	}
	
	@Override
	public void disableStdout(){
		disableStdout = true;
	}
	
	@Override
	public void enableStdout(){
		disableStdout = false;
	}
	
	@Override
	public PFunctionSerie getPFunctionSerie(){
		return pFuncSerie;
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
		attMAESquareList.clear();
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
		
//		Set<Point> preXqSet = new HashSet<Point>();
		PointPfuncSet preXqSet = new PointPfuncSet((int)Math.round(minX), 
				(int)Math.round(maxX), 
				(int)Math.round(minY), 
				(int)Math.round(maxY),
				pFuncSerie.getPFuncs().size());

		HashOracle hashOracle = new HashOracle();

		allPoints = GeoUtils.generatePointList(minX, maxX, minY, maxY, eps);
		if (! writeFileDisabled) {
			for(int k = 0; k < allPoints.size(); k++){
				Point curPoint = allPoints.get(k);
				allPointsbw.write(curPoint.getX() + "\t" + curPoint.getY() + "\n");
			}
		}
		
		List<Integer> buttonThreIndexes = new ArrayList<>();
		List<Integer> topThreIndexes = new ArrayList<>();
		for (int i = 0; i < allPoints.size(); i++) {
			buttonThreIndexes.add(-1); 	// stands for null
			topThreIndexes.add(Integer.MAX_VALUE);		// stands for null
		}
		
		List<Double> probX;
		if (realDataFile == null) {
			probX = ProbUtils.getFlatInitialProbX(allPoints);
		} else {
			probX = ProbUtils.getRealInitialProbX(allPoints, realDataFile); //???
		}
		
		//output user MSE to file
		// using the first PFunc for now.
		userMSE = ProbUtils.MeanSquareErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX + maxX, minY + maxY), squareSize); 
		userMAE = ProbUtils.MeanAbsErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX + maxX, minY + maxY), squareSize);
		userPAE = ProbUtils.PerpostAbsErrForUserUnderDefense(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX + maxX, minY + maxY), squareSize);
		if (!writeFileDisabled) {
			userMSEbw.write(userMSE + "\t" + userMAE + "\t" + userPAE + "\n");
		}
		
		Double H = ProbUtils.entropy(probX);
		Double attMSE = ProbUtils.MeanSquareErrForAttackerWithDefense(allPoints, probX, tweetSquareCenter, squareSize);
		Double attMAE = ProbUtils.MeanAbsErrForAttackerWithDefense(allPoints, probX, tweetSquareCenter, squareSize);
		Double attMAESquare = ProbUtils.MeanAbsErrForAttacker(allPoints, probX, tweetSquareCenter, squareSize);
		Point Xq = new Point(0., 0.);
		int Xq_index = 0;
		for (int i = 0; i < allPoints.size(); i++) {
			if (allPoints.get(i).equals(Xq)) {
				Xq_index = i;
				System.out.println("First Xq_index = " + Xq_index);
				break;
			}
		}
		
		int pFunc_index = pFuncSerie.getPFuncs().size()-1;
		PFunction pFunc = pFuncSerie.getPFuncs().get(pFunc_index); // use the largest pFunc at beginning
		
		HList.add(H);
		attMSEList.add(attMSE);
		attMAEList.add(attMAE);
		attMAESquareList.add(attMAESquare);
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
			
			int topIndex = topThreIndexes.get(Xq_index);
			int buttonIndex = buttonThreIndexes.get(Xq_index);
			
			//calculate the new distribution for tweet
			if(i == 1){
				probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
				if(!disableStdout){
					System.out.println("Tweet_0 is shown");
				}
				topThreIndexes.set(Xq_index, pFunc_index);
				preXqSet.add(Xq, pFunc_index);
				
				for (int postId = 1; postId < numOfDuplicatedPosts; postId++) {
					double randomHash = hashOracle.getXqSquareHashValue(Xq, tweetSquareCenter, postId, pFunc_index);
					
					if(randomHash < pFunc.getValue(tweetSquareCenter.getDistance(Xq))){
						probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
						if(!disableStdout){
							System.out.println("Tweet_" + postId + " is shown");
						}
						
						topThreIndexes.set(Xq_index, pFunc_index);
						for (int k = pFunc_index; k < Math.min(pFuncSerie.getPFuncs().size(), topIndex); k++) {
							preXqSet.add(Xq, k);
						}
						
					} else{
						probX = ProbUtils.probX_nRx(allPoints, Xq, probX, pFunc);
						if(!disableStdout){
							System.out.println("Tweet_" + postId + " is not shown");
						}
						
						buttonThreIndexes.set(Xq_index, pFunc_index);
						for (int k = buttonIndex + 1; k <= pFunc_index; k++) {
							preXqSet.add(Xq, k);
						}
					}
				}
				
			} else{
				
				if (pFunc_index <= buttonIndex || pFunc_index >= topIndex) {
					System.out.println(buttonIndex + "," + pFunc_index + "," + topIndex);
					System.out.println("PFunctin is outside (button, top), no information gain");
				} else {
					
					
					// double randomHash = hashOracle.getHashValue(Xq.getDistance(tweetSquareCenter), tweetSquareCenter);
					for (int postId = 0; postId < numOfDuplicatedPosts; postId++) {
						double randomHash = hashOracle.getXqSquareHashValue(Xq, tweetSquareCenter, postId, pFunc_index);
		
						if(randomHash < pFunc.getValue(tweetSquareCenter.getDistance(Xq))){
							probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
							if(!disableStdout){
								System.out.println("Tweet is shown");
							}
							
							topThreIndexes.set(Xq_index, pFunc_index);
							for (int k = pFunc_index; k < Math.min(pFuncSerie.getPFuncs().size(), topIndex); k++) {
								preXqSet.add(Xq, k);
							}
						} else{
							probX = ProbUtils.probX_nRx(allPoints, Xq, probX, pFunc);
							if(!disableStdout){
								System.out.println("Tweet is not shown");
							}
							
							buttonThreIndexes.set(Xq_index, pFunc_index);
							for (int k = buttonIndex + 1; k <= pFunc_index; k++) {
								preXqSet.add(Xq, k);
							}
						}
					}
				}
			}
			
			H = ProbUtils.entropy(probX);
			attMSE = ProbUtils.MeanSquareErrForAttackerWithDefense(allPoints, probX, tweetSquareCenter, squareSize);
			attMAE = ProbUtils.MeanAbsErrForAttackerWithDefense(allPoints, probX, tweetSquareCenter, squareSize);
			attMAESquare = ProbUtils.MeanAbsErrForAttacker(allPoints, probX, tweetSquareCenter, squareSize);
			
			HList.add(H);
			attMSEList.add(attMSE);
			attMAEList.add(attMAE);
			attMAESquareList.add(attMAESquare);
			probXList.add(probX);

			if(!disableStdout){
				System.out.println(i + "th round: H = " + H + ", AttackerMAE = " + attMAE + 
						", AttackMAESquare = " + attMAESquare + ", Xq = (" + Xq.getX() + ", " + Xq.getY() + ")");
			}
			if (!writeFileDisabled) {
				entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
				attackerMSEbw.write(attMSE + "\t" + attMAE + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
	
				for(int k = 0; k < probX.size(); k++){
					probXbw.write(probX.get(k) + "\t");
				}
				probXbw.newLine();
			}
			
			////////
//			logBw.write("----------------Round " + i + "th----------------\n");
//			logBw.write("ProbX = " + probX + "\n");
//			logBw.write("Sum of ProbX = " + ProbUtils.SumOfProbList(probX) + "\n");
//			logBw.write("ProbRx = " + ProbUtils.probRx(allPoints, probX, Xq, pFunc) + "\n");
//			logBw.write("H = " + H + "\n");
			////////

			//search for the optXq for next round;
			if(i >= numOfStep){
				continue;
			}
			
			Point optNextXq = new Point(0., 0.);
			int optNextXq_index = -1;
			Integer optNextPFuncIndex = 0;
			PFunction optNextPFunc = null;
			Double minNextExpH = Double.MAX_VALUE;

			for(int j = 0; j < allPoints.size(); j++){
				
				Point tmpNextXq = allPoints.get(j);
				int tmpButtonIndex = buttonThreIndexes.get(j);
				int tmpTopIndex = topThreIndexes.get(j);
				PFunction tmpButtonPFunc = null;
				PFunction tmpTopPFunc = null;
				
				if (tmpButtonIndex >= 0) {
					tmpButtonPFunc = pFuncSerie.getPFuncs().get(tmpButtonIndex);
				}
				
				if (tmpTopIndex < pFuncSerie.getPFuncs().size()) {
					tmpTopPFunc = pFuncSerie.getPFuncs().get(tmpTopIndex);
				}
				
				
				// also find the best threshold for next round
				for (int k = tmpButtonIndex + 1; k < pFuncSerie.getPFuncs().size() && k < tmpTopIndex; k++) {
					
					if(preXqSet.contains(tmpNextXq, k)){
						continue;
					}
					
					
					PFunction tmpPFunc = new MonoPFunction(
							tmpButtonPFunc, 
							pFuncSerie.getPFuncs().get(k), 
							tmpTopPFunc);
							
					
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
						optNextPFuncIndex = k;
						optNextPFunc = tmpPFunc;
						optNextXq_index = j;
					}
				}
			}
			
			//////
//			logBw.write("nextHList = " + nextHList + "\n");
//			logBw.write("nextXq = " + optNextXq.getX() + "," + optNextXq.getY() + "\n");
//			logBw.write("--------------------------------------------------\n");
//			logBw.flush();
			//////
			Xq = optNextXq;
			Xq_index = optNextXq_index;
			pFunc = optNextPFunc;
			pFunc_index = optNextPFuncIndex;
			if(preXqSet.contains(Xq, optNextPFuncIndex) || optNextPFunc == null || Xq_index < 0){
				System.out.println("All squares have been tried! Finished.");
				
				// fill the rest of HList and MSEList with the last value
				for (int j = i; j < numOfStep; j++) {
					HList.add(H);
					attMSEList.add(attMSE);
					attMAEList.add(attMAE);
					attMAESquareList.add(attMAESquare);
				}
				
				break;
			}
			
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
	
}
