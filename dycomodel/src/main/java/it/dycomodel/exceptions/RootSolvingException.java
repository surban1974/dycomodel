package it.dycomodel.exceptions;

import it.dycomodel.equation.IEquation;
import it.dycomodel.polynomial.APolynomial;

public class RootSolvingException extends Exception {
	private static final long serialVersionUID = 1L;
	private APolynomial<Number> completePolynomial;
	private IEquation<Number> incompleteEquation;
	private Double startPeriod;
	private Double finishPeriod;


	public RootSolvingException(Exception e){
		super(e);
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> APolynomial<T> getCompletePolynomial() {
		return (APolynomial<T>) completePolynomial;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Number> RootSolvingException setCompletePolynomial(APolynomial<T> completePolynomial2) {
		completePolynomial = (APolynomial<Number>) completePolynomial2;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> IEquation<T> getIncompleteEquation() {
		return (IEquation<T>) incompleteEquation;
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> RootSolvingException setIncompleteEquation(IEquation<T> incompleteEquation) {
		this.incompleteEquation = (IEquation<Number>) incompleteEquation;
		return this;
	}

	public Double getStartPeriod() {
		return startPeriod;
	}

	public RootSolvingException setStartPeriod(Double startPeriod) {
		this.startPeriod = startPeriod;
		return this;
	}

	public Double getFinishPeriod() {
		return finishPeriod;
	}

	public RootSolvingException setFinishPeriod(Double finishPeriod) {
		this.finishPeriod = finishPeriod;
		return this;
	}
}
