package com.fourinone.tests.ParkMasterSlaveDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;

public class SetConfig
{
	public static void main(String[] args)
	{
		ParkLocal pl = BeanContext.getPark();
		ObjectBean xihu = pl.create("zhejiang", "hangzhou", "xihu");
		try{Thread.sleep(8000);}catch(Exception e){}
		ObjectBean yuhang = pl.update("zhejiang", "hangzhou", "yuhang");
	}
}