package it.dycomodel.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.polynomial.APolynomial;

public class ComputingLaguerreComplex extends ApacheCommonMath implements IComputing {
	private static final long serialVersionUID = 1L;


	@Override
	public <T extends Number> T[] getPolynomialRoots(APolynomial<T> completePolynomial, IEquation<T> incompleteEquation, T startPeriod, T finishPeriod, APolynomial<T> adapter) throws RootSolvingException {
		T[] result = adapter.initArray(0);
		double initialDelta = 0;
		double maxInterval = 0;
		if(incompleteEquation!=null){
			initialDelta=incompleteEquation.getInitialDelta();
			maxInterval=incompleteEquation.getMaxInterval();
		}
		try{
		
			double min = (startPeriod!=null)
	    				?
		    				startPeriod.doubleValue()-initialDelta
		    			:
		    				incompleteEquation.getInitialDelta();
			double max = (finishPeriod!=null)
						?
							finishPeriod.doubleValue()-initialDelta
						:
							startPeriod.doubleValue()-initialDelta+maxInterval;
							
		    Complex[] roots = new LaguerreSolver().solveAllComplex(completePolynomial.toDoubleArray(), min, 100);
		    List<Double> reals = new ArrayList<Double>();
		    for(Complex complex: roots){
		    	if(complex.getImaginary()==0 && complex.getReal()>=min && complex.getReal()<=max && complex.getReal()!=startPeriod.doubleValue())
		    		reals.add(complex.getReal());
		    }
		    result = adapter.initArray(reals.size());
		    for(int i=0;i<reals.size();i++)
		    	result[i] = adapter.convertValue(reals.get(i));
		    if(result.length==0){
			    for(Complex complex: roots){
			    	if(complex.getImaginary()==0 && complex.getReal()<=max )
			    		reals.add(complex.getReal());
			    }
			    result = adapter.initArray(reals.size());
			    for(int i=0;i<reals.size();i++)
			    	result[i] = adapter.convertValue(reals.get(i));
		    	
		    }
		    if(result.length==0){
		    	PolynomialFunction polynomial = new PolynomialFunction(completePolynomial.toDoubleArray());
			    double root = new LaguerreSolver().solve(
			    		1000,
			    		polynomial,
			    		min,
						max
				);
			    result = adapter.initArray(1);	  
			    result[0]=adapter.convertValue(root);
		    }
		}catch(Exception e){
			throw new RootSolvingException(e)
					.setCompletePolynomial(completePolynomial)
					.setIncompleteEquation(incompleteEquation)
					.setStartPeriod(startPeriod.doubleValue())
					.setFinishPeriod(finishPeriod.doubleValue());
		}
		return result;

	}


}
