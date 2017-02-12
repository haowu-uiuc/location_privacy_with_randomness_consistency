package defenseExp;

import java.util.List;

public interface PFunctionSerie extends Cloneable {

	public void setThreshold(List<Double> threList);
	
	public List<Double> getThresholds();
	
	public List<PFunction> getPFuncs();
	
	public PFunctionSerie clone();
	
	public void scaleBy(double distTimes);
	
}
