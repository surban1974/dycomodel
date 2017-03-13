package it.dycomodel.admin.components.controllers; 




import java.io.Serializable;

import it.classhidra.core.controller.action;
import it.classhidra.core.controller.i_action;
import it.classhidra.core.controller.i_bean;
import it.classhidra.serialize.Serialized;


public abstract class AbstractBase extends action implements i_action, i_bean, Serializable{

	private static final long serialVersionUID = 1L;
	
	@Serialized
	private String error;
	
	@Serialized
	private String warning;
	
	@Serialized
	private String info;
	
	@Serialized
	private String success;

public AbstractBase(){
	super();
}

public void clear(){
	error="";
	warning="";
	info="";
	success="";
}

public String getError() {
	return error;
}

public void setError(String error) {
	this.error = error;
}



public String getInfo() {
	return info;
}

public void setInfo(String info) {
	this.info = info;
}


public String getWarning() {
	return warning;
}

public void setWarning(String warning) {
	this.warning = warning;
}

public String getSuccess() {
	return success;
}

public void setSuccess(String success) {
	this.success = success;
}



}
