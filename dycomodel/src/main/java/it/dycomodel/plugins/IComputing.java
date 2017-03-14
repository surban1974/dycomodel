package it.dycomodel.plugins;

import java.io.Serializable;

import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.polynomial.APolynomial;

public interface IComputing extends Serializable{
	double[] getPolynomialCoeficients(double[] x, double[] y) throws PolynomialConstantsException;
	<T extends Number> double[] getPolynomialRoots(APolynomial<T> completePolynomial, IEquation<T> incompleteEquation, Double startPeriod, Double finishPeriod) throws RootSolvingException;
	Double getMinConstValue();
	IComputing setMinConstValue(Double minConstValue);
}
