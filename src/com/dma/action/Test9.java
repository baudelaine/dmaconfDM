package com.dma.action;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class Test9 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long num0 = 1;
		long num1 = 3;
		
		double d0 = Double.parseDouble(String.valueOf(num0));
		double d1 = Double.parseDouble(String.valueOf(num1));
		
		double num = (d0/d1) * 100;
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
//		nf.setMinimumFractionDigits(5);	    
		nf.setRoundingMode(RoundingMode.UP);
	    num = Double.parseDouble(nf.format(num));
	    System.out.println(num);
		
	}

}
