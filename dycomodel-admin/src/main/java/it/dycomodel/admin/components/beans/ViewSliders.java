package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
	public ViewSliders init(){ 
		this.initialising=true;

		
		try{
			consumption = new ArrayList<ViewSlider>();
			for(Map.Entry<Date, Double> entry : controller.getConsumption().entrySet()){
				consumption.add(new ViewSlider(this, "C", entry.getKey(), entry.getValue()));
				if(minC>entry.getValue())
					minC=entry.getValue();
				if(maxC<entry.getValue())
					maxC=entry.getValue();

			}
			stock = new ArrayList<ViewSlider>();

			for(Map.Entry<Date, Double> entry : controller.getSecureStock().entrySet()){
				stock.add(new ViewSlider(this, "S", entry.getKey(), entry.getValue()));
				if(minS>entry.getValue())
					minS=entry.getValue();
				if(maxS<entry.getValue())
					maxS=entry.getValue();				

			}
			
		}catch(Exception e){
			
		}
		this.initialising=false;
		return this;
	}	
	
	public void changed(ViewSlider slider){
		if(!this.initialising){
			if(slider!=null){
				if(slider.getType().equals("C"))
					controller.getConsumption().put(slider.getPoint(),slider.getValue());
				if(slider.getType().equals("S"))
					controller.getSecureStock().put(slider.getPoint(),slider.getValue());	
				try{
					controller.getProxy().init(controller.getConsumption(), controller.getSecureStock());
					controller.setRedrawcharts(true);
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
