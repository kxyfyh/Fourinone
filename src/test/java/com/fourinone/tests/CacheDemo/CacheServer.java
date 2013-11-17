package com.fourinone.tests.CacheDemo;

import com.fourinone.BeanContext;

public class CacheServer
{
	public static void main(String[] args)
	{
		String[][] cacheServerA = new String[][]{{"localhost","2000"},{"localhost","2001"}};
		String[][] cacheServerB = new String[][]{{"localhost","2002"},{"localhost","2003"}};
		String[][] cacheServerC = new String[][]{{"localhost","2004"},{"localhost","2005"}};
		
		String[][] cacheServer = null;
		if(args[0].equals("A"))
			cacheServer = cacheServerA;
		else if(args[0].equals("B"))
			cacheServer = cacheServerB;	
		else if(args[0].equals("C"))
			cacheServer = cacheServerC;
		
		BeanContext.startCache(cacheServer[0][0],Integer.parseInt(cacheServer[0][1]), cacheServer);
		//BeanContext.startCache();
	}
}