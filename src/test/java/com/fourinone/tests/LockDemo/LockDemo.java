package com.fourinone.tests.LockDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;
import java.util.List;

public class LockDemo
{
	public void lockutil(String node)
	{
		ParkLocal pl = BeanContext.getPark();
		ObjectBean ob = pl.create("lock", node, node);
		
		System.out.print("try get lock.");
		while(true){
			List<ObjectBean> oblist = pl.get("lock");
			String curnode = (String)oblist.get(0).toObject();
			//System.out.println(curnode);
			if(curnode.equals(node)){
				System.out.println("");
				System.out.println("ok, get lock and doing...");
				try{Thread.sleep(8000);}catch(Exception e){}
				pl.delete("lock", node);
				System.out.println("done.");
				break;
			}
			else
				System.out.print(".");
		}
		
		
	}
	
	public static void main(String[] args)
	{
		LockDemo ld = new LockDemo();
		ld.lockutil(args[0]);
	}
}