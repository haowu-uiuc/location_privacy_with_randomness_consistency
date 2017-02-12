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

@Deprecated
public class Experiments {
	/**
	 * experiment to get the detail of one entropy reduction process
	 * @throws IOException 
	 */
	static void GetEntropyProcess( double minX,
									double maxX,
									double minY,
									double maxY,
									double thre,
									int numOfStep,
									Point tweetPos,
									String optXqStrategy,
									PFunction pFunc
									) throws IOException{
		
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
		
		double eps = 1;			//fixed to 1, easy for matlab to process
		
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
				
				Double nextH;
				switch (optXqStrategy){
					case "exp": nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, pFunc); 
								break;
					case "min": nextH = ProbUtils.minEntropy(allPoints, probX, tmpNextXq, pFunc);
								break;
					case "max": nextH = ProbUtils.maxEntropy(allPoints, probX, tmpNextXq, pFunc);
								break;
					default: 	nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, pFunc);
								break;
				}
				
				if(nextH != Double.NaN && nextH <= minNextExpH){
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
	
	/**
	 * experiment to get the average entropy on multiple entropy processes
	 */
	static void GetAveEntropy(  double minX,
								double maxX,
								double minY,
								double maxY,
								double thre,
								int numOfStep,
								Point tweetPos,
								String optXqStrategy,
								int numOfRepeats,
								PFunction pFunc
								) throws IOException{
		
		File outProbXDir = new File("./data/probX");
		File outEntropyDir = new File("./data/entropy");
		
		if(!outProbXDir.exists()){
			outProbXDir.mkdirs();
		}
		
		if(!outEntropyDir.exists()){
			outEntropyDir.mkdirs();
		}
		
		File outProbXFile = new File(outProbXDir.getAbsolutePath() + "/probXAve_" + optXqStrategy + ".txt");
		File outAllPointsFile = new File(outProbXDir.getAbsolutePath() + "/allPoints.txt");
		File outEntropyFile = new File(outEntropyDir.getAbsolutePath() + "/entropyAve_" + optXqStrategy + ".txt");
		BufferedWriter probXbw = new BufferedWriter(new FileWriter(outProbXFile));
		BufferedWriter entropybw = new BufferedWriter(new FileWriter(outEntropyFile));
		BufferedWriter allPointsbw = new BufferedWriter(new FileWriter(outAllPointsFile));
		
		double eps = 1;			//fixed to 1, easy for matlab to process
				
		List<Point> allPoints = GeoUtils.generatePointList(minX, maxX, minY, maxY, eps);
		for(int k = 0; k < allPoints.size(); k++){
			Point curPoint = allPoints.get(k);
			allPointsbw.write(curPoint.getX() + "\t" + curPoint.getY() + "\n");
		}
		
		//create probXList, and set it as all zero
		List<List<Double>> probXList = new ArrayList<>(numOfStep + 1);
		for(int i = 0; i < numOfStep + 1; i++){
			probXList.add(new ArrayList<>(allPoints.size()));
			for(int j = 0; j < allPoints.size(); j++){
				probXList.get(i).add(0.);
			}
		}
		//create HList, and set it as all zero
		List<Double> HList = new ArrayList<>(numOfStep + 1);
		for(int i = 0; i < numOfStep + 1; i++){
			HList.add(0.);
		}
		
		for(int m = 0; m < numOfRepeats; m++){
			
			Set<Point> preXqSet = new HashSet<Point>();
			
			List<Double> probX = ProbUtils.getFlatInitialProbX(allPoints);
			Double H = ProbUtils.entropy(probX);
			Point Xq = new Point(0., 0.);
			preXqSet.add(Xq);
			
			System.out.println((m+1) + "th repeat: " + "Initialization: H = " + H);
			if(m == 0){
				HList.set(0, H);
				for(int k = 0; k < probX.size(); k++){
					probXList.get(0).set(k, probX.get(k));
				}
			}
			
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
				System.out.println((m+1) + "th repeat, " + i + "th round: H = " + H + ", Xq = (" + Xq.getX() + ", " + Xq.getY() + ")");
				HList.set(i, (H + HList.get(i) * m) / (m + 1));
				for(int k = 0; k < probX.size(); k++){
					probXList.get(i).set(k, (probX.get(k) + probXList.get(i).get(k) * m) / (m + 1));
				}
				
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
					
					Double nextH;
					switch (optXqStrategy){
						case "exp": nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, pFunc); 
									break;
						case "min": nextH = ProbUtils.minEntropy(allPoints, probX, tmpNextXq, pFunc);
									break;
						case "max": nextH = ProbUtils.maxEntropy(allPoints, probX, tmpNextXq, pFunc);
									break;
						default: 	nextH = ProbUtils.expEntropy(allPoints, probX, tmpNextXq, pFunc);
									break;
					}
					
					if(nextH != Double.NaN && nextH <= minNextExpH){
						minNextExpH = nextH;
						optNextXq = tmpNextXq;
					}
				}
				
				Xq = optNextXq;
				preXqSet.add(Xq);
	
			}
		
		}
		
		for(int i = 0 ; i < numOfStep + 1; i++){
			entropybw.write(HList.get(i) + "\n");
			for(int k = 0; k < probXList.get(i).size(); k++){
				probXbw.write(probXList.get(i).get(k) + "\t");
			}
			probXbw.newLine();
		}
		
		
		
		probXbw.close();
		entropybw.close();
		allPointsbw.close();
		
		
		
	}
}
