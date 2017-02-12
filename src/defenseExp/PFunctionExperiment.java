package defenseExp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import utils.Point;

public class PFunctionExperiment implements Experiment{

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private List<Double> threList;
	private int numOfStep;
	private int numOfRepeats;
	private Point tweetPos;
	private String optXqStrategy;
	private HyperbolaPFunctionSerie pFuncSerie;
	private boolean disableStdout;
	private File outEntropyDir;
	private BasicSingleExperiment basicExp;
	
	public PFunctionExperiment(){
		outEntropyDir = new File("./data/entropy/pFunc");
		pFuncSerie = new HyperbolaPFunctionSerie();	//default
		disableStdout = false;
	}
	
	public void setRange(double minX, double maxX, double minY, double maxY){
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void setPfuncThreshold(List<Double> threList){
		this.threList = threList;
		pFuncSerie.setThreshold(threList);
	}
	
	public void setNumOfStep(int numOfStep){
		this.numOfStep = numOfStep;
	}
	
	public void setTweetPoint(double x_t, double y_t){
		tweetPos = new Point(x_t, y_t);
	}
	
	public void setSearchStrategy(String strategy){
		this.optXqStrategy = strategy;
	}
	
	public void setNumOfRepeats(int numOfRepeats){
		this.numOfRepeats = numOfRepeats;
	}
	
	public void setBasicSingleExperiment(BasicSingleExperiment basicExp){
		this.basicExp = basicExp;
	}
	
	public void disableStdout(){
		disableStdout = true;
	}
	
	public void enableStdout(){
		disableStdout = false;
	}
	
	
	
	@Override
	public void run() throws IOException {
		if(!outEntropyDir.exists()){
			outEntropyDir.mkdirs();
		}
		
		BufferedWriter MASbwa = new BufferedWriter(new FileWriter(new File(outEntropyDir.toString() + "/user_att_msa_by_a.txt")));
		BufferedWriter MASbwb = new BufferedWriter(new FileWriter(new File(outEntropyDir.toString() + "/user_att_msa_by_b.txt")));
		BufferedWriter MASbwc = new BufferedWriter(new FileWriter(new File(outEntropyDir.toString() + "/user_att_msa_by_c.txt")));
		
		
		AveEntropyExperiment exp = new AveEntropyExperiment();
		exp.setBasicSingleExperiment(basicExp.createNewOne());
		exp.disableOutProbX();
		if(disableStdout){
			exp.disableStdout();
		} else{
			exp.enableStdout();
		}
		exp.setNumOfRepeats(numOfRepeats);
		exp.setNumOfStep(numOfStep);
		exp.setPFunctionSerie(pFuncSerie);
		exp.setPfuncThreshold(threList);
		exp.setRange(minX, maxX, minY, maxY);
		exp.setSearchStrategy(optXqStrategy);
		exp.setTweetPoint(tweetPos.getX(), tweetPos.getY());
		
		double a = 1.;
		pFuncSerie.setB(1.05);
		pFuncSerie.setC(0.);
		
		for(int i = 0; i < 10; i++){
			pFuncSerie.setA(a);
			exp.run();
			
			List<Double> attMSAList = exp.getAveAttMSAList();
			MASbwa.write(exp.getUserMAE() + "\t" + attMSAList.get(attMSAList.size() - 1) + "\t" + a + "\n");
			a ++;
			MASbwa.flush();
		}
		MASbwa.close();
		
		double b = 1.;
		pFuncSerie.setA(1.);
		pFuncSerie.setC(0.);
		for(int i = 0; i < 10; i++){
			pFuncSerie.setB(b);
			exp.run();
			
			List<Double> attMAEList = exp.getAveAttMSAList();
			MASbwb.write(exp.getUserMAE() + "\t" + attMAEList.get(attMAEList.size() - 1) + "\t" + b + "\n");
			b += 0.02;
			MASbwb.flush();
		}
		
		for(int i = 0; i < 10; i++){
			pFuncSerie.setB(b);
			exp.run();
			
			List<Double> attMAEList = exp.getAveAttMSAList();
			MASbwb.write(exp.getUserMAE() + "\t" + attMAEList.get(attMAEList.size() - 1) + "\t" + b + "\n");
			b += 0.2;
			MASbwb.flush();
		}
		
		for(int i = 0; i < 10; i++){
			pFuncSerie.setB(b);
			exp.run();
			
			List<Double> attMAEList = exp.getAveAttMSAList();
			MASbwb.write(exp.getUserMAE() + "\t" + attMAEList.get(attMAEList.size() - 1) + "\t" + b + "\n");
			b += i+1;
			MASbwb.flush();
		}
		
		MASbwb.close();
		
		double c = 0.;
		pFuncSerie.setA(1.);
		pFuncSerie.setB(2.);
		
		for(int i = 0; i < 10; i++){
			pFuncSerie.setC(c);
			exp.run();
			
			List<Double> attMSAList = exp.getAveAttMSAList();
			MASbwc.write(exp.getUserMAE() + "\t" + attMSAList.get(attMSAList.size() - 1) + "\t" + c + "\n");
			c += 0.05;
			MASbwc.flush();
		}
		
		MASbwc.close();
	}
	
}
