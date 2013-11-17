package com.fourinone.tests.CacheDemo;

import com.fourinone.BeanContext;
import com.fourinone.ParkLocal;
import com.fourinone.CacheLocal;

public class CacheGetDemo
{
	public static String[] getSmallCache()
	{
		ParkLocal pl = BeanContext.getPark();
		return (String[])pl.get("cache", "keyArray").toObject();
	}
	
	public static void getBigCache(String[] keyArray)
	{
		CacheLocal cc = BeanContext.getCache();
		for(String k:keyArray)
			System.out.println(cc.get(k, "key"));
	}
	
	public static void main(String[] args){
		String[] keyArray = getSmallCache();
		getBigCache(keyArray);
	}
}