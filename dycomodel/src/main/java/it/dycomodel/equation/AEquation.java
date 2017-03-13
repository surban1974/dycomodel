package it.dycomodel.equation;

import java.io.Serializable;

import it.dycomodel.plugins.ApacheCommonMathLaguerre;
import it.dycomodel.plugins.IComputing;
import it.dycomodel.polynomial.APolynomial;


public abstract class AEquation<T extends Number> implements Serializable, IEquation<T>{
	private static final long serialVersionUID = 1L;


	protected APolynomial<T> consumption;
	protected APolynomial<T> consumptionIntegral;
	protected APolynomial<T> secureStock;
	protected APolynomial<T> secureStockIntegral;
	protected APolynomial<T> incompleteEquation;
	protected double initialDelta;
	protected double maxInterval;

	protected IComputing computingPlugin;
	
	public AEquation(){
		super();
		consumption = initPolynomial();
		consumptionIntegral = initPolynomial();
		secureStock = initPolynomial();
		secureStockIntegral = initPolynomial();
		incompleteEquation = initPolynomial();		
	}
	
	public abstract APolynomial<T> initPolynomial();
	public abstract APolynomial<T> setConstant(APolynomial<T> polynomial, int n, double value);
	
	public IEquation<T> setAveragePoints(double[][] forecastedConsumption, double[][] forecastedStock) throws Exception{
		if(computingPlugin==null)
			computingPlugin = new ApacheCommonMathLaguerre();
		
		double[] xc = new double[forecastedConsumption.length];
		double[] yc = new double[forecastedConsumption.length];
		if(initialDelta==0 && forecastedConsumption.length>0)
			initialDelta = forecastedConsumption[0][0];
		for(int i=0;i<forecastedConsumption.length;i++){
			if(initialDelta>forecastedConsumption[i][0])
				initialDelta = forecastedConsumption[i][0];
			if(maxInterval<forecastedConsumption[i][0])
				maxInterval= forecastedConsumption[i][0];
			xc[i]=forecastedConsumption[i][0];
			yc[i]=forecastedConsumption[i][1];
		}
		
		double[] xs = new double[forecastedStock.length];
		double[] ys = new double[forecastedStock.length];
		for(int i=0;i<forecastedStock.length;i++){
			xs[i]=forecastedStock[i][0];
			ys[i]=forecastedStock[i][1];
		}

		if(initialDelta>0){
			for(int i=0;i<xc.length;i++)
				xc[i]=xc[i]-initialDelta;
			for(int i=0;i<xs.length;i++)
				xs[i]=xs[i]-initialDelta;
		}
		
		double[] coeficients = computingPlugin.getPolynomialCoeficients(xc, yc);
		if(coeficients!=null){
			for(int i=0;i<coeficients.length;i++)
				setConstant(consumption, i, new Double(coeficients[i]));
			
			consumptionIntegral.init(consumption).integral();
		}
		
		coeficients = computingPlugin.getPolynomialCoeficients(xs, ys);
		if(coeficients!=null){
			for(int i=0;i<coeficients.length;i++)
				setConstant(secureStock, i, new Double(coeficients[i]));
			
		}
		return this;
	}
	
	public T compute(APolynomial<T> polynomial, T value){
		if(polynomial==null || value==null)
			return null;
		return 
			polynomial.compute(polynomial.subtraction(value,polynomial.convertValue(initialDelta)));
	}

	public IEquation<T> makeIncompleteEquation() throws Exception{

		incompleteEquation = initPolynomial()
					.subtraction(secureStock)
					.subtraction(getConsumptionIntegral())
					;
		return this;
	}
	
	public double solveEquation(T initialQuantity, Double startPeriod, Double finishPeriod) throws Exception{
		if(computingPlugin!=null){
			APolynomial<T> computedEquation = 
					initPolynomial()
					.init(incompleteEquation)
					.addition(
							initPolynomial()
							.setConstant(0, initialQuantity)
					)					
					.addition(
							initPolynomial()
								.setConstant(
										0,
										compute(
											getConsumptionIntegral(),
											getConsumptionIntegral().convertValue(startPeriod)
										)
								)
					)
					;
			double[] roots = computingPlugin.getPolynomialRoots(computedEquation,this,startPeriod,finishPeriod);
			if(roots.length>0)
				return roots[0]+initialDelta;			
		}
		return 0;
	}
	
	public APolynomial<T> getConsumption() {
		return consumption;
	}

	public APolynomial<T> getSecureStock() {
		return secureStock;
	}


	public APolynomial<T> getConsumptionIntegral() {
		return consumptionIntegral;
	}

	public APolynomial<T> getSecureStockIntegral() {
		return secureStockIntegral;
	}

	public double getInitialDelta() {
		return initialDelta;
	}

	public APolynomial<T> getIncompleteEquation() {
		return incompleteEquation;
	}

	public IEquation<T> setComputingPlugin(IComputing computingPlugin) {
		this.computingPlugin = computingPlugin;
		return this;
	}

	public double getMaxInterval() {
		return maxInterval;
	}


}
