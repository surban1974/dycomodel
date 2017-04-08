package it.dycomodel.admin.components.beans;

import java.io.Serializable;
import java.util.Date;

import it.classhidra.core.tool.util.util_format;
import it.classhidra.serialize.Format;
import it.classhidra.serialize.Serialized;




public class ViewSlider implements Serializable{
	private static final long serialVersionUID = 1L;
	private double value;
	private String description;
	private Date point;
	private String type;
	private ViewSliders owner;
	private boolean enabled=true;
	
	public ViewSlider(){
		super();
	}
	
	public ViewSlider(ViewSliders owner, String type, Date point, double value){
		super();
		this.type = type;
		this.owner = owner;
		this.point = point;
		this.value = value;
		this.description = util_format.dataToString(point, "MMM yyyy");
	}
	
	@Serialized
	public double getValue() {
		return value;
	}
	@Serialized
	public void setValue(double value) {
		this.value = value;
		if(this.owner!=null)
			this.owner.changed(this);
		
	}
	public void setValueFromOwner(double value) {
		this.value = value;
	}	
	
	@Serialized(output=@Format(name="desc"))
	public String getDescription() {
		return description;
	}
	
	@Serialized(input=@Format(name="desc"))
	public void setDescription(String description) {
		this.description = description;
	}
	

	public Date getPoint() {
		return point;
	}
	public void setPointOnly(Date point) {
		this.point = point;
	}


	public ViewSliders getOwner() {
		return owner;
	}

	public void setOwner(ViewSliders owner) {
		this.owner = owner;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Serialized
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if(this.owner!=null)
			this.owner.enabled(this);

	}
	public void setEnabledFromOwner(boolean enabled) {
		this.enabled = enabled;
	}	
	
	@Serialized
	public long getPointL(){
		if(point!=null)
			return point.getTime();
		else
			return new Date().getTime();
	}
	
	@Serialized
	public void setPointL(long pointL){
		point=new Date(pointL);
	}	
}
