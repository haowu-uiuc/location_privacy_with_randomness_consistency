package defenseExp;

/**
 * randomly add error from [-noiseWidth, noiseWidth] to the tweet before deciding whether include it in the return
 * @author HaoWu
 *
 */
public class NoisePFunction implements PFunction{
	
	private Double thre;
	private double noiseWidth = 0;
	
	@Override
	public void setThreshold(Double thre) {
		this.thre = thre;
	}
	
	public void setNoiseWidth(double noiseWidth){
		this.noiseWidth = noiseWidth;
	}

	@Override
	public Double getThreshold() {
		return thre;
	}

	@Override
	public Double getValue(Double dist) {
		double prob = 0.;
		
		if(dist <= thre - noiseWidth){
			prob = 1.;
		} else if(dist > thre - noiseWidth && dist <= thre + noiseWidth){
			prob = 1 - (dist - thre + noiseWidth) / 2 / noiseWidth;
		}
		
		if(prob < 0){
			prob = 0;
		}
		
		return prob;
	}
	
}
