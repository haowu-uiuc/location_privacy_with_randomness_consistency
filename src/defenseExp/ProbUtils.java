package defenseExp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.DefenseAbsErrCalculator;
import utils.Point;

public class ProbUtils {
	
	public static String lastRealDataFilePath = null;
	public static int[][] lastRealPopGird = null;
	
	public static List<Double> getFlatInitialProbX(List<Point> allPoints){
		List<Double> probX = new ArrayList<Double>(allPoints.size());
		Double flatProb = 1. / allPoints.size();
		for(int i = 0; i < allPoints.size(); i++){
			probX.add(flatProb);
		}
		
		return probX;
	}
	
	public static List<Double> getRealInitialProbX(List<Point> allPoints, File realDataFile) throws IOException {
		int[][] realPopGird;
		
		if (lastRealPopGird != null && lastRealDataFilePath.equals(realDataFile.toString())){
			realPopGird = lastRealPopGird;
		} else {
			realPopGird = readPopGridFile(realDataFile);
		}

		// extract the grid data from the center
		int x_c = (realPopGird[0].length - 1) / 2;
		int y_c = (realPopGird.length - 1) / 2;
		
		List<Double> probX = new ArrayList<>();
		double sum = 0.;
		for (int i = 0; i < allPoints.size(); i++) {
			int x = (int)Math.round(allPoints.get(i).getX());
			int y = (int)Math.round(allPoints.get(i).getY());
			
			double pop = (double)realPopGird[y_c - y][x_c + x];
			probX.add(pop);
			sum += pop;
		}
		
		// normalization
		for (int i = 0; i < allPoints.size(); i++) {
			probX.set(i, probX.get(i) / sum);
		}		
				
		return probX;
	}
	
	public static int[][] readPopGridFile(File gridDataFile) throws IOException {
		// read the file to learn min/max x index and min/max y index
		BufferedReader br = new BufferedReader(new FileReader(gridDataFile));
		br.readLine();
		String line;
		int maxX = -1;
		int maxY = -1;
		
		while ((line = br.readLine()) != null) {
			String[] cols = line.split("\t");
			int xIndex = Integer.valueOf(cols[3]);
			int yIndex = Integer.valueOf(cols[4]);
			
			if (maxX < xIndex) {
				maxX = xIndex;
			}
			
			if (maxY < yIndex) {
				maxY = yIndex;
			}
			
		}
		br.close();
		

		int[][] popGrid = new int[maxX + 1][maxY + 1];
		// read the data from file
		br = new BufferedReader(new FileReader(gridDataFile));
		// skip the title line
		br.readLine();
		
		while ((line = br.readLine()) != null) {
			String[] cols = line.split("\t");
			int pop = Integer.valueOf(cols[2]);
			int xIndex = Integer.valueOf(cols[3]);
			int yIndex = Integer.valueOf(cols[4]);
			
			popGrid[yIndex][xIndex] = pop;
		}
		br.close();
		
		lastRealDataFilePath = gridDataFile.toString();
		lastRealPopGird = popGrid;
		
		return popGrid;
	}
	
	//O(n)
	/**
	 * get P(rx|x_q)
	 * @param allPoints
	 * @param probX from last round
	 * @param Xq location of query location
	 * @return
	 */
	public static Double probRx(List<Point> allPoints, List<Double> probX, Point Xq, PFunction pFunc){
		Double P = 0.;
		for(int i = 0; i < allPoints.size(); i++){
			Double dist = allPoints.get(i).getDistance(Xq);
			P += pFunc.getValue(dist) * probX.get(i);
		}
		
		return P;
	}
	
	//O(n)
	public static List<Double> probX_Rx(List<Point> allPoints, Point Xq, 
								 List<Double> probX, PFunction pFunc){
		Double probRx = probRx(allPoints, probX, Xq, pFunc);
		
		List<Double> probList = new ArrayList<>();
		Double totalProb = 0.;
		for(int i = 0; i < allPoints.size(); i++){
			Double prob = pFunc.getValue(Xq.getDistance(allPoints.get(i))) * probX.get(i) 
							/ probRx;
			probList.add(prob);
			totalProb += prob;
		}
//		System.out.println("TotalProb = " + totalProb);
		
		//adjust the error resulted by computation, and ensure that the totalProb after adjust is always 1
		for(int i = 0; i < allPoints.size(); i++){
			probList.set(i, probList.get(i) / totalProb);
		}
		
		return probList;
		
	}
	
	public static List<Double> probX_nRx(List<Point> allPoints, Point Xq, 
			 				List<Double> probX, PFunction pFunc){
		Double probRx = probRx(allPoints, probX, Xq, pFunc);

		List<Double> probList = new ArrayList<>();
		Double totalProb = 0.;
		for(int i = 0; i < allPoints.size(); i++){
			Double prob = (1 - pFunc.getValue(Xq.getDistance(allPoints.get(i)))) * probX.get(i) 
							/ (1 - probRx);
			probList.add(prob);
			totalProb += prob;
		}
//		System.out.println("TotalProb = " + totalProb);
		
		//adjust the error resulted by computation, and ensure that the totalProb after adjust is always 1
		for(int i = 0; i < allPoints.size(); i++){
			probList.set(i, probList.get(i) / totalProb);
		}
		
		return probList;
		
	}
	
	
	
	//O(n)
	/**
	 * in hierarchical subdivision
	 * get P(rx|x_q)
	 * @param allPoints
	 * @param probX from last round
	 * @param Xq location of query location
	 * @return
	 */
	public static Double hier_probRx(List<Point> allPoints, List<Double> probX, Point Xq, PFunction pFunc
			, HierarchicalSubdivision hierSub){
		Double P = 0.;
		for(int i = 0; i < allPoints.size(); i++){
			Double dist = hierSub.getCenterDist(Xq, allPoints.get(i));
			P += pFunc.getValue(dist) * probX.get(i);
		}
		
		return P;
	}
	
	//O(n)
	public static List<Double> hier_probX_Rx(List<Point> allPoints, Point Xq, 
								 List<Double> probX, PFunction pFunc, HierarchicalSubdivision hierSub){
		Double probRx = hier_probRx(allPoints, probX, Xq, pFunc, hierSub);
		
		List<Double> probList = new ArrayList<>();
		Double totalProb = 0.;
		for(int i = 0; i < allPoints.size(); i++){
			Double dist = hierSub.getCenterDist(Xq, allPoints.get(i));
			Double prob = pFunc.getValue(dist) * probX.get(i) 
							/ probRx;
			probList.add(prob);
			totalProb += prob;
		}
//			System.out.println("TotalProb = " + totalProb);
		
		//adjust the error resulted by computation, and ensure that the totalProb after adjust is always 1
		for(int i = 0; i < allPoints.size(); i++){
			probList.set(i, probList.get(i) / totalProb);
		}
		
		return probList;
		
	}
	
	public static List<Double> hier_probX_nRx(List<Point> allPoints, Point Xq, 
			 				List<Double> probX, PFunction pFunc, HierarchicalSubdivision hierSub){
		Double probRx = hier_probRx(allPoints, probX, Xq, pFunc, hierSub);

		List<Double> probList = new ArrayList<>();
		Double totalProb = 0.;
		for(int i = 0; i < allPoints.size(); i++){
			Double dist = hierSub.getCenterDist(Xq, allPoints.get(i));
			Double prob = (1 - pFunc.getValue(dist)) * probX.get(i) 
							/ (1 - probRx);
			probList.add(prob);
			totalProb += prob;
		}
//			System.out.println("TotalProb = " + totalProb);
		
		//adjust the error resulted by computation, and ensure that the totalProb after adjust is always 1
		for(int i = 0; i < allPoints.size(); i++){
			probList.set(i, probList.get(i) / totalProb);
		}
		
		return probList;
		
	}
	
	
	
	public static Double entropy(List<Double> probList){
		Double H = 0.;
		for(int i = 0; i < probList.size(); i++){
			if(probList.get(i) != 0){
				
				Double log = Math.log(1. / probList.get(i));
				Double curH = 0.;
				if(log != Double.POSITIVE_INFINITY && log != Double.NEGATIVE_INFINITY){
					curH = probList.get(i) * log;
				}

				H += curH;
			}
		}
		
		H = H / Math.log(2);
		
		return H;
	}
	
	/**
	 * to get E(H_{n+1}), probX is from nth round
	 * @param allPoints
	 * @param H_next_probX_Rx   entropy of x|Rx for n+1 round
	 * @param H_next_probX_nRx  entropy of x|nRx for n+1 round
	 * @param this_probX		probX from this round (nth round)
	 * @param Xq				the attempting query location
	 * @return
	 */
	public static Double expEntropy(List<Point> allPoints, List<Double> this_probX, Point Xq, PFunction pFunc){
		
		List<Double> next_probX_Rx = ProbUtils.probX_Rx(allPoints, Xq, this_probX, pFunc);
		List<Double> next_probX_nRx = ProbUtils.probX_nRx(allPoints, Xq, this_probX, pFunc);
		Double H_next_probX_Rx = ProbUtils.entropy(next_probX_Rx);
		Double H_next_probX_nRx = ProbUtils.entropy(next_probX_nRx);
		Double next_probRx = probRx(allPoints, this_probX, Xq, pFunc);
		
		next_probX_Rx.clear();
		next_probX_nRx.clear();
		
		return H_next_probX_Rx * next_probRx + H_next_probX_nRx * (1 - next_probRx);
	}
	
	/**
	 * in hierarchical subdivision
	 * to get E(H_{n+1}), probX is from nth round
	 * @param allPoints
	 * @param H_next_probX_Rx   entropy of x|Rx for n+1 round
	 * @param H_next_probX_nRx  entropy of x|nRx for n+1 round
	 * @param this_probX		probX from this round (nth round)
	 * @param Xq				the attempting query location
	 * @return
	 */
	public static Double hier_expEntropy(List<Point> allPoints, List<Double> this_probX, Point Xq, PFunction pFunc
			, HierarchicalSubdivision hierSub){
		
		List<Double> next_probX_Rx = ProbUtils.hier_probX_Rx(allPoints, Xq, this_probX, pFunc, hierSub);
		List<Double> next_probX_nRx = ProbUtils.hier_probX_nRx(allPoints, Xq, this_probX, pFunc, hierSub);
		Double H_next_probX_Rx = ProbUtils.entropy(next_probX_Rx);
		Double H_next_probX_nRx = ProbUtils.entropy(next_probX_nRx);
		Double next_probRx = hier_probRx(allPoints, this_probX, Xq, pFunc, hierSub);
		
		next_probX_Rx.clear();
		next_probX_nRx.clear();
		
		return H_next_probX_Rx * next_probRx + H_next_probX_nRx * (1 - next_probRx);
	}
	
	public static Double minEntropy(List<Point> allPoints, List<Double> this_probX, Point Xq, PFunction pFunc){
		List<Double> nextProbX_Rx = ProbUtils.probX_Rx(allPoints, Xq, this_probX, pFunc);
		List<Double> nextProbX_nRx = ProbUtils.probX_nRx(allPoints, Xq, this_probX, pFunc);
		Double H_Rx = ProbUtils.entropy(nextProbX_Rx);
		Double H_nRx = ProbUtils.entropy(nextProbX_nRx);
		
		if(H_Rx == Double.NaN){
			return H_nRx;
		} else if(H_nRx == Double.NaN){
			return H_Rx;
		}
		
		return Math.min(H_Rx, H_nRx);
	}
	
	public static Double maxEntropy(List<Point> allPoints, List<Double> this_probX, Point Xq, PFunction pFunc){
		List<Double> nextProbX_Rx = ProbUtils.probX_Rx(allPoints, Xq, this_probX, pFunc);
		List<Double> nextProbX_nRx = ProbUtils.probX_nRx(allPoints, Xq, this_probX, pFunc);
		Double H_Rx = ProbUtils.entropy(nextProbX_Rx);
		Double H_nRx = ProbUtils.entropy(nextProbX_nRx);
		
		if(H_Rx == Double.NaN){
			return H_nRx;
		} else if(H_nRx == Double.NaN){
			return H_Rx;
		}
		
		return Math.max(H_Rx, H_nRx);
	}
	
	public static Double MeanSquareErrForUser(List<Double> init_probX, List<Point> allPoints, PFunction pFunc, Point Xq, double squareSize){
		double mse = 0;
		double showTweet = 0;
		double thre = pFunc.getThreshold();
		for (int m = 0; m < allPoints.size(); m++) {
			double point_prob = init_probX.get(m);
			Point point = allPoints.get(m);
			
			double dist = point.getDistance(Xq);
			if(dist > thre){
				mse += pFunc.getValue(dist) * (dist - thre) * (dist - thre) * point_prob;
			} else{
				mse += (1 - pFunc.getValue(dist)) * (dist - thre) * (dist - thre) * point_prob;
			}
			showTweet += pFunc.getValue(dist);
		}
		
		mse /= allPoints.size();
//		mse /= showTweet;
		
		return mse * squareSize * squareSize;
	}
	
	public static Double MeanAbsErrForUser(List<Double> init_probX, List<Point> allPoints, PFunction pFunc, Point Xq, double squareSize){
		double mae = 0;
		double showTweet = 0;
		double thre = pFunc.getThreshold();
		for (int m = 0; m < allPoints.size(); m++) {
			double point_prob = init_probX.get(m);
			Point point = allPoints.get(m);
			
			double dist = point.getDistance(Xq);
			if(dist > thre){
				mae += pFunc.getValue(dist) * Math.abs(dist - thre) * point_prob;
			} else{
				mae += (1 - pFunc.getValue(dist)) * Math.abs(dist - thre) * point_prob;
			}
			showTweet += pFunc.getValue(dist);
		}
		
		mae /= allPoints.size();
//		mae /= showTweet;
		
		return mae * squareSize;
	}
	
	public static Double PerpostAbsErrForUser(List<Double> init_probX, List<Point> allPoints, PFunction pFunc, Point Xq, double squareSize) {
		int k = 10; // number of sample points in the square is (2k+1)^2
		double pae = 0.0;
		double FPE = 0.0;
		double FNE = 0.0;
		double expectedFP = 0.0;
		double TP = 0.0;
		double t = pFunc.getThreshold();
		
		k = ((int)(k * squareSize/2)) * 2 + 1;
		
		for (int m = 0; m < allPoints.size(); m++) {
			double point_prob = init_probX.get(m);
			Point point = allPoints.get(m);
			for (int i = -k; i <= k; i++) {
				for (int j = -k; j <= k; j++) {
					Point samplePoint = new Point(point.getX() + (double)i/(double)(k*2+1), 
							point.getY() + (double)j/(double)(k*2+1));
					double dist = samplePoint.getDistance(Xq);
					if (dist <= t ) {
						TP += 1.0 * point_prob;
						FNE += (t - dist) * (1 - pFunc.getValue(dist)) * point_prob;
					} else {
						expectedFP += pFunc.getValue(dist) * point_prob;
						FPE += (dist - t) * pFunc.getValue(dist) * point_prob;
					}
				}
			}
		}
		
		pae = (FPE + FNE) / (TP + expectedFP) * squareSize;
		
		return pae;
	}
	
	public static Double PerpostAbsErrForUserUnderDefense(List<Double> init_probX, List<Point> allPoints, PFunction pFunc, Point Xq, double squareSize) {
		int k = 10; // number of sample points in the square is (2k+1)^2
		double pae = 0.0;
		double FPE = 0.0;
		double FNE = 0.0;
		double expectedFP = 0.0;
		double TP = 0.0;
		double t = pFunc.getThreshold();
		
		k = (int)(k * squareSize);
		
		for (int m = 0; m < allPoints.size(); m++) {
			double point_prob = init_probX.get(m);
			Point point = allPoints.get(m);
			
			for (int i = -k; i <= k; i++) {
				for (int j = -k; j <= k; j++) {
					Point samplePoint = new Point(point.getX() + (double)i/(double)(k*2+1), 
							point.getY() + (double)j/(double)(k*2+1));
					double dist = samplePoint.getDistance(Xq);
					
					Point samplePointSquareCenter = point;
					double distToSquareCenter = samplePointSquareCenter.getDistance(Xq);
					
					double pFuncValue = pFunc.getValue(distToSquareCenter);
					if (dist <= t ) {
						TP += 1.0 * point_prob;
						FNE += (t - dist) * (1 - pFuncValue) * point_prob;
					} else {
						expectedFP += pFuncValue * point_prob;
						FPE += (dist - t) * pFuncValue * point_prob;
					}
				}
			}
		}
		
		pae = (FPE + FNE) / (TP + expectedFP) * squareSize;
		
		return pae;
	}
	
	public static Double MeanSquareErrForAttacker(List<Point> allPoints, List<Double> probX, Point tweetPos, double squareSize){
		double mse = 0;
		
		for(int i = 0; i < allPoints.size(); i++){
			double dist = allPoints.get(i).getDistance(tweetPos);
			mse += dist * dist * probX.get(i);
		}
		
		return mse * squareSize * squareSize;
	}
	
	public static Double MeanSquareErrForAttackerWithDefense(List<Point> allPoints, List<Double> probX, Point tweetPos, double squareSize){
		double mse = 0;
		DefenseAbsErrCalculator absErrCalulator = DefenseAbsErrCalculator.getInstance();

		for(int i = 0; i < allPoints.size(); i++){
			double distErr = absErrCalulator.getAbsErrBetween(allPoints.get(i), tweetPos);
			mse += distErr * distErr * probX.get(i);
		}
		
		return mse * squareSize * squareSize;
	}
	
	public static Double MeanAbsErrForAttacker(List<Point> allPoints, List<Double> probX, Point tweetPos, double squareSize){
		double mae = 0;
		
		for(int i = 0; i < allPoints.size(); i++){
			double dist = allPoints.get(i).getDistance(tweetPos);
			mae += Math.abs(dist) * probX.get(i);
		}
		
		return mae * squareSize;
	
	}
	
	/**
	 * MAE under defense, namely consider the post is uniformly distributed inside its square
	 * @param allPoints
	 * @param probX
	 * @param tweetPos
	 * @param squareSize
	 * @return
	 */
	public static Double MeanAbsErrForAttackerWithDefense(List<Point> allPoints, List<Double> probX, Point tweetPos, double squareSize){
		double mae = 0;
		DefenseAbsErrCalculator absErrCalulator = DefenseAbsErrCalculator.getInstance();
		
		for(int i = 0; i < allPoints.size(); i++){
			double distErr = absErrCalulator.getAbsErrBetween(allPoints.get(i), tweetPos);
			mae += Math.abs(distErr) * probX.get(i);
		}
		
		return mae * squareSize;
	}	
	
	public static Double QuantizedMeanAbsErrForAttacker(List<Point> allPoints, List<Double> probX, Point tweetPos, double squareSize){
		double center_x = Math.round(tweetPos.getX());
		double center_y = Math.round(tweetPos.getY());
		Point centerTweetPos = new Point(center_x, center_y);
		
		double mae = 0;
		
		for(int i = 0; i < allPoints.size(); i++){
			double dist = allPoints.get(i).getDistance(centerTweetPos);
			mae += Math.abs(dist) * probX.get(i);
		}
		
		return mae * squareSize;
	
	}
	
	public static Double SumOfProbList(List<Double> probList){
		Double sum = 0.;
		for(int i = 0; i < probList.size(); i++){
			sum += probList.get(i);
		}
		
		return sum;
	}
}
