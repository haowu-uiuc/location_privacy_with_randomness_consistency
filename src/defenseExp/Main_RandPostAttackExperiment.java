package defenseExp;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import utils.Point;

public class Main_RandPostAttackExperiment {

	public static void main(String[] args) throws IOException {

		double minX = -20.;
		double maxX = 20.;
		double minY = -20.;
		double maxY = 20.;
//		int numOfStep = ((int)maxX - (int)minX + 1) * ((int)maxY - (int)minY + 1);
		int numOfStep = 1000;
		List<Double> threList = Arrays.asList(7.);
//		List<Double> threList = Arrays.asList(3., 5., 7., 10., 15.);
		Point tweetPos = new Point(5., 3.);
				
		HyperbolaPFunctionSerie hPFuncSerie3 = new HyperbolaPFunctionSerie();		
		HyperbolaPFunctionSerie hPFuncSerie4 = new HyperbolaPFunctionSerie();	
		HyperbolaPFunctionSerie hPFuncSerie5 = new HyperbolaPFunctionSerie();
		GaussianPFunctionSerie hPFuncSerie6 = new GaussianPFunctionSerie();		
		StepPFunctionSerie hPFuncSerie1 = new StepPFunctionSerie();
		NoisePFunctionSerie hPFuncSerie2 = new NoisePFunctionSerie();
		
		// P2
		hPFuncSerie2.setNoiseWidth(2.);
		
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
		hPFuncSerie6.setVar(0.251);
		
		PFunctionSerie pfuncSerie = hPFuncSerie1;
		
		if (args.length == 2) {
			if (args[0].equals("P1")) {
				pfuncSerie = hPFuncSerie1;
			} else if (args[0].equals("P2")) {
				pfuncSerie = hPFuncSerie2;
			} else if (args[0].equals("P3")) {
				pfuncSerie = hPFuncSerie3;
			} else if (args[0].equals("P4")) {
				pfuncSerie = hPFuncSerie4;
			} else if (args[0].equals("P5")) {
				pfuncSerie = hPFuncSerie5;
			} else if (args[0].equals("P6")) {
				pfuncSerie = hPFuncSerie6;
			}
			System.out.println(pfuncSerie.getClass());
		}		
		
		
		BasicSingleExperiment exp1 = new NaiveDefenseExperiment();
//		BasicSingleExperiment exp1 = new DefenseExperiment();
		
		// for gradually shrink part
//		GradShrinkNaiveDefenseExperiment exp1 = new GradShrinkNaiveDefenseExperiment();
//		exp1.setShrinkTimes((maxX - minX) + 1);
//		exp1.setQueryOffset(12 - tweetPos.getX(), 3 - tweetPos.getY()); // for P5
//		exp1.setQueryOffset(12 - tweetPos.getX(), 3 - tweetPos.getY());  // for P4
//		exp1.setQueryOffset(12 - tweetPos.getX(), 3 - tweetPos.getY()); // for P3
//		exp1.setQueryOffset(12 - tweetPos.getX(), 3 - tweetPos.getY()); // for P2
//		exp1.setQueryOffset(12 - tweetPos.getX(), 3 - tweetPos.getY()); // for P1		
		
		
		exp1.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
		
		exp1.setRange(minX, maxX, minY, maxY);
		exp1.setSquareSize(1.0);
		exp1.setPFunctionSerie(pfuncSerie);
		exp1.setPfuncThreshold(threList);
		exp1.setNumOfStep(numOfStep);
		exp1.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp1.setSearchStrategy("exp");
//		exp1.run();
		
		AveEntropyExperiment exp2 = new AveEntropyExperiment();
		BasicSingleExperiment basicExp2 = new NaiveDefenseExperiment();
//		BasicSingleExperiment basicExp2 = new DefenseExperiment();
		basicExp2.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
		
		exp2.setBasicSingleExperiment(basicExp2);
		exp2.setPFunctionSerie(pfuncSerie);
		exp2.setPfuncThreshold(threList);
		exp2.setNumOfStep(numOfStep);
		exp2.setRange(minX, maxX, minY, maxY);
		exp2.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		exp2.setSearchStrategy("exp");
		exp2.setNumOfRepeats(100);		
		exp2.disableOutProbX();
		exp2.enableRandPost();
		
//		exp2.setNumOfRepeats(1);		
//		exp2.setNumOfStep(5);
		
		exp2.run();
		
		
		BasicSingleExperiment hier_exp = new HierDefenseExperiment();
		hier_exp.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));

		hier_exp.setRange(minX, maxX, minY, maxY);
		hier_exp.setSquareSize(1.0);
		hier_exp.setBaseMapRadius(2);
		hier_exp.setPFunctionSerie(pfuncSerie);
		hier_exp.setPfuncThreshold(threList);
		hier_exp.setNumOfStep(numOfStep);
		hier_exp.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		hier_exp.setSearchStrategy("exp");
//		hier_exp.run();
		
	}
	
}
