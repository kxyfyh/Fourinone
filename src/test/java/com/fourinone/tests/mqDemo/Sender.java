package com.fourinone.tests.mqDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.ObjectBean;
import java.io.Serializable;

public class Sender
{
	private static ParkLocal pl = BeanContext.getPark();
	
	public static void send(String queue, Object obj)
	{
		pl.create(queue, (Serializable)obj);
	}
		
	public static void main(String[] args)
	{
		send("queue1","hello");
		send("queue1","world");
		send("queue1","mq");
	}
}