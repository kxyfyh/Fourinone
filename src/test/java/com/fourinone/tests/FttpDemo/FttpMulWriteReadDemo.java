package com.fourinone.tests.FttpDemo;

import com.fourinone.FttpAdapter;
import com.fourinone.FttpException;
import com.fourinone.Result;

public class FttpMulWriteReadDemo
{
	public static void fttpMulWrite(){
		try{
			String fttppath = "fttp://192.168.0.1/home/log/1.log";
			Result<Integer>[] rs = new Result[3];
			FttpAdapter fa0 = new FttpAdapter(fttppath);
			rs[0]=fa0.getFttpWriter(0,5).tryWrite("hello".getBytes());			
			FttpAdapter fa1 = new FttpAdapter(fttppath);
			rs[1]=fa1.getFttpWriter(5,5).tryWrite("world".getBytes());
			FttpAdapter fa2 = new FttpAdapter(fttppath);
			rs[2]=fa2.getFttpWriter(10,5).tryWrite("fttp!".getBytes());
			
			int n=0;
			while(n<3){
				for(int i=0;i<rs.length;i++){
					if(rs[i]!=null&&rs[i].getStatus()!=Result.NOTREADY){
						System.out.println(rs[i].getResult());
						rs[i]=null;
						n++;
					}
				}
			}
			
			fa0.close();
			fa1.close();
			fa2.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public static void fttpMulRead(){
		try{
			Result<byte[]>[] rs = new Result[3];
			String fttppath = "fttp://192.168.0.1/home/log/1.log";
			
			FttpAdapter fa0 = new FttpAdapter(fttppath);
			rs[0]=fa0.getFttpReader(0,5).tryReadAll();
			FttpAdapter fa1 = new FttpAdapter(fttppath);
			rs[1]=fa1.getFttpReader(5,5).tryReadAll();
			FttpAdapter fa2 = new FttpAdapter(fttppath);
			rs[2]=fa2.getFttpReader(10,5).tryReadAll();
			
			int n=0;
			while(n<3){
				for(int i=0;i<rs.length;i++){
					if(rs[i]!=null&&rs[i].getStatus()!=Result.NOTREADY){
						System.out.println(new String(rs[i].getResult()));
						rs[i]=null;
						n++;
					}
				}
			}
			
			fa0.close();
			fa1.close();
			fa2.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		fttpMulWrite();
		fttpMulRead();
	}
}