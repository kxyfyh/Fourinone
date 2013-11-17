package com.fourinone.tests.SayHelloDemo;

import com.fourinone.Contractor;
import com.fourinone.WareHouse;
import com.fourinone.WorkerLocal;

public class HelloCtor extends Contractor
{
	public WareHouse giveTask(WareHouse inhouse)
	{
		WorkerLocal[] wks = getWaitingWorkers("helloworker");
		System.out.println("wks.length:"+wks.length);
		WareHouse wh = new WareHouse("word", "hello, i am your Contractor.");
		WareHouse[] hmarr = doTaskBatch(wks, wh);

		for(WareHouse result:hmarr)
			System.out.println(result);

		return null;
	}
	
	public static void main(String[] args)
	{
		HelloCtor a = new HelloCtor();
		a.giveTask(null);
		a.exit();
	}
}