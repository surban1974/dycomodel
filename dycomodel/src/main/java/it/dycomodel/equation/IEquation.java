package it.dycomodel.equation;


import java.util.SortedMap;

import org.w3c.dom.Node;

import it.dycomodel.plugins.IComputing;
import it.dycomodel.polynomial.APolynomial;


public interface IEquation<T extends Number> {
	
	
	
	final static int COMPUTE_CONSUMPTION = 1;
	final static int COMPUTE_CONSUMPTION_INTEGRAL = 2;
	final static int COMPUTE_STOCK = 3;
	final static int COMPUTE_STOCK_INTEGRAL = 4;

	IEquation<T> initEquation();
	
	APolynomial<T> setConstant(APolynomial<T> polynomial, int n, T value);

	IEquation<T> setAveragePoints(T[][] forecastingConsumption, T[][] forecastingStock) throws Exception;
	
	IEquation<T> init(T[] coeficientsConsumption, T[] coeficientsStock) throws Exception;

	T compute(APolynomial<T> polynomial, T value);
	
	T compute(int type, T value);
	
	T computeConsumption(T initialQuantity, T startPeriod, T finishPeriod);

	IEquation<T> makeIncompleteEquation() throws Exception;

	T solveEquation(T initialQuantity, T startPeriod, T finishPeriod) throws Exception;

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
	
	IEquation<T> init(Node node)throws Exception;


}