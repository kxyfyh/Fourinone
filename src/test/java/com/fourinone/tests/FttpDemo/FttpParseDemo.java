package com.fourinone.tests.FttpDemo;

import com.fourinone.FttpAdapter;
import com.fourinone.FttpException;
import com.fourinone.FileAdapter;
import com.fourinone.FileAdapter.ByteReadParser;

public class FttpParseDemo
{
	public static void main(String[] args){
		try{
			FttpAdapter fa = new FttpAdapter("fttp://192.168.0.1/home/log/b.log");
			byte[] bts = fa.getFttpReader(0,100).readAll();
			System.out.println(bts.length);
			ByteReadParser brp = FileAdapter.getByteReadParser(bts);
			byte[] splitbts = brp.read(" ".getBytes());
			System.out.println(new String(splitbts));
			byte[] linebts = brp.readLine();
			System.out.println(new String(linebts));
			byte[] lastbts = brp.readLast("googl".getBytes());
			System.out.println(new String(lastbts));
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
}