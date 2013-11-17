package com.fourinone;

public class ParkMeta
{
	private static String YSJ,YBB,YCJZ,YCIP,YCSJ,YQX,YSX,YGXZ,YGIP,YGSJ,SXXT;
	static{
		MulBean mb = ConfigContext.getMulBean();
		YSJ = mb.getString("YSJ");
		YBB = mb.getString("YBB");
		YCJZ = mb.getString("YCJZ");
		YCIP = mb.getString("YCIP");
		YCSJ = mb.getString("YCSJ");
		YQX = mb.getString("YQX");
		YSX = mb.getString("YSX");
		YGXZ = mb.getString("YGXZ");
		YGIP = mb.getString("YGIP");
		YGSJ = mb.getString("YGSJ");
		SXXT = mb.getString("SXXT");
	}
	static String getYSJ(){
		return YSJ;
	}
	static String getYBB(){
		return YBB;
	}
	static String getYBB(String dnstr){
		return dnstr+YBB;
	}
	static String getYCJZ(String dnstr){
		return dnstr+YCJZ;
	}
	static String getYCIP(String dnstr){
		return dnstr+YCIP;
	}
	static String getYCSJ(){
		return YCSJ;
	}
	static String getYCSJ(String dnstr){
		return dnstr+YCSJ;
	}
	static String getYQX(String dnstr){
		return dnstr+YQX;
	}
	static String getYSX(){
		return YSX;
	}
	static String getYSX(String dnstr){
		return dnstr+YSX;
	}
	static String getYGXZ(String dnstr){
		return dnstr+YGXZ;
	}
	static String getYGIP(String dnstr){
		return dnstr+YGIP;
	}
	static String getYGSJ(String dnstr){
		return dnstr+YGSJ;
	}
	static String getSXXT(){
		return SXXT;
	}
	
	public static void main(String[] args)
	{
		System.out.println("ParkMeta.getYBB():"+getYBB());
	}
}