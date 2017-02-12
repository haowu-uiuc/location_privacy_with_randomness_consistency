package defenseExp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.Point;

public class ScaleExperiment implements Experiment {

	private int minScale; // size of the world map.
	private int maxScale;
	private int baseMapRadius = -1;
	private int scaleStep;
	private double squareSize = 1.0;
	private int numOfRepeats;
	private String optXqStrategy;
//	private double threRatio;
	private List<Double> threList;
	private double tweetPosRatioX;
	private double tweetPosRatioY;
	private Point tweetPos;
	private PFunctionSerie pFuncSerie;
	private BasicSingleExperiment basicExp;
	private int numOfStepMax = -1;
	private String expName = "default-exp";
	private boolean enableOutProbX = false;
	private int numOfDuplicatedPosts = 1;
	private String parentDirName = null;
	
	public void setExpName(String expName) {
		this.expName = expName;
	}
	
	public void setParentDir(String dirName) {
		parentDirName = dirName;
	}

	public void setMinScale(int minScale) {
		this.minScale = minScale;
	}

	public void setMaxScale(int maxScale) {
		this.maxScale = maxScale;
	}

	public void setBaseMapRadius(int baseMapRadius) {
		this.baseMapRadius = baseMapRadius;
	}

	public void setScaleStep(int scaleStep) {
		this.scaleStep = scaleStep;
	}

	public void setNumOfRepeats(int numOfRepeats) {
		this.numOfRepeats = numOfRepeats;
	}

	public void setOptXqStrategy(String optXqStrategy) {
		this.optXqStrategy = optXqStrategy;
	}

//	public void setThreRatio(double threRatio) {
//		this.threRatio = threRatio;
//	}

	public void setThre(List<Double> threList) {
		this.threList = threList;
	}

	public void setTweetPosRatio(double tweetPosRatioX, double tweetPosRatioY) {
		this.tweetPosRatioX = tweetPosRatioX;
		this.tweetPosRatioY = tweetPosRatioY;
	}

	public void setTweetPos(double tweetPosX, double tweetPosY) {
		tweetPos = new Point(tweetPosX, tweetPosY);
	}

	public void setPFunctionSerie(PFunctionSerie pFuncSerie) {
		this.pFuncSerie = pFuncSerie;
	}

	public void setBasicSingleExperiment(BasicSingleExperiment basicExp) {
		this.basicExp = basicExp;
	}

	public void setNumOfStepMax(int numOfStepMax) {
		this.numOfStepMax = numOfStepMax;
	}

	public void setSquareSize(double squareSize) {
		this.squareSize = squareSize;
	}
	
	public void enableOutProbX() {
		enableOutProbX = true;
	}

	@Override
	public void run() throws IOException {

		double k = 1. / squareSize;

		for (int scale = minScale; scale <= maxScale; scale += scaleStep) {
			AveEntropyExperiment subExp = new AveEntropyExperiment();
			subExp.setBasicSingleExperiment(basicExp.createNewOne());
			
			String outputDirPath = "./data/entropy-scale-square/" + expName
					+ "/" + scale + "/"
					+ ((double) ((int) (squareSize * 100)) / 100.0);
			
			if (parentDirName != null) {
				outputDirPath = "./data/entropy-scale-square/" + parentDirName + "/" + expName
						+ "/" + scale + "/"
						+ ((double) ((int) (squareSize * 100)) / 100.0);
			}
			
			if (baseMapRadius > 0) {
				outputDirPath += "/" + baseMapRadius;
			}
			
			File outputDir = new File(outputDirPath);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}

			int scaleK = (int) (scale * k + 0.5);
			int minX = -scaleK;
			int maxX = scaleK;
			int minY = -scaleK;
			int maxY = scaleK;
			int relativeBaseMapRadius = (int) (baseMapRadius * k + 0.5);
			// if the square size is larger than 1, the extreme case that the
			// relativeBaseMapRadius = 0
			// however, we cannot have this case, so we use
			// relativeBaseMapRadius = 1 for it.
			if (relativeBaseMapRadius == 0) {
				relativeBaseMapRadius = 1;
			}

			//double curThre = maxX * threRatio;
			double T_x;
			double T_y;
			int numOfStep = numOfStepMax;
			if (numOfStepMax <= 0) {
				numOfStep = (maxX - minX + 1) * (maxY - minY + 1) * threList.size();
			}

			if (tweetPos == null) {
				T_x = (int) (tweetPosRatioX * maxX);
				T_y = (int) (tweetPosRatioY * maxY);
			} else {
				T_x = tweetPos.getX() * k;
				T_y = tweetPos.getY() * k;
			}

//			if (threList <= 0) {
//				curThre = maxX * threRatio;
//			} else {
//				curThre = threList * k;
//			}
			
			List<Double> curThreList = new ArrayList<>();
			for (Double thre : threList) {
				curThreList.add(thre * k);
			}

			subExp.setRange(minX, maxX, minY, maxY);
			subExp.setBaseMapRadius(relativeBaseMapRadius);
			subExp.setPfuncThreshold(curThreList);
			subExp.setNumOfStep(numOfStep);
			subExp.setTweetPoint(T_x, T_y);
			subExp.setSearchStrategy(optXqStrategy);
			subExp.setNumOfRepeats(numOfRepeats);
			subExp.setOutEntropyDir(outputDirPath);
			if (!enableOutProbX) {
				subExp.disableOutProbX();
			}
			subExp.setPFunctionSerie(pFuncSerie);
			subExp.setSquareSize(squareSize);
			subExp.setNumOfDuplicatedPosts(numOfDuplicatedPosts);

			subExp.run();

			File configFile = new File(outputDirPath + "/config.txt");
			BufferedWriter configBw = new BufferedWriter(new FileWriter(
					configFile));
			configBw.write("maxX\tminX\tmaxY\tminY\tT_x\tT_y\tThre\tRepests\tSquareSize\tScale\n");
			configBw.write(maxX + "\t" + minX + "\t" + maxY + "\t" + minY
					+ "\t" + T_x + "\t" + T_y + "\t" + threList + "\t"
					+ numOfRepeats + "\t" + squareSize + "\t" + (2 * scale + 1));

			configBw.close();

			System.out
					.println("maxX = " + maxX + ", maxY = " + maxY
							+ ", scale = " + scale + ", sq_size = "
							+ squareSize + ", thre = " + curThreList + ", T = ("
							+ T_x + "," + T_y + ")");
		}

	}
	
	public void setNumOfDuplicatedPosts(int numOfPosts) {
		numOfDuplicatedPosts = numOfPosts;
	}

}
