package it.csrp.test.main;

import java.math.BigDecimal;

import it.dycomodel.equation.EquationD;
import it.dycomodel.equation.IEquation;
import it.dycomodel.plugins.ApacheCommonMathLaguerre;



public class TestEquation {

	public static void main(String[] args) {
	
		double[][] speed = 					
				new double[][] {
					{0,752},
					{30,512},
					{60,580},
					{90,491},
					{120,487},
					{150,516},
					{180,612},
					{210,698},
					{240,544},
					{270,471},
					{300,577},
					{330,718}
					}; 

		
					double[][] secure = 					
							new double[][] {
								{0,200},
								{30,300},
								{60,300},
								{90,100},
								{120,200},
								{150,100},
								{180,100},
								{210,200},
								{240,250},
								{270,100},
								{300,100},
								{330,200}
								}; 
			for(int i =0;i<330;i++)	{	
				try{		
						IEquation<Double> equation = 
								new EquationD()
								.setComputingPlugin(new ApacheCommonMathLaguerre())
								.setAveragePoints(speed,secure)
								.makeIncompleteEquation();
						

						
						System.out.println("day="+i+" point="+(new BigDecimal(
								equation.solveEquation(Double.valueOf(10000d),Double.valueOf(i),null)
								).divide(new BigDecimal(1),2, BigDecimal.ROUND_HALF_DOWN).doubleValue()-i));
				
						
				}catch(Exception e){
					e.toString();
				}
			}
	}
	
	

}
