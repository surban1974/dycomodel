package it.dycomodel.equation;



import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.polynomial.PolynomialD;

public class EquationD extends AEquation<Double> {
	private static final long serialVersionUID = 1L;

	@Override
	public APolynomial<Double> initPolynomial() {		
		return new PolynomialD();
	}

	@Override
	public APolynomial<Double> setConstant(APolynomial<Double> polynomial, int n, double value) {
		return polynomial.setConstant(n, value);
	}



}
