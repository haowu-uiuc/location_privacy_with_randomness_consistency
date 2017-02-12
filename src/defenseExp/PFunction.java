package defenseExp;

/**
 * distribution function for probability
 * @author HaoWu
 *
 */
public interface PFunction {

	void setThreshold(Double thre);
	
	Double getThreshold();
	
	Double getValue(Double dist);
		
}
