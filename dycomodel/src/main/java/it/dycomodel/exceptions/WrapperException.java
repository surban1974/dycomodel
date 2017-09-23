package it.dycomodel.exceptions;

public class WrapperException extends Exception {
	private static final long serialVersionUID = 1L;

	public WrapperException(){
		super();
	}
	public WrapperException(Exception e){
		super(e);
	}
}
