package com.fourinone;

public abstract class AsyncExector{
	public abstract void task();
	public void run(){
		try{
			new Thread(new Runnable(){
				public void run(){
					task();
				}
			}).start();
		}catch(Exception e){
			//e.printStackTrace();
			LogUtil.info("AsyncExector", "task", e);
		}
	}
}