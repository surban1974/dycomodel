package it.csrp.test.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


import it.dycomodel.plugins.ComputingCubicSpline;
import it.dycomodel.polynomial.PolynomialD;
import it.dycomodel.wrappers.ADateWrapper;
import it.dycomodel.wrappers.DateWrapperD;


public class TestWrapper {

	public static void main(String[] args) {
	

		try{
			SortedMap<Date, Double> speedM = new TreeMap<Date, Double>() {
					private static final long serialVersionUID = 1L;
			{
					put(new SimpleDateFormat("yyyyMMdd").parse("20160115"),752d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160215"),512d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160315"),580d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160415"),491d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160515"),487d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160615"),516d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160715"),612d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160815"),698d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160915"),544d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20161015"),471d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20161115"),577d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20161215"),718d);			 
			}};
			
			SortedMap<Date, Double> processedOrders = new TreeMap<Date, Double>() {{
					put(new SimpleDateFormat("yyyyMMdd").parse("20160125"),3000d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160210"),15000d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160410"),15000d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160510"),15000d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160710"),15000d);
					put(new SimpleDateFormat("yyyyMMdd").parse("201601110"),15000d);
				}};

			SortedMap<Date, Double> secureM = new TreeMap<Date, Double>() {{
					put(new SimpleDateFormat("yyyyMMdd").parse("20160115"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160215"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160315"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160415"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160515"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160615"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160715"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160815"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20160915"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20161015"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20161115"),50d);
					put(new SimpleDateFormat("yyyyMMdd").parse("20161215"),50d);			 
			}};
			
			SortedSet<Date> fixedDates = new TreeSet<Date>(){{
					add(new SimpleDateFormat("yyyyMMdd").parse("20160315"));
					add(new SimpleDateFormat("yyyyMMdd").parse("20160615"));
					add(new SimpleDateFormat("yyyyMMdd").parse("20160705"));
					add(new SimpleDateFormat("yyyyMMdd").parse("20160915"));
			}};
			
		
		
		ADateWrapper<Double> wrapper = new DateWrapperD()
				.setComputingPlugin(new ComputingCubicSpline())
				.init(speedM, secureM)
				.setLead(new PolynomialD().setConstant(0, 15d));
				;
				
		Date result = wrapper.getFirstPoint(10000d, new SimpleDateFormat("yyyyMMdd").parse("20160120"), null, processedOrders);
		System.out.println(result+ " - "+wrapper.computeLead(result));
				
		Date forecastedLead = wrapper.forecastPointWithLead(new SimpleDateFormat("yyyyMMdd").parse("20160201"))	;
		System.out.println(forecastedLead+ " - "+wrapper.computeLead(forecastedLead));
		

		
		SortedMap<Date, Double> fixed = wrapper.getPoints(1000d, 20000d, new SimpleDateFormat("yyyyMMdd").parse("20160120"), new SimpleDateFormat("yyyyMMdd").parse("20161220"), null, 1d);
		System.out.println(fixed);	
		
		SortedMap<Date, Double> fixedD = wrapper.getPoints(1000d, fixedDates, new SimpleDateFormat("yyyyMMdd").parse("20160120"), new SimpleDateFormat("yyyyMMdd").parse("20161220"), processedOrders, true, 1d);
		System.out.println(fixedD);
		
//					IEquation<Double> equation = 
//							new EquationD()
//							.setComputingPlugin(new ApacheCommonMathLaguerre())
//							.setStartPeriod(20160515d)	
//							.setFinishPeriod(20160722d)
//							.setInitialQuantity(10000d)
//							.setAveragePoints(speedM,secureM)
//							.makeEquation()
							;
					
//					System.out.println(equation.compute(equation.getConsumption(), 20160201d));
//					System.out.println(equation.compute(equation.getSecureStock(),20160201d));
					
//					APolynomial<Double> eq = equation.getEquation();
					
//					System.out.println(eq.toString());
					
//					System.out.println(new BigDecimal(equation.solveEquation()).divide(new BigDecimal(1),0, BigDecimal.ROUND_HALF_DOWN));
					
			}catch(Exception e){
				e.toString();
			}
	}
	
	

}
