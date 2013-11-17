package com.fourinone.tests.GroupManagerDemo;

import com.fourinone.BeanContext;

public class GroupManager
{
	public static void main(String[] args)
	{
		String[][] master = new String[][]{{"localhost","1888"},{"localhost","1889"}};
		String[][] slave = new String[][]{{"localhost","1889"},{"localhost","1888"}};
		
		String[][] server = null;
		if(args[0].equals("M"))
			server = master;
		else if(args[0].equals("S"))
			server = slave;
		
		BeanContext.startPark(server[0][0],Integer.parseInt(server[0][1]), server);
	}
}