package com.fourinone.tests.FttpDemo;

import com.fourinone.FttpAdapter;
import com.fourinone.FttpException;
import com.fourinone.Result;
import com.fourinone.FttpAdapter.FttpReadAdapter;
import com.fourinone.FttpAdapter.FttpWriteAdapter;
import com.fourinone.FileBatch;

public class FttpBatchWriteReadDemo extends FileBatch
{
	public void fttpBatchWrite(){
		try{
			String fttppath = "fttp://192.168.0.1/home/log/1.log";
			FttpWriteAdapter[] fwas = new FttpWriteAdapter[3];
			
			FttpAdapter fa0 = new FttpAdapter(fttppath);
			fwas[0]=fa0.getFttpWriter(0,5);
			
			FttpAdapter fa1 = new FttpAdapter(fttppath);
			fwas[1]=fa1.getFttpWriter(5,5);
			
			FttpAdapter fa2 = new FttpAdapter(fttppath);
			fwas[2]=fa2.getFttpWriter(10,5);
			
			Result<Integer>[] rs = this.writeBatch(fwas, "abcde".getBytes());
			
			System.out.println(rs[0].getResult());
			System.out.println(rs[1].getResult());
			System.out.println(rs[2].getResult());
			
			fa0.close();
			fa1.close();
			fa2.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public void fttpBatchRead(){
		try{
			String fttppath = "fttp://192.168.0.1/home/log/1.log";
			
			FttpReadAdapter[] fras = new FttpReadAdapter[3];
			
			FttpAdapter fa0 = new FttpAdapter(fttppath);
			fras[0]=fa0.getFttpReader(0,5);
			
			FttpAdapter fa1 = new FttpAdapter(fttppath);
			fras[1]=fa1.getFttpReader(5,5);
			
			FttpAdapter fa2 = new FttpAdapter(fttppath);
			fras[2]=fa2.getFttpReader(10,5);
			
			Result<byte[]>[] rs = this.readAllBatch(fras);
			
			System.out.println(new String(rs[0].getResult()));
			System.out.println(new String(rs[1].getResult()));
			System.out.println(new String(rs[2].getResult()));
			
			fa0.close();
			fa1.close();
			fa2.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public void fttpBatchReadWrite(){
		try{
			String readpath = "fttp://192.168.0.1/home/log/1.log";
			FttpReadAdapter[] fras = new FttpReadAdapter[3];
			FttpAdapter fa0 = new FttpAdapter(readpath);
			fras[0]=fa0.getFttpReader(0,5);
			FttpAdapter fa1 = new FttpAdapter(readpath);
			fras[1]=fa1.getFttpReader(5,5);
			FttpAdapter fa2 = new FttpAdapter(readpath);
			fras[2]=fa2.getFttpReader(10,5);
			
			String writepath = "fttp://192.168.0.1/home/log/2.log";
			FttpWriteAdapter[] fwas = new FttpWriteAdapter[3];
			FttpAdapter faw0 = new FttpAdapter(writepath);
			fwas[0]=faw0.getFttpWriter(0,5);
			FttpAdapter faw1 = new FttpAdapter(writepath);
			fwas[1]=faw1.getFttpWriter(5,5);
			FttpAdapter faw2 = new FttpAdapter(writepath);
			fwas[2]=faw2.getFttpWriter(10,5);
			
			Result<Integer>[] rs = this.readWriteBatch(fras,fwas);
			
			System.out.println(rs[0].getResult());
			System.out.println(rs[1].getResult());
			System.out.println(rs[2].getResult());
			
			fa0.close();
			fa1.close();
			fa2.close();
			faw0.close();
			faw1.close();
			faw2.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
	
	public Result[] undo(Result[] rtarr){
		System.out.println("undo.........");
		for(int i=0;i<rtarr.length;i++){
			if(rtarr[i].getStatus()==Result.EXCEPTION)
				System.out.println("Result index"+i+" Error");
		}
		return rtarr;
	}
	
	public static void main(String[] args){
		FttpBatchWriteReadDemo fwrd = new FttpBatchWriteReadDemo();
		fwrd.fttpBatchWrite();
		fwrd.fttpBatchRead();
		fwrd.fttpBatchReadWrite();
	}
}