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
import utils.PointPfuncSet;

public class HierDefenseExperiment implements BasicSingleExperiment {

	private double minX = Integer.MAX_VALUE;
	private double maxX = Integer.MAX_VALUE;
	private double minY = Integer.MAX_VALUE;
	private double maxY = Integer.MAX_VALUE;

	private int baseMapRadius = -1;
	private HierarchicalSubdivision hierSub;

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

	public HierDefenseExperiment() {
		threList = Arrays.asList(0.);
		pFuncSerie = new HyperbolaPFunctionSerie();
		HList = new ArrayList<>();
		attMSEList = new ArrayList<>();
		attMAEList = new ArrayList<>();
		attMAESquareList = new ArrayList<>();
		probXList = new ArrayList<List<Double>>();
		disableStdout = false;

	}

	public HierDefenseExperiment createNewOne() {
		HierDefenseExperiment exp = new HierDefenseExperiment();
		if (realDataFile != null) {
			exp.useRealData(realDataFile);
		}
		return exp;
	}

	public void setRange(double minX, double maxX, double minY, double maxY) {
		int minX_int = (int) Math.round(minX);
		int maxX_int = (int) Math.round(maxX);
		int minY_int = (int) Math.round(minY);
		int maxY_int = (int) Math.round(maxY);
		double e = 0.000001;
		if (Math.abs(minX - minX_int) > e || (Math.abs(maxX - maxX_int) > e)
				|| (Math.abs(minY - minY_int) > e)
				|| (Math.abs(maxY - maxY_int) > e)) {
			System.out
					.println("The EntropyProcessExperiment cannot process float coordinates, please input interger");
		}

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		
		if (baseMapRadius > 0) {
			hierSub = new HierarchicalSubdivision(maxX_int, minX_int, maxY_int,
					minY_int, baseMapRadius);
		}
	}

	public void setPfuncThreshold(List<Double> threList) {
		this.threList = threList;
		pFuncSerie.setThreshold(threList);
	}

	public void setNumOfStep(int numOfStep) {
		this.numOfStep = numOfStep;
	}

	public void setTweetPoint(double x_t, double y_t) {
		tweetPos = new Point(x_t, y_t);
		tweetSquareCenter = new Point((double) Math.round(x_t),
				(double) Math.round(y_t));
	}

	public void setSearchStrategy(String strategy) {
		this.optXqStrategy = strategy;
	}

	public void setPFunctionSerie(PFunctionSerie pFuncSerie) {
		this.pFuncSerie = pFuncSerie;
		this.pFuncSerie.setThreshold(threList);
	}

	public Double getUserMSE() {
		return userMSE;
	}

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
	public List<Double> getAttMAESquareList() {
		return attMAESquareList;
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

		if (!outProbXDir.exists()) {
			outProbXDir.mkdirs();
		}

		if (!outEntropyDir.exists()) {
			outEntropyDir.mkdirs();
		}

		File outProbXFile = new File(outProbXDir.getAbsolutePath()
				+ "/probX.txt");
		File outAllPointsFile = new File(outProbXDir.getAbsolutePath()
				+ "/allPoints.txt");
		File outEntropyFile = new File(outEntropyDir.getAbsolutePath()
				+ "/entropy.txt");
		File outAtackerMSEFile = new File(outEntropyDir.getAbsolutePath()
				+ "/attackerMSE.txt");
		File outUserMSEFile = new File(outEntropyDir.getAbsolutePath()
				+ "/userMSE.txt");
		BufferedWriter probXbw = new BufferedWriter(
				new FileWriter(outProbXFile));
		BufferedWriter entropybw = new BufferedWriter(new FileWriter(
				outEntropyFile));
		BufferedWriter allPointsbw = new BufferedWriter(new FileWriter(
				outAllPointsFile));
		BufferedWriter attackerMSEbw = new BufferedWriter(new FileWriter(
				outAtackerMSEFile));
		BufferedWriter userMSEbw = new BufferedWriter(new FileWriter(
				outUserMSEFile));
		BufferedWriter logBw = new BufferedWriter(new FileWriter(
				outEntropyDir.getAbsolutePath() + "/log.txt"));

		double eps = 1; // fixed to 1, easy for matlab to process

		// Set<Point> preXqSet = new HashSet<Point>();
		PointPfuncSet preXqSet = new PointPfuncSet((int) Math.round(minX),
				(int) Math.round(maxX), (int) Math.round(minY),
				(int) Math.round(maxY), pFuncSerie.getPFuncs().size());

		HashOracle hashOracle = new HashOracle();

		allPoints = GeoUtils.generatePointList(minX, maxX, minY, maxY, eps);
		if (!writeFileDisabled) {
			for (int k = 0; k < allPoints.size(); k++) {
				Point curPoint = allPoints.get(k);
				allPointsbw.write(curPoint.getX() + "\t" + curPoint.getY()
						+ "\n");
			}
		}

		List<Double> probX;
		if (realDataFile == null) {
			probX = ProbUtils.getFlatInitialProbX(allPoints);
		} else {
			probX = ProbUtils.getRealInitialProbX(allPoints, realDataFile); //???
		}
		
		// output user MSE to file
		// TODO how to calculate the errors in the case of multiple thresholds???
		// using the first PFunc for now.
		userMSE = ProbUtils.MeanSquareErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(
				minX + maxX, minY + maxY), squareSize);
		userMAE = ProbUtils.MeanAbsErrForUser(probX, allPoints, pFuncSerie.getPFuncs().get(0), new Point(minX
				+ maxX, minY + maxY), squareSize);
		userPAE = ProbUtils.PerpostAbsErrForUserUnderDefense(probX, allPoints, pFuncSerie.getPFuncs().get(0),
				new Point(minX + maxX, minY + maxY), squareSize);
		if (!writeFileDisabled) {
			userMSEbw.write(userMSE + "\t" + userMAE + "\t" + userPAE + "\n");
		}

		
		Double H = ProbUtils.entropy(probX);
		Double attMSE = ProbUtils.MeanSquareErrForAttackerWithDefense(
				allPoints, probX, tweetSquareCenter, squareSize);
		Double attMAE = ProbUtils.MeanAbsErrForAttackerWithDefense(allPoints,
				probX, tweetSquareCenter, squareSize);
		Double attMAESquare = ProbUtils.MeanAbsErrForAttacker(allPoints, probX,
				tweetSquareCenter, squareSize);
		Point Xq = new Point(0., 0.);
		PFunction pFunc = pFuncSerie.getPFuncs().get(0);
		int pFunc_index = 0;
		preXqSet.add(Xq, 0);

		HList.add(H);
		attMSEList.add(attMSE);
		attMAEList.add(attMAE);
		attMAESquareList.add(attMAESquare);
		probXList.add(probX);

		if (!disableStdout) {
			System.out.println("Initialization: H = " + H + ", AttackerMAE = "
					+ attMAE + ", UserPAE = " + userPAE);
		}
		if (!writeFileDisabled) {
			entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
			attackerMSEbw.write(attMSE + "\t" + attMAE + "\t" + Xq.getX()
					+ "\t" + Xq.getY() + "\n");

			for (int k = 0; k < probX.size(); k++) {
				probXbw.write(probX.get(k) + "\t");
			}
			probXbw.newLine();
		}

		for (int i = 1; i <= numOfStep; i++) {

			// calculate the new distribution for tweet
			if (i == 1) {
				probX = ProbUtils.hier_probX_Rx(allPoints, Xq, probX, pFunc, hierSub);
				if (!disableStdout) {
					System.out.println("Tweet is shown");
				}
				
				for (int postId = 1; postId < numOfDuplicatedPosts; postId++) {
					double randomHash = hashOracle.getXqSquareHashValue(Xq, tweetSquareCenter, postId, pFunc_index);
					
					if (randomHash < pFunc.getValue(tweetSquareCenter
							.getDistance(Xq))) {
						probX = ProbUtils.hier_probX_Rx(allPoints, Xq, probX, pFunc, hierSub);
						if (!disableStdout) {
							System.out.println("Tweet_" + postId + " is shown");
						}
					} else {
						probX = ProbUtils.hier_probX_nRx(allPoints, Xq, probX, pFunc, hierSub);
						if (!disableStdout) {
							System.out.println("Tweet_" + postId + " is not shown");
						}
					}
				}
				
			} else {
				// double randomHash =
				// hashOracle.getHashValue(Xq.getDistance(tweetSquareCenter),
				// tweetSquareCenter);
				
				for (int postId = 0; postId < numOfDuplicatedPosts; postId++) {
					double randomHash = hashOracle.getXqSquareHashValue(Xq,
							tweetSquareCenter, postId, pFunc_index);
	
					if (randomHash < pFunc.getValue(tweetSquareCenter
							.getDistance(Xq))) {
						probX = ProbUtils.hier_probX_Rx(allPoints, Xq, probX, pFunc, hierSub);
						if (!disableStdout) {
							System.out.println("Tweet_" + postId + " is shown");
						}
					} else {
						probX = ProbUtils.hier_probX_nRx(allPoints, Xq, probX, pFunc, hierSub);
						if (!disableStdout) {
							System.out.println("Tweet_" + postId + " is not shown");
						}
					}
				}
			}

			H = ProbUtils.entropy(probX);
			attMSE = ProbUtils.MeanSquareErrForAttackerWithDefense(allPoints,
					probX, tweetSquareCenter, squareSize);
			attMAE = ProbUtils.MeanAbsErrForAttackerWithDefense(allPoints,
					probX, tweetSquareCenter, squareSize);
			attMAESquare = ProbUtils.MeanAbsErrForAttacker(allPoints, probX,
					tweetSquareCenter, squareSize);

			HList.add(H);
			attMSEList.add(attMSE);
			attMAEList.add(attMAE);
			attMAESquareList.add(attMAESquare);
			probXList.add(probX);

			if (!disableStdout) {
				System.out.println(i + "th round: H = " + H
						+ ", AttackerMAE = " + attMAE + ", AttackMAESquare = "
						+ attMAESquare + ", Xq = (" + Xq.getX() + ", "
						+ Xq.getY() + ")");
			}
			if (!writeFileDisabled) {
				entropybw.write(H + "\t" + Xq.getX() + "\t" + Xq.getY() + "\n");
				attackerMSEbw.write(attMSE + "\t" + attMAE + "\t" + Xq.getX()
						+ "\t" + Xq.getY() + "\n");

				for (int k = 0; k < probX.size(); k++) {
					probXbw.write(probX.get(k) + "\t");
				}
				probXbw.newLine();
			}

			// //////
			// logBw.write("----------------Round " + i +
			// "th----------------\n");
			// logBw.write("ProbX = " + probX + "\n");
			// logBw.write("Sum of ProbX = " + ProbUtils.SumOfProbList(probX) +
			// "\n");
			// logBw.write("ProbRx = " + ProbUtils.probRx(allPoints, probX, Xq,
			// pFunc) + "\n");
			// logBw.write("H = " + H + "\n");
			// //////

			// search for the optXq for next round;
			if (i >= numOfStep) {
				continue;
			}

			Point optNextXq = new Point(0., 0.);
			Integer optNextPFuncIndex = 0;
			Double minNextExpH = Double.MAX_VALUE;

			for (int j = 0; j < allPoints.size(); j++) {

				Point tmpNextXq = allPoints.get(j);

				for (int k = 0; k < pFuncSerie.getPFuncs().size(); k++) {
					
					if (preXqSet.contains(tmpNextXq, k)) {
						continue;
					}
					
					PFunction tmpPFunc = pFuncSerie.getPFuncs().get(k);
					
					Double nextH;
					switch (optXqStrategy) {
					case "exp":
						nextH = ProbUtils.hier_expEntropy(allPoints, probX, tmpNextXq,
								pFunc, hierSub);
						break;
	//				case "min":
	//					nextH = ProbUtils.minEntropy(allPoints, probX, tmpNextXq,
	//							pFunc);
	//					break;
	//				case "max":
	//					nextH = ProbUtils.maxEntropy(allPoints, probX, tmpNextXq,
	//							pFunc);
	//					break;
					default:
						nextH = ProbUtils.hier_expEntropy(allPoints, probX, tmpNextXq,
								pFunc, hierSub);
						break;
					}
	
					if (nextH != Double.NaN && nextH <= minNextExpH) {
						minNextExpH = nextH;
						optNextXq = tmpNextXq;
						optNextPFuncIndex = k;
					}
				}
			}

			// ////
			// logBw.write("nextHList = " + nextHList + "\n");
			// logBw.write("nextXq = " + optNextXq.getX() + "," +
			// optNextXq.getY() + "\n");
			// logBw.write("--------------------------------------------------\n");
			// logBw.flush();
			// ////
			Xq = optNextXq;
			pFunc = pFuncSerie.getPFuncs().get(optNextPFuncIndex);
			pFunc_index = optNextPFuncIndex;
			if (preXqSet.contains(Xq, optNextPFuncIndex)) {
				System.out.println("All squares have been tried! Finished.");
				break;
			}

			preXqSet.add(Xq, optNextPFuncIndex);

		}

		// //////
		logBw.close();
		// //////
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
		if (baseMapRadius <= 0) {
			System.out.println("Base map scale have to be larger than 0 !");
			System.exit(0);
		}
		this.baseMapRadius = baseMapRadius;
		
		if (maxX == Integer.MAX_VALUE || minX == Integer.MAX_VALUE
				|| maxY == Integer.MAX_VALUE || minY == Integer.MAX_VALUE) {
			System.out.println("Pleanse initial the maxX, minX, maxY, and minY first !");
			return;
		}
		
		hierSub = new HierarchicalSubdivision((int) Math.round(maxX),
				(int) Math.round(minX), (int) Math.round(maxY),
				(int) Math.round(minY), baseMapRadius);
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
