package it.dycomodel.plugins;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.polynomial.APolynomial;

public class ComputingPolynomialFitter extends ApacheCommonMath implements IComputing {
	private static final long serialVersionUID = 1L;

	private int degree;

	@Override
	public <T extends Number> SortedMap<T, T[]> getPolynomialCoeficients(T[] x, T[] y, APolynomial<T> adapter) throws PolynomialConstantsException {
		try{
			if(x.length==0)
				return null;			
			
			WeightedObservedPoints obs = new WeightedObservedPoints();
			for(int i=0;i<x.length;i++){
				double xV = x[i].doubleValue();
				double yV = 0;
				if(i<y.length)
					yV = y[i].doubleValue();
				obs.add(xV,yV);
			}
			// Instantiate a third-degree polynomial fitter.
			if(degree==0)
				degree = x.length;

			final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
			

			// Retrieve fitted parameters (coefficients of the polynomial function).
			final double[] result = fitter.fit(obs.toList());
			
			
			final T[] convResult = adapter.initArray(result.length);
			for(int i=0;i<result.length;i++)
				convResult[i]=adapter.convertValue(result[i]);
			final T firstX = x[0];
			return
					new TreeMap<T, T[]>() {
						private static final long serialVersionUID = 1L;
						{
							put(firstX,convResult);
						}
					};	

		}catch(Exception e){
			throw new PolynomialConstantsException(e)
				.setX(adapter.copyTodoubleArray(x))
				.setY(adapter.copyTodoubleArray(y));
		}
	}






	public int getDegree() {
		return degree;
	}


	public ComputingPolynomialFitter setDegree(int degree) {
		this.degree = degree;
		return this;
	}

}
