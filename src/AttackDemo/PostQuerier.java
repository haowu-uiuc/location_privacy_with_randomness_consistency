package attackDemo;

import utils.Point;

public interface PostQuerier {

	/**
	 * query the post by its ID
	 * @param postId
	 * @return true -> post is in the response, 
	 * false -> post is not in the response
	 */
	public boolean queryPost(String postId, int pfuncId, Point grid_Xq);
	
	public void setMapQuantizer(MapQuantizer mapQuantizer);
	
}
