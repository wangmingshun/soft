package com.sinolife.pos.pubInterface.biz.impl;

import com.sinolife.sf.framework.comm.BarCodeUtil;

public class CssTest {
	
	
	public static void main(String args[])
	{
		
		
		
		String barcodeNo1 = 
				new Long(System.currentTimeMillis()).toString() ;
		System.out.println(barcodeNo1);
		
		//barcodeNo1 += BarCodeUtil.getBarCodeSum(barcodeNo1);
		
		String barcodeNo2 = ("CCS"
				+ new Long(System.currentTimeMillis()).toString().substring(6) + (int) (Math
				.random() * 1000000000)).substring(0, 15);
		barcodeNo2 += BarCodeUtil.getBarCodeSum(barcodeNo2);
		String barcodeNo3= ("CCS"
				+ new Long(System.currentTimeMillis()).toString().substring(6) + (int) (Math
				.random() * 1000000000)).substring(0, 15);
		barcodeNo3 += BarCodeUtil.getBarCodeSum(barcodeNo3);
		System.out.println(barcodeNo1);
		System.out.println(barcodeNo2);
		System.out.println(barcodeNo3);
		
	}

}
