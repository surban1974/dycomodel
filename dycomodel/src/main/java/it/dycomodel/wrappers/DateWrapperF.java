package it.dycomodel.wrappers;

import it.dycomodel.equation.EquationF;
import it.dycomodel.equation.IEquation;

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

}
