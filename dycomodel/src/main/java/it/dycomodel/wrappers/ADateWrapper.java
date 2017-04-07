package it.dycomodel.wrappers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.dycomodel.equation.AEquation;
import it.dycomodel.equation.IEquation;
import it.dycomodel.exceptions.InputParameterException;
import it.dycomodel.plugins.IComputing;
import it.dycomodel.polynomial.APolynomial;

import it.dycomodel.utils.Normalizer;

public abstract class ADateWrapper<T extends Number> implements Serializable {

	private static final long serialVersionUID = 1L;

	
	protected Date initialDeltaDate;
	protected IEquation<T> equation;
	protected APolynomial<T> lead;
	protected IComputing computingPlugin;
	


	public ADateWrapper<T> init(SortedMap<Date, Double> forecastedConsumption, SortedMap<Date, Double> forecastedStock) throws Exception{
		APolynomial<T> adapter = initAdapter();
		double[][] speed = new double[0][0];
		double[][] secure = new double[0][0];
	

		if(forecastedConsumption!=null){			
			speed = new double[forecastedConsumption.size()][2];
			int count=0;
			for(Map.Entry<Date, Double> entry : forecastedConsumption.entrySet()) {
				if(count==0)
					initialDeltaDate=entry.getKey();
				
				
				speed[count][0] = Double.valueOf((entry.getKey().getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
				speed[count][1] = entry.getValue();
				count++;
			}
		}
		
		if(forecastedStock!=null){			
			secure = new double[forecastedStock.size()][2];

			int count=0;
			for(Map.Entry<Date, Double> entry : forecastedStock.entrySet()) {
				if(speed.length>1 && speed[0][0]<=secure[count][0] && secure[count][0]<=speed[speed.length-1][0]){
					secure[count][0] = Double.valueOf((entry.getKey().getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
					secure[count][1] = entry.getValue();
				}
				count++;
			}
		}	
		
		equation = initEquation()
				.setComputingPlugin(computingPlugin)
				.setAveragePoints(
						adapter.copyToTArray(speed),
						adapter.copyToTArray(secure)
					)
				.makeIncompleteEquation();
		
		return this;		

	}
	
	public Date computeLead(Date point){
		if(point==null)
			return null;
		double solved = 0;
		if(initialDeltaDate!=null)
			solved = Double.valueOf((point.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
		else
			solved = Double.valueOf((point.getTime()) / (1000 * 60 * 60 * 24));
		double solvedLead = 0;
		if(lead!=null)
			solvedLead = solved - lead.compute(lead.convertValue(solved)).doubleValue();
		else 
			solvedLead = solved;
		Date pointLead = null;
		if(initialDeltaDate!=null)
			pointLead = new Date(Long.valueOf(initialDeltaDate.getTime()+(long)solvedLead*(1000 * 60 * 60 * 24)));
		else		
			pointLead = new Date(Long.valueOf((long)solvedLead*(1000 * 60 * 60 * 24)));
		
		return pointLead;
	}
	
		
	
	public Date forecastPointWithLead(Date date, Date limitDate){
		APolynomial<T> adapter = initAdapter();
		if(date==null)
			return null;
		if(lead==null)
			return date;
		
		double solved = 0;
		double limit = 0;
		if(initialDeltaDate!=null){
			solved = Double.valueOf((date.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
			limit = Double.valueOf((limitDate.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
		}else{
			solved = Double.valueOf((date.getTime()) / (1000 * 60 * 60 * 24));
			limit = Double.valueOf((limitDate.getTime()) / (1000 * 60 * 60 * 24));
		}
		if(limit<solved)
			limit=solved+365;
		
		APolynomial<T> computedEquation = 
				equation.initPolynomial()
				.init(lead)
				.subtraction(
						equation.initPolynomial()
						.setConstant(0, convertValue(-solved))
						.setConstant(1, convertValue(1))
				)
				;
		
		double solvedPoint = 0;
		try{
			
			double[] roots = adapter.copyTodoubleArray(
						computingPlugin.getPolynomialRoots(computedEquation,null,adapter.convertValue(solved),adapter.convertValue(limit), adapter)
					);
			if(roots.length>0)
				solvedPoint = roots[0];
			else 
				solvedPoint = solved;
			
		}catch(Exception e){
			solvedPoint = solved;
		}

		Date point = null;
		if(initialDeltaDate!=null)
			point = new Date(Long.valueOf(initialDeltaDate.getTime()+(long)(solvedPoint+1)*(1000 * 60 * 60 * 24)));
		else		
			point = new Date(Long.valueOf((long)solvedPoint*(1000 * 60 * 60 * 24)+1));
		
		return point;
	}	
	
	public Date getFirstPoint(T initialQuantity, Date startDate, Date finishDate, SortedMap<Date, T> processedOrders) throws InputParameterException{
		APolynomial<T> adapter = initAdapter();
		Double startPeriod = null;
		Double finishPeriod = null;
		if(initialDeltaDate!=null){
			if(startDate!=null)
				startPeriod = Double.valueOf((startDate.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
			if(finishDate!=null)
				finishPeriod = Double.valueOf((finishDate.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));			
		}else
			startPeriod = 0d;
		if(startPeriod<0 ||  initialQuantity==null)
			throw new InputParameterException();
		
		if(finishPeriod-startPeriod<100)
			finishPeriod+=100;
		
		Date point = null;
		T currentQuantity = initialQuantity;
		T controlQuantity = initialQuantity;

		while(
			point==null ||
			currentQuantity.doubleValue()<controlQuantity.doubleValue()
		){
			currentQuantity = controlQuantity;
			double solved=0;
			try{
				solved = equation.solveEquation(
						currentQuantity,
						adapter.convertValue(startPeriod),
						adapter.convertValue(finishPeriod)).doubleValue();
			}catch(Exception e){
				return null;
			}
			
			if(solved==-1)
				return null;
			
			if(initialDeltaDate!=null)
				point = new Date(Long.valueOf(initialDeltaDate.getTime()+(long)solved*(1000 * 60 * 60 * 24)));
			else		
				point = new Date(Long.valueOf((long)solved*(1000 * 60 * 60 * 24)));
			
			controlQuantity = getProcessedOrdersQuantity(initialQuantity,processedOrders,startDate,point, finishDate);
			
		}
		return point;

	}
	
	protected T getProcessedOrdersQuantity(T initialQuantity, SortedMap<Date, T> processedOrders, Date startDate, Date pointDate, Date finishDate){
		if(startDate==null || pointDate==null || processedOrders==null || processedOrders.size()==0)
			return initialQuantity;
		APolynomial<T> calc = equation.initPolynomial();
		
		T quantity = initialQuantity;
		for(Map.Entry<Date, T> entry : processedOrders.entrySet()) {
			Date withLead = forecastPointWithLead(entry.getKey(), finishDate);
			if(withLead.after(startDate) && withLead.before(pointDate))
				quantity = calc.addition(quantity, convertValue(entry.getValue()));
		}
		return quantity;
	}
	
	public T computeConsumptionInPoint(T initialQuantity, SortedMap<Date, T> processedOrders, Date startDate, Date point, Date finishDate){
		T processedInitialQuantity = getProcessedOrdersQuantity(initialQuantity, processedOrders, startDate, point, finishDate);
		
		Double startPeriod = null;
		Double finishPeriod = null;
		if(initialDeltaDate!=null){
			if(startDate!=null)
				startPeriod = Double.valueOf((startDate.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));
			if(point!=null)
				finishPeriod = Double.valueOf((point.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));			
		}else{
			if(startDate!=null)
				startPeriod = Double.valueOf((startDate.getTime()) / (1000 * 60 * 60 * 24));
			if(point!=null)
				finishPeriod = Double.valueOf((point.getTime()) / (1000 * 60 * 60 * 24));			

		}
		
/*		
		T diff = calc
				.subtraction(
						convertValue(processedInitialQuantity),
						calc
							.subtraction(
								equation.getConsumptionIntegral().compute(convertValue(finishPeriod)),
								equation.getConsumptionIntegral().compute(convertValue(startPeriod))
							)
						);
*/						
		T diff = equation.computeConsumption(processedInitialQuantity, convertValue(startPeriod), convertValue(finishPeriod));
				
		
		return diff;
	}
	
	public T computeSecureStockInPoint(Date point){
		Double pointPeriod = null;
		if(initialDeltaDate!=null){
			if(point!=null)
				pointPeriod = Double.valueOf((point.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));			
		}else{
			if(point!=null)
				pointPeriod = Double.valueOf((point.getTime()) / (1000 * 60 * 60 * 24));			
		}

//		return equation.getSecureStock().compute(convertValue(pointPeriod));
		return equation.compute(AEquation.COMPUTE_STOCK, convertValue(pointPeriod));
	}
	
	public T computeSpeedConsumptionInPoint(Date point){
		Double pointPeriod = null;
		if(initialDeltaDate!=null){
			if(point!=null)
				pointPeriod = Double.valueOf((point.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));			
		}else{
			if(point!=null)
				pointPeriod = Double.valueOf((point.getTime()) / (1000 * 60 * 60 * 24));			
		}
//		return equation.getConsumption().compute(convertValue(pointPeriod));
		return equation.compute(AEquation.COMPUTE_CONSUMPTION, convertValue(pointPeriod));
	}	


	public abstract T convertValue(Number value);
	
	public T roundWithGranulation(T quantity, T granulation){
		if(granulation==null)
			return quantity;
		APolynomial<T> calc = equation.initPolynomial();
		
		T division = calc.division(quantity, granulation);
		if(calc.equal(calc.fractionalPart(division), convertValue(0)))
			return quantity;
		else
			return calc.multiplication(
				calc.addition(calc.floorPart(division),convertValue(1)),
				granulation
			);
	}

	
	public SortedMap<Date, T> getPoints(T initialQuantity, T fixedQuantity, Date startDate, Date finishDate, SortedMap<Date, T> processedOrders, T granulation) throws InputParameterException{

		if(startDate==null){
			if(initialDeltaDate!=null)
				startDate=initialDeltaDate;
			else 
				return null;
		}
		if(finishDate==null)
			finishDate=new Date(startDate.getTime()+365 * 1000 * 60 * 60 * 24);
		else
			finishDate=new Date(finishDate.getTime()+30 * 1000 * 60 * 60 * 24);
		
		SortedMap<Date, T> result = new TreeMap<Date, T>();
		result.putAll(processedOrders);
		Date current = getFirstPoint(initialQuantity, startDate, finishDate, processedOrders);
		Date leadCurrent = computeLead(current);
		if(current==null )
			return result;
		result.put(leadCurrent, roundWithGranulation(fixedQuantity, granulation));
		
		while(current!=null && leadCurrent.before(finishDate)){
			T diff = equation.initPolynomial().addition(fixedQuantity, computeConsumptionInPoint(initialQuantity, result, startDate, current, finishDate));
			Date point = getFirstPoint(diff, current, finishDate, processedOrders);
			if(point==null || point.compareTo(current)<=0)
				return result;
			current=point;
			leadCurrent = computeLead(current);
			result.put(leadCurrent, roundWithGranulation(fixedQuantity, granulation));
		}
		return result;
	}


	public SortedMap<Date, T> getPoints(T initialQuantity, SortedSet<Date> pointDates, Date startDate, Date finishDate, SortedMap<Date, T> processedOrders, boolean withLead, T granulation) throws InputParameterException{

		if(startDate==null){
			if(initialDeltaDate!=null)
				startDate=initialDeltaDate;
			else 
				return null;
		}
		if(finishDate==null)
			finishDate=new Date(startDate.getTime()+365 * 1000 * 60 * 60 * 24);
		
		SortedMap<Date, T> result = new TreeMap<Date, T>();
		result.putAll(processedOrders);
		
		Date k0Date = null;
		Date k1Date = null;
		
		for(Date d:pointDates){
			
			if(d.after(startDate) && (k0Date==null || k0Date.before(finishDate))){
				if(k0Date==null){
					k0Date = forecastPointWithLead(d,finishDate);
					T diff = computeConsumptionInPoint(initialQuantity, result, startDate, k0Date, finishDate);
					APolynomial<T> calc = equation.initPolynomial();

					Double finishPeriod = null;
					if(initialDeltaDate!=null){
						if(k0Date!=null)
							finishPeriod = Double.valueOf((k0Date.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));			
					}else{
						if(k0Date!=null)
							finishPeriod = Double.valueOf((k0Date.getTime()) / (1000 * 60 * 60 * 24));			
					}
					
/*					
					diff = calc
							.subtraction(
								diff,
								equation.getSecureStock().compute(convertValue(finishPeriod))
							);
*/					
					diff = calc
							.subtraction(
								diff,
								equation.compute(AEquation.COMPUTE_STOCK, convertValue(finishPeriod))
							);
					
					if(diff.doubleValue()<0){
						
						Date firstPoint = getFirstPoint(initialQuantity, startDate, finishDate, result);
					
						result.put(
								(withLead)
									?
										computeLead(firstPoint)
									:
										firstPoint
								,
								roundWithGranulation(
									calc
									.multiplication(diff, convertValue(-1))
									,
									granulation
								)
							);
						
					}
				}else{
					k1Date = forecastPointWithLead(d,finishDate);
					T diff = computeConsumptionInPoint(initialQuantity, result, startDate, k1Date, finishDate);
					APolynomial<T> calc = equation.initPolynomial();

					Double finishPeriod = null;
					if(initialDeltaDate!=null){
						if(k0Date!=null)
							finishPeriod = Double.valueOf((k1Date.getTime() - initialDeltaDate.getTime()) / (1000 * 60 * 60 * 24));			
					}else{
						if(k0Date!=null)
							finishPeriod = Double.valueOf((k1Date.getTime()) / (1000 * 60 * 60 * 24));			
					}
					
/*					
					diff = calc
							.subtraction(
								diff,
								equation.getSecureStock().compute(convertValue(finishPeriod))
							);
*/
					diff = calc
							.subtraction(
								diff,
								equation.compute(AEquation.COMPUTE_STOCK, convertValue(finishPeriod))
							);
					
					
					if(diff.doubleValue()>0){
						result.put(
								(withLead)
									?
										computeLead(k0Date)
									:
										k0Date
								,
								convertValue(0)
								);					
					}else{
						result.put(
								(withLead)
									?
										computeLead(k0Date)
									:
										k0Date
								,
								roundWithGranulation(
									calc
									.multiplication(diff, convertValue(-1))
									,
									granulation
								)
							);
					}
					k0Date=k1Date;
					
				}
					
			}
			
		}
		return result;
	}
	
	protected abstract IEquation<T> initEquation();
	protected abstract APolynomial<T> initAdapter();

	public IEquation<T> getEquation() {
		return equation;
	}

	public APolynomial<T> getLead() {
		return lead;
	}

	public ADateWrapper<T> setLead(APolynomial<T> lead) {
		this.lead = lead;
		return this;
	}

	public IComputing getComputingPlugin() {
		return computingPlugin;
	}

	public ADateWrapper<T> setComputingPlugin(IComputing computingPlugin) {
		this.computingPlugin = computingPlugin;
		return this;
	}
	
	public String toXml(int level, String info){
		String result="<dycomodel>\n";
		result+=Normalizer.spaces(level)+"<wrapper provider=\""+this.getClass().getName()+"\">\n";
		if(initialDeltaDate!=null)
			result+=Normalizer.spaces(level+1)+"<initialDeltaDate>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(initialDeltaDate)+"</initialDeltaDate>\n";
				
		if(equation!=null)
			result+=equation.toXml(level+1);
		if(lead!=null){
			result+=Normalizer.spaces(level+1)+"<lead>\n";
			result+=lead.toXml(level+2);
			result+=Normalizer.spaces(level+1)+"</lead>\n";
		}		
		if(computingPlugin!=null)
			result+=Normalizer.spaces(level+1)+"<computingPlugin>"+computingPlugin.getClass().getName()+"</computingPlugin>\n";

		result+=Normalizer.spaces(level)+"</wrapper>\n";
		if(info!=null)
			result+=info;
		result+="</dycomodel>\n";
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public ADateWrapper<T> init(Node node) throws Exception{
		
		NodeList list = node.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node child_node = list.item(i);
			if(child_node.getNodeType()== Node.ELEMENT_NODE){
				
				
				if(child_node.getNodeName().equalsIgnoreCase("initialDeltaDate")){
					try{
						this.initialDeltaDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(child_node.getFirstChild().getNodeValue());
					}catch(Exception e){
						e.printStackTrace();
					}
				}else if(child_node.getNodeName().equalsIgnoreCase("computingPlugin")){
					try{
						computingPlugin = Class.forName(child_node.getFirstChild().getNodeValue()).asSubclass(IComputing.class).newInstance();
					}catch(Exception e){
						e.printStackTrace();
					}
				}else if(child_node.getNodeName().equalsIgnoreCase("lead")){
					for(int j=0;j<child_node.getChildNodes().getLength();j++){
						if(child_node.getChildNodes().item(j).getNodeType()== Node.ELEMENT_NODE){
							try{
								lead = Class.forName(child_node.getChildNodes().item(j).getAttributes().getNamedItem("provider").getNodeValue()).asSubclass(APolynomial.class).newInstance();
								lead.init(child_node.getChildNodes().item(j));
								break;
							}catch(Exception e){		
								e.printStackTrace();
							}
						}
					}
				}else if(child_node.getNodeName().equalsIgnoreCase("equation")){
					try{
						equation = Class.forName(child_node.getAttributes().getNamedItem("provider").getNodeValue()).asSubclass(IEquation.class).newInstance();
						equation.init(child_node);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		
	
		
		return this;
	}

}
