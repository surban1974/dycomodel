package it.dycomodel.approximation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class ApproximationMean implements IApproximation {

	private static final long serialVersionUID = 1L;
	
	private Calendar startInterval;
	
	private Calendar finishInterval;

	@Override
	public IApproximation init(){
		return this;
	}
	
	@Override
	public SortedMap<Date, Double> approximateByYear(SortedMap<Long, Double> rawdata, Date startDate, Date finishDate){
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		return result;
	}

	@Override
	public SortedMap<Date, Double> approximateByMonth(SortedMap<Long, Double> rawdata, Date startDate, Date finishDate){
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		
		startInterval = Calendar.getInstance();
		startInterval.setTime(startDate);
		startInterval.set(Calendar.DAY_OF_MONTH, 1);
		startInterval.set(Calendar.YEAR, startInterval.get(Calendar.YEAR)-1);
		
		finishInterval = Calendar.getInstance();
		finishInterval.setTime(finishDate);
		finishInterval.set(Calendar.DAY_OF_MONTH, finishInterval.getActualMaximum(Calendar.DAY_OF_MONTH));	
		finishInterval.set(Calendar.YEAR, finishInterval.get(Calendar.YEAR)-1);
		
		Map<Integer, Double> aggr = new HashMap<Integer, Double>();
	
		for(Map.Entry<Long, Double> entry : rawdata.entrySet()) {
		
			Calendar currentC = Calendar.getInstance();
			currentC.setTimeInMillis(entry.getKey());
			
			if(currentC.after(startInterval) && currentC.before(finishInterval)){
				int code = getDateAsYYYYMM(currentC.getTime());
				if(aggr.get(code)==null)
					aggr.put(code, entry.getValue());
				else
					aggr.put(code, aggr.get(code) + entry.getValue());
				
			}
		}

		
		Calendar startC = Calendar.getInstance();
		startC.setTimeInMillis(startInterval.getTimeInMillis());
		
		while(startC.before(finishInterval)){
			int code = getDateAsYYYYMM(startC.getTime());
			double average=0;
			startC.set(Calendar.DAY_OF_MONTH,15);
			if(aggr.get(code)!=null)
				average = new BigDecimal(aggr.get(code)/startC.getActualMaximum(Calendar.DAY_OF_MONTH)).setScale(0, RoundingMode.HALF_UP).doubleValue();
			result.put(startC.getTime(),average);
			startC.set(Calendar.MONTH,startC.get(Calendar.MONTH)+1);
		}
		return result;
	}

	@Override
	public SortedMap<Date, Double> approximateByWeek(SortedMap<Long, Double> rawdata, Date startDate, Date finishDate){
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
	

}
