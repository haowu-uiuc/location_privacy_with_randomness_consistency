package attackDemo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import defenseExp.PFunction;
import defenseExp.PFunctionSerie;
import defenseExp.ProbUtils;
import defenseExp.StepPFunctionSerie;
import utils.GeoUtils;
import utils.Point;

public class AttackToNaiveDefense {

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private int numOfStep;
	private PFunctionSerie pFuncSerie;
	private boolean disableStdout;
	private boolean writeFileDisabled = false;
	private File realDataFile = null;
	
	private PostQuerier postQuerier;
	private String postId;
	
	private List<Double> HList; 
	private List<Double> probX;
	private List<Point> allPoints;
	private boolean postHasBeenSeen;
	
	public AttackToNaiveDefense(){
		pFuncSerie = new StepPFunctionSerie();
		HList = new ArrayList<>();
		disableStdout = false;
		postHasBeenSeen = false;
		
		// TODO:
		postQuerier = null;
	}

	/**
	 * set the range of the grid
	 * recommend: maxX = -minX, maxY = -minY
	 * 
	 * @param minX integer 
	 * @param maxX integer
	 * @param minY integer
	 * @param maxY integer
	 */
	public void setRange(int minX, int maxX, int minY, int maxY) {
		this.minX = (double) minX;
		this.minY = (double) minY;
		this.maxX = (double) maxX;
		this.maxY = (double) maxY;
	}

	public void setNumOfStep(int numOfStep) {
		this.numOfStep = numOfStep;	
	}

	public void setPFunctionSerie(PFunctionSerie pFuncSerie) {
		this.pFuncSerie = pFuncSerie;
	}
	
	public List<Double> getHList() {
		return HList;
	}
	
	public List<Double> getProbX() {
		return probX;
	}

	public List<Point> getAllPoints() {
		return allPoints;
	}

	public void disableStdout() {
		disableStdout = true;
	}

	public void enableStdout() {
		disableStdout = false;
	}

	public PFunctionSerie getPFunctionSerie() {
		return pFuncSerie;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public void setPostQuerier(PostQuerier pq) {
		this.postQuerier = pq;
	}

	public void run() throws IOException {
		HList.clear();
		
		File outProbXDir = new File("./attack_demo/probX");
		File outEntropyDir = new File("./attack_demo/entropy");
		
		if(!outProbXDir.exists()){
			outProbXDir.mkdirs();
		}
		
		if(!outEntropyDir.exists()){
			outEntropyDir.mkdirs();
		}
		
		File outProbXFile = new File(outProbXDir.getAbsolutePath() + "/probX.txt");
		File outAllPointsFile = new File(outProbXDir.getAbsolutePath() + "/allPoints.txt");
		File outEntropyFile = new File(outEntropyDir.getAbsolutePath() + "/entropy.txt");
		BufferedWriter probXbw = new BufferedWriter(new FileWriter(outProbXFile));
		BufferedWriter entropybw = new BufferedWriter(new FileWriter(outEntropyFile));
		BufferedWriter allPointsbw = new BufferedWriter(new FileWriter(outAllPointsFile));
		
		double eps = 1;			//fixed to 1, easy for matlab to process
		
		allPoints = GeoUtils.generatePointList(minX, maxX, minY, maxY, eps);
		if (!writeFileDisabled) {
			for(int k = 0; k < allPoints.size(); k++){
				Point curPoint = allPoints.get(k);
				allPointsbw.write(curPoint.getX() + "\t" + curPoint.getY() + "\n");
			}
		}

		if (realDataFile == null) {
			probX = ProbUtils.getFlatInitialProbX(allPoints);
		} else {
			probX = ProbUtils.getRealInitialProbX(allPoints, realDataFile); //???
		}
		
		Double H = ProbUtils.entropy(probX);
		Point Xq = new Point(0., 0.);
		PFunction pFunc = pFuncSerie.getPFuncs().get(0);
		int pFuncId = 0;
		
		HList.add(H);
		
		if(!disableStdout){
			System.out.println("Initialization: H = " + H);
		}
		if (!writeFileDisabled) {
			entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
	
			for(int k = 0; k < probX.size(); k++){
				probXbw.write(probX.get(k) + "\t");
			}
			probXbw.newLine();
		}
		
		for(int i = 1; i <= numOfStep; i++){

			boolean isPostReturned = postQuerier.queryPost(postId, pFuncId, Xq);
			
			if (!postHasBeenSeen && !isPostReturned) {
				// if the post has not been seen, 
				// randomly choose a position for Xq and do the query again
				double x =  Math.round(Math.random() * (maxX - minX)) + minX;
				double y =  Math.round(Math.random() * (maxY - minY)) + minY;
				Xq = new Point(x, y);
				continue;
			} else if (!postHasBeenSeen && isPostReturned) {
				postHasBeenSeen = true;
			}
			
			//calculate the new distribution for post
			if(isPostReturned){
				probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
				if(!disableStdout){
					System.out.println("Post " + postId + " is shown");
				}
			} else{
				probX = ProbUtils.probX_nRx(allPoints, Xq, probX, pFunc);
				if(!disableStdout){
					System.out.println("Post " + postId + " is not shown");
				}
			}
			
			H = ProbUtils.entropy(probX);
			
			HList.add(H);

			if(!disableStdout){
				System.out.println(i + "th round: H = " + H + ", Xq = (" + Xq.getX() + ", " + Xq.getY() + ")");
			}
			if (!writeFileDisabled) {
				entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
				for(int k = 0; k < probX.size(); k++){
					probXbw.write(probX.get(k) + "\t");
				}
				probXbw.newLine();
			}
			
			//search for the optXq for next round;
			if(i >= numOfStep){
				continue;
			}
			
			if (H == 0) {
				break;
			}
			
			Point optNextXq = new Point(0., 0.);
			int optNextPFuncId = 0;
			PFunction optNextPFunc = pFuncSerie.getPFuncs().get(optNextPFuncId);
			Double minNextExpH = Double.MAX_VALUE;
			for(int j = 0; j < allPoints.size(); j++){
				
				Point tmpNextXq = allPoints.get(j);
				
				for (int k = 0; k < pFuncSerie.getPFuncs().size(); k++) {
					PFunction tmpPFunc = pFuncSerie.getPFuncs().get(k);
//				for (PFunction tmpPFunc : pFuncSerie.getPFuncs()) {
					Double nextH;
					String optXqStrategy = "exp"; // hard code the strategy for now
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
						optNextPFuncId = k;
					}
				}
			}

			Xq = optNextXq;
			pFunc = optNextPFunc;
			pFuncId = optNextPFuncId;
			
		}
		
		probXbw.close();
		entropybw.close();
		allPointsbw.close();
	}

	public void disableWriteFile() {
		writeFileDisabled = true;		
	}
	
	public void useRealData(File realDataFile) {
		this.realDataFile = realDataFile;
	}

	public void useFlatData() {
		realDataFile = null;
	}
}
