package com.fourinone.tests.WordCountDemo;

import com.fourinone.Contractor;
import com.fourinone.WareHouse;
import com.fourinone.WorkerLocal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;

public class WordcountCT extends Contractor
{
	public WareHouse giveTask(WareHouse inhouse)
	{
		WorkerLocal[] wks = getWaitingWorkers("wordcount");
		System.out.println("wks.length:"+wks.length);
		
		WareHouse[] hmarr = doTaskBatch(wks, inhouse);
		
		HashMap<String,Integer> wordcount = new HashMap<String,Integer>();
		for(WareHouse hm:hmarr)
		{
			HashMap<String,Integer> wordhm = (HashMap<String,Integer>)hm.get("word");
			for(Iterator<String> iter=wordhm.keySet().iterator();iter.hasNext();){
				String curword = iter.next();
				if(wordcount.containsKey(curword))
					wordcount.put(curword, wordcount.get(curword)+wordhm.get(curword));
				else
					wordcount.put(curword, wordhm.get(curword));
			}
		}
		
		return new WareHouse("word", wordcount);
	}
	
	public static void main(String[] args)
	{
		Contractor a = new WordcountCT();
		long begin = (new Date()).getTime();
		WareHouse result = a.giveTask(new WareHouse("filepath", args[0]));//"D:\\demo\\parallel\\a\\three.txt"
		long end = (new Date()).getTime();
		System.out.println("time:"+(end-begin)/1000+"s");
		System.out.println("result:"+result);
	}
}