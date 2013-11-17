package com.fourinone.tests.IntegratedDemo;
import com.fourinone.MigrantWorker;
import com.fourinone.WareHouse;

public class WorkerDemo extends MigrantWorker
{
	private String workname;
	public WorkerDemo(String workname)
	{
		this.workname = workname;
	}
	
	public WareHouse doTask(WareHouse inhouse)
	{
		String v = inhouse.getString("id");
		System.out.println(workname+" inhouse:"+v);
		if(inhouse.containsKey("run")){
			((Runnable)inhouse.get("run")).run();
		}
		return new WareHouse("id",v+"-"+workname+"-");
	}
	
	public static void main(String[] args)
	{
		args=new String[]{"localhost","1112"};
		WorkerDemo wd = new WorkerDemo(args[0]);
		wd.waitWorking("localhost",Integer.parseInt(args[1]),"workdemo");
	}
}