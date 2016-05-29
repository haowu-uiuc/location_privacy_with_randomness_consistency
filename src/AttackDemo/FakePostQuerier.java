package AttackDemo;

import Entropy.PFunctionSerie;
import Utils.Point;

public class FakePostQuerier implements PostQuerier {

	PFunctionSerie pfuncs;
	MapQuantizer mapQuantizer;
	Point Xp;
	
	public void setPFuncSerie(PFunctionSerie pfuncs) {
		this.pfuncs = pfuncs;
	}
	
	public void setPostLocation(Point Xp) {
		this.Xp = Xp;
	}
	
	@Override
	public boolean queryPost(String postId, int pfuncId, Point grid_Xq) {
		Point grid_Xp = mapQuantizer.getGridPoint(Xp);

		double rand = Math.random();
		double prob = pfuncs.getPFuncs().get(pfuncId)
				.getValue(grid_Xp.getDistance(grid_Xq) * mapQuantizer.getSquareSize());
		
		if (prob > rand) {
			return true;
		}
		
		return false;
	}

	@Override
	public void setMapQuantizer(MapQuantizer mapQuantizer) {
		this.mapQuantizer = mapQuantizer;
	}

}
