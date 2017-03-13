package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.classhidra.core.tool.util.util_format;
import it.classhidra.serialize.Serialized;
import it.dycomodel.admin.components.controllers.ControllerDemo;

public class ViewChartAverage implements Serializable{
	private static final long serialVersionUID = 1L;

	@Serialized
	private String[] header;
	
	@Serialized
	private String[][] datas;
	
	
	public ViewChartAverage(String[] _header, ControllerDemo controller){
		super();
		this.header = _header;
		try{
			SortedMap<Date, Double[]> combine = new TreeMap<Date, Double[]>();
			for(Map.Entry<Date, Double> entry : controller.getConsumption().entrySet()) 
				combine.put(entry.getKey(), new Double[]{new BigDecimal(entry.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue(),0d});
			
			for(Map.Entry<Date, Double> entry : controller.getSecureStock().entrySet()){ 
				Double[] exist = combine.get(entry.getKey());
				if(exist!=null)
					exist[1]=new BigDecimal(entry.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue();
				else
					combine.put(entry.getKey(), new Double[]{0d,new BigDecimal(entry.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()});
			}
			
			datas = new String[combine.size()][3];
			int i=0;
			for(Map.Entry<Date, Double[]> entry : combine.entrySet()){
				datas[i][0]=util_format.dataToString(entry.getKey(), "MM dd yyyy");
				datas[i][1]=String.valueOf(entry.getValue()[0]);
				datas[i][2]=String.valueOf(entry.getValue()[1]);
				i++;
			}
			
		}catch(Exception e){
			
		}

	}
	public String[] getHeader() {
		return header;
	}
	public void setHeader(String[] header) {
		this.header = header;
	}
	public String[][] getDatas() {
		return datas;
	}
	public void setDatas(String[][] datas) {
		this.datas = datas;
	}
}
