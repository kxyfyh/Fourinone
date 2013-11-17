package com.fourinone.tests.ParkMasterSlaveDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;

public class GetConfigA
{
	public static void main(String[] args)
	{
		ParkLocal pl = BeanContext.getPark();
		ObjectBean oldob = null;
		while(true){
			ObjectBean newob = pl.getLastest("zhejiang", "hangzhou", oldob);
			if(newob!=null){
				System.out.println(newob);
				oldob = newob;
			}
		}
	}
}