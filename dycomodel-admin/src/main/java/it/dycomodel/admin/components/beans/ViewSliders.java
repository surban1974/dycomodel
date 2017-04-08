package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.classhidra.serialize.Serialized;
import it.dycomodel.admin.components.controllers.ControllerDemo;

public class ViewSliders implements Serializable{
	private static final long serialVersionUID = 1L;

	private List<ViewSlider> consumption;
	private List<ViewSlider> stock;
	boolean initialising = false;
	private double minC;
	private double maxC;
	private double minS;
	private double maxS;	

	private ControllerDemo controller;
	
	
	public ViewSliders(ControllerDemo controller){ 
		super();
		this.controller = controller;
	}
	
	public ViewSliders init(boolean maintainDisabled){ 
		SortedMap<Date, Double> consumptionDisabled = new TreeMap<Date, Double>();
		if(maintainDisabled && getConsumption()!=null){			
			for(ViewSlider consSlider: getConsumption()){
				if(!consSlider.isEnabled())
					consumptionDisabled.put(consSlider.getPoint(), consSlider.getValue());
			}
		}
		
		this.initialising=true;

		maxC = 0;
		maxS = 0;
		try{
			Calendar demoC = Calendar.getInstance();
			if(controller.isViewFullApproximation())
				demoC.setTimeInMillis(controller.getConsumption().firstKey().getTime());
			else
				demoC.setTimeInMillis(controller.getStartAvrDate().getTime());
//			demoC.set(Calendar.DATE,15);
			
			Calendar demoF = Calendar.getInstance();
			if(controller.isViewFullApproximation())
				demoF.setTimeInMillis(controller.getConsumption().lastKey().getTime());
			else
				demoF.setTimeInMillis(controller.getFinishAvrDate().getTime());
			
			consumption = new ArrayList<ViewSlider>();
			for(Map.Entry<Date, Double> entry : controller.getConsumption().entrySet()){
				if(entry.getKey().compareTo(demoC.getTime())>=0 && entry.getKey().compareTo(demoF.getTime())<=0){
					ViewSlider consSlider = new ViewSlider(this, "C", entry.getKey(), entry.getValue());
					if(maintainDisabled && consumptionDisabled.get(consSlider.getPoint())!=null)
						consSlider.setEnabled(false);
					consumption.add(consSlider);
					if(maxC<entry.getValue())
						maxC=entry.getValue();
				}

			}
			stock = new ArrayList<ViewSlider>();

			for(Map.Entry<Date, Double> entry : controller.getSecureStock().entrySet()){
				if(entry.getKey().compareTo(demoC.getTime())>=0 && entry.getKey().compareTo(demoF.getTime())<=0){
					ViewSlider stockSlider = new ViewSlider(this, "S", entry.getKey(), entry.getValue());
					if(maintainDisabled && consumptionDisabled.get(stockSlider.getPoint())!=null)
						stockSlider.setEnabled(false);
					stock.add(stockSlider);
				}
//				if(minS>entry.getValue())
//					minS=entry.getValue();
//				if(maxS<entry.getValue())
//					maxS=entry.getValue();				

			}
			if(maxC==0)
				maxC=10;
			else
				maxC = Math.pow(10d, (int)(Math.log10(maxC)+1));
			
			maxS = maxC*controller.getDayStockDelta();
			
			
		}catch(Exception e){
			
		}
		this.initialising=false;
		return this;
	}	
	
	public void changed(ViewSlider slider){
		if(!this.initialising){
			if(slider!=null){
				if(slider.getType().equals("C")){
					controller.getConsumption().put(slider.getPoint(),slider.getValue());
					if(controller.getDayStockDelta()>0){
						double stock = slider.getValue()*controller.getDayStockDelta();
						for(ViewSlider stockSlider: getStock()){
							if(stockSlider.getPoint().compareTo(slider.getPoint())==0){
								stockSlider.setValueFromOwner(stock);
								controller.getSecureStock().put(stockSlider.getPoint(),stockSlider.getValue());
								break;
							}
								
						}
						
					}
				}
				if(slider.getType().equals("S"))
					controller.getSecureStock().put(slider.getPoint(),slider.getValue());	
				try{
					controller.getProxy().init(controller.getEnabledConsumption(), controller.getEnabledSecureStock());
					controller.setRedrawcharts(true);
				}catch(Exception e){
					
				}
			}
		}
	}
	
	public void changedD(ViewSlider slider, Date oldPoint){
		if(!this.initialising){
			if(slider!=null){
				if(slider.getType().equals("C")){
					controller.getConsumption().remove(oldPoint);
					controller.getConsumption().put(slider.getPoint(),slider.getValue());
					if(controller.getDayStockDelta()>0){
						double stock = slider.getValue()*controller.getDayStockDelta();
						for(ViewSlider stockSlider: getStock()){
							if(stockSlider.getPoint().compareTo(oldPoint)==0){
								stockSlider.setValueFromOwner(stock);
								stockSlider.setPointOnly(slider.getPoint());
								controller.getSecureStock().remove(oldPoint);
								controller.getSecureStock().put(slider.getPoint(),stockSlider.getValue());
								break;
							}
								
						}
						
					}
				}
				if(slider.getType().equals("S")){
					controller.getSecureStock().remove(oldPoint);
					controller.getSecureStock().put(slider.getPoint(),slider.getValue());	
				}
				try{
					controller.getProxy().init(controller.getEnabledConsumption(), controller.getEnabledSecureStock());
					controller.setRedrawcharts(true);
				}catch(Exception e){
					
				}
			}
		}
	}
	
	public void removeD(ViewSlider slider, Date oldPoint){
		if(!this.initialising){
			if(slider!=null){
				getConsumption().remove(slider);
				if(slider.getType().equals("C")){
					controller.getConsumption().remove(oldPoint);
					if(controller.getDayStockDelta()>0){
						for(ViewSlider stockSlider: getStock()){
							if(stockSlider.getPoint().compareTo(oldPoint)==0){
								controller.getSecureStock().remove(oldPoint);
								getStock().remove(stockSlider);
								break;
							}
								
						}
						
					}
				}
				if(slider.getType().equals("S")){
					controller.getSecureStock().remove(oldPoint);
				}
				try{
					controller.getProxy().init(controller.getEnabledConsumption(), controller.getEnabledSecureStock());
					controller.setRedrawcharts(true);
				}catch(Exception e){
					
				}
			}
		}
	}
	
	

	public void enabled(ViewSlider slider){
		if(!this.initialising){
			if(slider!=null){

				if(slider.getType().equals("C")){
					for(ViewSlider stockSlider: getStock()){
						if(stockSlider.getPoint().compareTo(slider.getPoint())==0){
							stockSlider.setEnabledFromOwner(slider.isEnabled());
							break;
						}
																			
					}
				}

				if(slider.getType().equals("S")){
					for(ViewSlider consSlider: getConsumption()){
						if(consSlider.getPoint().compareTo(slider.getPoint())==0){
							consSlider.setEnabledFromOwner(slider.isEnabled());
							break;
						}
					}
				}
				
				try{
					
					controller.getProxy().init(controller.getEnabledConsumption(), controller.getEnabledSecureStock());
					controller.setRedrawcharts(true);
					controller.setRedraworders(true);
				}catch(Exception e){
					
				}
			}
		}
	}
	
	
	@Serialized
	public List<ViewSlider> getConsumption() {
		return consumption;
	}
	public void setConsumption(List<ViewSlider> consumption) {
		this.consumption = consumption;
	}
	
	@Serialized
	public List<ViewSlider> getStock() {
		return stock;
	}
	public void setStock(List<ViewSlider> stock) {
		this.stock = stock;
	}

	public boolean isInitialising() {
		return initialising;
	}

	public void setInitialising(boolean initialising) {
		this.initialising = initialising;
	}

	@Serialized
	public double getMinC() {
		return minC;
	}
	@Serialized
	public double getMaxC() {
		return maxC;
	}
	@Serialized
	public double getMinS() {
		return minS;
	}
	@Serialized
	public double getMaxS() {
		return maxS;
	}

	public ControllerDemo getController() {
		return controller;
	}
	
	
}
