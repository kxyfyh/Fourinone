package com.fourinone;

//import java.io.Serializable;

public class WareHouse extends ObjValue{
	public final static int NOTREADY=1,READY=0,EXCEPTION=-1;
	int status=READY;
	//private Exception expt = null;
	private boolean ready = true;
	private boolean mark = true;
	
	public WareHouse(){
		super();
	}
	
	public WareHouse(boolean ready){
		this();
		this.ready = ready;
		status = NOTREADY;
	}
	
	public WareHouse(Object k, Object v)
	{
		this();
		this.put(k,v);
	}
	
	/*void setStatus(int status){
		this.status = status;
	}*/
	
	public int getStatus()
	{
		return status;
	}
	
	public String getStatusName()
	{
		String[] statusName = new String[]{"EXCEPTION","READY","NOTREADY"};
		return statusName[status+1];
	}
	
	/*synchronized void setReady(boolean ready)
	{
		this.ready = ready;
		status = ready?READY:NOTREADY;
	}*/
	
	synchronized void setReady(int status)//, Exception expt
	{
		this.ready = true;
		//System.out.println("setReady status:"+status);
		this.status = status;
		//this.expt = expt;
	}
	
	public synchronized boolean isReady()//throws Throwable
	{
		return ready;
	}
	
	public void setMark(boolean mark)
	{
		this.mark = mark;
	}
	
	public boolean getMark()
	{
		return mark;
	}
	/*
	public Exception getException()
	{
		return expt;
	}
	*/
	//get
	/*public <T extends Serializable, S extends Serializable> WareHouse(S k, T v)
	{
		this();
		System.out.println("WareHouse(S k, T v)");
		this.put(k,v);
	}
	
	public <T extends Serializable, S extends Serializable> T put(S k, T v)
	{
		System.out.println("put(S k, T v)");
		return put(k,v);
	}*/
	
	public static void main(String[] args)
	{
		WareHouse wh = new WareHouse("key",new java.util.ArrayList());
		//wh.put("bbb",new Bean("",99,new java.util.ArrayList()));
		System.out.println(wh.get("key"));
	}
}
/*
class Bean implements Serializable
{
	public String a;
	public int b;
	public java.util.ArrayList al;
	public Bean(String a, int b, java.util.ArrayList al)
	{
		this.a = a;
		this.b = b;
		this.al = al;
	}
	
	public String toString(){
		return a+b+al.toString();
	}
}*/