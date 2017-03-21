package it.dycomodel.wrappers;

import it.dycomodel.equation.EquationD;
import it.dycomodel.equation.IEquation;
import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.polynomial.PolynomialD;

public class DateWrapperD extends ADateWrapper<Double> {
	private static final long serialVersionUID = 1L;

	@Override
	public IEquation<Double> initEquation() {
		return new EquationD();
	}
	@Override
	public Double convertValue(Number value){
		return value.doubleValue();
	}
	@Override
	protected APolynomial<Double> initAdapter() {
		return new PolynomialD();
	}

}
