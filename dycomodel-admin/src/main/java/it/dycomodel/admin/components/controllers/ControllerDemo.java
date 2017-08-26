package it.dycomodel.admin.components.controllers; 




import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.classhidra.annotation.elements.Action;
import it.classhidra.annotation.elements.ActionCall;
import it.classhidra.annotation.elements.Entity;
import it.classhidra.annotation.elements.Expose;
import it.classhidra.annotation.elements.Parameter;
import it.classhidra.annotation.elements.Redirect;
import it.classhidra.annotation.elements.Rest;
import it.classhidra.annotation.elements.SessionDirective;
import it.classhidra.core.controller.bsController;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_bean;
import it.classhidra.core.controller.response_wrapper;
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
import it.dycomodel.approximation.DefaultSetAdapter;
import it.dycomodel.approximation.ISetAdapter;
import it.dycomodel.plugins.ComputingCubicSpline;
import it.dycomodel.plugins.ComputingLaguerre;
import it.dycomodel.plugins.ComputingLaguerreComplex;
import it.dycomodel.plugins.ComputingLinear;
import it.dycomodel.plugins.ComputingPolynomialFitter;
import it.dycomodel.polynomial.PolynomialD;
import it.dycomodel.utils.Normalizer;
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
	
	@Serialized(output=@Format(format="dd/MM/yyyy"), input=@Format(format="dd/MM/yyyy"))
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
	
	@Serialized
	private String uploadType;	

	public ControllerDemo(){
		super();
	}
	

	@ActionCall(
			name="upload",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/upload/")})
			
	)
	public response_wrapper upload(HttpServletRequest request, HttpServletResponse response){

		int indexFile=0;
		boolean isFile=true;
		String error=null;
		SortedMap<Long, Double> newrawdata = new TreeMap<Long, Double>();
		while(isFile){
			@SuppressWarnings("unchecked")
			HashMap<String, Object> file = (HashMap<String, Object>)get_bean().getParametersMP().get("file"+indexFile);
			if(file!=null){
				
				byte[] content = (byte[])file.get("content");

				
				if(content!=null){
					
					InputStream is = null;
			        BufferedReader bfReader = null;
			        try {
			            is = new ByteArrayInputStream(content);
			            bfReader = new BufferedReader(new InputStreamReader(is));
			            String temp = null;
			            int row=0;
			            while(error==null && (temp = bfReader.readLine()) != null){
			                if(temp.trim().length()>0){
			                	String[] parts = temp.split(",");
			                	if(parts.length!=2)
			                		error="File data format error - row "+row;
			                	else{
			                		try{
			                			newrawdata.put(new SimpleDateFormat("dd/MM/yyyy").parse(parts[0].trim()).getTime(), Double.valueOf(parts[1].trim()));
			                		}catch(Exception pe){
			                			error="File data format error - row "+row;
			                		}
			                	}
			                }
			                row++;
			            }
			        } catch (IOException e) {
			            error= e.toString();
			        } finally {
			            try{
			                if(is != null) is.close();
			            } catch (Exception ex){
			                 
			            }
			        }
					
			        if(newrawdata.size()>0){
			        	if(rawdata==null)
			        		rawdata = newrawdata;
			        	else{
			        		rawdata.clear();
			        		rawdata.putAll(newrawdata);
			        	}
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
								}catch(Exception e){
									
								}
								
							}
						}			        	
			        }
					
				}else
					error="Empty file";
			}else
				isFile=false;
			indexFile++;
		}
		if(error!=null)
			return new response_wrapper()
					.setContent(error)
					.setResponseStatus(Rest.MISSING_PARAMETERS_400);	
		else{
			setInfo("Consumption data was loaded successfully ");
			String json = modelAsJson(request, response);
			setInfo("");
			return new response_wrapper()
					.setContent(json)
					.setResponseStatus(Rest.EXIST_200);	
		}
			
	}	
	
	@ActionCall(
			name="uploadinventory",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/upload/")})
			
	)
	public response_wrapper uploadinventory(HttpServletRequest request, HttpServletResponse response){

		int indexFile=0;
		boolean isFile=true;
		String error=null;
		SortedMap<Date, Double> inventory = new TreeMap<Date, Double>();
		while(isFile){
			@SuppressWarnings("unchecked")
			HashMap<String, Object> file = (HashMap<String, Object>)get_bean().getParametersMP().get("file"+indexFile);
			if(file!=null){
				
				byte[] content = (byte[])file.get("content");

				
				if(content!=null){
					
					InputStream is = null;
			        BufferedReader bfReader = null;
			        try {
			            is = new ByteArrayInputStream(content);
			            bfReader = new BufferedReader(new InputStreamReader(is));
			            String temp = null;
			            int row=0;
			            while(error==null && (temp = bfReader.readLine()) != null){
			                if(temp.trim().length()>0){
			                	String[] parts = temp.split(",");
			                	if(parts.length!=2)
			                		error="File data format error - row "+row;
			                	else{
			                		try{
			                			inventory.put(new SimpleDateFormat("dd/MM/yyyy").parse(parts[0].trim()), Double.valueOf(parts[1].trim()));
			                		}catch(Exception pe){
			                			error="File data format error - row "+row;
			                		}
			                	}
			                }
			                row++;
			            }
			        } catch (IOException e) {
			            error= e.toString();
			        } finally {
			            try{
			                if(is != null) is.close();
			            } catch (Exception ex){
			                 
			            }
			        }
					
			        if(inventory.size()>0){
			        	Double last = inventory.get(inventory.lastKey());
			        	setQuantity(last);
			        	setStartDate(inventory.lastKey());
			        }
					
				}else
					error="Empty file";
			}else
				isFile=false;
			indexFile++;
		}
		if(error!=null)
			return new response_wrapper()
					.setContent(error)
					.setResponseStatus(Rest.MISSING_PARAMETERS_400);	
		else{
			setInfo("Inventory data was loaded successfully ");
			String json = modelAsJson(request, response);
			setInfo("");
			return new response_wrapper()
					.setContent(json)
					.setResponseStatus(Rest.EXIST_200);	
		}
			
	}	
	
	
	@ActionCall(
			name="uploadmodel",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/upload/")})
			
	)
	public response_wrapper uploadmodel(HttpServletRequest request, HttpServletResponse response){
		
		int indexFile=0;
		boolean isFile=true;
		String error=null;
		while(isFile){
			@SuppressWarnings("unchecked")
			HashMap<String, Object> file = (HashMap<String, Object>)get_bean().getParametersMP().get("file"+indexFile);
			if(file!=null){
				
				byte[] content = (byte[])file.get("content");
				
				if(content!=null){
					try{
						Document documentXML = Normalizer.readXMLData(content);
						if(documentXML!=null){
							Node node = null;
							try{
								int first=0;
								while(node==null && first < documentXML.getChildNodes().getLength()){
									if(documentXML.getChildNodes().item(first).getNodeType()== Node.ELEMENT_NODE)
										node = documentXML.getChildNodes().item(first);
									first++;
								}
							}catch(Exception e){}

							if(node!=null && node.getNodeName().equals("dycomodel")){
								if(this.init(node)){
									error = "Load error: incorrect/incomplete data";
								}else{
									getSliders().init(true);
									setRedrawcharts(true);
									setRedraworders(true);
								}
							}


						}
					}catch(Exception e){
						error = "System error "+e.toString();
					}
					
				}else
					error="Empty file";
			}else
				isFile=false;
			indexFile++;
		}
		if(error!=null)
			return new response_wrapper()
					.setContent(error)
					.setResponseStatus(Rest.MISSING_PARAMETERS_400);	
		else{
			setInfo("Consumption model was loaded successfully ");
			String json = modelAsJson(request, response);
			setInfo("");
			return new response_wrapper()
					.setContent(json)
					.setResponseStatus(Rest.EXIST_200);	
		}		
	}
	
	@ActionCall(
			name="download",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/download/")})
			
	)
	public response_wrapper download(){
		StringBuffer buffer = new StringBuffer();
		for(Map.Entry<Long, Double> entry : rawdata.entrySet()) 
			buffer.append(
					new SimpleDateFormat("dd/MM/yyyy").format(new Date(entry.getKey()))+","+entry.getValue()+"\n"
			);
		return new response_wrapper()
				.setContent(buffer.toString().getBytes())
				.setContentType("application/csv")
				.setContentName("data.csv");
	}	
	
	@ActionCall(
			name="model",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/model/")})
			
	)
	public response_wrapper model(){		
		return new response_wrapper()
				.setContent((getProxy()!=null)?getProxy().toXml(1,prepareInfo(1)).getBytes():new byte[0])
				.setContentType("application/xml")
				.setContentName("model.xml");
	}	
	
	@ActionCall(
			name="orders",
			navigated="false",
			Expose=@Expose(methods = {Expose.POST,Expose.GET},restmapping={@Rest(path="/demo/model/")})
			
	)
	public response_wrapper orders(){		
		StringBuffer buffer = new StringBuffer();
		for(Map.Entry<Date, Double> entry : computedOrders.entrySet()) 
			buffer.append(
					new SimpleDateFormat("dd/MM/yyyy").format(entry.getKey())+","+entry.getValue()+"\n"
			);
		return new response_wrapper()
				.setContent(buffer.toString().getBytes())
				.setContentType("application/csv")
				.setContentName("orders.csv");
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
			name="editconsumption",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.GET)
			)
	public String editconsumption_get(@Parameter(name="value") String desc){	
		if(getSliders()!=null && getSliders().getConsumption()!=null){
			for(ViewSlider slider:getSliders().getConsumption()){
				if(slider.getDescription().equals(desc)){
					return JsonWriter.object2json(
							slider,
							"slider");
				}
			}
		}
		return "";
	}	

	@ActionCall(
			name="editconsumption",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.PUT)
			)
	public String editconsumption_update(@Parameter(name="value") ViewSlider value){	
		if(getSliders()!=null && getSliders().getConsumption()!=null){
			for(ViewSlider slider:getSliders().getConsumption()){
				if(slider.getDescription().equals(value.getDescription())){
					Date oldPoint = slider.getPoint();
					slider.setPointL(value.getPointL());
					slider.setValue(value.getValue());
					slider.setDescription(util_format.dataToString(slider.getPoint(), "MMM yyyy"));
					getSliders().changedD(slider, oldPoint);
					getSliders().init(true);
					return JsonWriter.object2json(
							getSliders(),
							"sliders");
				}
			}
		}
		return "";
	}
	
	@ActionCall(
			name="editconsumption",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.POST)
			)
	public String editconsumption_insert(@Parameter(name="value") ViewSlider value){
		
		getConsumption().put(value.getPoint(),value.getValue());
		getSecureStock().put(value.getPoint(),value.getValue()*getDayStockDelta());
		getSliders().init(true);
		try{
		getProxy().init(getEnabledConsumption(), getEnabledSecureStock());
		}catch(Exception e){
			
		}
		setRedrawcharts(true);
					return JsonWriter.object2json(
							getSliders(),
							"sliders");

	}	
	
	@ActionCall(
			name="editconsumption",
			navigated="false",
			Redirect=@Redirect(contentType="application/json"),
			Expose=@Expose(method = Expose.DELETE)
			)
	public String editconsumption_delete(@Parameter(name="value") ViewSlider value){	
		if(getSliders()!=null && getSliders().getConsumption()!=null){
			for(ViewSlider slider:getSliders().getConsumption()){
				if(slider.getDescription().equals(value.getDescription())){
					Date oldPoint = slider.getPoint();
					getSliders().removeD(slider, oldPoint);
					getSliders().init(true);
					return JsonWriter.object2json(
							getSliders(),
							"sliders");
				}
			}
		}
		return "";
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
			@SuppressWarnings("unchecked")
			Map<String,Object> mapped = new JsonReader2Map().mapping(null, json, null);
			List<String> parameters = null;
			if(mapped!=null && mapped.size()>0){
				if(redrawcharts)
					mapped.put("model.redrawcharts", new Boolean(true));
				if(redraworders)
					mapped.put("model.redraworders", new Boolean(true));
				if(redrawslider)
					mapped.put("model.redrawslider", new Boolean(true));	
				if(getInfo()!=null && getInfo().trim().length()>0)
					mapped.put("model.info", getInfo());

				if(getWarning()!=null && getWarning().trim().length()>0)
					mapped.put("model.warning", getWarning());
			
				if(getError()!=null && getError().trim().length()>0)
					mapped.put("model.error", getError());
	
				if(getSuccess()!=null && getSuccess().trim().length()>0)
					mapped.put("model.success", getSuccess());
			
				
				parameters = new ArrayList<String>();
				Iterator<String> it = mapped.keySet().iterator();
				while(it.hasNext())
					parameters.add(it.next().toString());
			}
			
			String output = JsonWriter.object2json(
					this.get_bean(),
					modelName,
					parameters);
			setRedrawcharts(false);
			redraworders=false;
			setInfo("");
			setWarning("");
			setError("");
			setSuccess("");
			return output;
					
		}else{
			String output =  
					JsonWriter.object2json(
							this.get_bean(),
							modelName
							);
			setRedrawcharts(false);
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
		demoC.set(Calendar.MILLISECOND,0);
		return demoC.getTime();
	}	

	@Override
	public void reimposta() {
		super.reimposta();
		try{
			setUploadType("C");
			setStartDate(normalizeDate(new Date()));
			setFinishDate(demoFromStartDate(12));
			setStartAvrDate(getStartDate());
			setFinishAvrDate(getFinishDate());
			
			Calendar calendar = Calendar.getInstance();
				calendar.setTime(getStartDate());
			setRawdata(DemoRawData.prepareDemoRawData(calendar.get(Calendar.YEAR)-1));
			setDayStockDelta(3);
			setItemsForPack(1);
			
			
			setAdapter = new DefaultSetAdapter().setDayStockDelta(new Double(getDayStockDelta()));
			
			
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

	private boolean init(Node node) throws Exception{
		
		ControllerDemo copy = new ControllerDemo();
		boolean error = false;
		NodeList list = node.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node child_node = list.item(i);
			if(child_node.getNodeType()== Node.ELEMENT_NODE){
				if(child_node.getNodeName().equalsIgnoreCase("wrapper")){
					try{
						@SuppressWarnings("unchecked")
						ADateWrapper<Double> newProxy = Class.forName(child_node.getAttributes().getNamedItem("provider").getNodeValue()).asSubclass(ADateWrapper.class).newInstance();
						newProxy.init(child_node);
						copy.setProxy(newProxy);
					}catch(Exception e){
						e.printStackTrace();
						error = true;
					}
				}else if(child_node.getNodeName().equalsIgnoreCase("baseinfo")){					
					NodeList listchild = child_node.getChildNodes();
					for(int j=0;j<listchild.getLength();j++){
						Node child_node1 = listchild.item(j);
						if(child_node1.getNodeType()== Node.ELEMENT_NODE){
							if(child_node1.getNodeName().equalsIgnoreCase("startAvrDate")){
								try{
									copy.setStartAvrDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("finishAvrDate")){
								try{
									copy.setFinishAvrDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("approximator")){
								try{
									copy.setApproximator(Class.forName(child_node1.getFirstChild().getNodeValue()).asSubclass(ADateApproximator.class).newInstance());
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("setAdapter")){
								try{
									copy.setSetAdapter(Class.forName(child_node1.getFirstChild().getNodeValue()).asSubclass(ISetAdapter.class).newInstance());
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("approximationType")){
								try{
									copy.setApproximationTypeOnly(Integer.valueOf(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("approximationAlgorithm")){
								try{
									copy.setApproximationAlgorithmOnly(child_node1.getFirstChild().getNodeValue());
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("itemsForPack")){
								try{
									copy.setItemsForPack(Integer.valueOf(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("dayStockDelta")){
								try{
									copy.setDayStockDeltaOnly(Integer.valueOf(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("fixedPeriod")){
								try{
									copy.setFixedPeriodOnly(child_node1.getFirstChild().getNodeValue());
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("quantity")){
								try{
									copy.setQuantity(Double.valueOf(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("fixedQuantity")){
								try{
									copy.setFixedQuantity(Double.valueOf(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("leadDays")){
								try{
									copy.setLeadDays(Double.valueOf(child_node1.getFirstChild().getNodeValue()));
								}catch(Exception e){
									e.printStackTrace();
									error = true;
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("consumptions")){ 
								copy.setConsumption(new TreeMap<Date,Double>());
								NodeList list2 = child_node1.getChildNodes();
								for(int k=0;k<list2.getLength();k++){
									Node child_node2 = list2.item(k);
									if(child_node2.getNodeType()== Node.ELEMENT_NODE){
										if(child_node2.getNodeName().equalsIgnoreCase("consumption")){
											NodeList list3 = child_node2.getChildNodes();
											Date currentPosition = null;
											for(int l=0;l<list3.getLength();l++){
												Node child_node3 = list3.item(l);
												if(child_node3.getNodeType()== Node.ELEMENT_NODE){													
													if(child_node3.getNodeName().equalsIgnoreCase("point")){
														try{
															currentPosition = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(child_node3.getFirstChild().getNodeValue());
														}catch(Exception e){
															e.printStackTrace();
														}
													}else if(child_node3.getNodeName().equalsIgnoreCase("value")){
														try{
															if(currentPosition!=null){
																copy.getConsumption().put(
																		currentPosition,
																		Double.valueOf(child_node3.getFirstChild().getNodeValue())
																	);
																currentPosition = null;
															}
														}catch(Exception e){
															e.printStackTrace();
														}
													}
												}
											}
										}
									}								
								}
							}else if(child_node1.getNodeName().equalsIgnoreCase("secureStocks")){ 
								copy.setSecureStock(new TreeMap<Date,Double>());
								NodeList list2 = child_node1.getChildNodes();
								for(int k=0;k<list2.getLength();k++){
									Node child_node2 = list2.item(k);
									if(child_node2.getNodeType()== Node.ELEMENT_NODE){
										if(child_node2.getNodeName().equalsIgnoreCase("secureStock")){
											NodeList list3 = child_node2.getChildNodes();
											Date currentPosition = null;
											for(int l=0;l<list3.getLength();l++){
												Node child_node3 = list3.item(l);
												if(child_node3.getNodeType()== Node.ELEMENT_NODE){													
													if(child_node3.getNodeName().equalsIgnoreCase("point")){
														try{
															currentPosition = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(child_node3.getFirstChild().getNodeValue());
														}catch(Exception e){
															e.printStackTrace();
														}
													}else if(child_node3.getNodeName().equalsIgnoreCase("value")){
														try{
															if(currentPosition!=null){
																copy.getSecureStock().put(
																		currentPosition,
																		Double.valueOf(child_node3.getFirstChild().getNodeValue())
																	);
																currentPosition = null;
															}
														}catch(Exception e){
															e.printStackTrace();
														}
													}
												}
											}
										}
									}								
								}
							}

						}
					}
				}

			}
		}
		
		if(!error){
			consumption = copy.getConsumption();
			secureStock = copy.getSecureStock();
			proxy = copy.getProxy();
			approximator = copy.getApproximator();
			dayStockDelta = copy.getDayStockDelta();
			setAdapter = copy.getSetAdapter();
				setAdapter.setDayStockDelta(Double.valueOf(dayStockDelta));
				approximator.setStockAdapter(setAdapter);				
				
			approximationType = copy.getApproximationType();
				approximator.setType(approximationType);
				
			if(getStartAvrDate()!=null && getFinishAvrDate()!=null){
				Calendar startAC = Calendar.getInstance();
					startAC.setTime(getStartAvrDate());
					startAC.set(Calendar.DAY_OF_MONTH,1);
					startAC.set(Calendar.MONTH,startAC.get(Calendar.MONTH)-3);
					startAC.set(Calendar.YEAR,startAC.get(Calendar.YEAR));
				Calendar finishAC = Calendar.getInstance();
					finishAC.setTime(getFinishAvrDate());
					finishAC.set(Calendar.DAY_OF_MONTH,finishAC.getActualMaximum(Calendar.DAY_OF_MONTH));
					finishAC.set(Calendar.MONTH,finishAC.get(Calendar.MONTH)+3);	
					
				approximator.setStartApproximationDate(startAC.getTime());
				approximator.setFinishApproximationDate(finishAC.getTime());
				approximator.setStartDate(getStartAvrDate());
			}
			

				
			approximationAlgorithm = copy.getApproximationAlgorithm();
			itemsForPack = copy.getItemsForPack();
			
			fixedPeriod = copy.getFixedPeriod();
			quantity = copy.getQuantity();
			fixedQuantity = copy.getFixedQuantity();
			leadDays = copy.getLeadDays();
			
			
		



		}
		return error;
	}

	private String prepareInfo(int level){
		String result="";
		result+=Normalizer.spaces(level)+"<baseinfo>\n";
		if(startAvrDate!=null)
			result+=Normalizer.spaces(level+1)+"<startAvrDate>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(startAvrDate)+"</startAvrDate>\n";	
		if(finishAvrDate!=null)
			result+=Normalizer.spaces(level+1)+"<finishAvrDate>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(finishAvrDate)+"</finishAvrDate>\n";	

		if(approximator!=null)
			result+=Normalizer.spaces(level+1)+"<approximator>"+approximator.getClass().getName()+"</approximator>\n";	
		if(setAdapter!=null)
			result+=Normalizer.spaces(level+1)+"<setAdapter>"+setAdapter.getClass().getName()+"</setAdapter>\n";	
		result+=Normalizer.spaces(level+1)+"<approximationType>"+approximationType+"</approximationType>\n";
	

		if(approximationAlgorithm!=null)
			result+=Normalizer.spaces(level+1)+"<approximationAlgorithm>"+approximationAlgorithm+"</approximationAlgorithm>\n";	
		result+=Normalizer.spaces(level+1)+"<itemsForPack>"+itemsForPack+"</itemsForPack>\n";	
		result+=Normalizer.spaces(level+1)+"<dayStockDelta>"+dayStockDelta+"</dayStockDelta>\n";
		if(fixedPeriod!=null)
			result+=Normalizer.spaces(level+1)+"<fixedPeriod>"+fixedPeriod+"</fixedPeriod>\n";	
		
		result+=Normalizer.spaces(level+1)+"<quantity>"+quantity+"</quantity>\n";
		result+=Normalizer.spaces(level+1)+"<fixedQuantity>"+fixedQuantity+"</fixedQuantity>\n";
		result+=Normalizer.spaces(level+1)+"<leadDays>"+leadDays+"</leadDays>\n";

		
		if(consumption!=null && consumption.size()>0){
			SortedMap<Date, Double> consumptionDisabled = new TreeMap<Date, Double>();
			if(sliders!=null && sliders.getConsumption()!=null){			
				for(ViewSlider consSlider: sliders.getConsumption()){
					if(!consSlider.isEnabled())
						consumptionDisabled.put(consSlider.getPoint(), consSlider.getValue());
				}
			}
			
			result+=Normalizer.spaces(level+1)+"<consumptions>\n";
			for(Map.Entry<Date, Double> entry : consumption.entrySet()){
				result+=Normalizer.spaces(level+2)+"<consumption>\n";
				result+=Normalizer.spaces(level+3)+"<point>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(entry.getKey())+"</point>\n";
				result+=Normalizer.spaces(level+3)+"<value>"+entry.getValue().toString()+"</value>\n";
				if(consumptionDisabled.get(entry.getKey())!=null)
					result+=Normalizer.spaces(level+3)+"<disabled>true</disabled>\n";				
				result+=Normalizer.spaces(level+2)+"</consumption>\n";
			}
			
			result+=Normalizer.spaces(level+1)+"</consumptions>\n";
		}
		
		if(secureStock!=null && secureStock.size()>0){
			SortedMap<Date, Double> secureStockDisabled = new TreeMap<Date, Double>();
			if(sliders!=null && sliders.getStock()!=null){			
				for(ViewSlider consSlider: sliders.getStock()){
					if(!consSlider.isEnabled())
						secureStockDisabled.put(consSlider.getPoint(), consSlider.getValue());
				}
			}
			
			result+=Normalizer.spaces(level+1)+"<secureStocks>\n";
			for(Map.Entry<Date, Double> entry : secureStock.entrySet()){
				result+=Normalizer.spaces(level+2)+"<secureStock>\n";
				result+=Normalizer.spaces(level+3)+"<point>"+new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(entry.getKey())+"</point>\n";
				result+=Normalizer.spaces(level+3)+"<value>"+entry.getValue().toString()+"</value>\n";
				if(secureStockDisabled.get(entry.getKey())!=null)
					result+=Normalizer.spaces(level+3)+"<disabled>true</disabled>\n";				
				result+=Normalizer.spaces(level+2)+"</secureStock>\n";
			}
			
			result+=Normalizer.spaces(level+1)+"</secureStocks>\n";
		}

		


		
		result+=Normalizer.spaces(level)+"</baseinfo>\n";
		return result;
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



//	public void setStartDate(Date startDate) {
//		this.startDate = startDate;
//	}

	public void setStartDate(Date startDate) {
		if(startDate!=null && this.startDate!=null && this.startDate.compareTo(startDate)!=0){
			this.startDate = startDate;		
			setFinishDate(demoFromStartDate(12));
			setRedrawcharts(true);
		}else
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
		setRedrawcharts(true);

	}


	public int getCalculatemode() {
		return calculatemode;
	}


	public void setCalculatemode(int calculatemode) {
		this.calculatemode = calculatemode;
		setRedrawcharts(true);
	}


	public int getChartConsumptionDayInterval() {
		return chartConsumptionDayInterval;
	}


	public void setChartConsumptionDayInterval(int chartConsumptionDayInterval) {
		this.chartConsumptionDayInterval = chartConsumptionDayInterval;
		setRedrawcharts(true);
	}


	public double getQuantity() {
		return quantity;
	}


	public void setQuantity(double quantity) {
		this.quantity = quantity;
		setRedrawcharts(true);
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
		setRedrawcharts(true);
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

	public void setLeadDaysOnly(double leadDays) {
		this.leadDays = leadDays;
	}
	public void setLeadDays(double leadDays) {
		this.leadDays = leadDays;
		if(getProxy()!=null)
			getProxy().setLead(new PolynomialD().setConstant(0, this.leadDays));
		setRedrawcharts(true);
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

	public void setFixedPeriodOnly(String fixedPeriod) {
		this.fixedPeriod = fixedPeriod;
	}
	public void setFixedPeriod(String fixedPeriod) {
		this.fixedPeriod = fixedPeriod;
		if(getFixedFeatureOrders()==null)
			return;
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
		
		
		setRedrawcharts(true);
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

	public void setDayStockDeltaOnly(long dayStockDelta) {
		this.dayStockDelta = dayStockDelta;
	}
	public void setDayStockDelta(long dayStockDelta) {
		if(this.dayStockDelta!=dayStockDelta){
			this.dayStockDelta = dayStockDelta;
			if(getApproximator()!=null){	
				setSecureStock(
						getApproximator().getStockAdapter().setDayStockDelta(Double.valueOf(this.dayStockDelta)).adapt(getConsumption())
				);
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

	public void setApproximationTypeOnly(int approximationType) {
		this.approximationType = approximationType;
	}
	public void setApproximationType(int approximationType) {
		if(this.approximationType!=approximationType){
			this.approximationType = approximationType;
			if(this.approximationType==4){
				this.approximationType=1;
				setWarning("Neural Network forecasting isn't avialable for the demo version.");
			}
			
			if(getApproximator()!=null){				
				getApproximator()
				.setType(this.approximationType)
				.approximation(getRawdata());	
				
				forecastingStartDate = getApproximator().getApproximation().getStartInterval();
				forecastingFinishDate = getApproximator().getApproximation().getFinishInterval();
				
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
		setRedrawcharts(true);
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


	public void setApproximationAlgorithmOnly(String approximationAlgorithm){
		this.approximationAlgorithm = approximationAlgorithm;
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
				
				setRedrawcharts(true);
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
//			this.redrawcharts=true;
			getSliders().init(true);
			this.redrawslider=true;
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
			setRedrawcharts(true);
			this.redraworders=true;
		}
		
	}


	public String getUploadType() {
		return uploadType;
	}


	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}


	public ISetAdapter getSetAdapter() {
		return setAdapter;
	}


	public void setSetAdapter(ISetAdapter setAdapter) {
		this.setAdapter = setAdapter;
	}


}
