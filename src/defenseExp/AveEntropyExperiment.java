package defenseExp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.Point;

public class AveEntropyExperiment implements Experiment {

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private List<Double> threList;
	private int baseMapRadius;
	private int numOfStep = -1;
	private Point tweetPos;
	private String optXqStrategy;
	private int numOfRepeats;
	private File outProbXDir;
	private File outEntropyDir;
	private boolean outProbXIsDsiabled = false;
	private PFunctionSerie pFuncSerie;
	private boolean disableStdout;
	private BasicSingleExperiment basicExp;
	private double squareSize = 1.0;
	private int numOfDuplicatedPosts = 1;
	private boolean enableRandPost = false;

	private Double userMSE;
	private Double userMAE;
	private Double userPAE;
	private List<Double> aveAttMSEList;
	private List<Double> aveAttMAEList;
	private List<Double> aveAttMAESquareList;
	private List<Double> aveHList; 
	private List<List<Double>> aveProbXList;
	private List<Point> allPoints;
	
	public AveEntropyExperiment(){
		threList = Arrays.asList(0.);
		outProbXDir = new File("./data/probX");
		outEntropyDir = new File("./data/entropy");
		pFuncSerie = new HyperbolaPFunctionSerie();	//default
		aveHList = new ArrayList<>();
		aveAttMSEList = new ArrayList<>();
		aveAttMAEList = new ArrayList<>();
		aveAttMAESquareList = new ArrayList<>();
		disableStdout = false;
	}
	
	public void setRange(double minX, double maxX, double minY, double maxY){
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void setBaseMapRadius(int baseMapRadius) {
		this.baseMapRadius = baseMapRadius;
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
	}
	
	public void setSearchStrategy(String strategy){
		this.optXqStrategy = strategy;
	}
	
	public void setNumOfRepeats(int numOfRepeats){
		this.numOfRepeats = numOfRepeats;
	}

	public void setOutProbXDir(String dir){
		outProbXDir = new File(dir);
	}
	
	public void setOutEntropyDir(String dir){
		outEntropyDir = new File(dir);
	}
	
	public void setPFunctionSerie(PFunctionSerie pFuncSerie){
		this.pFuncSerie = pFuncSerie;
		this.pFuncSerie.setThreshold(threList);
	}
	
	public void setBasicSingleExperiment(BasicSingleExperiment basicExp){
		this.basicExp = basicExp;
	}
	
	public void setSquareSize(double squareSize) {
		this.squareSize = squareSize;
	}
	
	public Double getUserMSE(){
		return userMSE;
	}
	
	public Double getUserMAE(){
		return userMAE;
	}
	
	public Double getUserPAE(){
		return userPAE;
	}
	
	public List<Double> getAveHList(){
		return aveHList;
	}
	
	public List<Double> getAveAttMSEList(){
		return aveAttMSEList;
	}
	
	public List<Double> getAveAttMSAList(){
		return aveAttMAEList;
	}
	
	public List<Double> getAveAttMSASquareList(){
		return aveAttMAESquareList;
	}
	
	public List<List<Double>> getAveProbX(){
		return aveProbXList;
	}
	
	public List<Point> getAllPoints(){
		return allPoints;
	}
	
	public void disableOutProbX(){
		outProbXIsDsiabled = true;
	}
	
	public void disableStdout(){
		disableStdout = true;
	}
	
	public void enableStdout(){
		disableStdout = false;
	}
	
	public PFunctionSerie getPFunctionSerie(){
		return pFuncSerie;
	}
		
	@Override
	public void run() throws IOException {

		aveAttMSEList.clear();
		aveAttMAEList.clear();
		aveAttMAESquareList.clear();
		aveHList.clear(); 
		
		if(!outProbXDir.exists()){
			outProbXDir.mkdirs();
		}
		
		if(!outEntropyDir.exists()){
			outEntropyDir.mkdirs();
		}
		
		File outProbXFile = new File(outProbXDir.getAbsolutePath() + "/probXAve_" + optXqStrategy + ".txt");
		File outAllPointsFile = new File(outProbXDir.getAbsolutePath() + "/allPoints.txt");
		File outEntropyFile = new File(outEntropyDir.getAbsolutePath() + "/entropyAve_" + optXqStrategy + ".txt");
		File outAtackerMSEFile = new File(outEntropyDir.getAbsolutePath() + "/attackerAveMSE_" + optXqStrategy + ".txt");
		File outUserMSEFile = new File(outEntropyDir.getAbsolutePath() + "/userAveMSE.txt");
		File outLogFile = new File(outEntropyDir.getAbsolutePath() + "/log.txt");
		BufferedWriter userMSEbw = new BufferedWriter(new FileWriter(outUserMSEFile));
		
		BasicSingleExperiment exp = basicExp.createNewOne();
		exp.setNumOfStep(numOfStep);
		exp.setPFunctionSerie(pFuncSerie);
		exp.setPfuncThreshold(threList);
		exp.setRange(minX, maxX, minY, maxY);
		exp.setBaseMapRadius(baseMapRadius);
		exp.setSearchStrategy(optXqStrategy);
		exp.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp.setSquareSize(squareSize);
		exp.disableWriteFile();
		exp.setNumOfDuplicatedPosts(numOfDuplicatedPosts);
		if(disableStdout){
			exp.disableStdout();
		} else{
			exp.enableStdout();
		}
		
		for(int m = 0; m < numOfRepeats; m++){
			
			if (enableRandPost) {
				double randX = Math.round((Math.random() - 0.5) * maxX);
				double randY = Math.round((Math.random() - 0.5) * maxY);
				
				while (randX * randX + randY * randY >= (threList.get(0)-0.1) * (threList.get(0)-0.1)) {
					randX = Math.round((Math.random() - 0.5) * maxX);
					randY = Math.round((Math.random() - 0.5) * maxY);
				}
				
				exp.setTweetPoint(randX, randY);
				System.out.println("Random Post enabled! Post at (" + randX + "," + randY + ")");
			}
			
			
			
			if(!disableStdout){
				System.out.println(">>>>>>" + (m+1) + "th repeat<<<<<<<<");
			}

			exp.run();
			
			if(m == 0){
				//output user MSE and MAE to file
				userMAE = exp.getUserMAE();
				userMSE = exp.getUserMSE();
				userPAE = exp.getUserPAE();
				userMSEbw.write(userMSE + "\t" + userMAE + "\t" + userPAE + "\n");
				userMSEbw.close();

				aveHList = new ArrayList<>(exp.getHList());
				aveAttMSEList = new ArrayList<>(exp.getAttMSEList());
				aveAttMAEList = new ArrayList<>(exp.getAttMAEList());
				aveAttMAESquareList = new ArrayList<>(exp.getAttMAESquareList());
				if(!outProbXIsDsiabled){
					aveProbXList = new ArrayList<>(exp.getProbXList());
					for(int i = 0; i < aveProbXList.size(); i++){
						aveProbXList.set(i, new ArrayList<>(exp.getProbXList().get(i)));
					}
					allPoints = new ArrayList<>(exp.getAllPoints());
				}
			} else{				
				for(int i = 0; i < aveHList.size(); i++){
					aveHList.set(i, (exp.getHList().get(i) + aveHList.get(i) * m) / (m + 1));
					aveAttMSEList.set(i, (exp.getAttMSEList().get(i) + aveAttMSEList.get(i) * m) / (m + 1));
					aveAttMAEList.set(i, (exp.getAttMAEList().get(i) + aveAttMAEList.get(i) * m) / (m + 1));
					aveAttMAESquareList.set(i, (exp.getAttMAESquareList().get(i) + aveAttMAESquareList.get(i) * m) / (m + 1));
					if(!outProbXIsDsiabled){
						for(int k = 0; k < aveProbXList.get(i).size(); k++){
							aveProbXList.get(i).set(k, (exp.getProbXList().get(i).get(k) + aveProbXList.get(i).get(k) * m) / (m + 1));
						}
					}
				}
			}
			
			// output the average result after each repeat
			BufferedWriter logbw = new BufferedWriter(new FileWriter(outLogFile));
			BufferedWriter probXbw = new BufferedWriter(new FileWriter(outProbXFile));
			BufferedWriter entropybw = new BufferedWriter(new FileWriter(outEntropyFile));
			BufferedWriter allPointsbw = new BufferedWriter(new FileWriter(outAllPointsFile));
			BufferedWriter attackerMSEbw = new BufferedWriter(new FileWriter(outAtackerMSEFile));
			
			logbw.write("numOfRepeats\t" + (m+1));
			int realNumOfStep = aveHList.size();
			for(int i = 0 ; i < realNumOfStep; i++){
				entropybw.write(aveHList.get(i) + "\n");
				attackerMSEbw.write(aveAttMSEList.get(i) + "\t" + aveAttMAESquareList.get(i) + "\t" + aveAttMAEList.get(i) + "\n");
				if(!outProbXIsDsiabled){
					for(int k = 0; k < aveProbXList.get(i).size(); k++){
						probXbw.write(aveProbXList.get(i).get(k) + "\t");
					}
					probXbw.newLine();				
				}
			}
			
			if (!outProbXIsDsiabled) {
				for (int k = 0; k < allPoints.size(); k ++) {
					allPointsbw.write(allPoints.get(k) + "\t");
				}
			}
			
			probXbw.close();
			entropybw.close();
			allPointsbw.close();
			attackerMSEbw.close();
			logbw.close();
			
			System.gc();
		}
	}
	
	public void setNumOfDuplicatedPosts(int numOfPosts) {
		numOfDuplicatedPosts = numOfPosts;
	}
	
	public void enableRandPost() {
		enableRandPost = true;
	}
	
	
}
