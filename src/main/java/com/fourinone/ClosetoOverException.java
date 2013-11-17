package com.fourinone;

import java.text.DecimalFormat;

public class ClosetoOverException extends ServiceException {
	private double tm,fm,mm;
	private static double safeMemoryPer = Double.parseDouble(ConfigContext.getConfig("PARK","SAFEMEMORYPER",null,"0.95"));
	
	public ClosetoOverException(double tm, double fm, double mm){
		super("The capacity close to out of memory, please clear out some data!");
		this.tm = tm;
		this.fm = fm;
		this.mm = mm;
	}
	
	public double getTm(){
		return tm;
	}
	
	public double getFm(){
		return fm;
	}
	
	public double getMm(){
		return mm;
	}
	
	public String print(){
		return print(tm,fm,mm);
	}
	
	public static String print(double tm, double fm, double mm){
		DecimalFormat df = new DecimalFormat("0.00");
		return "tm:"+df.format(tm/(1024*1024))+"m,fm:"+df.format(fm/(1024*1024))+"m,mm:"+df.format(mm/(1024*1024))+"m";
	}
	
	public static boolean checkMemCapacity() throws ClosetoOverException
	{
		double tm = new Long(Runtime.getRuntime().totalMemory()).doubleValue();
		double fm = new Long(Runtime.getRuntime().freeMemory()).doubleValue();
		double mm = new Long(Runtime.getRuntime().maxMemory()).doubleValue();
		
		LogUtil.fine("[checkMemCapacity]", "[MemCapacityInfo]", print(tm, fm, mm));
		
		if(tm/mm>safeMemoryPer&&fm/mm<1-safeMemoryPer)
			throw new ClosetoOverException(tm,fm,mm);
		
		return true;
		
		//return (((double)totalMemory/(double)maxMemory)>0.95&&((double)freeMemory/(double)maxMemory)<0.05)?true:false;
	}
}