package defenseExp;

import java.util.HashMap;

import utils.Point;

public class HashOracle {
	
	private HashMap<String, Double> distHashMap;
	private HashMap<String, Double> xqSquareHashMap;
	
	public HashOracle(){
		distHashMap = new HashMap<String, Double>();
		xqSquareHashMap = new HashMap<String, Double>();
	}
	
	public double getDistHashValue(Double dist, Point tweetPos){
		Double roughDist = (int)(dist * 1000) / 1000.;
		String key = roughDist.toString() + "#" + tweetPos.getX() + "#" + tweetPos.getY();
		if(distHashMap.containsKey(key)){
			return distHashMap.get(key);
		} else{
			double hashValue = Math.random();
			distHashMap.put(key, hashValue);
			return hashValue;
		}
	}
	
	public double getXqSquareHashValue(Point queryPos, Point tweetPos, int postId, int threId){
		String key = threId + "-" + postId + "-" 
				+ queryPos.getX() + "#" + queryPos.getY() + "#" 
				+ tweetPos.getX() + "#" + tweetPos.getY();
		if(xqSquareHashMap.containsKey(key)){
			return xqSquareHashMap.get(key);
		} else{
			double hashValue = Math.random();
			xqSquareHashMap.put(key, hashValue);
			return hashValue;
		}
	}
	
}
