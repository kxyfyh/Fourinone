package com.fourinone;

import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;

public class StartResult<E> extends Result{
	private Process p;
	private long s;
	
	public StartResult(){
		super();
	}
	
	public StartResult(boolean ready)
	{
		super(ready);
	}
	
	public StartResult(Process p, boolean ready){
		this(ready);
		this.p = p;
		s = (new Date()).getTime();
	}
	
	public int getStatus()
	{
		try{
			//System.out.println("p.exitValue():"+p.exitValue());
			setResult(p.exitValue(),p.exitValue()==0?READY:EXCEPTION);
		}catch(IllegalThreadStateException ex){
			//status = NOTREADY;
		}
		return status;
	}
	
	public int getStatus(long timeout){
		if((new Date()).getTime()-s>timeout){
			TimeoutException te = new TimeoutException("TryStart StartResult Timeout");
			LogUtil.info("[TryStart]", "[Timeout]", te.getMessage()+" and be killed");
			kill(EXCEPTION);
			//System.out.println("kill status:"+status);
			return status;
		}else return getStatus();
	}
	
	public void kill(){
		kill(READY);
	}
	
	private void kill(int status){
		p.destroy();
		setResult(1,status);
	}
	
	public void print(final String logpath){
		final InputStream is = p.getInputStream();
		new AsyncExector(){
			public void task(){ //throws Exception
				BufferedReader stdout = new BufferedReader(new InputStreamReader(is));
				FileAdapter fa = new FileAdapter(new File(logpath).getPath());
				String line="";
				try{
					while((line=stdout.readLine())!=null){
						//System.out.println(line);
						fa.getWriter().write((line+"\r\n").getBytes());
					}	
				}catch(Exception e){
					LogUtil.info("AsyncExector", "print", e);
				}finally{
					try{
						stdout.close();
						fa.close();
					}catch(Exception e){
						LogUtil.info("AsyncExector", "close", e);
					}
				}
				//System.out.println("print over.");
			}
		}.run();
	}
	
	void setResult(int res, int status)
	{
		setResult(res);
		setReady(status);
	}
	
	public static long h(long t){
		return m(t*60l);
	}
	
	public static long m(long t){
		return s(t*60l);
	}
	
	public static long s(long t){
		return t*1000l;
	}
}