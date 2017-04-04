package it.dycomodel.admin.components.controllers; 




import java.io.Serializable;
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
import it.classhidra.annotation.elements.Rest;
import it.classhidra.annotation.elements.SessionDirective;
import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_bean;
import it.classhidra.core.tool.util.util_format;
import it.classhidra.framework.web.beans.option_element;
import it.classhidra.serialize.Format;
import it.classhidra.serialize.JsonReader2Map;
import it.classhidra.serialize.JsonWriter;
import it.classhidra.serialize.Serialized;
import it.dycomodel.admin.components.beans.DemoRawData;
import it.dycomodel.admin.components.beans.ViewChartAverage;
import it.dycomodel.admin.components.beans.ViewChartConsumption;
import it.dycomodel.admin.components.beans.ViewOrders;
import it.dycomodel.admin.components.beans.ViewSlider;
import it.dycomodel.admin.components.beans.ViewSliders;
import it.dycomodel.approximation.ISetAdapter;
import it.dycomodel.plugins.ComputingLaguerre;
import it.dycomodel.plugins.ComputingLaguerreComplex;
import it.dycomodel.plugins.ComputingLinear;
import it.dycomodel.plugins.ComputingPolynomialFitter;
import it.dycomodel.plugins.ComputingCubicSpline;
import it.dycomodel.polynomial.PolynomialD;
import it.dycomodel.wrappers.ADateApproximator;
import it.dycomodel.wrappers.ADateWrapper;
import it.dycomodel.wrappers.DateWrapperD;





@Action (
	path="demo",
	name="model",
//	memoryInSession="true",
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
	
	@Serialized
	private ADateApproximator approximator;
	
	private SortedMap<Date, Double> consumption;
	
	private SortedMap<Date, Double> secureStock;
	
	@Serialized
	private SortedMap<Long, Double> rawdata;
	
	private ISetAdapter setAdapter;
	
	@Serialized
	private SortedMap<Date, Double> processedOrders;

//	@Serialized
	private SortedSet<Date> fixedFeatureOrders;
	

	private SortedMap<Date, Double> computedOrders;
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date startDate;
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date finishDate;
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date startAvrDate;
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date finishAvrDate;	
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date forecastingStartDate;
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date forecastingFinishDate;
	
	@Serialized
	private ViewSliders sliders;
	
	@Serialized
	private boolean redrawcharts=false;
	
	@Serialized
	private boolean redraworders=false;
	
	@Serialized
	private boolean redrawslider=false;	
	
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
	private long dayStockDelta;	
	
	@Serialized
	private double leadDays;	
	
	@Serialized
	private int chartConsumptionDayInterval=1;	
	
	@Serialized(children=true,depth=2)
	private List<option_element> selectFixedPeriod;
	
	@Serialized(children=true,depth=2)
	private List<option_element> selectApproximationType;
	
	@Serialized(children=true,depth=2)
	private List<option_element> selectApproximationAlgorithm;	

	@Serialized
	private int leastSqDegree;	
	
	@Serialized
	private int approximationType;
	
	@Serialized
	private String approximationAlgorithm;
	
	@Serialized
	private int itemsForPack;		
	
	@Serialized
	private String fixedPeriod;
	
	@Serialized
	private boolean viewFullApproximation;	

	public ControllerDemo(){
		super();
	}
	

	@ActionCall(
			name="test",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/test/")})
			
	)
	public String test(){

		return "test";
		
	}	
	
	@ActionCall(
			name="chartavr",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/chartavr/")})
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
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/chartcons/")})
	)
	public String chartcons(){
		clear();
		ViewChartConsumption cha = new ViewChartConsumption(new String[]{"Period", "Consumption", "Daily Secure stock", "Reorder points"}, this);
		String json = JsonWriter.object2json(cha,"chart",null,true,3);
		clear();
		return json;
		
	}	
	
	

	@ActionCall(
			name="json2",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.POST,restmapping={@Rest(path="/demo/json/")})
			)
	public String json2(HttpServletRequest request, HttpServletResponse response){	
		return modelAsJson(request, response);
	}
	
	@ActionCall(
			name="xml2",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.POST,restmapping={@Rest(path="/demo/xml/")})
			)
	public String xml2(HttpServletRequest request, HttpServletResponse response){	
		return modelAsXml(request, response);
	}	
	
	@ActionCall(
			name="diff",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.POST,restmapping={@Rest(path="/demo/diff/")})
			)
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
				if(redrawslider)
					mapped.put("model.redrawslider", new Boolean(true));				
				
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

	private Date demoFromStartDate(int months){
		Calendar demoC = Calendar.getInstance();
		demoC.setTimeInMillis(startDate.getTime());
		demoC.set(Calendar.MONTH, demoC.get(Calendar.MONTH)-1+months);
		return normalizeDate(demoC.getTime());
	}
	
	public Date normalizeDate(Date date){
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
			setStartAvrDate(getStartDate());
			setFinishAvrDate(getFinishDate());
			
			Calendar calendar = Calendar.getInstance();
				calendar.setTime(getStartDate());
			setRawdata(DemoRawData.prepareDemoRawData(calendar.get(Calendar.YEAR)-1));
			setDayStockDelta(3);
			setItemsForPack(1);
			
			
			setAdapter = new ISetAdapter() {						
				private static final long serialVersionUID = 1L;

				@Override
				public SortedMap<Date, Double> adapt(SortedMap<Date, Double> set1) {
					SortedMap<Date, Double> result = new TreeMap<Date, Double>();
					for(Map.Entry<Date, Double> entry : set1.entrySet()) {
						result.put(entry.getKey(), entry.getValue()*getDayStockDelta());
					}
					return result;
				}
			};
			
			
			Calendar startAC = Calendar.getInstance();
				startAC.setTime(startDate);
				startAC.set(Calendar.DAY_OF_MONTH,1);
				startAC.set(Calendar.MONTH,startAC.get(Calendar.MONTH)-3);
				startAC.set(Calendar.YEAR,startAC.get(Calendar.YEAR));
			Calendar finishAC = Calendar.getInstance();
				finishAC.setTime(finishDate);
				finishAC.set(Calendar.DAY_OF_MONTH,finishAC.getActualMaximum(Calendar.DAY_OF_MONTH));
				finishAC.set(Calendar.MONTH,finishAC.get(Calendar.MONTH)+3);			
			

			
			setApproximator(
					new ADateApproximator()
					.setStartApproximationDate(startAC.getTime())
					.setFinishApproximationDate(finishAC.getTime())
					.setStartDate(getStartDate())
					.setType(ADateApproximator.APPROXIMATION_MEAN)
					.setStockAdapter(setAdapter)
					.approximation(getRawdata())
				);
			
			forecastingStartDate = getApproximator().getApproximation().getStartInterval();
			forecastingFinishDate = getApproximator().getApproximation().getFinishInterval();
			
			setConsumption(getApproximator().getForecastedConsumption(1));
			setSecureStock(getApproximator().getForecastedStock(1));
			
			for(Map.Entry<Date, Double> entry :  getConsumption().entrySet()){
				if( Integer.valueOf(util_format.dataToString(entry.getKey(), "yyyyMM")).intValue() == Integer.valueOf(util_format.dataToString(getStartDate(), "yyyyMM")).intValue())
					setStartAvrDate(entry.getKey());
				if( Integer.valueOf(util_format.dataToString(entry.getKey(), "yyyyMM")).intValue() == Integer.valueOf(util_format.dataToString(getFinishDate(), "yyyyMM")).intValue())
					setFinishAvrDate(entry.getKey());
				
			}
			
			
			
			dayFinishDate = (getFinishDate().getTime()-getStartDate().getTime())/(1000 * 60 * 60 * 24);
			
			setQuantity(1000d);
			setFixedQuantity(1000d);
			setLeadDays(15d);



			
			setProcessedOrders(
				new TreeMap<Date, Double>() {
					private static final long serialVersionUID = 1L;
					{
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170125"),3000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170210"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170320"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170410"),1500d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20170510"),15000d);
//						put(new SimpleDateFormat("yyyyMMdd").parse("20171110"),15000d);
					}}
				);	
			
			setFixedFeatureOrders(
					new TreeSet<Date>(){
						private static final long serialVersionUID = 1L;
						{			
						}}
					);		
			
			setSelectFixedPeriod(
				new ArrayList<option_element>(){
					private static final long serialVersionUID = 1L;
					{	
						add(new option_element("w", "Every week"));
						add(new option_element("m", "Every month"));
						add(new option_element("3m", "Every quarter"));
						add(new option_element("6m", "Every semester"));
						add(new option_element("12m", "Every year"));
						
					}}
				);
			setFixedPeriod("m");
			
			setSelectApproximationType(
					new ArrayList<option_element>(){
						private static final long serialVersionUID = 1L;
						{	
							add(new option_element("1", "Simple mean"));
							add(new option_element("2", "Mean quantile 25"));
							add(new option_element("3", "Mean quantile 75"));
							add(new option_element("4", "Neural Network"));
							
						}}
					);
			setApproximationType(1);
			
			setSelectApproximationAlgorithm(
					new ArrayList<option_element>(){
						private static final long serialVersionUID = 1L;
						{	
							add(new option_element("PL", 	"Global - Polynomial Laguerre"));
							add(new option_element("PLS", 	"Global - Polynomial least squares"));
							add(new option_element("LIN", 	"Local - Linear method"));
							add(new option_element("SPL", 	"Local - Cubic Spline method"));
							
						}}
					);
			setApproximationAlgorithm("SPL");
			
			setProxy(
						new DateWrapperD()
						.setLead(new PolynomialD().setConstant(0, getLeadDays()))
						.setComputingPlugin(new ComputingCubicSpline())
						.init(getConsumption(), getSecureStock())
					);
				
			setSliders(
						new ViewSliders(this)
						.init(false)
					);
			
			setDayFinishDate(365);
			
			if(getProxy()!=null && getProxy().getComputingPlugin()!=null){
				if(getProxy().getComputingPlugin() instanceof ComputingPolynomialFitter)
					this.leastSqDegree = ((ComputingPolynomialFitter)(getProxy().getComputingPlugin())).getDegree();
			}				
	
		}catch(Exception e){
			e.toString();
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


	public SortedMap<Date, Double> getEnabledConsumption() {
		if(getSliders()!=null && getSliders().getConsumption()!=null && getSliders().getConsumption().size()>0){
			SortedMap<Date, Double> enabledConsumption = new TreeMap<Date, Double>();
			enabledConsumption.putAll(consumption);
			for(ViewSlider consSlider: getSliders().getConsumption()){
				if(!consSlider.isEnabled())
					enabledConsumption.remove(consSlider.getPoint());
			}
			return enabledConsumption;
		}
		return consumption;
	}


	public void setConsumption(SortedMap<Date, Double> consumption) {
		this.consumption = consumption;
	}





	public SortedMap<Date, Double> getSecureStock() {
		return secureStock;
	}

	public SortedMap<Date, Double> getEnabledSecureStock() {
		if(getSliders()!=null && getSliders().getStock()!=null && getSliders().getStock().size()>0){
			SortedMap<Date, Double> enabledStock = new TreeMap<Date, Double>();
			enabledStock.putAll(secureStock);
			for(ViewSlider consSlider: getSliders().getStock()){
				if(!consSlider.isEnabled())
					enabledStock.remove(consSlider.getPoint());
			}
			return enabledStock;
		}
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


	public List<option_element> getSelectFixedPeriod() {
		return selectFixedPeriod;
	}


	public void setSelectFixedPeriod(List<option_element> selectFixedPeriod) {
		this.selectFixedPeriod = selectFixedPeriod;
	}


	public String getFixedPeriod() {
		return fixedPeriod;
	}


	public void setFixedPeriod(String fixedPeriod) {
		this.fixedPeriod = fixedPeriod;
		getFixedFeatureOrders().clear();
		
		Calendar current = Calendar.getInstance();
		current.setTimeInMillis(getStartDate().getTime());
		if(this.fixedPeriod.equalsIgnoreCase("w")){
			current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			current.set(Calendar.WEEK_OF_MONTH, current.get(Calendar.WEEK_OF_MONTH)+1);			
			while(current.getTime().before(getFinishDate())){
				getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
				current.set(Calendar.WEEK_OF_MONTH, current.get(Calendar.WEEK_OF_MONTH)+1);
			}
			getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
		}else if(this.fixedPeriod.equalsIgnoreCase("m")){
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.MONTH, current.get(Calendar.MONTH)+1);			
			while(current.getTime().before(getFinishDate())){
				getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
				current.set(Calendar.MONTH, current.get(Calendar.MONTH)+1);			
			}
			getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
		}else if(this.fixedPeriod.equalsIgnoreCase("3m")){
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.MONTH, current.get(Calendar.MONTH)+3);			
			while(current.getTime().before(getFinishDate())){
				getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
				current.set(Calendar.MONTH, current.get(Calendar.MONTH)+3);			
			}
			getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));	
		}else if(this.fixedPeriod.equalsIgnoreCase("6m")){
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.MONTH, current.get(Calendar.MONTH)+6);			
			while(current.getTime().before(getFinishDate())){
				getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
				current.set(Calendar.MONTH, current.get(Calendar.MONTH)+6);			
			}
			getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
		}else if(this.fixedPeriod.equalsIgnoreCase("12m")){
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.MONTH, current.get(Calendar.MONTH)+12);			
			while(current.getTime().before(getFinishDate())){
				getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));
				current.set(Calendar.MONTH, current.get(Calendar.MONTH)+12);			
			}
			getFixedFeatureOrders().add(new Date(current.getTimeInMillis()));	
		}
		
		
		this.redrawcharts=true;
	}


	public SortedMap<Long, Double> getRawdata() {
		return rawdata;
	}


	public void setRawdata(SortedMap<Long, Double> rawdata) {
		this.rawdata = rawdata;
	}


	public long getDayStockDelta() {
		return dayStockDelta;
	}


	public void setDayStockDelta(long dayStockDelta) {
		if(this.dayStockDelta!=dayStockDelta){
			this.dayStockDelta = dayStockDelta;
			if(getApproximator()!=null){				
				getApproximator().approximation(getRawdata());			
				setConsumption(getApproximator().getForecastedConsumption(1));
				setSecureStock(getApproximator().getForecastedStock(1));
				if(getProxy()!=null){
					try{
						getProxy().init(getEnabledConsumption(), getEnabledSecureStock());
						getSliders().init(true);
						redraworders=true;
					}catch(Exception e){
						
					}
					
				}
			}
			
		}
	}


	public ADateApproximator getApproximator() {
		return approximator;
	}


	public void setApproximator(ADateApproximator approximator) {
		this.approximator = approximator;
	}


	public List<option_element> getSelectApproximationType() {
		return selectApproximationType;
	}


	public void setSelectApproximationType(List<option_element> selectApproximationType) {
		this.selectApproximationType = selectApproximationType;
	}


	public int getApproximationType() {
		return approximationType;
	}


	public void setApproximationType(int approximationType) {
		if(this.approximationType!=approximationType){
			this.approximationType = approximationType;
			if(this.approximationType==4)
				this.approximationType=1;
			
			if(getApproximator()!=null){				
				getApproximator()
				.setType(this.approximationType)
				.approximation(getRawdata());	
				
				setConsumption(getApproximator().getForecastedConsumption(1));
				setSecureStock(getApproximator().getForecastedStock(1));
				if(getProxy()!=null){
					try{
						getProxy().init(getEnabledConsumption(), getEnabledSecureStock());
						getSliders().init(true);
						redraworders=true;
					}catch(Exception e){
						
					}
					
				}
			}

		}
		
	}


	public int getItemsForPack() {
		return itemsForPack;
	}


	public void setItemsForPack(int itemsForPack) {
		this.itemsForPack = itemsForPack;
		this.redrawcharts=true;
		this.redraworders=true;
	}


	public Date getStartAvrDate() {
		return startAvrDate;
	}


	public void setStartAvrDate(Date startAvrDate) {
		this.startAvrDate = startAvrDate;
	}


	public Date getFinishAvrDate() {
		return finishAvrDate;
	}


	public void setFinishAvrDate(Date finishAvrDate) {
		this.finishAvrDate = finishAvrDate;
	}


	public List<option_element> getSelectApproximationAlgorithm() {
		return selectApproximationAlgorithm;
	}


	public void setSelectApproximationAlgorithm(List<option_element> selectApproximationAlgorithm) {
		this.selectApproximationAlgorithm = selectApproximationAlgorithm;
	}


	public String getApproximationAlgorithm() {
		return approximationAlgorithm;
	}


	public void setApproximationAlgorithm(String approximationAlgorithm) throws Exception{
		if(this.approximationAlgorithm==null || !this.approximationAlgorithm.equals(approximationAlgorithm)){
			this.approximationAlgorithm = approximationAlgorithm;
			if(getProxy()!=null){
				if(this.approximationAlgorithm.equals("PL")){
					getProxy()
						.setComputingPlugin(new ComputingLaguerre())
						.init(getEnabledConsumption(), getEnabledSecureStock());
				}else  if(this.approximationAlgorithm.equals("PLC")){
					getProxy()
						.setComputingPlugin(new ComputingLaguerreComplex())
						.init(getEnabledConsumption(), getEnabledSecureStock());
				}else if(this.approximationAlgorithm.equals("PLS")){
					getProxy()
						.setComputingPlugin(new ComputingPolynomialFitter())
						.init(getEnabledConsumption(), getEnabledSecureStock());
					
					if(getProxy()!=null && getProxy().getComputingPlugin()!=null){
						if(getProxy().getComputingPlugin() instanceof ComputingPolynomialFitter)
							this.leastSqDegree = ((ComputingPolynomialFitter)(getProxy().getComputingPlugin())).getDegree();
					}	
				}else  if(this.approximationAlgorithm.equals("LIN")){
					getProxy()
						.setComputingPlugin(new ComputingLinear())
						.init(getEnabledConsumption(), getEnabledSecureStock());
				}else if(this.approximationAlgorithm.equals("SPL")){
					getProxy()
						.setComputingPlugin(new ComputingCubicSpline())
						.init(getEnabledConsumption(), getEnabledSecureStock());
				}
				
				this.redrawcharts=true;
				this.redraworders=true;
			}
		}
		
	}


	public boolean isViewFullApproximation() {
		return viewFullApproximation;
	}


	public void setViewFullApproximation(boolean viewFullApproximation) {
		if(this.viewFullApproximation!=viewFullApproximation){
			this.viewFullApproximation = viewFullApproximation;
			this.redrawcharts=true;
			getSliders().init(true);
			redrawslider=true;
		}
	}


	public boolean isRedrawslider() {
		return redrawslider;
	}


	public void setRedrawslider(boolean redrawslider) {
		this.redrawslider = redrawslider;
	}


	public int getLeastSqDegree() {
		if(getProxy()!=null && getProxy().getComputingPlugin()!=null){
			if(getProxy().getComputingPlugin() instanceof ComputingPolynomialFitter)
				return ((ComputingPolynomialFitter)(getProxy().getComputingPlugin())).getDegree();
		}
		return leastSqDegree;
	}


	public void setLeastSqDegree(int leastSqDegree) throws Exception{
		if(this.leastSqDegree != leastSqDegree){
			this.leastSqDegree = leastSqDegree;
			getProxy()
				.setComputingPlugin(new ComputingPolynomialFitter().setDegree(this.leastSqDegree))
				.init(getEnabledConsumption(), getEnabledSecureStock());
			this.redrawcharts=true;
			this.redraworders=true;
		}
		
	}


}
