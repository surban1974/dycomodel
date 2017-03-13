package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.util.Date;

import it.classhidra.core.tool.util.util_format;
import it.classhidra.serialize.Format;
import it.classhidra.serialize.Serialized;


public class ForecastItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private double quantity;
	
	public ForecastItem(Date _date, double _quantity){
		super();
		this.date = _date;
		this.quantity = _quantity;
	}
	
	
	@Serialized(output=@Format(name="date"))
	public String getDateFormated() {
		return util_format.dataToString(date, "dd/MM/yyyy");
	}
	

	public Date getDate() {
		return date;
	}
	
	@Serialized(input=@Format(format="dd/MM/yyyy",name="date"))
	public void setDate(Date data) {
		this.date = data;
	}
	
	@Serialized
	public double getQuantity() {
		return quantity;
	}
	
	@Serialized(input=@Format(format="###.00"))
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

}
