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
		
		Calendar startC = Calendar.getInstance();
		startC.setTime(startDate);
		startC.set(Calendar.DAY_OF_MONTH, 1);
		startC.set(Calendar.YEAR, startC.get(Calendar.YEAR)-1);
		
		Calendar finishC = Calendar.getInstance();
		finishC.setTime(finishDate);
		finishC.set(Calendar.DAY_OF_MONTH, finishC.getActualMaximum(Calendar.DAY_OF_MONTH));	
		finishC.set(Calendar.YEAR, finishC.get(Calendar.YEAR)-1);
		
		Map<Integer, Double> aggr = new HashMap<Integer, Double>();
	
		for(Map.Entry<Long, Double> entry : rawdata.entrySet()) {
		
			Calendar currentC = Calendar.getInstance();
			currentC.setTimeInMillis(entry.getKey());
			
			if(currentC.after(startC) && currentC.before(finishC)){
				int code = getDateAsYYYYMM(currentC.getTime());
				if(aggr.get(code)==null)
					aggr.put(code, entry.getValue());
				else
					aggr.put(code, aggr.get(code) + entry.getValue());
				
			}
		}

		
		while(startC.before(finishC)){
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
	

}
