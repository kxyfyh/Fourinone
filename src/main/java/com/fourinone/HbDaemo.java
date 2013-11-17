package com.fourinone;

import java.util.Timer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.ArrayList;
import java.rmi.ConnectException;
import java.util.List;

public class HbDaemo 
{
	static Timer tm = new Timer();
	private static PutHbTask putTask = null;
	private static GetHbTask getTask = null;
	private static ClearTask clrTask = null;
	final static long pt = Long.parseLong(ConfigContext.getConfig("PARK","HEARTBEAT",null,"500"));
	final static long dt = Long.parseLong(ConfigContext.getConfig("PARK","MAXDELAY",null,"0"));
	final static long gt = pt*2;
	final static String vs = "\u3001";
	
	public static void runTask(TimerTask tt, int d, long p){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, d);
		tm.scheduleAtFixedRate(tt, cal.getTime(), p);
	}
	
	public static void runPutTask(Park pk, ParkLeader pl, String domain, String node, String sessionid){
		if(putTask==null)
		{
			LogUtil.fine("", "", "heartbeat runPutTask:");
			putTask = new PutHbTask(pk, pl, domain, node, sessionid);
			runTask(putTask, (int)pt, pt);
		}
		else{
			putTask.append(domain, node);
		}
	}

	public static void runGetTask(ObjValue hbinfo, ParkService ps){
		if(getTask==null)
		{
			LogUtil.fine("", "", "heartbeat runGetTask:"+hbinfo);
			getTask = new GetHbTask(hbinfo, ps);
			runTask(getTask, (int)pt, pt);
		}
	}
	
	//run clear cache
	public static void runClearTask(ParkService ps){
		if(clrTask==null)
		{
			Double cpd = new Double(ConfigContext.getConfig("PARK","CLEARPERIOD",null,"12"));
			if(cpd>0)
			{
				LogUtil.fine("", "", "Run ClearTask");
				Double exp = new Double(ConfigContext.getConfig("PARK","EXPIRATION",null,"24"));
				clrTask = new ClearTask(ps,ConfigContext.getSecTime(exp));
				runTask(clrTask, 5000, ConfigContext.getSecTime(cpd));
			}
		}
	}
	
	public static void main(String[] args)
	{
		//if(args[0].equals("get"))
			//HbDaemo.runTask(new GetHbTask());
		//else
			//HbDaemo.runTask(new PutHbTask());
	}
}

class PutHbTask extends TimerTask
{
	private Park pk;
	private ParkLeader pl;
	private String domain;
	private String node;
	private String sessionid;
	private ArrayList<String> putList = new ArrayList<String>();
	
	PutHbTask(Park pk, ParkLeader pl, String domain, String node, String sessionid){
		this.pk=pk;
		this.pl=pl;
		this.domain=domain;
		this.node=node;
		this.sessionid=sessionid;
		append(domain, node);
	}
	
	public void append(String domain, String node){
		putList.add(domain+HbDaemo.vs+node);
	}
	
	public void run()
	{
		try{
			//System.out.println("heartbeat");
			String[] putarr = new String[putList.size()];
			putList.toArray(putarr);
			pk.heartbeat(putarr, sessionid);
		}catch(Exception e){
			//e.printStackTrace();
			LogUtil.info("[PutHbTask]", "[heartbeat:]", e.getMessage());
			//this.cancel();
			//HbDaemo.tm.cancel();
			if(e instanceof ConnectException){
				this.pk = pl.getNextLeader();
			}
		}
	}
}

class GetHbTask extends TimerTask
{
	private ObjValue hbinfo;
	private ParkService ps;
	GetHbTask(ObjValue hbinfo, ParkService ps){
		this.hbinfo = hbinfo;
		this.ps = ps;
	}

	public void run() 
	{
		ArrayList<String> hbarr = hbinfo.getObjNames();
		for(String domainnodekey:hbarr)
		{
			long curtime = new Date().getTime();
			Long lasttime = (Long)hbinfo.getObj(domainnodekey);
			long t = lasttime!=null?curtime-lasttime:0;
			//System.out.println(domainnodekey+":"+t);
			if(t>HbDaemo.gt){
				//System.out.println("t/HbDaemo.gt:"+t/HbDaemo.gt);
				if(HbDaemo.dt>0&&t/HbDaemo.gt<2)
					LogUtil.warn("[Park]", "[Slow]", domainnodekey+" slow and weak heartbeat!");
				if(t>HbDaemo.gt+HbDaemo.dt){
					if(HbDaemo.dt>0)
						LogUtil.warn("[Park]", "[Dead]", domainnodekey+" has exceeded max delaytime and is regarded as dead by park.");
					hbinfo.remove(domainnodekey);
					String[] keyarr = domainnodekey.split(HbDaemo.vs);
					ps.delete(keyarr[0], keyarr[1]);
					LogUtil.fine("", "", "hbinfo:"+hbinfo);
				}
			}
		}
	}
}

class ClearTask extends TimerTask
{
	private ParkService ps;
	private long expl;
	ClearTask(ParkService ps, long expl){
		this.ps = ps;
		this.expl = expl;
	}

	public void run()
	{
		//long expl = HbDaemo.ConfigContext.getSecTime(exp);
		ParkObjValue pov = (ParkObjValue)ps.getTheParkinfo();
		List<String[]> expkeys = pov.getParkInfoExp(expl);
		
		if(expkeys.size()>0)
		{
			LogUtil.fine("", "", "Get some expiration data and save for backup...");
			//intofile and date for name
		}
		
		for(String[] keyarr:expkeys)
		{
			LogUtil.fine("[Clear]", "[Expiration]", pov.getDomainnodekey(keyarr[0], keyarr[1]));
			ps.delete(keyarr[0], keyarr[1]);
		}
	}
}