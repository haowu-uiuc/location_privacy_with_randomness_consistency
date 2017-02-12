package defenseExp;

public class MonoPFunction implements PFunction{

	private PFunction midPFunc = null;
	private PFunction buttonPFunc = null;
	private PFunction topPFunc = null;
	
	public MonoPFunction(PFunction buttonPFunc,
			PFunction midPFunc,
			PFunction topPFunc) {
		if (midPFunc == null) {
			System.out.println("Mid PFunction cannot be null!");
		}
		this.buttonPFunc = buttonPFunc;
		this.midPFunc = midPFunc;
		this.topPFunc = topPFunc;
	}
	
	@Override
	public void setThreshold(Double thre) {
	}

	@Override
	public Double getThreshold() {
		return 0.;
	}

	@Override
	public Double getValue(Double dist) {
		double topValue = 1.;
		double buttonValue = 0.;
		
		if (topPFunc != null) {
			topValue = topPFunc.getValue(dist);
		}
		
		if (buttonPFunc != null) {
			buttonValue = buttonPFunc.getValue(dist);
		}
		
		return (midPFunc.getValue(dist) - buttonValue) / (topValue - buttonValue);
	}

}
