package it.dycomodel.admin.components.controllers; 




import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.classhidra.annotation.elements.Action;
import it.classhidra.annotation.elements.Entity;
import it.classhidra.annotation.elements.Redirect;
import it.classhidra.annotation.elements.SessionDirective;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_bean;
import it.classhidra.serialize.Serialized;
import it.dycomodel.admin.components.beans.ForecastItem;
import it.dycomodel.wrappers.ADateWrapper;
import it.dycomodel.wrappers.DateWrapperD;





@Action (
	path="item",
	name="model",
	memoryInSession="true",
	entity=@Entity(
		property="allway:public"
	),
	redirects={
		@Redirect(auth_id="item",path="/pages/item.html",avoidPermissionCheck="true")
	}
)

@SessionDirective
public class ControllerItem extends AbstractBase implements i_action, i_bean, Serializable{
	private static final long serialVersionUID = 1L;

//	@Serialized(children=true,depth=4)
	@Serialized
	private ADateWrapper<Double> proxy;
	
	@Serialized
	private List<ForecastItem> previous;
	
	@Serialized
	private SortedMap<String, List<ForecastItem>> prevbyyears;
	
	@Serialized
	private String selectedYear;



	public ControllerItem(){
		super();
	}
	
	



	@Override
	public void reimposta() {
		super.reimposta();
		try{
			final SortedMap<Date, Double> speedM16 = new TreeMap<Date, Double>() {{
				put(new SimpleDateFormat("yyyyMMdd").parse("20160115"),752d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160215"),512d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160315"),580d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160415"),491d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160515"),487d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160615"),516d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160715"),612d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160815"),698d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20160915"),544d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20161015"),471d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20161115"),577d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20161215"),718d);			 
			}};
			
			this.previous = new ArrayList<ForecastItem>();
			
			for(Map.Entry<Date, Double> entry : speedM16.entrySet()) 
				previous.add(new ForecastItem(entry.getKey(), entry.getValue()));
			this.prevbyyears = new TreeMap<String, List<ForecastItem>>();
			prevbyyears.put("2016", previous);
			
			final SortedMap<Date, Double> speedM15 = new TreeMap<Date, Double>() {{
				put(new SimpleDateFormat("yyyyMMdd").parse("20150115"),577d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150215"),317d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150315"),580d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150415"),491d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150515"),560d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150615"),516d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150715"),612d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150815"),698d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20150915"),544d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20151015"),471d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20151115"),752d);
				put(new SimpleDateFormat("yyyyMMdd").parse("20151215"),786d);			 
			}};
			
			this.previous = new ArrayList<ForecastItem>();
			
			for(Map.Entry<Date, Double> entry : speedM15.entrySet()) 
				previous.add(new ForecastItem(entry.getKey(), entry.getValue()));
			prevbyyears.put("2015", previous);
			
			selectedYear="2016";
			
		}catch(Exception e){
			
		}
		
		proxy = new DateWrapperD();
	}





	public ADateWrapper<Double> getProxy() {
		return proxy;
	}





	public void setProxy(ADateWrapper<Double> proxy) {
		this.proxy = proxy;
	}





	public List<ForecastItem> getPrevious() {
		return previous;
	}





	public void setPrevious(List<ForecastItem> previous) {
		this.previous = previous;
	}





	public SortedMap<String, List<ForecastItem>> getPrevbyyears() {
		return prevbyyears;
	}





	public void setPrevbyyears(SortedMap<String, List<ForecastItem>> prevbyyears) {
		this.prevbyyears = prevbyyears;
	}





	public String getSelectedYear() {
		return selectedYear;
	}





	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}








}
