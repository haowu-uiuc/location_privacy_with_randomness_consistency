package defenseExp;

import java.util.ArrayList;
import java.util.List;

public class HyperbolaPFunctionSerie implements PFunctionSerie {

	private List<PFunction> pFuncList;
	private List<Double> threList;
	private double a = 4.;
	private double b = 1.05;
	private double c = 0.;
	
	@Override
	public void setThreshold(List<Double> threList) {
		this.threList = new ArrayList<>(threList);
		pFuncList = new ArrayList<>();
		for (Double thre : threList) {
			HyperbolaPFunction pFunc = new HyperbolaPFunction();
			pFunc.setA(a);
			pFunc.setB(b);
			pFunc.setC(c);
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
	
	public void setA(Double a){
		this.a = a;
	}
	
	public void setB(Double b){
		this.b = b;
	}
	
	public void setC(Double c){
		this.c = c;
	}
	
	public Double getC(){
		return c;
	}
	
	public PFunctionSerie clone() {
		HyperbolaPFunctionSerie cloned = new HyperbolaPFunctionSerie();
		cloned.setA(a);
		cloned.setB(b);
		cloned.setC(c);
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
