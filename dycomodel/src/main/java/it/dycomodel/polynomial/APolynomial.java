package it.dycomodel.polynomial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class APolynomial<T extends Number> implements Serializable{
	private static final long serialVersionUID = 1L;
	protected List<T> constants;


	
	public abstract APolynomial<T> additionConstant(int n, T value);
	public abstract APolynomial<T> subtractionConstant(int n, T value);
	public abstract APolynomial<T> multiplicationConstant(int n, T value);
	public abstract APolynomial<T> divisionConstant(int n, T value);
	public abstract T addition(T value1, T value2);
	public abstract T subtraction(T value1, T value2);
	public abstract T multiplication(T value1, T value2);
	public abstract T division(T value1, T value2);
	
	public abstract T convertValue(Number value);
	
	public APolynomial<T> init(APolynomial<T> arg){
		if(arg==null || arg.getConstants()==null)
			return null;
		normalizeSize(arg.getConstants().size()-1);
		for(int i=0;i<arg.getConstants().size();i++)
			constants.set(i, arg.getConstants().get(i));
			
		return this;
	}
	public APolynomial<T> setConstant(int n, T value){
		normalizeSize(n);
		constants.set(n, value);
		return this;
	}
	public APolynomial<T> addition(APolynomial<T> arg){
		if(arg==null || arg.getConstants()==null)
			return null;
		if(constants==null)
			constants = new ArrayList<T>();
		for(int i=0;i<arg.getConstants().size();i++)
			additionConstant(i, arg.getConstants().get(i));
		return this;
	}
	
	public APolynomial<T> subtraction(APolynomial<T> arg){
		if(arg==null || arg.getConstants()==null)
			return null;
		if(constants==null)
			constants = new ArrayList<T>();
		for(int i=0;i<arg.getConstants().size();i++)
			subtractionConstant(i, arg.getConstants().get(i));
		return this;
	}	

	public APolynomial<T> integral(){
		if(constants==null)
			constants = new ArrayList<T>();
		for(int i=getConstants().size()-1;i>=0;i--){
			setConstant(i+1, getConstants().get(i));
			divisionConstant(i+1, convertValue(i+1));
		}
		if(constants.size()>0)
			setConstant(0, convertValue(0));
		
		return this;
	}	
	
	public T compute(T arg){
		if(constants==null)
			constants = new ArrayList<T>();
		T result = convertValue(0);
		for(int i=0;i<getConstants().size();i++){
			T pow = convertValue(1);
			if(i==1)
				pow=convertValue(arg);
			else if(i>1){
				pow=convertValue(arg);
				for(int k=1;k<i;k++)
					pow=multiplication(pow, arg);
			}			
			result=addition(result,multiplication(pow, getConstants().get(i)));
		}
		return result;
	}	
	
	
	protected APolynomial<T> normalizeSize(int n){
		if(constants==null)
			constants = new ArrayList<T>();
		while(constants.size()<(n+1)){
			constants.add(convertValue(0));
		}
		return this;
	}
	public List<T> getConstants() {
		return constants;
	}

	public void setConstants(List<T> constants) {
		this.constants = constants;
	}
	
	public double[] toDoubleArray(){
		if(constants==null) return new double[0];
		double[] result = new double[constants.size()];
		for(int i=0;i<constants.size();i++)
			result[i]=constants.get(i).doubleValue();
		return result;
	}
	
	public String toString(){
		String result="";
		for(int i=0;i<constants.size();i++){
			double roundConst = new BigDecimal(constants.get(i).toString()).divide(new BigDecimal(1),6,BigDecimal.ROUND_HALF_UP).doubleValue();
			if(roundConst>0)
				result+=" +"+new BigDecimal(constants.get(i).toString()).divide(new BigDecimal(1),6,BigDecimal.ROUND_HALF_UP)+"*x^"+i;
			else if(roundConst<0)
				result+=" "+new BigDecimal(constants.get(i).toString()).divide(new BigDecimal(1),6,BigDecimal.ROUND_HALF_UP)+"*x^"+i;
		}
		return result;
	}
	
}