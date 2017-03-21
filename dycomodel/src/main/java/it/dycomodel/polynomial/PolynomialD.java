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
	public Double[] initArray(int length){
		return new Double[length];
	}	
	@Override
	public Double[][] initArray(int length1, int length2) {
		return new Double[length1][length2];
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

	@Override
	public Double floorPart(Double value1) {
		if(value1==null)
			return 0d;
		return new Double(value1.longValue());
	}

	@Override
	public Double fractionalPart(Double value1) {
		if(value1==null)
			return 0d;
		return value1 - floorPart(value1);
	}

	@Override
	public boolean equal(Double value1, Double value2) {
		if(value1==null || value2==null)
			return false;
		else if(value1.doubleValue()==value2.doubleValue())
			return true;
		else return false;					
	}




}
