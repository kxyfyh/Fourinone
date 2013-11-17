package com.fourinone.tests.FttpDemo;

import com.fourinone.BeanContext;

public class FttpServer
{
	public static void main(String[] args)
	{
		BeanContext.startFttpServer(args[0]);
	}
}