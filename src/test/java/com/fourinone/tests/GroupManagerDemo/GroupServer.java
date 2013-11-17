package com.fourinone.tests.GroupManagerDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;
import com.fourinone.AuthPolicy;
import java.util.List;

public class GroupServer
{
	public static void main(String[] args)
	{
		ParkLocal pl = BeanContext.getPark();
		pl.create("group", args[0], args[0], AuthPolicy.OP_ALL, true);
		
		List<ObjectBean> oldls = null;
		while(true){
			List<ObjectBean> newls = pl.getLastest("group", oldls);
			if(newls!=null){
				System.out.println("Group:"+newls);
				oldls = newls;
			}
		}
	}
}