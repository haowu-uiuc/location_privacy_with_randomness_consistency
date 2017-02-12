package test;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.DefenseAbsErrCalculator;
import utils.Point;

public class DefenseAbsErrCalculatorTest {

	private DefenseAbsErrCalculator calculator;
	
	@Test
	public void test() {
		calculator = DefenseAbsErrCalculator.getInstance();
		System.out.println(calculator.getAbsErrBetween(new Point(2., 2.), new Point(0., 0.)));
		System.out.println(calculator.getAbsErrBetween(new Point(4., 4.), new Point(2., 2.)));
		System.out.println(calculator.getAbsErrBetween(new Point(0., 0.), new Point(2., 2.)));
		System.out.println(calculator.getAbsErrBetween(new Point(4., 0.), new Point(2., 2.)));
		System.out.println(calculator.getAbsErrBetween(new Point(0., 4.), new Point(2., 2.)));
		System.out.println(calculator.getAbsErrBetween(new Point(3., 1.), new Point(0., 0.)));
		System.out.println(calculator.getAbsErrBetween(new Point(1., 3.), new Point(0., 0.)));
		System.out.println(calculator.getAbsErrBetween(new Point(4., 2.), new Point(0., 0.)));
		System.out.println(calculator.getAbsErrBetween(new Point(2., 4.), new Point(0., 0.)));
		System.out.println(calculator.getAbsErrBetween(new Point(-1., -1.), new Point(1., 1.)));
		System.out.println(calculator.getAbsErrBetween(new Point(-1.00001, -1.00001), new Point(1.00001, 1.00001)));
		System.out.println(calculator.getAbsErrBetween(new Point(-1.1, -1.1), new Point(1.1, 1.1)));
		
		
		System.out.println((int)(111.23127389127311 * 1000) / 1000.);
	}

}
