package com.fourinone.tests.mqDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;
import com.fourinone.LastestEvent;
import com.fourinone.LastestListener;
import java.util.ArrayList;

public class Subscriber implements LastestListener
{
	private static ParkLocal pl = BeanContext.getPark();
	
	public boolean happenLastest(LastestEvent le)
	{
		ObjectBean ob = (ObjectBean)le.getSource();
		ArrayList arr = (ArrayList)ob.toObject();
		System.out.println("published message:"+arr);
		ObjectBean newob = pl.update(ob.getDomain(), ob.getNode(), new ArrayList());
		le.setSource(newob);
		return false;
	}
	
	public static void subscrib(String topic, String subscribeName, LastestListener lister)
	{
		ArrayList arr = new ArrayList();
		ObjectBean ob = pl.create(topic, subscribeName, arr);
		pl.addLastestListener(topic, subscribeName, ob, lister);
	} 
	
	public static void main(String[] args)
	{
		subscrib("topic1", args[0], new Subscriber());
	}
}