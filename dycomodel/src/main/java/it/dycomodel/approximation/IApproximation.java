package it.dycomodel.approximation;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedMap;

public interface IApproximation extends Serializable{

	IApproximation init();

	SortedMap<Date, Double> approximateByYear(SortedMap<Long, Double> rawdata, Date startApproximationDate, Date finishApproximationDate);
	
	SortedMap<Date, Double> approximateByMonth(SortedMap<Long, Double> rawdata, Date startApproximationDate, Date finishApproximationDate);

	SortedMap<Date, Double> approximateByWeek(SortedMap<Long, Double> rawdata, Date startApproximationDate, Date finishApproximationDate);
	
	Date getStartInterval();
	
	Date getFinishInterval();

	IApproximation setPercentile(Double percentile, Date startDate);


}