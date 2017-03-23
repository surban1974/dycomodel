package it.dycomodel.approximation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import it.dycomodel.polynomial.APolynomial;
import it.dycomodel.polynomial.PolynomialD;


public class ApproximationMean implements IApproximation {

	private static final long serialVersionUID = 1L;
	
	private Calendar startInterval;	
	private Calendar finishInterval;
	private Double percentile;
	private Date startDate;

	

	@Override
	public IApproximation init(){
		return this;
	}
	
	@Override
	public SortedMap<Date, Double> approximateByYear(SortedMap<Long, Double> rawdata, Date startApproximationDate, Date finishApproximationDate){
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		return result;
	}

	@Override
	public SortedMap<Date, Double> approximateByMonth(SortedMap<Long, Double> rawdata, Date startApproximationDate, Date finishApproximationDate){
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		
		startInterval = Calendar.getInstance();
		startInterval.setTime(startApproximationDate);
		startInterval.set(Calendar.DAY_OF_MONTH, 1);
		startInterval.set(Calendar.YEAR, startInterval.get(Calendar.YEAR)-1);
		
		finishInterval = Calendar.getInstance();
		finishInterval.setTime(finishApproximationDate);
		finishInterval.set(Calendar.DAY_OF_MONTH, finishInterval.getActualMaximum(Calendar.DAY_OF_MONTH));	
		finishInterval.set(Calendar.YEAR, finishInterval.get(Calendar.YEAR)-1);
		
		Map<Integer, SortedSet<Double>> aggr = new HashMap<Integer, SortedSet<Double>>();
	
		for(Map.Entry<Long, Double> entry : rawdata.entrySet()) {
		
			Calendar currentC = Calendar.getInstance();
			currentC.setTimeInMillis(entry.getKey());
			
			if(currentC.after(startInterval) && currentC.before(finishInterval)){
				int code = getDateAsYYYYMM(currentC.getTime());
				if(aggr.get(code)==null){
					SortedSet<Double> set = new TreeSet<Double>();
					set.add(entry.getValue());
					aggr.put(code, set);
				}
				else
					aggr.get(code).add(entry.getValue());
			}
		}
		
		PolynomialD percPolynomial = null;
		Calendar startPercentile = Calendar.getInstance();
		if(startDate!=null){
			startPercentile.setTime(startDate);
			startPercentile.set(Calendar.YEAR, startPercentile.get(Calendar.YEAR)-1);
		}else 
			startPercentile.setTime(startInterval.getTime());
		if(percentile!=null && percentile>0){
			percPolynomial = new PolynomialD();
			Double m = (percentile-0.5)/(finishInterval.getTimeInMillis()-startPercentile.getTimeInMillis());
			percPolynomial.setConstant(0, -1*m*startPercentile.getTimeInMillis());
			percPolynomial.setConstant(1, m);
		}
		
		Calendar startC = Calendar.getInstance();
		startC.setTimeInMillis(startInterval.getTimeInMillis());
		
		while(startC.before(finishInterval)){
			int code = getDateAsYYYYMM(startC.getTime());
			startC.set(Calendar.DAY_OF_MONTH,15);
			Double setValue = 0d;
			if(aggr.get(code)!=null){
				Double meanValue = 0d;
				if(percPolynomial==null || (percPolynomial!=null && startC.before(startPercentile))){
					int counter = 0;
					for(Double current:aggr.get(code)){
						meanValue+=(current);
						counter++;
					}
					meanValue=meanValue/startC.getActualMaximum(Calendar.DAY_OF_MONTH);
				}else{
					Double currentShift = percPolynomial.compute((double)startC.getTimeInMillis());
					int start = new Double(currentShift*aggr.get(code).size()).intValue();
					int finish = start+aggr.get(code).size();
					int counter = 0;
					int i=0;
					for(Double current:aggr.get(code)){
						if(i>=0 && i>=start && i<finish && i<aggr.get(code).size()){
							meanValue+=(current);
							counter++;
						}
						i++;
					}
					meanValue=meanValue/(counter*startC.getActualMaximum(Calendar.DAY_OF_MONTH)/aggr.get(code).size());
				}
				
				setValue = new BigDecimal(meanValue).setScale(0, RoundingMode.HALF_UP).doubleValue();
				
			}
			result.put(startC.getTime(),setValue);
			startC.set(Calendar.MONTH,startC.get(Calendar.MONTH)+1);
		}
		return result;
	}

	@Override
	public SortedMap<Date, Double> approximateByWeek(SortedMap<Long, Double> rawdata, Date startApproximationDate, Date finishApproximationDate){
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		
		
		return result;
	}	
	
	private int getDateAsYYYYMM(Date current){
		return 
				Integer.valueOf(new java.text.SimpleDateFormat("YYYYMM").format(current));
	}

	public Date getStartInterval() {
		if(startInterval==null)
			return null;
		return startInterval.getTime();
	}

	public Date getFinishInterval() {
		if(finishInterval==null)
			return null;
		return finishInterval.getTime();
	}

	public IApproximation setPercentile(Double percentile, Date startDate) {
		this.percentile = percentile;
		this.startDate = startDate;
		return this;
	}
}
