package com.fourinone.tests.FttpDemo;

import com.fourinone.FttpAdapter;
import com.fourinone.FttpException;
import com.fourinone.FttpAdapter.FileProperty;

public class FttpOperateDemo{
	public static void printProp(FileProperty prop){
		System.out.println("exists:"+prop.exists());
		System.out.println("isFile:"+prop.isFile());
		System.out.println("isDirectory:"+prop.isDirectory());
		System.out.println("isHidden:"+prop.isHidden());
		System.out.println("canRead:"+prop.canRead());
		System.out.println("canWrite:"+prop.canWrite());
		System.out.println("lastModifiedDate:"+prop.lastModifiedDate());
		System.out.println("length:"+prop.length());
		System.out.println("getParent:"+prop.getParent());
		System.out.println("getName:"+prop.getName());
		System.out.println("getPath:"+prop.getPath());
		if(prop.isDirectory())
			System.out.println("fp.list():"+prop.list().length);
		System.out.println("");
	}
	
	public static void main(String[] args){
		try{
			FttpAdapter dir = new FttpAdapter("fttp://localhost/d:/fttp/tmp/");
			dir.createDirectory();
			FileProperty dirProp = dir.getProperty();
			printProp(dirProp);
			
			FttpAdapter f1 = new FttpAdapter(dirProp.getPath(),"1.log");
			FttpAdapter f2 = null;
			FttpAdapter f3 = null;
			
			if(dirProp.exists()){
				f1.createFile();
				f2 = f1.rename("2.log");
				f3 = f2.copyTo("fttp://localhost/d:/fttp/tmp/3.log");
			}
			
			FileProperty[] childProps = dir.getChildProperty();
			for(int i=0;i<childProps.length;i++){
				printProp(childProps[i]);
			}
			
			System.out.println(f1.delete());
			System.out.println(f2.delete());
			System.out.println(f3.delete());
			System.out.println(dir.delete());
			
			dir.close();
			f1.close();
			f2.close();
			f3.close();
		}catch(FttpException fe){
			fe.printStackTrace();
		}
	}
}