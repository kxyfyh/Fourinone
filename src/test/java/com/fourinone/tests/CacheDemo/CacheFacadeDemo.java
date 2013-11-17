package com.fourinone.tests.CacheDemo;

import com.fourinone.BeanContext;

public class CacheFacadeDemo
{
	public static void main(String[] args)
	{
		BeanContext.startCacheFacade();
		System.out.println("CacheFacade is ok...");
	}
}