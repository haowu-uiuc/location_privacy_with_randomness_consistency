package defenseExp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.Point;

public class Main_GradShrinkAttackExperiment {

	public static void main(String[] args) throws IOException {

		double minX = -5.;
		double maxX = 5.;
		double minY = -5.;
		double maxY = 5.;
//		int numOfStep = ((int)maxX - (int)minX + 1) * ((int)maxY - (int)minY + 1);
		int numOfStep = 1000;
		List<Double> threList = Arrays.asList(7.);
//		List<Double> threList = Arrays.asList(3., 5., 7., 10., 15.);
		Point tweetPos = new Point(0., 0.);
		List<Double> xOffsetList = Arrays.asList(
				7., 6., 5., 4., 3., 2., 1., 8., 9., 10., 11., 12., 13., 14.,
				0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.);
		List<Double> yOffsetList = Arrays.asList(
				0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 
				7., 6., 5., 4., 3., 2., 1., 8., 9., 10., 11., 12., 13., 14.);
		double shrinkTimes = 11;
		
//		xOffsetList = new ArrayList<>();
//		for (int x = -20; x <= 20; x += 2) {
//			xOffsetList.add((double)x - tweetPos.getX());
//			xOffsetList.add(0.);
//		}
//		yOffsetList = new ArrayList<>();
//		for (int y = -20; y <= 20; y += 2) {
//			yOffsetList.add(0.);
//			yOffsetList.add((double)y - tweetPos.getY());
//		}
		
		HyperbolaPFunctionSerie hPFuncSerie3 = new HyperbolaPFunctionSerie();		
		HyperbolaPFunctionSerie hPFuncSerie4 = new HyperbolaPFunctionSerie();	
		HyperbolaPFunctionSerie hPFuncSerie5 = new HyperbolaPFunctionSerie();		
		StepPFunctionSerie hPFuncSerie1 = new StepPFunctionSerie();
		NoisePFunctionSerie hPFuncSerie2 = new NoisePFunctionSerie();
		GaussianPFunctionSerie hPFuncSerie6 = new GaussianPFunctionSerie();		
		
		// P2
		hPFuncSerie2.setNoiseWidth(2. * shrinkTimes);
		
		// P3
		hPFuncSerie3.setA(4.);
		hPFuncSerie3.setB(1.05);
		hPFuncSerie3.setC(0.);
				
		// P4
		hPFuncSerie4.setA(3.);
		hPFuncSerie4.setB(1.05);
		hPFuncSerie4.setC(0.);
		
		// P5
		hPFuncSerie5.setA(2.);
		hPFuncSerie5.setB(1.05);
		hPFuncSerie5.setC(0.);
		
		// P6
		hPFuncSerie6.setVar(0.251 * shrinkTimes);
		
		PFunctionSerie pfuncSerie = hPFuncSerie6;
		boolean isAverageExp = false;
		
		if (args.length == 1) {
			numOfStep = Integer.valueOf(args[0]);
			System.out.println("Num of step = " + numOfStep);
		} else if (args.length == 2) {
			numOfStep = Integer.valueOf(args[0]);
			System.out.println("Num of step = " + numOfStep);
			if (args[1].equals("P1")) {
				pfuncSerie = hPFuncSerie1;
			} else if (args[1].equals("P2")) {
				pfuncSerie = hPFuncSerie2;
			} else if (args[1].equals("P3")) {
				pfuncSerie = hPFuncSerie3;
			} else if (args[1].equals("P4")) {
				pfuncSerie = hPFuncSerie4;
			} else if (args[1].equals("P5")) {
				pfuncSerie = hPFuncSerie5;
			} else if (args[1].equals("P6")) {
				pfuncSerie = hPFuncSerie6;
			}
			
		} else if (args.length == 3) {
			numOfStep = Integer.valueOf(args[0]);
			System.out.println("Num of step = " + numOfStep);
			if (args[1].equals("P1")) {
				pfuncSerie = hPFuncSerie1;
			} else if (args[1].equals("P2")) {
				pfuncSerie = hPFuncSerie2;
			} else if (args[1].equals("P3")) {
				pfuncSerie = hPFuncSerie3;
			} else if (args[1].equals("P4")) {
				pfuncSerie = hPFuncSerie4;
			} else if (args[1].equals("P5")) {
				pfuncSerie = hPFuncSerie5;
			} else if (args[1].equals("P6")) {
				pfuncSerie = hPFuncSerie6;
			}
			
			if (args[2].equals("ave")) {
				isAverageExp = true;;
			}
		}
		
		
//		BasicSingleExperiment exp1 = new NaiveDefenseExperiment();
//		BasicSingleExperiment exp1 = new DefenseExperiment();
		
		// for gradually shrink part
		GradShrinkNaiveDefenseExperiment exp1 = new GradShrinkNaiveDefenseExperiment();
		exp1.setShrinkTimes(shrinkTimes);
		exp1.setQueryOffset(xOffsetList, yOffsetList);
		
//		exp1.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
		
		exp1.setRange(minX, maxX, minY, maxY);
		exp1.setSquareSize(1.0);
		exp1.setPFunctionSerie(pfuncSerie);
		exp1.setPfuncThreshold(threList);
		exp1.setNumOfStep(numOfStep);
		exp1.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp1.setSearchStrategy("exp");
		if (!isAverageExp) {
			exp1.run();
		}
		
		AveEntropyExperiment exp2 = new AveEntropyExperiment();
//		BasicSingleExperiment basicExp2 = new NaiveDefenseExperiment();
//		BasicSingleExperiment basicExp2 = new DefenseExperiment();
		
		GradShrinkNaiveDefenseExperiment basicExp2 = new GradShrinkNaiveDefenseExperiment();
		basicExp2.setShrinkTimes(shrinkTimes);
		basicExp2.setQueryOffset(xOffsetList, yOffsetList);

//		basicExp2.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
		
		exp2.setBasicSingleExperiment(basicExp2);
		exp2.setPFunctionSerie(pfuncSerie);
		exp2.setPfuncThreshold(threList);
		exp2.setNumOfStep(numOfStep);
		exp2.setRange(minX, maxX, minY, maxY);
		exp2.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp2.setSearchStrategy("exp");
		exp2.setNumOfRepeats(100);		
		exp2.disableOutProbX();
		if (isAverageExp) {
			exp2.run();
		}
	}
	
}
