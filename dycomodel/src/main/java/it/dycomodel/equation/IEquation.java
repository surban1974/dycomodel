package it.dycomodel.equation;


import it.dycomodel.plugins.IComputing;
import it.dycomodel.polynomial.APolynomial;


public interface IEquation<T extends Number> {

	APolynomial<T> setConstant(APolynomial<T> polynomial, int n, double value);

	IEquation<T> setAveragePoints(double[][] forecastingConsumption, double[][] forecastingStock) throws Exception;

	T compute(APolynomial<T> polynomial, T value);

	IEquation<T> makeIncompleteEquation() throws Exception;

	double solveEquation(T initialQuantity, Double startPeriod, Double finishPeriod) throws Exception;

	APolynomial<T> getConsumption();

	APolynomial<T> getSecureStock();

	APolynomial<T> getConsumptionIntegral();
	
	APolynomial<T> getSecureStockIntegral();

	double getInitialDelta();

	APolynomial<T> getIncompleteEquation();

	IEquation<T> setComputingPlugin(IComputing computingPlugin);
	
	double getMaxInterval();
	
	APolynomial<T> initPolynomial();


}