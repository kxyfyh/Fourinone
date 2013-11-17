package com.fourinone.tests.mqDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;
import java.util.List;

public class Receiver
{
	private static ParkLocal pl = BeanContext.getPark();
	
	public static Object receive(String queue)
	{
		Object obj=null;
		List<ObjectBean> oblist = null;
		while(true)
		{
			oblist = pl.get(queue);
			if(oblist!=null)
			{
				ObjectBean ob = oblist.get(0);
				obj = ob.toObject();
				pl.delete(ob.getDomain(), ob.getNode());
				break;
			}
		}
		return obj;
	}
	
	public static void main(String[] args)
	{
		System.out.println(receive("queue1"));
		System.out.println(receive("queue1"));
		System.out.println(receive("queue1"));
	}
}