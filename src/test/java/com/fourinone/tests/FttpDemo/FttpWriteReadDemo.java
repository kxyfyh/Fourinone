package com.fourinone.tests.FttpDemo;

import com.fourinone.FttpAdapter;
import com.fourinone.FttpException;

public class FttpWriteReadDemo
{
	public static void fttpWrite(){
		try{
			FttpAdapter fa = new FttpAdapter("fttp://192.168.0.1/home/log/1.log");
			fa.getFttpWriter().write("hello world".getBytes());
			fa.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public static void fttpRead(){
		try{
			FttpAdapter fa = new FttpAdapter("fttp://192.168.0.1/home/log/1.log");
			byte[] bts = fa.getFttpReader().readAll();
			System.out.println("logstr:"+new String(bts));
			
			byte[] hellobts = fa.getFttpReader(0,5).readAll();
			System.out.println("hellostr:"+new String(hellobts));
			
			fa.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		fttpWrite();
		fttpRead();
	}
}