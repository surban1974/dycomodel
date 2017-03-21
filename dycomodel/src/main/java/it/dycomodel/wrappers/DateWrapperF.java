package it.dycomodel.wrappers;

import it.dycomodel.equation.EquationF;
import it.dycomodel.equation.IEquation;
import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.polynomial.PolynomialF;

public class DateWrapperF extends ADateWrapper<Float> {
	private static final long serialVersionUID = 1L;

	@Override
	public IEquation<Float> initEquation() {
		return new EquationF();
	}
	@Override
	public Float convertValue(Number value){
		return value.floatValue();
	}
	@Override
	protected APolynomial<Float> initAdapter() {
		return new PolynomialF();
	}

}
