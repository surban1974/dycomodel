package it.dycomodel.plugins;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.polynomial.APolynomial;

public class ApacheCommonMathPolynomialFitter implements IComputing {
	private static final long serialVersionUID = 1L;
	private Double minConstValue;

	@Override
	public double[] getPolynomialCoeficients(double[] x, double[] y) throws PolynomialConstantsException {
		try{
			
			
			WeightedObservedPoints obs = new WeightedObservedPoints();
			for(int i=0;i<x.length;i++){
				double xV = x[i];
				double yV = 0;
				if(i<y.length)
					yV = y[i];
				obs.add(xV,yV);
			}
			// Instantiate a third-degree polynomial fitter.
			final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(x.length);

			// Retrieve fitted parameters (coefficients of the polynomial function).
			final double[] result = fitter.fit(obs.toList());
			
			if(minConstValue!=null){
					for(int i=0;i<result.length;i++){
						if(result[i]<minConstValue)
							result[i]=0;
					}
				
				return result;
			}
			else
				return result;
		}catch(Exception e){
			throw new PolynomialConstantsException(e)
				.setX(x)
				.setY(y);
		}
	}


	@Override
	public <T extends Number> double[] getPolynomialRoots(APolynomial<T> completePolynomial, IEquation<T> incompleteEquation, Double startPeriod, Double finishPeriod) throws RootSolvingException {
		double[] result = new double[1];
		double initialDelta = 0;
		double maxInterval = 0;
		if(incompleteEquation!=null){
			initialDelta=incompleteEquation.getInitialDelta();
			maxInterval=incompleteEquation.getMaxInterval();
		}
		try{
			PolynomialFunction polynomial = new PolynomialFunction(completePolynomial.toDoubleArray());
		    double root = new LaguerreSolver().solve(
		    		100,
		    		polynomial,
		    		(startPeriod!=null)
		    			?
		    				startPeriod.doubleValue()-initialDelta
		    			:
		    				initialDelta,
					(finishPeriod!=null)
						?
							finishPeriod.doubleValue()-initialDelta
						:
							startPeriod.doubleValue()-initialDelta + maxInterval);
		    
		    result[0]=root;
		}catch(Exception e){
			throw new RootSolvingException(e)
					.setCompletePolynomial(completePolynomial)
					.setIncompleteEquation(incompleteEquation)
					.setStartPeriod(startPeriod)
					.setFinishPeriod(finishPeriod);
		}
		return result;
	}


	public Double getMinConstValue() {
		return minConstValue;
	}


	public ApacheCommonMathPolynomialFitter setMinConstValue(Double minConstValue) {
		this.minConstValue = minConstValue;
		return this;
	}

}
