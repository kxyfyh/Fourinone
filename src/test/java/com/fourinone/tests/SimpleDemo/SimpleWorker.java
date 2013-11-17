package com.fourinone.tests.SimpleDemo;

import com.fourinone.MigrantWorker;
import com.fourinone.WareHouse;

public class SimpleWorker extends MigrantWorker
{	
	public WareHouse doTask(WareHouse inhouse)
	{
		String word = inhouse.getString("word");
		System.out.println(word+" from Contractor.");
		return new WareHouse("word", word+" world!");
	}
	
	public static void main(String[] args)
	{
		SimpleWorker mw = new SimpleWorker();
		mw.waitWorking("simpleworker");
	}
}