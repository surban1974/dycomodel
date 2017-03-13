package it.dycomodel.polynomial;

import java.util.ArrayList;

public class PolynomialD extends APolynomial<Double>{
	private static final long serialVersionUID = 1L;

	public PolynomialD(){
		super();
		constants = new ArrayList<Double>();
	}
	
	public PolynomialD(double[] arrayd){
		super();
		constants = new ArrayList<Double>();
		for(double d : arrayd) 
			constants.add(d);
	}

	@Override
	public APolynomial<Double> additionConstant(int n, Double value){
		normalizeSize(n);
		constants.set(n, constants.get(n)+value);
		return this;
	}
	@Override
	public APolynomial<Double> subtractionConstant(int n, Double value){
		normalizeSize(n);
		constants.set(n, constants.get(n)-value);
		return this;
	}	
	@Override
	public APolynomial<Double> multiplicationConstant(int n, Double value){
		normalizeSize(n);
		constants.set(n, constants.get(n)*value);
		return this;
	}	
	@Override
	public APolynomial<Double> divisionConstant(int n, Double value){
		normalizeSize(n);
		if(value==null || value==0)
			return null;
		constants.set(n, constants.get(n)/value);
		return this;
	}	
	@Override
	public Double convertValue(Number value){
		return value.doubleValue();
	}
	@Override
	public Double addition(Double value1, Double value2) {
		if(value1==null || value2==null)
			return null;
		else 
			return value1+value2;
	}
	@Override
	public Double subtraction(Double value1, Double value2) {
		if(value1==null || value2==null)
			return null;
		else 
			return value1-value2;
	}
	@Override
	public Double multiplication(Double value1, Double value2) {
		if(value1==null || value2==null)
			return null;
		else 
			return value1*value2;
	}
	@Override
	public Double division(Double value1, Double value2) {
		if(value1==null || value2==null || value2==0)
			return null;
		else 
			return value1/value2;
	}


}
