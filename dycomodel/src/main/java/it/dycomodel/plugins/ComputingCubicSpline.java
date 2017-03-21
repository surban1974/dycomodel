package it.dycomodel.plugins;

import java.util.SortedMap;
import java.util.TreeMap;


import it.dycomodel.exceptions.PolynomialConstantsException;
import it.dycomodel.polynomial.APolynomial;

public class ComputingCubicSpline extends ApacheCommonMath implements IComputing {
	private static final long serialVersionUID = 1L;

	@Override
	public <T extends Number> SortedMap<T, T[]> getPolynomialCoeficients(T[] x, T[] y, APolynomial<T> adapter)
			throws PolynomialConstantsException {
		try{
			if(x.length<2)
				return null;
			
			final double[][] functions =  cubicSplineInterpolation(adapter.copyTodoubleArray(x), adapter.copyTodoubleArray(y));
			
			SortedMap<T, T[]> segments = new TreeMap<T, T[]>();
			for(int i=0;i<x.length;i++)
				if(i<functions.length)
					segments.put(x[i], adapter.copyToTArray(functions[i]));
			return segments;
		}catch(Exception e){
			throw new PolynomialConstantsException(e)
				.setX(adapter.copyTodoubleArray(x))
				.setY(adapter.copyTodoubleArray(y));
		}		
	}
	
	private double[][] cubicSplineInterpolation(final double[] x, final double[] y) {
		int row = 0;
		int solutionIndex = (x.length - 1) * 4;
	
		final double[][] m = new double[(x.length - 1) * 4][(x.length - 1) * 4+1]; // rows
		for (int i = 0; i < (x.length - 1) * 4; i++) {
			for (int j = 0; j <= (x.length - 1) * 4; j++)
				m[i][j]=0; 
		}
		for (int functionNr = 0; functionNr < x.length-1; functionNr++, row += 2) {
			double p0x = x[functionNr], p0y = y[functionNr], p1x = x[functionNr+1], p1y = y[functionNr+1] ;
			m[row][functionNr*4+0] = Math.pow(p0x, 3);
			m[row][functionNr*4+1] = Math.pow(p0x, 2); 
			m[row][functionNr*4+2] = Math.pow(p0x, 1); 
			m[row][functionNr*4+3] = 1; 
			m[row][solutionIndex] = p0y;
			m[row+1][(functionNr)*4+0] = Math.pow(p1x, 3);
			m[row+1][(functionNr)*4+1] = Math.pow(p1x, 2); 
			m[row+1][(functionNr)*4+2] = Math.pow(p1x, 1); 
			m[row+1][(functionNr)*4+3] = 1; 
			m[row+1][solutionIndex] = p1y;
		}
		for (int functionNr = 0; functionNr < x.length - 2; functionNr++, row++) {
			double p1x = x[functionNr+1];
			m[row][functionNr*4+0] = 3*Math.pow(p1x, 2);
			m[row][functionNr*4+1] = 2*p1x;
			m[row][functionNr*4+2] = 1;
			m[row][functionNr*4+4] = -3*Math.pow(p1x, 2);
			m[row][functionNr*4+5] = -2*p1x;
			m[row][functionNr*4+6] = -1;
		}
		for (int functionNr = 0; functionNr < x.length - 2; functionNr++, row++) {
			double p1x = x[functionNr+1];
			m[row][functionNr*4+0] = 6*p1x;
			m[row][functionNr*4+1] = 2;
			m[row][functionNr*4+4] = -6*p1x;
			m[row][functionNr*4+5] = -2;
		}
		m[row][0+0] = 6*x[0];
		m[row++][0+1] = 2;
		m[row][solutionIndex-4+0] = 6*x[x.length-1];
		m[row][solutionIndex-4+1] = 2;
	
		final double[] coefficients = solveMatrix(m);
	
		final double[][] functions = new double[coefficients.length/4][4];
		int k=0;
		for (int i = 0; i < coefficients.length; i += 4) {
			functions[k][3] = coefficients[i];
			functions[k][2] = coefficients[i+1];
			functions[k][1] = coefficients[i+2];
			functions[k][0] = coefficients[i+3];
	
			k++;
	
		}
		return functions;
	}
	
	private double[] solveMatrix(double[][] mat) {
		int len = mat.length;
		for (int i = 0; i < len; i++) { 
			for (int j = i+1; j < len; j++) {
				if (mat[i][i] == 0) { 
					int k = i;
					while (mat[k][i] == 0) k++;
					
					double[] tmp = new double[mat[k].length];
					System.arraycopy( mat[k], 0, tmp, 0, mat[k].length );
					System.arraycopy( mat[i], 0, mat[k], 0, mat[i].length );
					System.arraycopy( tmp, 0, mat[i], 0, tmp.length );
				}
				double fac = -mat[j][i]/mat[i][i];
				for(int k = i; k < len+1; k++) 
					mat[j][k] += fac *mat[i][k];
			}
		}
	
		double[] solution = new double[0];
		for (int i = len-1; i >= 0; i--) { // column
			solution = addFirst(solution,mat[i][len]/mat[i][i]);
			for (int k = i-1; k >= 0; k--) {
				mat[k][len] -= mat[k][i] * solution[0];
			}
		}
	
		return solution;
	}
	
	private double[] addFirst(double[] source, double value){
		double[] result = new double[source.length+1];
		System.arraycopy( source, 0, result, 1, source.length );
		result[0] = value;
		return result;
	}

}
