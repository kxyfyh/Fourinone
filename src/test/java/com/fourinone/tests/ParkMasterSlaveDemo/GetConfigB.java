package com.fourinone.tests.ParkMasterSlaveDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.LastestListener;
import com.fourinone.LastestEvent;
import com.fourinone.ObjectBean;

public class GetConfigB implements LastestListener
{
	public boolean happenLastest(LastestEvent le)
	{
		ObjectBean ob = (ObjectBean)le.getSource();
		System.out.println(ob);
		return false;
	}
	
	public static void main(String[] args)
	{
		ParkLocal pl = BeanContext.getPark();
		pl.addLastestListener("zhejiang", "hangzhou", null, new GetConfigB());
	}
}