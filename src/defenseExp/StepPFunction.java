package defenseExp;

public class StepPFunction implements PFunction{

	private Double thre; 
	
	@Override
	public void setThreshold(Double thre) {
		this.thre = thre;
	}

	@Override
	public Double getThreshold() {
		return thre;
	}

	@Override
	public Double getValue(Double dist) {
		if(dist <= thre){
			return 1.;
		}
		
		return 0.;
	}

}
