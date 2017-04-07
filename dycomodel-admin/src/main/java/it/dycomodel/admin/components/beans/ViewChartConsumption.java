package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.classhidra.core.tool.util.util_format;
import it.classhidra.serialize.Serialized;
import it.dycomodel.admin.components.controllers.ControllerDemo;

public class ViewChartConsumption implements Serializable{
	private static final long serialVersionUID = 1L;

	@Serialized
	private String[] header;
	
	@Serialized
	private String[][] datas;
	
	
	public ViewChartConsumption(String[] _header, ControllerDemo controller){
		super();
		this.header = _header;
		SortedMap<Date, Double> allProcessedOrders = new TreeMap<Date, Double>();
		if(controller.getProcessedOrders()!=null)
			allProcessedOrders.putAll(controller.getProcessedOrders());
		
		try{
			Date finishChartDate = controller.getFinishDate();
			if(controller.getConsumption().lastKey().before(finishChartDate))
				finishChartDate = controller.getConsumption().lastKey();
			if(controller.getSecureStock().lastKey().before(finishChartDate))
				finishChartDate = controller.getSecureStock().lastKey();
			
			if(controller.getCalculatemode()==1){
				try{
					Date point = controller.getProxy().getFirstPoint(controller.getQuantity(), controller.getStartDate(), finishChartDate, controller.getProcessedOrders());
					if(point!=null){
						point = controller.getProxy().computeLead(point);
						allProcessedOrders.put(point,0d);
					}
					controller.setComputedOrders(allProcessedOrders);
				}catch(Exception e){					
				}
			}
			
			if(controller.getCalculatemode()==2){
				try{
					SortedMap<Date, Double> newOrders = controller.getProxy().getPoints(controller.getQuantity(), controller.getFixedQuantity(), controller.getStartDate(), finishChartDate, controller.getProcessedOrders(), (double)controller.getItemsForPack());
					allProcessedOrders.putAll(newOrders);	
					controller.setComputedOrders(newOrders);
				}catch(Exception e){					
				}
			}
			
			if(controller.getCalculatemode()==3){
				try{
					SortedMap<Date, Double> newOrders = controller.getProxy().getPoints(controller.getQuantity(), controller.getFixedFeatureOrders(),controller.getStartDate(), finishChartDate, controller.getProcessedOrders(), true, (double)controller.getItemsForPack());
					allProcessedOrders.putAll(newOrders);
					controller.setComputedOrders(newOrders);
				}catch(Exception e){					
				}
			}			
		
			

			
			
				


			SortedMap<Date, Double[]> points = new TreeMap<Date, Double[]>();
			SortedMap<String, Double[]> pointsS = new TreeMap<String, Double[]>();
			if(allProcessedOrders!=null && allProcessedOrders.size()>0){
				for(Map.Entry<Date, Double> entry :  allProcessedOrders.entrySet()){
					double consumption = controller.getProxy().computeConsumptionInPoint(controller.getQuantity(), allProcessedOrders,  controller.getStartDate(), controller.normalizeDate(entry.getKey()), finishChartDate);
					double stock = controller.getProxy().computeSecureStockInPoint(controller.normalizeDate(entry.getKey()));
					points.put(controller.normalizeDate(entry.getKey()), new Double[]{
							new BigDecimal(consumption).setScale(2, RoundingMode.HALF_UP).doubleValue(),
							new BigDecimal(stock).setScale(2, RoundingMode.HALF_UP).doubleValue(),
							new BigDecimal(entry.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()
							});
					pointsS.put(util_format.dataToString(entry.getKey(), "yyyyMMdd"), new Double[]{
							new BigDecimal(consumption).setScale(2, RoundingMode.HALF_UP).doubleValue(),
							new BigDecimal(stock).setScale(2, RoundingMode.HALF_UP).doubleValue(),
							new BigDecimal(entry.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()
							});
					
				}
			}
			
			Calendar demoC = Calendar.getInstance();
			demoC.setTimeInMillis(controller.getStartDate().getTime());
//			demoC.set(Calendar.DATE,15);
			
			
			while(demoC.getTime().before(finishChartDate)){
				double consumption = controller.getProxy().computeConsumptionInPoint(controller.getQuantity(), allProcessedOrders,  controller.getStartDate(), demoC.getTime(), finishChartDate);
				double stock = controller.getProxy().computeSecureStockInPoint(demoC.getTime());
				Double[] reorder = pointsS.get(util_format.dataToString(demoC.getTime(), "yyyyMMdd"));
				if(reorder==null){
					if(consumption>=0)
						points.put(demoC.getTime(), new Double[]{
								new BigDecimal(consumption).setScale(2, RoundingMode.HALF_UP).doubleValue(),
								new BigDecimal(stock).setScale(2, RoundingMode.HALF_UP).doubleValue(),
								0d
								});
					else
						points.put(demoC.getTime(), new Double[]{0d,new BigDecimal(stock).setScale(2, RoundingMode.HALF_UP).doubleValue(),0d});
				}else{
					points.put(demoC.getTime(), new Double[]{
							new BigDecimal(consumption).setScale(2, RoundingMode.HALF_UP).doubleValue(),
							new BigDecimal(stock).setScale(2, RoundingMode.HALF_UP).doubleValue(),
							reorder[2]
							});
					
				}
				demoC.set(Calendar.DATE,demoC.get(Calendar.DATE)+controller.getChartConsumptionDayInterval());

			}
			datas = new String[points.size()][4];
			int i=0;
			for(Map.Entry<Date, Double[]> entry : points.entrySet()){
				datas[i][0]=util_format.dataToString(entry.getKey(), "MM dd yyyy");
				datas[i][1]=String.valueOf(entry.getValue()[0]);
				datas[i][2]=String.valueOf(entry.getValue()[1]);
				datas[i][3]=String.valueOf(entry.getValue()[2]);
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
