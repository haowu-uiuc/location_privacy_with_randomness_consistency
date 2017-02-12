package test;

import static org.junit.Assert.*;

import org.junit.Test;

import defenseExp.GaussianPFunction;
import defenseExp.PFunction;

public class GaussianPFunctionTest {

	@Test
	public void test() {
		GaussianPFunction pFunc = new GaussianPFunction();
		pFunc.setThreshold(5.);
		pFunc.setVar(1.);
		for (double x = 0.; x < 10.; x += 1.) {
			System.out.println(pFunc.getValue(x));
		}
	}

}
