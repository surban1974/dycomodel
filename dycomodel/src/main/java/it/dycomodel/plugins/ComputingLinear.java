package it.dycomodel.plugins;

import java.util.SortedMap;
import java.util.TreeMap;

import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.polynomial.APolynomial;

public class ComputingLinear extends ApacheCommonMath implements IComputing {
	private static final long serialVersionUID = 1L;

	@Override
	public <T extends Number> SortedMap<T, T[]> getPolynomialCoeficients(T[] x, T[] y, APolynomial<T> adapter)
			throws PolynomialConstantsException {
		try{
			if(x.length<2)
				return null;
			SortedMap<T, T[]> segments = new TreeMap<T, T[]>();
			for(int i=1;i<x.length;i++){
				double x1=x[i-1].doubleValue();
				double x2=x[i].doubleValue();
				double y1=y[i-1].doubleValue();
				double y2=y[i].doubleValue();	
				double m = (y2-y1)/(x2-x1);
				T[] segment = adapter.initArray(2);
				segment[0] = adapter.convertValue(y1-m*x1);
				segment[1] = adapter.convertValue(m);
				segments.put(adapter.convertValue(x1), segment);
			}
			return segments;
		}catch(Exception e){
			throw new PolynomialConstantsException(e)
				.setX(adapter.copyTodoubleArray(x))
				.setY(adapter.copyTodoubleArray(y));
		}
	}







}
