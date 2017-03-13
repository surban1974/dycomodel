package it.dycomodel.admin.components.controllers; 




import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.classhidra.annotation.elements.Action;
import it.classhidra.annotation.elements.ActionCall;
import it.classhidra.annotation.elements.Entity;
import it.classhidra.annotation.elements.Expose;
import it.classhidra.annotation.elements.Redirect;
import it.classhidra.annotation.elements.SessionDirective;
import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_bean;
import it.classhidra.serialize.JsonReader2Map;
import it.classhidra.serialize.JsonWriter;
import it.classhidra.serialize.Serialized;
import it.dycomodel.admin.components.beans.ViewChartAverage;
import it.dycomodel.admin.components.beans.ViewChartConsumption;
import it.dycomodel.admin.components.beans.ViewOrders;
import it.dycomodel.admin.components.beans.ViewSliders;
import it.dycomodel.plugins.ApacheCommonMathLaguerre;
import it.dycomodel.polynomial.PolynomialD;
import it.dycomodel.wrappers.ADateWrapper;
import it.dycomodel.wrappers.DateWrapperD;





@Action (
	path="demo",
	name="model",
	memoryInSession="true",
	entity=@Entity(
		property="allway:public"
	),
	redirects={
		@Redirect(auth_id="item",path="/pages/demo.html",avoidPermissionCheck="true")
	}
)

@SessionDirective
public class ControllerDemo extends AbstractBase implements i_action, i_bean, Serializable{
	private static final long serialVersionUID = 1L;

	@Serialized
	private ADateWrapper<Double> proxy;
	
	private SortedMap<Date, Double> consumption;
	
	private SortedMap<Date, Double> secureStock;
	
	@Serialized
	private SortedMap<Date, Double> processedOrders;

//	@Serialized
	private SortedSet<Date> fixedFeatureOrders;
	

	private SortedMap<Date, Double> computedOrders;
	
	@Serialized
	private Date startDate;
	
	@Serialized
	private Date finishDate;
	
	@Serialized
	private ViewSliders sliders;
	
	@Serialized
	private boolean redrawcharts=false;
	
	@Serialized
	private boolean redraworders=false;
	
	@Serialized
	private int tunemode=1;	
	
	@Serialized
	private int calculatemode=1;	
	
	@Serialized
	private double quantity=0;
	
	@Serialized
	private double fixedQuantity=0;	

	@Serialized
	private int page_tab=1;
	
	@Serialized
	private long dayFinishDate;	
	
	@Serialized
	private double leadDays;	
	
	@Serialized
	private int chartConsumptionDayInterval=1;		

	public ControllerDemo(){
		super();
	}
	
	
	@ActionCall(
			name="chartavr",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(methods = {Expose.POST,Expose.GET})
	)
	public String chartavr(){
		clear();
		ViewChartAverage cha = new ViewChartAverage(new String[]{"Period", "Average Day Consumption", "Daily Secure stock"}, this);
		String json = JsonWriter.object2json(cha,"chart",null,true,3);
		clear();
		return json;
		
	}	
	
	@ActionCall(
			name="chartcons",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(methods = {Expose.POST,Expose.GET})
	)
	public String chartcons(){
		clear();
		ViewChartConsumption cha = new ViewChartConsumption(new String[]{"Period", "Consumption", "Daily Secure stock","Reorder"}, this);
		String json = JsonWriter.object2json(cha,"chart",null,true,3);
		clear();
		return json;
		
	}	
	
	
	
	@ActionCall(
			name="diff",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.POST))
	public String diffAsJson(HttpServletRequest request, HttpServletResponse response){
		
		String modelName = getString("outputserializedname");
		if(modelName==null || modelName.equals(""))
			modelName = request.getParameter("outputserializedname");
		
		String outputappliedfor = getOutputappliedfor();
		if(outputappliedfor==null || outputappliedfor.equals(""))
			outputappliedfor = request.getParameter("outputappliedfor");
		if(outputappliedfor!=null && !outputappliedfor.trim().equals(""))
			return JsonWriter.object2json(get_bean().asBean().get(outputappliedfor), (modelName==null || modelName.equals(""))?outputappliedfor:modelName);
		

		if(modelName==null || modelName.equals("")){
			if(get_infobean()!=null && get_infobean().getName()!=null && !get_infobean().getName().equals(""))
				modelName = get_infobean().getName();
			else if(get_infoaction()!=null && get_infoaction().getName()!=null && !get_infoaction().getName().equals(""))
				modelName = get_infoaction().getName();
			else
				modelName = "model";
		}
		
		byte[] datas = (byte[])request.getAttribute(bsController.CONST_RECOVERED_REQUEST_CONTENT);
		if(datas!=null){
			String json = new String(datas);
			Map<String,Object> mapped = new JsonReader2Map().mapping(null, json, null);
			List<String> parameters = null;
			if(mapped!=null && mapped.size()>0){
				if(redrawcharts)
					mapped.put("model.redrawcharts", new Boolean(true));
				if(redraworders)
					mapped.put("model.redraworders", new Boolean(true));
				parameters = new ArrayList<String>();
				Iterator<String> it = mapped.keySet().iterator();
				while(it.hasNext())
					parameters.add(it.next().toString());
			}
			
			String output = JsonWriter.object2json(
					this.get_bean(),
					modelName,
					parameters);
			redrawcharts=false;
			redraworders=false;
			return output;
					
		}else{
			String output =  
					JsonWriter.object2json(
							this.get_bean(),
							modelName
							);
			redrawcharts=false;
			redraworders=false;
			return output;
		}
	}		

	private Date demoFromStartDate(int month){
		Calendar demoC = Calendar.getInstance();
		demoC.setTimeInMillis(startDate.getTime());
		demoC.set(Calendar.DATE,15);
		demoC.set(Calendar.MONTH, demoC.get(Calendar.MONTH)-1+month);
		return normalizeDate(demoC.getTime());
	}
	
	private Date normalizeDate(Date date){
		Calendar demoC = Calendar.getInstance();
		demoC.setTimeInMillis(date.getTime());
		demoC.set(Calendar.HOUR_OF_DAY,0);
		demoC.set(Calendar.MINUTE,0);
		demoC.set(Calendar.SECOND,0);
		return demoC.getTime();
	}	

	@Override
	public void reimposta() {
		super.reimposta();
		try{
			
			setStartDate(normalizeDate(new Date()));
			setFinishDate(demoFromStartDate(12));
			
			dayFinishDate = (getFinishDate().getTime()-getStartDate().getTime())/(1000 * 60 * 60 * 24);
			
			setQuantity(10000d);
			setFixedQuantity(10000d);
			setLeadDays(15d);




			
			setConsumption(
				new TreeMap<Date, Double>() {
					private static final long serialVersionUID = 1L;
					{
						put(demoFromStartDate(0),152d);
						put(demoFromStartDate(1),112d);
						put(demoFromStartDate(2),180d);
						put(demoFromStartDate(3),191d);
						put(demoFromStartDate(4),187d);
						put(demoFromStartDate(5),116d);
						put(demoFromStartDate(6),112d);
						put(demoFromStartDate(7),198d);
						put(demoFromStartDate(8),144d);
						put(demoFromStartDate(9),171d);
						put(demoFromStartDate(10),177d);
						put(demoFromStartDate(11),118d);			 
					}}
				);
				

			setSecureStock(
				new TreeMap<Date, Double>() {
					private static final long serialVersionUID = 1L;
					{
						put(demoFromStartDate(0),1150d);
						put(demoFromStartDate(1),1150d);
						put(demoFromStartDate(2),1100d);
						put(demoFromStartDate(3),1100d);
						put(demoFromStartDate(4),1075d);
						put(demoFromStartDate(5),1050d);
						put(demoFromStartDate(6),1025d);
						put(demoFromStartDate(7),1025d);
						put(demoFromStartDate(8),1050d);
						put(demoFromStartDate(9),1075d);
						put(demoFromStartDate(10),1150d);
						put(demoFromStartDate(11),1250d);			 
					}}
				);

				
			setProcessedOrders(
				new TreeMap<Date, Double>() {
					private static final long serialVersionUID = 1L;
					{
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170125"),3000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170210"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170320"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170410"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170510"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20171110"),15000d);
					}}
				);	
				
				
			setFixedFeatureOrders(
				new TreeSet<Date>(){
					private static final long serialVersionUID = 1L;
					{
						add(new SimpleDateFormat("yyyyMMdd").parse("20170301"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20170401"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20170501"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20170601"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20170701"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20170801"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20170901"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20171001"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20171101"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20171201"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20180101"));
						add(new SimpleDateFormat("yyyyMMdd").parse("20180201"));
					}}
				);
	
				setProxy(
						new DateWrapperD()
						.setLead(new PolynomialD().setConstant(0, getLeadDays()))
						.setComputingPlugin(new ApacheCommonMathLaguerre())
						.init(consumption, secureStock)
					);
				setSliders(new ViewSliders(this).init());
				
	
		}catch(Exception e){
			
		}
		
		
	}





	public ADateWrapper<Double> getProxy() {
		return proxy;
	}

	public void setProxy(ADateWrapper<Double> proxy) {
		this.proxy = proxy;
	}





	public SortedMap<Date, Double> getConsumption() {
		return consumption;
	}





	public void setConsumption(SortedMap<Date, Double> consumption) {
		this.consumption = consumption;
	}





	public SortedMap<Date, Double> getSecureStock() {
		return secureStock;
	}





	public void setSecureStock(SortedMap<Date, Double> secureStock) {
		this.secureStock = secureStock;
	}





	public SortedMap<Date, Double> getProcessedOrders() {
		return processedOrders;
	}





	public void setProcessedOrders(SortedMap<Date, Double> processedOrders) {
		this.processedOrders = processedOrders;
	}





	public SortedSet<Date> getFixedFeatureOrders() {
		return fixedFeatureOrders;
	}





	public void setFixedFeatureOrders(SortedSet<Date> fixedFeatureOrders) {
		this.fixedFeatureOrders = fixedFeatureOrders;
	}





	public Date getStartDate() {
		return startDate;
	}





	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}





	public Date getFinishDate() {
		return finishDate;
	}





	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}


	public ViewSliders getSliders() {
		return sliders;
	}


	public void setSliders(ViewSliders sliders) {
		this.sliders = sliders;
	}


	public boolean isRedrawcharts() {
		return redrawcharts;
	}


	public void setRedrawcharts(boolean redrawcharts) {
		this.redrawcharts = redrawcharts;
	}
	
	public boolean isRedraworders() {
		return redraworders;
	}


	public void setRedraworders(boolean redraworders) {
		this.redraworders = redraworders;
	}


	public int getTunemode() {
		return tunemode;
	}


	public void setTunemode(int tunemode) {
		this.tunemode = tunemode;
	}



	public int getPage_tab() {
		return page_tab;
	}


	public void setPage_tab(int page_tab) {
		this.page_tab = page_tab;
	}


	public long getDayFinishDate() {
		return dayFinishDate;
	}


	public void setDayFinishDate(long dayFinishDate) {
		this.dayFinishDate = dayFinishDate;
		setFinishDate(
					new Date(
							getStartDate().getTime()+this.dayFinishDate*1000 * 60 * 60 * 24
							)
				);
		this.redrawcharts=true;

	}


	public int getCalculatemode() {
		return calculatemode;
	}


	public void setCalculatemode(int calculatemode) {
		this.calculatemode = calculatemode;
		this.redrawcharts=true;
	}


	public int getChartConsumptionDayInterval() {
		return chartConsumptionDayInterval;
	}


	public void setChartConsumptionDayInterval(int chartConsumptionDayInterval) {
		this.chartConsumptionDayInterval = chartConsumptionDayInterval;
		this.redrawcharts=true;
	}


	public double getQuantity() {
		return quantity;
	}


	public void setQuantity(double quantity) {
		this.quantity = quantity;
		this.redrawcharts=true;
	}


	public SortedMap<Date, Double> getComputedOrders() {
		return computedOrders;
	}


	public void setComputedOrders(SortedMap<Date, Double> computedOrders) {
		this.computedOrders = computedOrders;
		this.redraworders=true;
	}


	public double getFixedQuantity() {
		return fixedQuantity;
	}


	public void setFixedQuantity(double fixedQuantity) {
		this.fixedQuantity = fixedQuantity;
		this.redrawcharts=true;
	}

	@Serialized
	public List<ViewOrders> getOrders(){
		List<ViewOrders> result = new ArrayList<ViewOrders>();
		if(computedOrders!=null && computedOrders.size()>0){
			for(Map.Entry<Date, Double> entry :  computedOrders.entrySet()){
				result.add(new ViewOrders(entry.getKey(), entry.getValue()));
			}
		}
		return result;
	}


	public double getLeadDays() {
		return leadDays;
	}


	public void setLeadDays(double leadDays) {
		this.leadDays = leadDays;
		if(getProxy()!=null)
			getProxy().setLead(new PolynomialD().setConstant(0, this.leadDays));
		this.redrawcharts=true;
	}


}