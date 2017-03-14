package it.dycomodel.approximation;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedMap;

public interface IApproximation extends Serializable{

	IApproximation init();

	SortedMap<Date, Double> approximateByYear(SortedMap<Long, Double> rawdata, Date startDate, Date finishDate);
	
	SortedMap<Date, Double> approximateByMonth(SortedMap<Long, Double> rawdata, Date startDate, Date finishDate);

	SortedMap<Date, Double> approximateByWeek(SortedMap<Long, Double> rawdata, Date startDate, Date finishDate);

}