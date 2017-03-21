package it.csrp.test.main;

import java.math.BigDecimal;

import it.dycomodel.equation.EquationD;
import it.dycomodel.equation.IEquation;
import it.dycomodel.plugins.ComputingLaguerre;




public class TestEquation {

	public static void main(String[] args) {
	
		Double[][] speed = 					
				new Double[][] {
					{0d,752d},
					{30d,512d},
					{60d,580d},
					{90d,491d},
					{120d,487d},
					{150d,516d},
					{180d,612d},
					{210d,698d},
					{240d,544d},
					{270d,471d},
					{300d,577d},
					{330d,718d}
					}; 

		
					Double[][] secure = 					
							new Double[][] {
								{0d,200d},
								{30d,300d},
								{60d,300d},
								{90d,100d},
								{120d,200d},
								{150d,100d},
								{180d,100d},
								{210d,200d},
								{240d,250d},
								{270d,100d},
								{300d,100d},
								{330d,200d}
								}; 
			for(int i =0;i<330;i++)	{	
				try{		
						IEquation<Double> equation = 
								new EquationD()
								.setComputingPlugin(new ComputingLaguerre())
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
