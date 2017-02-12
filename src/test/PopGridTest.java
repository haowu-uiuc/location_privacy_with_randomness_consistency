package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import defenseExp.ProbUtils;
import utils.Point;

public class PopGridTest {

	@Test
	public void test() throws IOException {
		File realDataFile = new File("./real_data/grid_pop_test.txt");
		
		assertTrue(ProbUtils.lastRealDataFilePath == null);
		assertTrue(ProbUtils.lastRealPopGird == null);
		
		int[][] popGrid = ProbUtils.readPopGridFile(realDataFile);
		
		assertTrue(ProbUtils.lastRealDataFilePath.equals(realDataFile.toString()));
		assertTrue(ProbUtils.lastRealPopGird != null);
		
		for (int i = 0; i < popGrid.length; i++) {
			for (int j = 0; j < popGrid[0].length; j++) {
				System.out.println(j + ", " + i + ", " + popGrid[i][j]);
			}
		}
		
		List<Point> allPoints = new ArrayList<>();
		allPoints.add(new Point(0.0, 0.0));
		allPoints.add(new Point(3.0, -5.0));
		allPoints.add(new Point(20., 20.));
		allPoints.add(new Point(-20., -20.));
		allPoints.add(new Point(20., -20.));
		allPoints.add(new Point(-20., 20.));
		
		List<Double> probX = ProbUtils.getRealInitialProbX(allPoints, realDataFile);

		double sum = 21. + 411. + 1246. + 2102.;
		System.out.println(probX.get(0) * sum);

		assertTrue(probX.get(0) == 21. / sum);
		assertTrue(probX.get(1) == 411. / sum);
		assertTrue(probX.get(2) == 0.);
		assertTrue(probX.get(3) == 1246. / sum);
		assertTrue(probX.get(4) == 0.);
		assertTrue(probX.get(5) == 2102. / sum);		
	}

}
