package it.dycomodel.equation;



import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.polynomial.PolynomialF;

public class EquationF extends AEquation<Float> { 
	private static final long serialVersionUID = 1L; 

	@Override
	public APolynomial<Float> initPolynomial() {		
		return new PolynomialF();
	}

	@Override
	public APolynomial<Float> setConstant(APolynomial<Float> polynomial, int n, Float value) {
		return polynomial.setConstant(n, value);
	}

	@Override
	public IEquation<Float> initEquation() {
		return new EquationF();
	}	

}
