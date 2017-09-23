package it.dycomodel.polynomial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.dycomodel.utils.ILogger;
import it.dycomodel.utils.Normalizer;


public abstract class APolynomial<T extends Number> implements Serializable{
	private static final long serialVersionUID = 1L;
	protected List<T> constants;
	private ILogger logger;

	
	public abstract APolynomial<T> additionConstant(int n, T value);
	public abstract APolynomial<T> subtractionConstant(int n, T value);
	public abstract APolynomial<T> multiplicationConstant(int n, T value);
	public abstract APolynomial<T> divisionConstant(int n, T value);
	public abstract T addition(T value1, T value2);
	public abstract T subtraction(T value1, T value2);
	public abstract T multiplication(T value1, T value2);
	public abstract T division(T value1, T value2);
	public abstract T floorPart(T value1);
	public abstract T fractionalPart(T value1);
	public abstract boolean identic(T value1, T value2);
	public abstract T[] initArray(int length);	
	public abstract T[][] initArray(int length1, int length2);
	public abstract T convertValue(Number value);
	public double[] copyTodoubleArray(T[] array){
		if(array==null)
			return new double[0];
		double[] result = new double[array.length];
		for(int i=0;i<array.length;i++)
			result[i] = array[i].doubleValue();
		return result;
	}
	public T[] copyToTArray(double[] array){
		if(array==null)
			return initArray(0);
		T[] result = initArray(array.length);
		for(int i=0;i<array.length;i++)
			result[i] = convertValue(array[i]);
		return result;
	}	
	public T[][] copyToTArray(double[][] array){
		if(array==null || array.length==0)
			return initArray(0,0);
		T[][] result = initArray(array.length,array[0].length);
		for(int i=0;i<array.length;i++){
			for(int j=0;j<array[i].length;j++)		
				result[i][j] = convertValue(array[i][j]);
		}
		return result;
	}		
	
	public APolynomial<T> init(APolynomial<T> arg){
		if(arg==null || arg.getConstants()==null)
			return null;
		normalizeSize(arg.getConstants().size()-1);
		for(int i=0;i<arg.getConstants().size();i++)
			constants.set(i, arg.getConstants().get(i));
			
		return this;
	}
	public APolynomial<T> setConstant(int n, T value){
		normalizeSize(n);
		constants.set(n, value);
		return this;
	}
	public APolynomial<T> addition(APolynomial<T> arg){
		if(arg==null || arg.getConstants()==null)
			return null;
		if(constants==null)
			constants = new ArrayList<>();
		for(int i=0;i<arg.getConstants().size();i++)
			additionConstant(i, arg.getConstants().get(i));
		return this;
	}
	
	public APolynomial<T> subtraction(APolynomial<T> arg){
		if(arg==null || arg.getConstants()==null)
			return null;
		if(constants==null)
			constants = new ArrayList<>();
		for(int i=0;i<arg.getConstants().size();i++)
			subtractionConstant(i, arg.getConstants().get(i));
		return this;
	}	

	public APolynomial<T> integral(){
		if(constants==null)
			constants = new ArrayList<>();
		for(int i=getConstants().size()-1;i>=0;i--){
			setConstant(i+1, getConstants().get(i));
			divisionConstant(i+1, convertValue(i+1));
		}
		if(!constants.isEmpty())
			setConstant(0, convertValue(0));
		
		return this;
	}	
	
	public T compute(T arg){
		if(constants==null)
			constants = new ArrayList<>();
		T result = convertValue(0);
		for(int i=0;i<getConstants().size();i++){
			T pow = convertValue(1);
			if(i==1)
				pow=convertValue(arg);
			else if(i>1){
				pow=convertValue(arg);
				for(int k=1;k<i;k++)
					pow=multiplication(pow, arg);
			}			
			result=addition(result,multiplication(pow, getConstants().get(i)));
		}
		return result;
	}	
	
	
	protected APolynomial<T> normalizeSize(int n){
		if(constants==null)
			constants = new ArrayList<>();
		while(constants.size()<(n+1)){
			constants.add(convertValue(0));
		}
		return this;
	}
	public List<T> getConstants() {
		return constants;
	}

	public void setConstants(List<T> constants) {
		this.constants = constants;
	}
	
	public double[] toDoubleArray(){
		if(constants==null) return new double[0];
		double[] result = new double[constants.size()];
		for(int i=0;i<constants.size();i++)
			result[i]=constants.get(i).doubleValue();
		return result;
	}
	
	public String toString(){
		StringBuilder result=new StringBuilder();
		for(int i=0;i<constants.size();i++){
			double roundConst = new BigDecimal(constants.get(i).toString()).divide(new BigDecimal(1),6,BigDecimal.ROUND_HALF_UP).doubleValue();
			if(roundConst>0)
				result.append(" +"+new BigDecimal(constants.get(i).toString()).divide(new BigDecimal(1),6,BigDecimal.ROUND_HALF_UP)+"*x^"+i);
			else if(roundConst<0)
				result.append(" "+new BigDecimal(constants.get(i).toString()).divide(new BigDecimal(1),6,BigDecimal.ROUND_HALF_UP)+"*x^"+i);
		}
		return result.toString();
	}
	
	public String toXml(int level){
		StringBuilder result=new StringBuilder();
		if(!constants.isEmpty())
			result.append(Normalizer.spaces(level)+"<polynomial provider=\""+this.getClass().getName()+"\"/>\n");
		else{
			result.append(Normalizer.spaces(level)+"<polynomial provider=\""+this.getClass().getName()+"\">\n");
			for(int i=0;i<constants.size();i++)
				result.append(Normalizer.spaces(level+1)+"<coefficient position=\""+i+"\">"+constants.get(i).toString()+"</coefficient>\n");		
			result.append(Normalizer.spaces(level)+"</polynomial>\n");
		}
		return result.toString();
	}
	
	public APolynomial<T> init(Node node){
		NodeList list = node.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node childNode = list.item(i);
			if(childNode.getNodeType()== Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("coefficient")){
					try{
						
						setConstant(
								Integer.valueOf(childNode.getAttributes().getNamedItem("position").getNodeValue()),
								convertValue(Double.valueOf(childNode.getFirstChild().getNodeValue()))								
						);
					}catch(Exception e){
						writeLog(e);
					}
				
			}
		}
		return this;
	}
	public APolynomial<T> setLogger(ILogger logger){
		this.logger = logger;
		return this;
	}

	protected void writeLog(Throwable t){
		if(logger!=null)
			logger.addThrowable(t);
	}
}
