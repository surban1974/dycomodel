package it.dycomodel.approximation;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedMap;

public interface ISetAdapter extends Serializable{

	SortedMap<Date, Double> adapt(SortedMap<Date, Double> set1);
	
	Double getDayStockDelta();

	ISetAdapter setDayStockDelta(Double dayStockDelta);

}