package it.dycomodel.equation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.dycomodel.exceptions.EquationException;
import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.exceptions.RootSolvingException;
import it.dycomodel.plugins.ComputingLaguerre;
import it.dycomodel.plugins.IComputing;
import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.utils.ILogger;
import it.dycomodel.utils.Normalizer;


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
	protected ILogger logger;
	
	public AEquation(){
		super();
		consumption = initPolynomial();
		consumptionIntegral = initPolynomial();
		secureStock = initPolynomial();
		secureStockIntegral = initPolynomial();
		incompleteEquation = initPolynomial();	
		adapter = initPolynomial();
	}

	
	public IEquation<T> setAveragePoints(T[][] forecastedConsumption, T[][] forecastedStock) throws EquationException, PolynomialConstantsException{
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
			segmentEquations = new TreeMap<>();
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
	
	public IEquation<T> init(T[] coeficientsConsumption, T[] coeficientsStock) throws EquationException{


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

	public IEquation<T> makeIncompleteEquation() throws EquationException{
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
	
	public T solveEquation(T initialQuantity, T startPeriod, T finishPeriod) throws EquationException, RootSolvingException{
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
					if(!adapter.identic(solved, adapter.convertValue(-1)))
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
	
	public String toXml(int level){
		StringBuilder result=new StringBuilder();
		result.append(Normalizer.spaces(level)+"<equation provider=\""+this.getClass().getName()+"\">\n");
		if(consumption!=null){
			result.append(Normalizer.spaces(level+1)+"<consumption>\n");
			result.append(consumption.toXml(level+2));
			result.append(Normalizer.spaces(level+1)+"</consumption>\n");
		}
		if(consumptionIntegral!=null){
			result.append(Normalizer.spaces(level+1)+"<consumptionIntegral>\n");
			result.append(consumptionIntegral.toXml(level+2));
			result.append(Normalizer.spaces(level+1)+"</consumptionIntegral>\n");
		}		
		if(secureStock!=null){
			result.append(Normalizer.spaces(level+1)+"<secureStock>\n");
			result.append(secureStock.toXml(level+2));
			result.append(Normalizer.spaces(level+1)+"</secureStock>\n");
		}	
		if(secureStockIntegral!=null){
			result.append(Normalizer.spaces(level+1)+"<secureStockIntegral>\n");
			result.append(secureStockIntegral.toXml(level+2));
			result.append(Normalizer.spaces(level+1)+"</secureStockIntegral>\n");
		}
		if(incompleteEquation!=null){
			result.append(Normalizer.spaces(level+1)+"<incompleteEquation>\n");
			result.append(incompleteEquation.toXml(level+2));
			result.append(Normalizer.spaces(level+1)+"</incompleteEquation>\n");
		}
		result.append(Normalizer.spaces(level+1)+"<initialDelta>"+initialDelta+"</initialDelta>\n");
		result.append(Normalizer.spaces(level+1)+"<maxInterval>"+maxInterval+"</maxInterval>\n");
		result.append(Normalizer.spaces(level+1)+"<global>"+global+"</global>\n");
		
		if(adapter!=null){
			result.append(Normalizer.spaces(level+1)+"<adapter>\n");
			result.append(adapter.toXml(level+2));
			result.append(Normalizer.spaces(level+1)+"</adapter>\n");
		}
		if(computingPlugin!=null)
			result.append(Normalizer.spaces(level+1)+"<computingPlugin>"+computingPlugin.getClass().getName()+"</computingPlugin>\n");
		if(segmentEquations!=null){
			result.append(Normalizer.spaces(level+1)+"<segments>\n");
			for(Map.Entry<T, IEquation<T>> entry : segmentEquations.entrySet()){
				result.append(Normalizer.spaces(level+2)+"<segment>\n");
				result.append(Normalizer.spaces(level+3)+"<position>"+entry.getKey().doubleValue()+"</position>\n");
				result.append(entry.getValue().toXml(level+3));	
				result.append(Normalizer.spaces(level+2)+"</segment>\n");
			}
			
			result.append(Normalizer.spaces(level+1)+"</segments>\n");
		}
		result.append(Normalizer.spaces(level)+"</equation>\n");
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	public IEquation<T> init(Node node) throws EquationException{

		
		final String constNodeProvider = "provider";
		NodeList list = node.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node childNode = list.item(i);
			if(childNode.getNodeType()== Node.ELEMENT_NODE){				
				if(childNode.getNodeName().equalsIgnoreCase("initialDeltaDate")){
					try{
						this.initialDelta = Double.valueOf(childNode.getFirstChild().getNodeValue());
					}catch(Exception e){
						writeLog(e);
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("maxInterval")){
					try{
						this.maxInterval = Double.valueOf(childNode.getFirstChild().getNodeValue());
					}catch(Exception e){
						writeLog(e);
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("global")){
					try{
						this.global = Boolean.valueOf(childNode.getFirstChild().getNodeValue());
					}catch(Exception e){
						writeLog(e);
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("computingPlugin")){
					try{
						computingPlugin = Class.forName(childNode.getFirstChild().getNodeValue()).asSubclass(IComputing.class).newInstance();
					}catch(Exception e){
						writeLog(e);
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("consumption")){
					for(int j=0;j<childNode.getChildNodes().getLength();j++){
						if(childNode.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								consumption = Class.forName(childNode.getChildNodes().item(j).getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(APolynomial.class).newInstance();
								consumption.init(childNode.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								writeLog(e);
							}
						}
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("consumptionIntegral")){
					for(int j=0;j<childNode.getChildNodes().getLength();j++){
						if(childNode.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								consumptionIntegral = Class.forName(childNode.getChildNodes().item(j).getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(APolynomial.class).newInstance();
								consumptionIntegral.init(childNode.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								writeLog(e);
							}
						}
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("secureStock")){
					for(int j=0;j<childNode.getChildNodes().getLength();j++){
						if(childNode.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								secureStock = Class.forName(childNode.getChildNodes().item(j).getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(APolynomial.class).newInstance();
								secureStock.init(childNode.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								writeLog(e);
							}
						}
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("secureStockIntegral")){
					for(int j=0;j<childNode.getChildNodes().getLength();j++){
						if(childNode.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								secureStockIntegral = Class.forName(childNode.getChildNodes().item(j).getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(APolynomial.class).newInstance();
								secureStockIntegral.init(childNode.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								writeLog(e);
							}
						}
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("incompleteEquation")){
					for(int j=0;j<childNode.getChildNodes().getLength();j++){
						if(childNode.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								incompleteEquation = Class.forName(childNode.getChildNodes().item(j).getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(APolynomial.class).newInstance();
								incompleteEquation.init(childNode.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								writeLog(e);
							}
						}
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("adapter")){
					for(int j=0;j<childNode.getChildNodes().getLength();j++){
						if(childNode.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								adapter = Class.forName(childNode.getChildNodes().item(j).getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(APolynomial.class).newInstance();
								adapter.init(childNode.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								writeLog(e);
							}
						}
					}
				}else if(childNode.getNodeName().equalsIgnoreCase("segments")){
					segmentEquations = new TreeMap<>();
					
					NodeList segmentslist = childNode.getChildNodes();
					for(int j=0;j<segmentslist.getLength();j++){
						Node segmentschildNode = segmentslist.item(j);
						if(segmentschildNode.getNodeType()== Node.ELEMENT_NODE && segmentschildNode.getNodeName().equalsIgnoreCase("segment")){
								NodeList segmentlist = segmentschildNode.getChildNodes();
								T currentPosition = null;
								for(int k=0;k<segmentlist.getLength();k++){
									Node segmentchildNode = segmentlist.item(k);
									if(segmentchildNode.getNodeType()== Node.ELEMENT_NODE){										
										if(segmentchildNode.getNodeName().equalsIgnoreCase("position")){
											try{
												currentPosition = adapter.convertValue(Double.valueOf(segmentchildNode.getFirstChild().getNodeValue()));
											}catch(Exception e){
												writeLog(e);
											}
										}else if(segmentchildNode.getNodeName().equalsIgnoreCase("equation")){
											try{
												if(currentPosition!=null){
													IEquation<T> equation = Class.forName(segmentchildNode.getAttributes().getNamedItem(constNodeProvider).getNodeValue()).asSubclass(IEquation.class).newInstance();
													equation.init(segmentchildNode);
													segmentEquations.put(currentPosition, equation);
													currentPosition = null;
												}
											}catch(Exception e){
												writeLog(e);
											}
										}
									}
								
							}
						}
					}
				}
			}
		}		
		return this;
	}
	
	public AEquation<T> setLogger(ILogger logger){
		this.logger = logger;
		return this;
	}

	protected void writeLog(Throwable t){
		if(logger!=null)
			logger.addThrowable(t);
	}
}
