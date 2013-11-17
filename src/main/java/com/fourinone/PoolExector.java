package com.fourinone;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class PoolExector //java.io.Closeable
{
	private static ThreadPoolExecutor tpe;
	private static ScheduledThreadPoolExecutor stpe;
	
	static ThreadPoolExecutor tpe()
	{
		if(tpe==null)
		{
			int corePoolSize = ConfigContext.getInitServices();
			int maximumPoolSize = ConfigContext.getMaxServices();
			long keepAliveTime = 3000;
			TimeUnit unit = TimeUnit.MILLISECONDS;
			BlockingQueue<Runnable> waitQueue = new ArrayBlockingQueue<Runnable>(2000);
			RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();//ThreadPoolExecutor.CallerRunsPolicy();
			tpe =new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, waitQueue, handler);
		}
		return tpe;
	}
	
	static ScheduledThreadPoolExecutor stpe()
	{
		if(stpe==null)
		{
			int corePoolSize = ConfigContext.getInitServices();
			stpe =new ScheduledThreadPoolExecutor(corePoolSize);
		}
		return stpe;
	}
	
	static void execute(Runnable d, Runnable i, long t){
		tpe().execute(d);
		if(t>0)
			stpe().schedule(i,t,TimeUnit.SECONDS);
	}
	
	static void close(){
		if(tpe!=null){
			try{
				tpe.shutdown();
				tpe=null;
			}catch(SecurityException se){
				LogUtil.info("[tpe]", "[close]", "[Error Exception:]", se);
			}
		}
		if(stpe!=null){
			try{
				stpe.shutdown();
				stpe=null;
			}catch(SecurityException se){
				LogUtil.info("[stpe]", "[close]", "[Error Exception:]", se);
			}
		}
	}
}