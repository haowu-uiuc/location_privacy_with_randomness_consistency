package defenseExp;

import java.util.ArrayList;
import java.util.List;

public class StepPFunctionSerie implements PFunctionSerie{
	private List<PFunction> pFuncList;
	private List<Double> threList;
	
	@Override
	public void setThreshold(List<Double> threList) {
		this.threList = new ArrayList<>(threList);
		pFuncList = new ArrayList<>();
		for (Double thre : threList) {
			StepPFunction pFunc = new StepPFunction();
			pFunc.setThreshold(thre);
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
	
	@Override
	public PFunctionSerie clone() {
		StepPFunctionSerie cloned = new StepPFunctionSerie();
		cloned.setThreshold(new ArrayList<Double>(threList));
		return cloned;
	}
	
	@Override
	public void scaleBy(double distTimes) {
		for (int i = 0; i < threList.size(); i++) {
			threList.set(i, threList.get(i) * distTimes);
		}
		
		setThreshold(threList);
	}
}
