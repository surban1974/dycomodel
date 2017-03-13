package it.dycomodel.exceptions;

public class PolynomialConstantsException extends Exception {
	private static final long serialVersionUID = 1L;
	private double[] x;
	private double[] y;

	public PolynomialConstantsException(Exception e){
		super(e);
	}

	public double[] getX() {
		return x;
	}

	public PolynomialConstantsException setX(double[] x) {
		this.x = x;
		return this;
	}

	public double[] getY() {
		return y;
	}

	public PolynomialConstantsException setY(double[] y) {
		this.y = y;
		return this;
	}
}
