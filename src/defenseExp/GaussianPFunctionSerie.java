package defenseExp;

import java.util.ArrayList;
import java.util.List;

public class GaussianPFunctionSerie implements PFunctionSerie{
	private List<PFunction> pFuncList;
	private List<Double> threList;
	private double a = 1.;
	
	@Override
	public void setThreshold(List<Double> threList) {
		this.threList = new ArrayList<>(threList);
		pFuncList = new ArrayList<>();
		for (Double thre : threList) {
			GaussianPFunction pFunc = new GaussianPFunction();
			pFunc.setThreshold(thre);
			pFunc.setVar(a);
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
	
	public void setVar(Double var) {
		a = var;
	}
	
	public PFunctionSerie clone() {
		GaussianPFunctionSerie cloned = new GaussianPFunctionSerie();
		cloned.setVar(a);
		cloned.setThreshold(new ArrayList<Double>(threList));
		return cloned;
	}
	
	@Override
	public void scaleBy(double distTimes) {
		for (int i = 0; i < threList.size(); i++) {
			threList.set(i, threList.get(i) * distTimes);
		}
		a *= distTimes * distTimes;
		setThreshold(threList);
	}
}

