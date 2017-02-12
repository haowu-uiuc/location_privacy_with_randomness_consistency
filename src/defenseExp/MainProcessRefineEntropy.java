package defenseExp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.GeoUtils;
import utils.Point;

@Deprecated
public class MainProcessRefineEntropy {

	public static void main(String[] args) throws IOException {
		
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
		BufferedWriter probXbw = new BufferedWriter(new FileWriter(outProbXFile));
		BufferedWriter entropybw = new BufferedWriter(new FileWriter(outEntropyFile));
		BufferedWriter allPointsbw = new BufferedWriter(new FileWriter(outAllPointsFile));
		
		
		double minX = -10.;
		double maxX = 10.;
		double minY = -10.;
		double maxY = 10.;
		double eps = 1;			//fixed to 1, easy for matlab to process
		double thre = 0.25 * maxX;
		int numOfStep = 20;
		Point tweetPos = new Point(0.4 * maxX, 0.0 * maxY);
		PFunction pFunc = new HyperbolaPFunction();
		pFunc.setThreshold(thre);
		
		Set<Point> preXqSet = new HashSet<Point>();
		
		List<Point> allPoints = GeoUtils.generatePointList(minX, maxX, minY, maxY, eps);
		for(int k = 0; k < allPoints.size(); k++){
			Point curPoint = allPoints.get(k);
			allPointsbw.write(curPoint.getX() + "\t" + curPoint.getY() + "\n");
		}
		
		List<Double> probX = ProbUtils.getFlatInitialProbX(allPoints);
		Double H = ProbUtils.entropy(probX);
		Point Xq = new Point(0., 0.);
		preXqSet.add(Xq);
		
		System.out.println("Initialization: H = " + H);
		entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
		for(int k = 0; k < probX.size(); k++){
			probXbw.write(probX.get(k) + "\t");
		}
		probXbw.newLine();
		
		for(int i = 1; i <= numOfStep; i++){
			
			//calculate the new distribution for tweet
			if(i == 1){
				probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
				System.out.println("Tweet is shown");
			} else{
				double randomHash = Math.random();
				if(randomHash < pFunc.getValue(tweetPos.getDistance(Xq))){
					probX = ProbUtils.probX_Rx(allPoints, Xq, probX, pFunc);
					System.out.println("Tweet is shown");
				} else{
					probX = ProbUtils.probX_nRx(allPoints, Xq, probX, pFunc);
					System.out.println("Tweet is not shown");
				}
			}
			
			H = ProbUtils.entropy(probX);
			System.out.println(i + "th round: H = " + H + ", Xq = (" + Xq.getX() + ", " + Xq.getY() + ")");
			entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
			for(int k = 0; k < probX.size(); k++){
				probXbw.write(probX.get(k) + "\t");
			}
			probXbw.newLine();
			
			//search for the optXq for next round;
			if(i >= numOfStep){
				continue;
			}
			
			Point optNextXq = new Point(0., 0.);
			Double minNextExpH = Double.MAX_VALUE;
			for(int j = 0; j < allPoints.size(); j++){
				
				Point tmpNextXq = allPoints.get(j);
				if(preXqSet.contains(tmpNextXq)){
					continue;
				}
				
				Double nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, pFunc);
//				Double nextH = ProbUtils.minEntropy(allPoints, probX, tmpNextXq, pFunc);
//				Double nextH = ProbUtils.maxEntropy(allPoints, probX, tmpNextXq, pFunc);
				
				if(nextH < minNextExpH){
					minNextExpH = nextH;
					optNextXq = tmpNextXq;
				}
			}
			
			Xq = optNextXq;
			preXqSet.add(Xq);

		}
		
		
		probXbw.close();
		entropybw.close();
		allPointsbw.close();
		
	}

}
