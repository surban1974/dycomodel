package it.dycomodel.equation;


import java.util.SortedMap;

import org.w3c.dom.Node;

import it.dycomodel.exceptions.EquationException;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.plugins.IComputing;
import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.utils.ILogger;


public interface IEquation<T extends Number> {
	
	
	
	static final int COMPUTE_CONSUMPTION = 1;
	static final int COMPUTE_CONSUMPTION_INTEGRAL = 2;
	static final int COMPUTE_STOCK = 3;
	static final int COMPUTE_STOCK_INTEGRAL = 4;

	IEquation<T> initEquation();
	
	APolynomial<T> setConstant(APolynomial<T> polynomial, int n, T value);

	IEquation<T> setAveragePoints(T[][] forecastingConsumption, T[][] forecastingStock) throws EquationException,PolynomialConstantsException;
	
	IEquation<T> init(T[] coeficientsConsumption, T[] coeficientsStock) throws EquationException;

	T compute(APolynomial<T> polynomial, T value);
	
	T compute(int type, T value);
	
	T computeConsumption(T initialQuantity, T startPeriod, T finishPeriod);

	IEquation<T> makeIncompleteEquation() throws EquationException;

	T solveEquation(T initialQuantity, T startPeriod, T finishPeriod) throws EquationException,RootSolvingException;

	APolynomial<T> getConsumption();

	APolynomial<T> getSecureStock();

	APolynomial<T> getConsumptionIntegral();
	
	APolynomial<T> getSecureStockIntegral();

	double getInitialDelta();

	APolynomial<T> getIncompleteEquation();

	IEquation<T> setComputingPlugin(IComputing computingPlugin);
	
	double getMaxInterval();
	
	APolynomial<T> initPolynomial();
	
	SortedMap<T, IEquation<T>> getSegmentEquations();
	
	boolean isGlobal();
	
	String toXml(int level);
	
	IEquation<T> init(Node node)throws EquationException;
	
	IEquation<T> setLogger(ILogger logger);


}