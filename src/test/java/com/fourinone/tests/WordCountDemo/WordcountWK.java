package com.fourinone.tests.WordCountDemo;

import com.fourinone.MigrantWorker;
import com.fourinone.WareHouse;
import com.fourinone.FileAdapter;
import com.fourinone.FileAdapter.ReadAdapter;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;

public class WordcountWK extends MigrantWorker
{	
	public WareHouse doTask(WareHouse inhouse)
	{
		String filepath = inhouse.getString("filepath");
		long n=64;//FileAdapter.m(8)
		long num = (new File(filepath)).length()/n;
		FileAdapter fa = null;
		ReadAdapter ra = null;
		byte[] bts = null;
		HashMap<String,Integer> wordcount = new HashMap<String,Integer>();
		fa = new FileAdapter(filepath);
		for(long i=0;i<num;i++){
			ra = fa.getReader(i*n, n);
			bts = ra.readAll();
			StringTokenizer tokenizer = new StringTokenizer(new String(bts));
			while(tokenizer.hasMoreTokens()){
				String curword = tokenizer.nextToken();
				if(wordcount.containsKey(curword))
					wordcount.put(curword, wordcount.get(curword)+1);
				else
					wordcount.put(curword, 1);
			}
		}
		fa.close();
		return new WareHouse("word", wordcount);
	}
	
	public static void main(String[] args)
	{
		WordcountWK mw = new WordcountWK();
		mw.waitWorking("localhost",Integer.parseInt(args[0]),"wordcount");
	}
}