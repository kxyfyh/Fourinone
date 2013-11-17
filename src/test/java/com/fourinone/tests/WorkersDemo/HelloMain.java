package com.fourinone.tests.WorkersDemo;

import com.fourinone.StartResult;
import com.fourinone.BeanContext;

public class HelloMain
{	
	public static void main(String[] args)
	{
		//five process:a main process and four child process
		System.out.println("Start ParkServerDemo and waiting 4 seconds...");
		StartResult<Integer> parkserver = BeanContext.tryStart("java","-cp","fourinone.jar;","ParkServerDemo");//,">>log/park.log","2>>&1"
		parkserver.print("log/park.log");
		try{Thread.sleep(4000);}catch(Exception ex){}
		
		System.out.println("Start two Workers and waiting 5 seconds...");
		StartResult<Integer> worker1 = BeanContext.tryStart("java","-cp","fourinone.jar;","HelloWorker","worker1","localhost","2008");
		worker1.print("log/worker1.log");
		//worker1.print("log/worker11.log");
		StartResult<Integer> worker2 = BeanContext.tryStart("java","-cp","fourinone.jar;","HelloWorker","worker2","localhost","2009");
		worker2.print("log/worker2.log");
		try{Thread.sleep(5000);}catch(Exception ex){}
		System.out.println("worker1's Status:"+worker1.getStatusName());
		
		System.out.println("Start Ctor say hello...");
		StartResult<Integer> ctor = BeanContext.tryStart("java","-cp","fourinone.jar;","HelloCtor");
		ctor.print("log/ctor.log");
		while(true){
			if(ctor.getStatus()!=StartResult.NOTREADY){
				System.out.println("ctor's Status:"+ctor.getStatusName());
				parkserver.kill();
				worker1.kill();
				worker2.kill();
				break;
			}
		}
	}
}