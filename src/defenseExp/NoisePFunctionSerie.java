package defenseExp;

import java.util.ArrayList;
import java.util.List;

public class NoisePFunctionSerie implements PFunctionSerie{
	private List<PFunction> pFuncList;
	private List<Double> threList;
	private double noiseWidth = 0;
	
	@Override
	public void setThreshold(List<Double> threList) {
		this.threList = new ArrayList<>(threList);
		pFuncList = new ArrayList<>();
		for (Double thre : threList) {
			NoisePFunction pFunc = new NoisePFunction();
			pFunc.setThreshold(thre);
			pFunc.setNoiseWidth(noiseWidth);
			pFuncList.add(pFunc);
		}
	}
	
	@Override
	public List<Double> getThresholds() {
		return threList;
	}

	@Override
	public List<PFunction> getPFuncs() {
		return pFuncList;
	}
	
	public void setNoiseWidth(double noiseWidth){
		this.noiseWidth = noiseWidth;
	}
	
	@Override
	public PFunctionSerie clone() {
		NoisePFunctionSerie cloned = new NoisePFunctionSerie();
		cloned.setNoiseWidth(noiseWidth);
		cloned.setThreshold(new ArrayList<Double>(threList));
		return null;
	}

	@Override
	public void scaleBy(double distTimes) {
		noiseWidth *= distTimes;
		
		for (int i = 0; i < threList.size(); i++) {
			threList.set(i, threList.get(i) * distTimes);
		}
		
		setThreshold(threList);
	}
}
