package defenseExp;

public class DounutPFunction implements PFunction{

	private Double thre;
	private double width = 1.;
	
	@Override
	public void setThreshold(Double thre) {
		this.thre = thre;
	}
	
	public void setWidth(Double width){
		this.width = width;
	}

	@Override
	public Double getThreshold() {
		return thre;
	}

	@Override
	public Double getValue(Double dist) {
		if(dist <= thre && dist >= thre - width){
			return 1.;
		}
		
		return 0.;
	}
}
