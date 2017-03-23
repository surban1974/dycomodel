package it.dycomodel.wrappers;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import it.dycomodel.approximation.ApproximationMean;
import it.dycomodel.approximation.IApproximation;
import it.dycomodel.approximation.ISetAdapter;

public class ADateApproximator implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int APPROXIMATION_MEAN = 1;
	public static final int APPROXIMATION_MEANQ25 = 2;
	public static final int APPROXIMATION_MEANQ75 = 3;
	public static final int APPROXIMATION_NNET = 4;
	
	private int type;
	private ISetAdapter stockAdapter;
	private Date startApproximationDate;
	private Date finishApproximationDate;
	private Date startDate;
	private SortedMap<Date, Double> consumption;
	private SortedMap<Date, Double> stock;
	private IApproximation approximation;
	

	public ADateApproximator approximation(SortedMap<Long, Double> rawdata){
		if(type==APPROXIMATION_MEAN){
			try{
				approximation = new ApproximationMean().init();
				consumption = approximation.approximateByMonth(rawdata, startApproximationDate, finishApproximationDate);
				stock = stockAdapter.adapt(consumption);
			}catch(Exception e){				
			}
		} else if(type==APPROXIMATION_MEANQ25){
			try{
				approximation = new ApproximationMean().init();
				consumption = approximation
						.setPercentile(0.25, startDate)
						.approximateByMonth(rawdata, startApproximationDate, finishApproximationDate);
				stock = stockAdapter.adapt(consumption);
			}catch(Exception e){				
			}
		}else if(type==APPROXIMATION_MEANQ75){
			try{
				approximation = new ApproximationMean().init();
				consumption = approximation
						.setPercentile(0.75, startDate)
						.approximateByMonth(rawdata, startApproximationDate, finishApproximationDate);
				stock = stockAdapter.adapt(consumption);
			}catch(Exception e){				
			}
		}
		return this;
	}

	public int getType() {
		return type;
	}

	public ADateApproximator setType(int type) {
		this.type = type;
		return this;
	}

	public ISetAdapter getStockAdapter() {
		return stockAdapter;
	}

	public ADateApproximator setStockAdapter(ISetAdapter stockAdapter) {
		this.stockAdapter = stockAdapter;
		return this;
	}

	public Date getStartApproximationDate() {
		return startApproximationDate;
	}

	public ADateApproximator setStartApproximationDate(Date startApproximationDate) {
		this.startApproximationDate = startApproximationDate;
		return this;
	}

	public Date getFinishApproximationDate() {
		return finishApproximationDate;
	}

	public ADateApproximator setFinishApproximationDate(Date finishApproximationDate) {
		this.finishApproximationDate = finishApproximationDate;
		return this;
	}

	public SortedMap<Date, Double> getForecastedConsumption(int year) {
		if(consumption==null)
			return null;
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		for(Map.Entry<Date, Double> entry : consumption.entrySet()) {
			Calendar currentC = Calendar.getInstance();
			currentC.setTime(entry.getKey());
			currentC.set(Calendar.YEAR, currentC.get(Calendar.YEAR)+year);
			result.put(currentC.getTime(), entry.getValue());
		}
		return result;
	}

	public SortedMap<Date, Double> getForecastedStock(int year) {
		if(stock==null)
			return null;
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		for(Map.Entry<Date, Double> entry : stock.entrySet()) {
			Calendar currentC = Calendar.getInstance();
			currentC.setTime(entry.getKey());
			currentC.set(Calendar.YEAR, currentC.get(Calendar.YEAR)+year);
			result.put(currentC.getTime(), entry.getValue());
		}
		return result;
	}

	public SortedMap<Date, Double> getConsumption() {
		return consumption;
	}

	public SortedMap<Date, Double> getStock() {
		return stock;
	}

	public IApproximation getApproximation() {
		return approximation;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ADateApproximator setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}


}
