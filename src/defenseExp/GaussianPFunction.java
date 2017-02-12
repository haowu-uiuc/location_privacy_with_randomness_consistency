package defenseExp;

public class GaussianPFunction implements PFunction{

	private Double thre = 0.; 
	private Double a = 1.;
	private Double max = 1./Math.sqrt(2. * Math.PI );
	
	@Override
	public void setThreshold(Double thre) {
		this.thre = thre;
	}
	
	public void setVar(Double var) {
		a = var;
		max = 1./Math.sqrt(2. * Math.PI ) / a;
	}

	@Override
	public Double getThreshold() {
		return thre;
	}

	@Override
	public Double getValue(Double dist) {
		if(dist <= thre && dist >= thre - 5 * a){
			return (2. * max - getGaussianValue(dist)) / 2. / max;
		} else if (dist > thre && dist <= thre + 5 * a){
			return getGaussianValue(dist) / 2. / max;
		} else if (dist < thre - 5 * a) {
			return 1.;
		} else {
			return 0.;
		}
	}
	
	private double getGaussianValue(double x) {
		return 1. / a /Math.sqrt(2. * Math.PI) * Math.exp(-(x - thre) * (x - thre) / 2. / a / a);
	}

}
