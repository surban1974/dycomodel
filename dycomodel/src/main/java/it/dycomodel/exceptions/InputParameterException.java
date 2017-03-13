package it.dycomodel.exceptions;

public class InputParameterException extends Exception {
	private static final long serialVersionUID = 1L;

	public InputParameterException(){
		super();
	}
	public InputParameterException(Exception e){
		super(e);
	}
}
