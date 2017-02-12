package defenseExp;

public class HyperbolaPFunction implements PFunction{

	private Double thre;
	private double a;
	private double b;
	private double c;
	
	public HyperbolaPFunction() {
		thre = 1.;
		a = 4;
		b = 1.05;
		c = 0.;
	}
	
	@Override
	public void setThreshold(Double thre) {
		this.thre = thre;
	}

	@Override
	public Double getThreshold() {
		return this.thre;
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

	@Override
	public Double getValue(Double dist) {
		// dist is the distance in the quantized square coordinate,
		// it is not the real world coordinate.

		double prod = 1;
		for(int i = 0; i < (int)a; i++){
			prod *= dist/thre;
		}
		
		Double prob = 1. / (prod + b) + c;		
//				Double prob = 1. / (Math.pow(dist/thre, a) + b) + c;
		
		return prob;
	}

}
