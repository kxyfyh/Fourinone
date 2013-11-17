package com.fourinone.tests.FttpDemo;

import com.fourinone.FttpAdapter;
import com.fourinone.FttpException;
import java.util.Date;

public class FttpCopyDemo
{
	public static void main(String[] args){
		try{
			long begin = (new Date()).getTime();
			FttpAdapter fromfile = new FttpAdapter("fttp://192.168.0.1/home/someone/fttp/tmp/a.log");
			FttpAdapter tofile = fromfile.copyTo("fttp://192.168.0.2/home/someone/fttp/tmp/a.log");
			if(tofile!=null)
				System.out.println("copy ok.");
			long end = (new Date()).getTime();
			System.out.println("time:"+(end-begin)/1000+"s");
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
}