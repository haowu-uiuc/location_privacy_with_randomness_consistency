package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import defenseExp.HierarchicalSubdivision;
import utils.Point;

public class HierarchicalSubdivisionTest {

	@Test
	public void test() {
		
		HierarchicalSubdivision hierSub = new HierarchicalSubdivision(20, -20, 20, -20, 1);
		Point X1 = new Point(4., 1.);
		Point cX1_1 = new Point(3., 0.);
		Point cX1_2 = new Point(0., 0.);
		Point X2 = new Point(3., -1.);
		Point cX2_1 = new Point(3., 0.);
		Point cX2_2 = new Point(0., 0.);
		Point X3 = new Point(5., 2.);
		Point cX3_1 = new Point(6., 3.);
		Point cX3_2 = new Point(9., 0.);
		
		assertTrue(hierSub.getCenters(X1)[1].equals(cX1_1));
		assertTrue(hierSub.getCenters(X1)[2].equals(cX1_2));
		assertTrue(hierSub.getCenters(X2)[1].equals(cX2_1));
		assertTrue(hierSub.getCenters(X2)[2].equals(cX2_2));
		assertTrue(hierSub.getCenters(X3)[1].equals(cX3_1));
		assertTrue(hierSub.getCenters(X3)[2].equals(cX3_2));
		
		System.out.println(Arrays.asList(hierSub.getCenters(X1)));
		System.out.println(Arrays.asList(hierSub.getCenters(X2)));
		System.out.println(Arrays.asList(hierSub.getCenters(X3)));
	
		System.out.println(hierSub.getCenterDist(X1, X3));
		System.out.println(hierSub.getCenterDist(X1, X2));
		assertTrue(hierSub.getCenterDist(X1, X2) == X1.getDistance(X2));
		assertTrue(hierSub.getCenterDist(X1, X3) == cX1_2.getDistance(cX3_2));
		
		
		hierSub = new HierarchicalSubdivision(20, -20, 20, -20, 2);
		X1 = new Point(6., 2.);
		cX1_1 = new Point(5., 0.);
		cX1_2 = new Point(0., 0.);
		X2 = new Point(9., -1.);
		cX2_1 = new Point(10., 0.);
		cX2_2 = new Point(0., 0.);
		X3 = new Point(16., 2.);
		cX3_1 = new Point(15., 0.);
		cX3_2 = new Point(25., 0.);
		Point X4 = new Point(5.0, 3.0);
		Point cX4_1 = new Point(5.0, 5.0);
		
		assertTrue(hierSub.getCenters(X1)[1].equals(cX1_1));
		assertTrue(hierSub.getCenters(X1)[2].equals(cX1_2));
		assertTrue(hierSub.getCenters(X2)[1].equals(cX2_1));
		assertTrue(hierSub.getCenters(X2)[2].equals(cX2_2));
		assertTrue(hierSub.getCenters(X3)[1].equals(cX3_1));
		assertTrue(hierSub.getCenters(X3)[2].equals(cX3_2));
		assertTrue(hierSub.getCenters(X4)[1].equals(cX4_1));
		
		System.out.println(Arrays.asList(hierSub.getCenters(X1)));
		System.out.println(Arrays.asList(hierSub.getCenters(X2)));
		System.out.println(Arrays.asList(hierSub.getCenters(X3)));
		System.out.println(Arrays.asList(hierSub.getCenters(X4)));
	
		System.out.println(hierSub.getCenterDist(X1, X3));
		System.out.println(hierSub.getCenterDist(X1, X2));
		assertTrue(hierSub.getCenterDist(X1, X2) == cX1_1.getDistance(cX2_1));
		assertTrue(hierSub.getCenterDist(X1, X3) == cX1_2.getDistance(cX3_2));
		
	}

}
