package attackDemo;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.Point;

public class MapQuantizerTest {

	@Test
	public void test() throws Exception {

		MapQuantizer mq = new MapQuantizer(new Point(-88.230942, 40.096681), 5., 0.1);
		System.out.println(mq.getGridMaxX());
		System.out.println(mq.getGridPoint(new Point(-88.200565, 40.096919)));
		System.out.println(mq.getGeoPoint(new Point(0., 10.)));
	
	}

}
