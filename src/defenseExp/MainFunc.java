package defenseExp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import utils.Point;

public class MainFunc {

	public static void main(String[] args) throws IOException {

		double minX = -20.;
		double maxX = 20.;
		double minY = -20.;
		double maxY = 20.;
//		int numOfStep = ((int)maxX - (int)minX + 1) * ((int)maxY - (int)minY + 1);
		int numOfStep = 1000;
		List<Double> threList = Arrays.asList(7.);
		Point tweetPos = new Point(5., 3.);
		
//		BasicSingleExperiment exp1 = new EntropyProcessExperiment();
		BasicSingleExperiment exp1 = new NaiveDefenseExperiment();
		AveEntropyExperiment exp2 = new AveEntropyExperiment();
		ScaleExperiment exp3 = new ScaleExperiment();
		PFunctionExperiment exp4 = new PFunctionExperiment();
		
		HyperbolaPFunctionSerie hPFuncSerie = new HyperbolaPFunctionSerie();
		StepPFunctionSerie sPFuncSerie = new StepPFunctionSerie();
		NoisePFunctionSerie nPFuncSerie = new NoisePFunctionSerie();
//		DounutPFunction dPFunc = new DounutPFunction();
		
		hPFuncSerie.setA(4.);
		hPFuncSerie.setB(1.05);
		hPFuncSerie.setC(0.);
		nPFuncSerie.setNoiseWidth(2.);
//		dPFunc.setWidth(2.);
		exp1.setRange(minX, maxX, minY, maxY);

		exp1.setPFunctionSerie(hPFuncSerie);
//		exp1.setPFunction(sPFunc);
//		exp1.setPFunction(nPFunc);
//		exp1.setPFunction(dPFunc);

		exp1.setPfuncThreshold(threList);
		exp1.setNumOfStep(numOfStep);
		exp1.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp1.setSearchStrategy("exp");
//		exp1.run();
		
		
//		
//		int a = 1;
//		double b = 1.1;
//		double c = 0;
//		double t = 2.5;
//		int scale = 10;
//		for(int i = 0; i < 100; i++){
//			hPFunc.setA(a);
//			hPFunc.setB(b);
//			hPFunc.setC(c);
//			exp1.setRange(-scale, scale, -scale, scale);
//			exp1.setNumOfStep(1);
//			exp1.disableStdout();
//			exp1.setPfuncThreshold(t);
//			exp1.run();
//			System.out.println("scale = " + scale + ", UserMSE = " + exp1.getUserMAE());
//			scale += 1;
//		}
		
		
		minX = -20.;
		maxX = 20.;
		minY = -20.;
		maxY = 20.;
		threList = Arrays.asList(7.);
		numOfStep = ((int)maxX - (int)minX + 1) * ((int)maxY - (int)minY + 1);
		numOfStep = 40;
		if(args.length == 1){
			numOfStep = Integer.valueOf(args[0]);
		}

		tweetPos = new Point(5., 3.);
		hPFuncSerie.setA(4.);
		hPFuncSerie.setB(1.05);
		hPFuncSerie.setC(0.);
		nPFuncSerie.setNoiseWidth(2.);
//		dPFuncSerie.setWidth(2.);
		
//		exp2.setBasicSingleExperiment(new EntropyProcessExperiment());
		exp2.setBasicSingleExperiment(new NaiveDefenseExperiment());
		
		exp2.setPFunctionSerie(hPFuncSerie);
//		exp2.setPFunction(sPFunc);
//		exp2.setPFunction(nPFunc);
//		exp2.setPFunction(dPFunc);
		
		exp2.setRange(minX, maxX, minY, maxY);
		exp2.setPfuncThreshold(threList);
		exp2.setNumOfStep(numOfStep);
		exp2.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp2.setSearchStrategy("exp");
		exp2.setNumOfRepeats(100);
//		exp2.disableStdout();
//		exp2.run();
		
		
		//scale experiment
		
		hPFuncSerie.setA(4.);
		hPFuncSerie.setB(1.05);
		hPFuncSerie.setC(0.);
//		exp3.setBasicSingleExperiment(new EntropyProcessExperiment());
		exp3.setBasicSingleExperiment(new NaiveDefenseExperiment());
		exp3.setPFunctionSerie(hPFuncSerie);
		exp3.setMaxScale(20);
		exp3.setMinScale(2);
		exp3.setNumOfRepeats(1);
		exp3.setNumOfStepMax(1);
		exp3.setOptXqStrategy("exp");
		exp3.setScaleStep(1);
//		exp3.setThreRatio(0.25);
		exp3.setThre(Arrays.asList(1.5));
//		exp3.setTweetPosRatio(0.4, 0.0);
		exp3.setTweetPos(2., 2.);
		if(args.length == 4){
			exp3.setMinScale(Integer.valueOf(args[0]));
			exp3.setMaxScale(Integer.valueOf(args[1]));
			exp3.setScaleStep(Integer.valueOf(args[2]));
			exp3.setNumOfRepeats(Integer.valueOf(args[3]));
		} else if(args.length == 5){
			exp3.setMinScale(Integer.valueOf(args[0]));
			exp3.setMaxScale(Integer.valueOf(args[1]));
			exp3.setScaleStep(Integer.valueOf(args[2]));
			exp3.setNumOfRepeats(Integer.valueOf(args[3]));
			exp3.setNumOfStepMax(Integer.valueOf(args[4]));
		}		
//		exp3.run();
		
		
		minX = -10.;
		maxX = 10.;
		minY = -10.;
		maxY = 10.;
		threList = Arrays.asList(0.4 * maxX);
		numOfStep = 20;
		tweetPos = new Point(0.25 * maxX, 0.25 * maxY);
		
		exp4.setBasicSingleExperiment(new DefenseExperiment());
		exp4.setNumOfStep(20);
		exp4.setPfuncThreshold(threList);
		exp4.setRange(minX, maxX, minY, maxY);
		exp4.setSearchStrategy("exp");
		exp4.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp4.setNumOfRepeats(100);
		if(args.length == 1){
			exp4.setNumOfRepeats(Integer.valueOf(args[0]));
		}
//		exp4.run();

	}

}
