package com.fourinone.tests.SimpleDemo;

import com.fourinone.Contractor;
import com.fourinone.WareHouse;
import com.fourinone.WorkerLocal;
import java.util.ArrayList;

public class SimpleCtor extends Contractor
{
	public WareHouse giveTask(WareHouse inhouse)
	{
		WorkerLocal[] wks = getWaitingWorkers("simpleworker");
		System.out.println("wks.length:"+wks.length);
		
		WareHouse wh = new WareHouse("word", "hello");
		WareHouse result = wks[0].doTask(wh);

		while(true){
			if(result.getStatus()==WareHouse.READY)
			{
				System.out.println("result:"+result);
				break;
			}
		}

		return null;
	}
	
	public static void main(String[] args)
	{
		SimpleCtor a = new SimpleCtor();
		a.giveTask(null);
	}
}