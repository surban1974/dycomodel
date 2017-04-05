package it.dycomodel.approximation;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DefaultSetAdapter implements ISetAdapter {
	private static final long serialVersionUID = 1L;
	private Double dayStockDelta = 0d;

	@Override
	public SortedMap<Date, Double> adapt(SortedMap<Date, Double> set1) {
		SortedMap<Date, Double> result = new TreeMap<Date, Double>();
		for(Map.Entry<Date, Double> entry : set1.entrySet()) {
			result.put(entry.getKey(), entry.getValue()*getDayStockDelta());
		}
		return result;
	}

	public Double getDayStockDelta() {
		return dayStockDelta;
	}

	public ISetAdapter setDayStockDelta(Double dayStockDelta) {
		this.dayStockDelta = dayStockDelta;
		return this;
	}

}
