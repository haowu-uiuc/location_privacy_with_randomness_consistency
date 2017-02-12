package attackDemo;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import defenseExp.PFunctionSerie;
import defenseExp.StepPFunctionSerie;
import utils.GeoUtils;
import utils.Point;

public class AttackDemoTest {

	// TODO: test real LSBN service
	// TODO: cmd line user interface
	
	@Test
	public void test() throws Exception {

		Point geo_center = new Point(-88.236637, 40.107103);
		double radius = 5.0;
		double squareSize = 0.3;
		Point post_loc = new Point(-88.234976, 40.122059);
		
		PFunctionSerie pfuncs = new StepPFunctionSerie();
		pfuncs.setThreshold(Arrays.asList(2.));
		
		FakePostQuerier postQuerier = new FakePostQuerier();
		postQuerier.setPFuncSerie(pfuncs);
		postQuerier.setPostLocation(post_loc);
		//TODO: map quantizer is not correct, test it
		
		LocationHacker hacker = new LocationHacker(geo_center, radius, squareSize, postQuerier);
		hacker.setPFuncSerie(pfuncs);
		hacker.setNumOfStep(100);
		hacker.setPostToHack("fake");
		
		Point estX = hacker.run();
		
		System.out.println("Estimated Location: " + estX);
		
		double error = GeoUtils.getDistanceBetweenTwoGeos(post_loc, estX);
		System.out.println("Error distance: " + error + " km");
	}

}
