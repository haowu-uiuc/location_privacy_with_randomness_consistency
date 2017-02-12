package defenseExp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main_MultiThreExperiment {

	//scale experiment
	public static void main(String[] args) throws IOException {
		
		
		
		String expName = "P5_test";
//		List<Double> threList = Arrays.asList(1.5, 3., 5., 10.);
		List<Double> threList = Arrays.asList(1.5);
		List<List<Double>> threLists = new ArrayList<>();
		threLists.add(threList);
		
		int repeat = 2;
		int numOfStep = -1;
		int[] scales = {10};
//		double[] squareSizes = {1./3., 1./2.5, 1./2., 1./1.5, 1., 2., 3.};
//		int[] scales = {2, 3};
		double[] squareSizes = {1.};
		
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
			
			threLists = new ArrayList<>();
			// threList format "1.0,1.5,2.0"
			for (int i = 0; i < args.length-4; i++) {
				String[] threStrs = args[i+4].split(",");
				
				List<Double> curThreList = new ArrayList<>();
				for (int j = 0; j < threStrs.length; j++) {
					double thre = Double.valueOf(threStrs[j]);
					curThreList.add(thre);
				}
				
				threLists.add(curThreList);
				System.out.println(threLists.get(i));
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
		
		
		for (int i = 0; i < threLists.size(); i++) {
			List<Double> curThreList = threLists.get(i);
			
			String subExpName = curThreList.get(0).toString();
			
			for (int k = 1; k < curThreList.size(); k++) {
				subExpName += ("-" + curThreList.get(k));
			}
			
			exp1.setExpName(expName + "/" + subExpName);
			
			for (int scaleIdx = 0; scaleIdx < scales.length; scaleIdx++){
				for (int sqIdx = 0; sqIdx < squareSizes.length; sqIdx++) {
					BasicSingleExperiment basicExp = new DefenseExperiment();
//					BasicSingleExperiment basicExp = new MonoDefenseExperiment();
//					BasicSingleExperiment basicExp = new NaiveDefenseExperiment();
					basicExp.useRealData(new File("./real_data/grid_pop_north_chicago.txt"));
					
					exp1.setBasicSingleExperiment(basicExp);
					exp1.setPFunctionSerie(hPFuncSerie5);
					exp1.setMaxScale(scales[scaleIdx]);
					exp1.setMinScale(scales[scaleIdx]);
					exp1.setNumOfRepeats(repeat);
//					exp1.setNumOfStepMax(-1);
					exp1.setNumOfStepMax(numOfStep);
					exp1.setOptXqStrategy("exp");
					exp1.setScaleStep(1);
					exp1.setThre(curThreList);
					exp1.setTweetPos(2., 2.);
					exp1.setSquareSize(squareSizes[sqIdx]);	
					exp1.run();
				}
			}
		}
		
		
	}
}
