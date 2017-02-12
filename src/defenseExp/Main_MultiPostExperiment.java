package defenseExp;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main_MultiPostExperiment {

	//scale experiment
	public static void main(String[] args) throws IOException {
		
		
		
		String expName = "P5_test";
//		List<Double> threList = Arrays.asList(1.5, 3., 5., 10.);
		List<Double> threList = Arrays.asList(1.5);
		
		int repeat = 2;
		int numOfStep = -1;
//		int[] scales = {2, 6, 10, 14, 20};
//		double[] squareSizes = {1./3., 1./2.5, 1./2., 1./1.5, 1., 2., 3.};
		int[] scales = {10};
//		double[] squareSizes = {4., 5, 6, 7., 8.};
		double[] squareSizes = {1.};
		int[] numOfPostsList = {1, 2, 3, 4, 5};
		
		System.out.println(args.length);
		
		if(args.length == 3){
			repeat = Integer.valueOf(args[0]);
			numOfStep = Integer.valueOf(args[1]);
			expName = args[2] + "_1";
		} else if (args.length == 4) {
			int batch = Integer.valueOf(args[0]);
			repeat = Integer.valueOf(args[1]);
			numOfStep = Integer.valueOf(args[2]);
			expName = args[3] + "_" + batch;
			
		} else if(args.length >= 5){
			int batch = Integer.valueOf(args[0]);
			repeat = Integer.valueOf(args[1]);
			numOfStep = Integer.valueOf(args[2]);
			expName = args[3] + "_" + batch;
			System.out.println(expName);
			numOfPostsList = new int[args.length-4];
			for (int i = 0; i < numOfPostsList.length; i++) {
				numOfPostsList[i] = Integer.valueOf(args[i+4]);
				System.out.println(numOfPostsList[i]);
			}
		}
		
		
		ScaleExperiment exp1 = new ScaleExperiment();
		HyperbolaPFunctionSerie hPFuncSerie3 = new HyperbolaPFunctionSerie();		
		HyperbolaPFunctionSerie hPFuncSerie4 = new HyperbolaPFunctionSerie();	
		HyperbolaPFunctionSerie hPFuncSerie5 = new HyperbolaPFunctionSerie();	
		HyperbolaPFunctionSerie hPFuncSerie5_old = new HyperbolaPFunctionSerie();		
		HyperbolaPFunctionSerie hPFuncSerie6 = new HyperbolaPFunctionSerie();		
		HyperbolaPFunctionSerie hPFuncSerie7 = new HyperbolaPFunctionSerie();		

		
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
		
		// P5_old
		hPFuncSerie5_old.setA(4.);
		hPFuncSerie5_old.setB(1.05);
		hPFuncSerie5_old.setC(0.);
		
		// P6
		hPFuncSerie6.setA(2.);
		hPFuncSerie6.setB(1.2);
		hPFuncSerie6.setC(0.);
		
		// P7
		hPFuncSerie7.setA(1.);
		hPFuncSerie7.setB(1.2);
		hPFuncSerie7.setC(0.);
		
		exp1.setExpName(expName);
		
		for (int i = 0; i < numOfPostsList.length; i++) {
			int numOfPosts = numOfPostsList[i];
			
			String subExpName = "post_num_" + String.valueOf(numOfPosts);
			
			exp1.setExpName(expName + "/" + subExpName);
		
			for (int scaleIdx = 0; scaleIdx < scales.length; scaleIdx++){
				for (int sqIdx = 0; sqIdx < squareSizes.length; sqIdx++) {
					BasicSingleExperiment basicExp = new DefenseExperiment();
//					BasicSingleExperiment basicExp = new NaiveDefenseExperiment();
					basicExp.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
					
					exp1.setBasicSingleExperiment(basicExp);
					exp1.setPFunctionSerie(hPFuncSerie3);
					exp1.setMaxScale(scales[scaleIdx]);
					exp1.setMinScale(scales[scaleIdx]);
					exp1.setNumOfRepeats(repeat);
	//				exp1.setNumOfStepMax(-1);
					exp1.setNumOfStepMax(numOfStep);
					exp1.setOptXqStrategy("exp");
					exp1.setScaleStep(1);
					exp1.setThre(threList);
					exp1.setTweetPos(2., 2.);
					exp1.setSquareSize(squareSizes[sqIdx]);	
					exp1.setNumOfDuplicatedPosts(numOfPosts);
					exp1.run();
				}
			}
		
		}
		
		
	}
}
