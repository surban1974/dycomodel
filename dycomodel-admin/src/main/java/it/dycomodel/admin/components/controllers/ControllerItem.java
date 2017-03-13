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
import it.classhidra.serialize.Format;
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



	public ControllerItem(){
		super();
	}
	
	



	@Override
	public void reimposta() {
		super.reimposta();
		try{
			final SortedMap<Date, Double> speedM = new TreeMap<Date, Double>() {{
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
			for(Map.Entry<Date, Double> entry : speedM.entrySet()) 
				previous.add(new ForecastItem(entry.getKey(), entry.getValue()));
			
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








}
