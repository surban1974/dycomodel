package it.dycomodel.plugins;

import java.io.Serializable;
import java.util.SortedMap;

import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.polynomial.APolynomial;

public interface IComputing extends Serializable{
	<T extends Number> SortedMap<T, T[]> getPolynomialCoeficients(T[] x, T[] y, APolynomial<T> adapter) throws PolynomialConstantsException;
	<T extends Number> T[] getPolynomialRoots(APolynomial<T> completePolynomial, IEquation<T> incompleteEquation, T startPeriod, T finishPeriod, APolynomial<T> adapter) throws RootSolvingException;
}
