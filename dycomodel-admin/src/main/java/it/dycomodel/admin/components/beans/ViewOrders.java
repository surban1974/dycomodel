package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.util.Date;

import it.classhidra.serialize.Format;
import it.classhidra.serialize.Serialized;

public class ViewOrders implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Serialized(output=@Format(format="dd/MM/yyyy"))
	private Date date;
	@Serialized(output=@Format(format="##0.00"))
	private Double quantity;
	
	public ViewOrders(Date date,Double quantity){
		super();
		this.date=date;
		this.quantity=quantity;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	
	

}
