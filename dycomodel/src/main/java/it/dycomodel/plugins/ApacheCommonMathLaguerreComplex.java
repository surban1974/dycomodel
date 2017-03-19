package it.dycomodel.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.polynomial.APolynomial;

public class ApacheCommonMathLaguerreComplex implements IComputing {
	private static final long serialVersionUID = 1L;
	private Double minConstValue;

	@Override
	public double[] getPolynomialCoeficients(double[] x, double[] y) throws PolynomialConstantsException {
		try{
			if(PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y, true)){
				double[] result = new PolynomialFunctionLagrangeForm(x, y).getCoefficients();
				if(minConstValue!=null){
					for(int i=0;i<result.length;i++){
						if(result[i]<minConstValue)
							result[i]=0;
					}
				}
				return result;
			}
			else
				return new double[0];
		}catch(Exception e){
			throw new PolynomialConstantsException(e)
				.setX(x)
				.setY(y);
		}
	}



	@Override
	public <T extends Number> double[] getPolynomialRoots(APolynomial<T> completePolynomial, IEquation<T> incompleteEquation, Double startPeriod, Double finishPeriod) throws RootSolvingException {
		double[] result = new double[0];
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
		    result = new double[reals.size()];
		    for(int i=0;i<reals.size();i++)
		    	result[i] = reals.get(i).doubleValue();
		    if(result.length==0){
			    for(Complex complex: roots){
			    	if(complex.getImaginary()==0 && complex.getReal()<=max )
			    		reals.add(complex.getReal());
			    }
			    result = new double[reals.size()];
			    for(int i=0;i<reals.size();i++)
			    	result[i] = reals.get(i).doubleValue();
		    	
		    }
		    if(result.length==0){
		    	PolynomialFunction polynomial = new PolynomialFunction(completePolynomial.toDoubleArray());
			    double root = new LaguerreSolver().solve(
			    		100,
			    		polynomial,
			    		min,
						max
				);
			    result = new double[1];	  
			    result[0]=root;
		    }
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


	public ApacheCommonMathLaguerreComplex setMinConstValue(Double minConstValue) {
		this.minConstValue = minConstValue;
		return this;
	}	

}
