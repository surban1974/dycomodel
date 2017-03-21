package it.dycomodel.equation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.dycomodel.plugins.ComputingLaguerre;
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
	protected boolean global=true;
	protected APolynomial<T> adapter;

	protected IComputing computingPlugin;
	protected SortedMap<T, IEquation<T>> segmentEquations;
	
	public AEquation(){
		super();
		consumption = initPolynomial();
		consumptionIntegral = initPolynomial();
		secureStock = initPolynomial();
		secureStockIntegral = initPolynomial();
		incompleteEquation = initPolynomial();	
		adapter = initPolynomial();
	}
	
	public abstract IEquation<T> initEquation();
	public abstract APolynomial<T> initPolynomial();
	public abstract APolynomial<T> setConstant(APolynomial<T> polynomial, int n, T value);
	
	public IEquation<T> setAveragePoints(T[][] forecastedConsumption, T[][] forecastedStock) throws Exception{
		if(computingPlugin==null)
			computingPlugin = new ComputingLaguerre();

		
		T[] xc = adapter.initArray(forecastedStock.length);
		T[] yc = adapter.initArray(forecastedStock.length);
		if(initialDelta==0 && forecastedConsumption.length>0)
			initialDelta = forecastedConsumption[0][0].doubleValue();
		for(int i=0;i<forecastedConsumption.length;i++){
			if(initialDelta>forecastedConsumption[i][0].doubleValue())
				initialDelta = forecastedConsumption[i][0].doubleValue();
			if(maxInterval<forecastedConsumption[i][0].doubleValue())
				maxInterval= forecastedConsumption[i][0].doubleValue();
			xc[i]=forecastedConsumption[i][0];
			yc[i]=forecastedConsumption[i][1];
		}
		
		T[] xs = adapter.initArray(forecastedStock.length);
		T[] ys = adapter.initArray(forecastedStock.length);
		for(int i=0;i<forecastedStock.length;i++){
			xs[i]=forecastedStock[i][0];
			ys[i]=forecastedStock[i][1];
		}

		if(initialDelta>0){
			for(int i=0;i<xc.length;i++)
				xc[i]= adapter.subtraction(xc[i],adapter.convertValue(initialDelta));
			for(int i=0;i<xs.length;i++)
				xs[i]=adapter.subtraction(xs[i],adapter.convertValue(initialDelta));
		}
		
		
		SortedMap<T, T[]> allCoeficients = computingPlugin.getPolynomialCoeficients(xc, yc, adapter);
		if(allCoeficients!=null && allCoeficients.size()>1){
			global=false;
			segmentEquations = new TreeMap<T, IEquation<T>>();
			for(Map.Entry<T, T[]> entry : allCoeficients.entrySet()){
				IEquation<T> segmentE = 
						initEquation()
						.setComputingPlugin(this.computingPlugin)
						.init(entry.getValue(), null);
				segmentEquations.put(entry.getKey(), segmentE);
			}

			allCoeficients = computingPlugin.getPolynomialCoeficients(xs, ys, adapter);
			for(Map.Entry<T, T[]> entry : allCoeficients.entrySet()){
				IEquation<T> segmentE = segmentEquations.get(entry.getKey());
				if(segmentE!=null)
					segmentE.init(null, entry.getValue());

			}			
			
		}else{
			T[] coeficientsConsumption = adapter.initArray(0);
			if(allCoeficients!=null && allCoeficients.size()>0)
				coeficientsConsumption =allCoeficients.get(allCoeficients.firstKey());
			
			T[] coeficientsStock = adapter.initArray(0);
			allCoeficients = computingPlugin.getPolynomialCoeficients(xs, ys, adapter);
			if(allCoeficients!=null && allCoeficients.size()>0)
				coeficientsStock =allCoeficients.get(allCoeficients.firstKey());
			
			init(coeficientsConsumption, coeficientsStock);
		}
		
		
		return this;
	}
	
	public IEquation<T> init(T[] coeficientsConsumption, T[] coeficientsStock) throws Exception{


		if(coeficientsConsumption!=null){
			for(int i=0;i<coeficientsConsumption.length;i++)
				setConstant(consumption, i, coeficientsConsumption[i]);			
			consumptionIntegral.init(consumption).integral();
		}
		
		if(coeficientsStock!=null){
			for(int i=0;i<coeficientsStock.length;i++)
				setConstant(secureStock, i, coeficientsStock[i]);
			
		}
		return this;
	}
	
	public T compute(APolynomial<T> polynomial, T value){
		if(polynomial==null || value==null)
			return null;
		return 
			polynomial.compute(polynomial.subtraction(value,polynomial.convertValue(initialDelta)));
	}
	
	public T compute(int type, T value){
		if(global){
			if(type==COMPUTE_CONSUMPTION)
				return compute(getConsumption(),value);
			else if(type==COMPUTE_CONSUMPTION_INTEGRAL)
				return compute(getConsumptionIntegral(),value);
			else if(type==COMPUTE_STOCK)
				return compute(getSecureStock(),value);
			else if(type==COMPUTE_STOCK_INTEGRAL)
				return compute(getSecureStockIntegral(),value);
		}else{
			if(segmentEquations!=null){
				IEquation<T> forCompute = null;
				for(Map.Entry<T, IEquation<T>> entry : segmentEquations.entrySet()){
					if(value.doubleValue()< entry.getKey().doubleValue() && forCompute!=null)
						return forCompute.compute(type, value);
					forCompute = entry.getValue();	
				}
				if(forCompute!=null)
					return forCompute.compute(type, value);
			}

		}
		return null;
	}
	
	
	public T computeConsumption(T initialQuantity, T startPeriod, T finishPeriod){
		if(global){
			return adapter
					.subtraction(
							adapter.convertValue(initialQuantity),
							adapter
								.subtraction(
									compute(COMPUTE_CONSUMPTION_INTEGRAL, adapter.convertValue(finishPeriod)),
									compute(COMPUTE_CONSUMPTION_INTEGRAL,adapter.convertValue(startPeriod))
								)
							);
		}else{
			if(segmentEquations!=null && segmentEquations.size()>0){
				T segmentStartPeriod = startPeriod;
				T segmentFinishPeriod = finishPeriod;
				Iterator<T> itr = segmentEquations.keySet().iterator();
				T firstSegmentKey = null;
				T secondSegmentKey = itr.next();
				while(startPeriod.doubleValue()>secondSegmentKey.doubleValue() && itr.hasNext()){
					firstSegmentKey = secondSegmentKey;
					secondSegmentKey = itr.next();
				}
				
				T integralAggregator = adapter.convertValue(0);


				while(firstSegmentKey!=null){				
					if(secondSegmentKey!=null){
						if(secondSegmentKey.doubleValue()>finishPeriod.doubleValue()){
							segmentFinishPeriod = finishPeriod;
							secondSegmentKey=null;
						}else
							segmentFinishPeriod = secondSegmentKey;
					}else
						segmentFinishPeriod = finishPeriod;
					
					IEquation<T> segmentEquation = segmentEquations.get(firstSegmentKey);
					
					integralAggregator =
							adapter
								.addition(
									integralAggregator,
									adapter
										.subtraction(
											compute(segmentEquation.getConsumptionIntegral(), adapter.convertValue(segmentFinishPeriod)),
											compute(segmentEquation.getConsumptionIntegral(),adapter.convertValue(segmentStartPeriod))
										)
								);
					
	
					if(secondSegmentKey!=null)
						segmentStartPeriod = secondSegmentKey;
						
					firstSegmentKey = secondSegmentKey;
					secondSegmentKey = null;	
					if(itr.hasNext())
						secondSegmentKey = itr.next();
					
				}

				return adapter
						.subtraction(
								adapter.convertValue(initialQuantity),
								integralAggregator
								);
			}

		}
		return null;
	}	

	public IEquation<T> makeIncompleteEquation() throws Exception{
		if(global)
			incompleteEquation = initPolynomial()
						.subtraction(secureStock)
						.subtraction(getConsumptionIntegral())
						;
		else{
			for(Map.Entry<T, IEquation<T>> entry : segmentEquations.entrySet())
				entry.getValue().makeIncompleteEquation();
			
		}
		return this;
	}
	
	public T solveEquation(T initialQuantity, T startPeriod, T finishPeriod) throws Exception{
		if(global){
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
				T[] roots = computingPlugin.getPolynomialRoots(computedEquation,this,startPeriod,finishPeriod, adapter);
				if(roots.length>0)				
					return  adapter.addition(roots[0], adapter.convertValue(initialDelta));	
	
			}
			return adapter.convertValue(-1);
		}else{
			if(segmentEquations!=null && segmentEquations.size()>0){
				T segmentInitialQuantity  = initialQuantity;
				T segmentStartPeriod = startPeriod;
				T segmentFinishPeriod = finishPeriod;
				Iterator<T> itr = segmentEquations.keySet().iterator();
				T firstSegmentKey = null;
				T secondSegmentKey = itr.next();
				while(startPeriod.doubleValue()>secondSegmentKey.doubleValue() && itr.hasNext()){
					firstSegmentKey = secondSegmentKey;
					secondSegmentKey = itr.next();
				}
				


				while(firstSegmentKey!=null){
				
					
					
					if(secondSegmentKey!=null)
						segmentFinishPeriod = secondSegmentKey;
					else
						segmentFinishPeriod = finishPeriod;
					
					IEquation<T> segmentEquation = segmentEquations.get(firstSegmentKey);
					T solved = segmentEquation.solveEquation(segmentInitialQuantity,segmentStartPeriod, segmentFinishPeriod);
					if(!adapter.equal(solved, adapter.convertValue(-1)))
						return solved;
					
					else{
						segmentInitialQuantity = computeConsumption(segmentInitialQuantity, segmentStartPeriod, segmentFinishPeriod);	
						if(secondSegmentKey!=null)
							segmentStartPeriod = secondSegmentKey;
						firstSegmentKey = secondSegmentKey;
						secondSegmentKey = null;	
						if(itr.hasNext())
							secondSegmentKey = itr.next();
					}

				}
				return adapter.convertValue(-1);
			}else
				return adapter.convertValue(-1);

		}
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

	public boolean isGlobal() {
		return global;
	}

	public SortedMap<T, IEquation<T>> getSegmentEquations() {
		return segmentEquations;
	}


}
