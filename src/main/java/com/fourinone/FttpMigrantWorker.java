package com.fourinone;

import java.net.URI;

public class FttpMigrantWorker extends MigrantWorker
{	
	//fw.waitWorking("fttp", "comuptername");
	static final String FTTPSN = "fttp_";
	static final int FTTPPORT = 2121;
	
	void waitWorkingByService(String host, int port, String workerType)//remove protected
	{
		this.host = host;
		this.port = port;
		this.workerType = workerType;
		BeanContext.startFttpWorker(host, port, workerType, this);
		ParkPatternExector.createWorkerTypeNode(workerType, host+":"+port);
	}
	
	public boolean receive(WareHouse inhouse)
	{
		String fp = inhouse.getString("filepath");
		//System.out.println("fp:"+fp);
		byte[] fbs = (byte[])inhouse.getObj("filebytes");
		boolean wr=true;
		FileAdapter fa = new FileAdapter(fp);
		try{
			if(fbs==null)
				fa.createFile();
			else
				fa.getWriter().writeSafety(fbs);
		}catch(FileException fe){
			LogUtil.info("[FttpMigrantWorker]", "[receive]", fe.getMessage());
			wr = false;
		}
		fa.close();
		
		return wr;
	}
	
	public boolean copy(String frompath, long every, URI tofttppath){//every 8m
		/*System.out.println("tofttppath:"+tofttppath);
		System.out.println("this.host:"+this.host);
		System.out.println("this.port:"+this.port);
		System.out.println("tofttppath.getHost:"+tofttppath.getHost());
		System.out.println("tofttppath.getPort:"+tofttppath.getPort());*/
		boolean r=true;
		FileAdapter fa = new FileAdapter(frompath);
		if(this.host.equals(tofttppath.getHost())&&this.port==getFttpPort(tofttppath)){
			if(fa.isDirectory()||fa.length()!=fa.copyTo(tofttppath.getPath(),every))
				r=false;
		}else{
			Workman wm = getWorkerElse(FTTPSN, tofttppath.getHost(), getFttpPort(tofttppath));
			//just for file
			WareHouse inhouse = new WareHouse("filepath", tofttppath.getPath());
			byte[] bts = null;
			long begin=0;
			while((bts=fa.getReader(begin, every).readAll())!=null){//readAllSafety
				inhouse.setObj("filebytes",bts);
				if(!wm.receive(inhouse)){
					r=false;
					break;
				}
				begin+=bts.length;
			}
			if(begin==0&&fa.isFile()&&r){
				inhouse.setObj("filebytes",bts);
				r=wm.receive(inhouse);
			}	
		}
		fa.close();
		return r;
	}	
	
	public WareHouse doTask(WareHouse inhouse)
	{
		//throws nosuchmethod exception
		return null;
	}
	
	private int getFttpPort(URI fttppath){
		return fttppath.getPort()==-1?FTTPPORT:fttppath.getPort();
	}
	
	public static void main(String[] args)
	{
		FttpMigrantWorker fw = new FttpMigrantWorker();
		//fw.waitWorking("localhost",Integer.parseInt(args[1]),"fttp");
		fw.waitWorking("localhost",2121,FttpMigrantWorker.FTTPSN);//default port
	}
}