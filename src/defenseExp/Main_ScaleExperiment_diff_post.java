package defenseExp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main_ScaleExperiment_diff_post {

	//scale experiment
	public static void main(String[] args) throws IOException {
		
		
		
		String expName = "P5_test";
//		List<Double> threList = Arrays.asList(1.5, 3., 5., 10.);
		List<Double> threList = Arrays.asList(1.5);
		
		int repeat = 2;
		int numOfStep = -1;
		int startPost = 0;
		int numOfPosts = 1;
		int[] scales = {2, 6, 10, 14, 20};
//		double[] squareSizes = {1./3., 1./2.5, 1./2., 1./1.5, 1., 2., 3.};
//		int[] scales = {2, 3};
//		double[] squareSizes = {4., 5, 6, 7., 8.};
		double[] squareSizes = {1.};
		
		System.out.println(args.length);
		
		if(args.length == 5){
			repeat = Integer.valueOf(args[0]);
			numOfStep = Integer.valueOf(args[1]);
			startPost = Integer.valueOf(args[2]);
			numOfPosts = Integer.valueOf(args[3]);
			expName = args[4] + "_1";
		} else if (args.length == 6) {
			int batch = Integer.valueOf(args[0]);
			repeat = Integer.valueOf(args[1]);
			numOfStep = Integer.valueOf(args[2]);
			startPost = Integer.valueOf(args[3]);
			numOfPosts = Integer.valueOf(args[4]);
			expName = args[5] + "_" + batch;
			
		} else if(args.length >= 7){
			int batch = Integer.valueOf(args[0]);
			repeat = Integer.valueOf(args[1]);
			numOfStep = Integer.valueOf(args[2]);
			startPost = Integer.valueOf(args[3]);
			numOfPosts = Integer.valueOf(args[4]);
			expName = args[5] + "_" + batch;
			System.out.println(expName);
			scales = new int[args.length-6];
			for (int i = 0; i < scales.length; i++) {
				scales[i] = Integer.valueOf(args[i+6]);
				System.out.println(scales[i]);
			}
		}
		
		
		ScaleExperiment exp1;
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
		
		BufferedReader post_br = new BufferedReader(new FileReader(new File("./post_list.txt")));
		
		// go to the startPost
		for (int i = 0; i < startPost; i++) {
			post_br.readLine();
		}
		
		for (int i = 0; i < numOfPosts; i++) {
			
			// randomly pick a post location from [-minScale, minScale]
//			int x = (int) (Math.random() * (2 * minScale + 1) - 0.000001) - minScale;
//			int y = (int) (Math.random() * (2 * minScale + 1) - 0.000001) - minScale;
			String line = post_br.readLine();
			if (line == null) {
				break;
			}
			
			String[] strs = line.split("\t");
			int x = Integer.valueOf(strs[0]);
			int y = Integer.valueOf(strs[1]);
			
			System.out.println(">>>>Post: (" + x + "," + y + ")<<<<");
			
			exp1 = new ScaleExperiment();
			exp1.setParentDir(expName);
			exp1.setExpName("post_" + (i + startPost));

			for (int scaleIdx = 0; scaleIdx < scales.length; scaleIdx++){
				for (int sqIdx = 0; sqIdx < squareSizes.length; sqIdx++) {
					BasicSingleExperiment basicExp = new DefenseExperiment();
//					BasicSingleExperiment basicExp = new NaiveDefenseExperiment();
					basicExp.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
	
					exp1.setBasicSingleExperiment(basicExp);
					exp1.setPFunctionSerie(hPFuncSerie5);
					exp1.setMaxScale(scales[scaleIdx]);
					exp1.setMinScale(scales[scaleIdx]);
					exp1.setNumOfRepeats(repeat);
	//				exp1.setNumOfStepMax(-1);
					exp1.setNumOfStepMax(numOfStep);
					exp1.setOptXqStrategy("exp");
					exp1.setScaleStep(1);
					exp1.setThre(threList);
					exp1.setTweetPos(x, y);
					exp1.setSquareSize(squareSizes[sqIdx]);		
					exp1.run();
				}
			}
		}
		
		post_br.close();
		
		
	}
}
