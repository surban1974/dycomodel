package it.dycomodel.polynomial;

import java.util.ArrayList;

public class PolynomialF extends APolynomial<Float>{
	private static final long serialVersionUID = 1L;

	public PolynomialF(){
		super();
		constants = new ArrayList<Float>();
	}
	
	public PolynomialF(float[] arrayf){
		super();
		constants = new ArrayList<Float>();
		for(float d : arrayf) 
			constants.add(d);
	}

	@Override
	public APolynomial<Float> additionConstant(int n, Float value){
		normalizeSize(n);
		constants.set(n, constants.get(n)+value);
		return this;
	}
	@Override
	public APolynomial<Float> subtractionConstant(int n, Float value){
		normalizeSize(n);
		constants.set(n, constants.get(n)-value);
		return this;
	}	
	@Override
	public APolynomial<Float> multiplicationConstant(int n, Float value){
		normalizeSize(n);
		constants.set(n, constants.get(n)*value);
		return this;
	}	
	@Override
	public APolynomial<Float> divisionConstant(int n, Float value){
		normalizeSize(n);
		if(value==null || value==0)
			return null;
		constants.set(n, constants.get(n)/value);
		return this;
	}	
	@Override
	public Float convertValue(Number value){
		return value.floatValue();
	}
	@Override
	public Float addition(Float value1, Float value2) {
		if(value1==null || value2==null)
			return null;
		else 
			return value1+value2;
	}
	@Override
	public Float subtraction(Float value1, Float value2) {
		if(value1==null || value2==null)
			return null;
		else 
			return value1-value2;
	}
	@Override
	public Float multiplication(Float value1, Float value2) {
		if(value1==null || value2==null)
			return null;
		else 
			return value1*value2;
	}
	@Override
	public Float division(Float value1, Float value2) {
		if(value1==null || value2==null || value2==0)
			return null;
		else 
			return value1/value2;
	}


}