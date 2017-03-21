package it.dycomodel.plugins;


import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.exception.TooManyEvaluationsException;

import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.polynomial.APolynomial;

public abstract class ApacheCommonMath implements IComputing {
	private static final long serialVersionUID = 1L;

	

	
	@Override
	public <T extends Number> SortedMap<T, T[]> getPolynomialCoeficients(T[] x, T[] y, APolynomial<T> adapter) throws PolynomialConstantsException {
		try{
			if(x.length==0)
				return null;
			if(PolynomialFunctionLagrangeForm.verifyInterpolationArray(adapter.copyTodoubleArray(x), adapter.copyTodoubleArray(y), true)){
				double[] result = new PolynomialFunctionLagrangeForm(adapter.copyTodoubleArray(x), adapter.copyTodoubleArray(y)).getCoefficients(); 

				final T[] convResult = adapter.copyToTArray(result);
				final T firstX = x[0];
				return
						new TreeMap<T, T[]>() {
							private static final long serialVersionUID = 1L;
							{
								put(firstX,convResult);
							}
						};	

			}
			else
				return null;
		}catch(Exception e){
			throw new PolynomialConstantsException(e)
				.setX(adapter.copyTodoubleArray(x))
				.setY(adapter.copyTodoubleArray(y));
		}
	}	
	
	@Override
	public <T extends Number> T[] getPolynomialRoots(APolynomial<T> completePolynomial, IEquation<T> incompleteEquation, T startPeriod, T finishPeriod, APolynomial<T> adapter) throws RootSolvingException {
//		double[] result = new double[0];
		double initialDelta = 0;
		double maxInterval = 0;
		if(incompleteEquation!=null){
			initialDelta=incompleteEquation.getInitialDelta();
			maxInterval=incompleteEquation.getMaxInterval();
		}
		try{
			PolynomialFunction polynomial = new PolynomialFunction(completePolynomial.toDoubleArray());
			int evaluation = 100;
			while(evaluation<10001){
				try{
				    double root = new LaguerreSolver().solve(
				    		evaluation,
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
				    
				    T[] result = adapter.initArray(1);
				    result[0] = adapter.convertValue(root);
				    return result;
				}catch(TooManyEvaluationsException tmee){
					evaluation=evaluation*10;
				}catch (Exception e) {
					return adapter.initArray(0);
				}
			}
		}catch(Exception e){
			throw new RootSolvingException(e)
					.setCompletePolynomial(completePolynomial)
					.setIncompleteEquation(incompleteEquation)
					.setStartPeriod(startPeriod.doubleValue())
					.setFinishPeriod(finishPeriod.doubleValue());
		}
		return adapter.initArray(0);
	}


}
