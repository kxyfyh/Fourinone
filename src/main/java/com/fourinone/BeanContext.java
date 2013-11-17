package com.fourinone;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class BeanContext extends ServiceContext
{
	public static void setConfigFile(String configFile){
		ConfigContext.configFile = configFile;
	}
	
	//get all config msg
	public static ParkLocal getPark(String host, int port, String sn, String[][] servers){
		return DelegateConsole.bind(ParkLocal.class, new ParkProxy(host, port, servers, sn));
	}
	
	public static ParkLocal getPark(String host, int port, String[][] servers){
		return getPark(host, port, ConfigContext.getParkService(), servers);
	}
	
	public static ParkLocal getPark(){
		String[][] parkcfg = ConfigContext.getParkConfig();
		return getPark(parkcfg[0][0], Integer.parseInt(parkcfg[0][1]), parkcfg);//input serverconfiglist string[][]
	}
	
	public static void startPark(String host, int port, String sn, String[][] servers)
	{
		try{
			//DelegateConsole.bind(Park.class, new ParkService(host, port, servers, sn));
			startService(host, port, sn, new ParkService(host, port, servers, sn));
			//BeanService.putBean(host, true, port, sn, new ParkService(host, port, servers, sn));//input serverconfiglist string[][]
			if(Boolean.valueOf(ConfigContext.getConfig("PARK","STARTWEBAPP",null,"false")))
				startInetServer();
		}catch(RemoteException e){//new ParkService throw
			LogUtil.info("[BeanContext]", "[startPark]", e);
			//e.printStackTrace();
		}
	}
	
	public static void startPark(String host, int port, String[][] servers)
	{
		startPark(host, port, ConfigContext.getParkService(), servers);
	}
	
	public static void startPark()//String configfile
	{
		String[][] parkcfg = ConfigContext.getParkConfig();
		startPark(parkcfg[0][0], Integer.parseInt(parkcfg[0][1]), parkcfg);
	}
	
	static void startInetServer()
	{
		String[] inetcfg = ConfigContext.getInetConfig();
		ParkInetServer.start(inetcfg[0], Integer.parseInt(inetcfg[1]), 0);
	}
	
	static void startWorker(String host, int port, String sn, MigrantWorker mwk)
	{
		/*try{
			//System.setProperty("responseTimeout", 1000+"");
			startService(host, port, sn, new WorkerService(mwk));
		}catch(RemoteException e){
			LogUtil.info("[BeanContext]", "[startWorker]", e);
		}*/
		startWorker(host, port, sn, mwk, false);
	}
	
	static void startWorker(String sn, MigrantWorker mwk)
	{
		String[] wkcfg = ConfigContext.getWorkerConfig();
		startWorker(wkcfg[0], Integer.parseInt(wkcfg[1]), sn, mwk);
	}
	
	static void startWorker(String host, int port, String sn, MigrantWorker mwk, boolean issup)
	{
		try{
			ParkActive pa = DelegateConsole.bind(Worker.class, new WorkerService(mwk));//Worker.class
			if(issup)
				startService(host, port, sn, pa, ConfigContext.getInetStrConfig(mwk.getWorkerJar()), ConfigContext.getPolicyConfig());
			else
				startService(host, port, sn, pa);
		}catch(RemoteException e){
			LogUtil.info("[BeanContext]", "[startWorker]", e);
		}
	}
	
	static void startFttpWorker(String host, int port, String sn, MigrantWorker mwk)
	{
		try{
			ParkActive pa = (ParkActive)DelegateConsole.bind(new Class[]{Worker.class, FttpWorker.class}, new FttpWorkerService(mwk));//Worker.class
			//ConfigContext.getInetStrConfig(mwk.getWorkerJar())
			startService(host, port, sn, pa);
		}catch(RemoteException e){
			LogUtil.info("[BeanContext]", "[startFttpWorker]", e);
		}
	}
	
	public static void startFttpServer(){//root
		String[] fttpcfg = ConfigContext.getFttpConfig();
		startFttpServer(fttpcfg[0], Integer.parseInt(fttpcfg[1]));
	}
	
	public static void startFttpServer(String host){
		startFttpServer(host, FttpMigrantWorker.FTTPPORT);
	}
	
	public static void startFttpServer(String host, int port){
		new FttpMigrantWorker().waitWorking(host,port,FttpMigrantWorker.FTTPSN);
	}
	
	static Worker getWorker(String host, int port, String sn){
		return getService(Worker.class, host, port, sn);
	}
	
	static WorkerLocal getWorkerLocal(String host, int port, String sn){
		return (WorkerLocal)DelegateConsole.bind(new Class[]{WorkerLocal.class,CtorLocal.class}, new WorkerServiceProxy(host, port, sn));
	}
	
	static WorkerLocal getWorkerLocal(String domainnodekey){
		return DelegateConsole.bind(WorkerLocal.class, new WorkerParkProxy(domainnodekey));
	}
	
	static WorkerLocal getFttpLocal(String host, int port, String sn){
		return (WorkerLocal)DelegateConsole.bind(new Class[]{WorkerLocal.class, CtorLocal.class, FttpLocal.class}, new FttpWorkerProxy(host, port, sn));
	}
	
	static Workman getWorkman(String host, int port, String sn){
		return DelegateConsole.bind(Workman.class, new WorkerServiceProxy(host, port, sn));
	}
	
	/*public static <I extends Remote> void start(String host, int port, String sn, I i)
	{
		try{
			BeanService.putBean(host,true,port,sn,i);
		}catch(RemoteException e){
			e.printStackTrace();
		}
	}
	
	public static <I extends Remote> I get(Class<I> a, String host, int port, String sn){
		I i=null;
		try{
			i=(I)BeanService.getBean(host,port,sn);
		}catch(RemoteException e){
			System.out.println(e);
		}
		return i;
	}*/
	
	public static void startCacheFacade(String host, int port)
	{
		try{
			startService(host, port, ConfigContext.getCacheFacadeService(), DelegateConsole.bind(Cache.class, new CacheFacade(ConfigContext.getCacheService(),ConfigContext.getCacheGroupConfig())));
		}catch(RemoteException e){
			LogUtil.info("[BeanContext]", "[startCacheFacade]", e);
			//e.printStackTrace();
		}
		/*try{
			BeanService.putBean(host,true,port,"CacheService",new CacheFacade());//input Groups
		}catch(RemoteException e){
			e.printStackTrace();
		}*/
	}
	
	public static void startCacheFacade()
	{
		String[] cachecfg = ConfigContext.getCacheFacadeConfig();
		startCacheFacade(cachecfg[0], Integer.parseInt(cachecfg[1]));
	}
	
	public static Cache getCacheFacade(String host, int port)
	{
		return getService(Cache.class, host, port, ConfigContext.getCacheFacadeService());
	}
	
	//<T> Class BeanContextBase
	public static Cache getCacheFacade(){//loadbalance server host and port
		String[] cachecfg = ConfigContext.getCacheFacadeConfig();
		return getCacheFacade(cachecfg[0], Integer.parseInt(cachecfg[1]));
		//return DelegateConsole.bind(Cache.class, new CacheFacade());
		/*
		Cache ca=null;
		try{
			ca=(Cache)BeanService.getBean(host,port,"CacheService");
		}catch(RemoteException e){
			//e.printStackTrace();
			System.out.println(e);
		}
		return ca;
		*/
	}
	
	public static CacheLocal getCache(){
		String[] cachecfg = ConfigContext.getCacheFacadeConfig();
		return getCache(cachecfg[0], Integer.parseInt(cachecfg[1]));
	}
	
	public static CacheLocal getCache(String host, int port){
		return DelegateConsole.bind(CacheLocal.class, new CacheProxy(host, port));
		//return DelegateHandle.bind(CacheLocal.class, CacheProxy.class);
	}
	
	public static void startCache(String host, int port, String[][] servers)
	{
		startPark(host, port, ConfigContext.getCacheService(), servers);
	}
	
	public static void startCache()
	{
		String[][] cachecfg = ConfigContext.getCacheConfig();
		startCache(cachecfg[0][0], Integer.parseInt(cachecfg[0][1]), cachecfg);
	}
	
	public static void exit(){
		close();
	}
	
	public static int start(String... params){
		return start(null, null, params);
	}
	
	public static int start(FileAdapter fa, String... params){
		return start(null, fa, params);
	}
	
	public static int start(Map env, String... params){
		return start(env, null, params);
	}
	
	public static int start(Map env, FileAdapter fa, String... params){
		ProcessBuilder pb = new ProcessBuilder(params);
		pb.redirectErrorStream(true);
		int exitcode = -1;
		if(env!=null)
			pb.environment().putAll(env);
		if(fa!=null)
			pb.directory(fa);
		try{
			Process p = pb.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line="";
			while((line=stdout.readLine())!=null){System.out.println(line);}
			stdout.close();
			//System.out.println("waitFor:"+p.waitFor());
			//System.out.println("exitValue:"+p.exitValue());
			exitcode = p.waitFor();
		}catch(Exception ex){
			LogUtil.info("[BeanContext]", "[start]", ex);
		}
		return exitcode;
	}
	/*
	public static Result<Integer> tryStart(String... params){
		return tryStart(null, params);
	}
	
	public static Result<Integer> tryStart(final FileAdapter fa, final String... params){
		final Result<Integer> fr = new Result<Integer>(false);
		tpe().execute(new Runnable(){
			public void run(){
				try{
					int r = start(fa, params);
					fr.setResult(new Integer(r));
					fr.setReady(FileResult.READY);
				}catch(Throwable e){
					LogUtil.info("[BeanContext]", "[tryStart]", e);
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	*/
	public static StartResult<Integer> tryStart(String... params){
		return tryStart(null, null, params);
	}
	
	public static StartResult<Integer> tryStart(FileAdapter fa, String... params){
		return tryStart(null, fa, params);
	}
	
	public static StartResult<Integer> tryStart(Map env, String... params){
		return tryStart(env, null, params);
	}
	
	public static StartResult<Integer> tryStart(Map env, FileAdapter fa, String... params){
		ProcessBuilder pb = new ProcessBuilder(params);
		pb.redirectErrorStream(true);
		if(env!=null)
			pb.environment().putAll(env);
		if(fa!=null)
			pb.directory(fa);
		try{
			return new StartResult(pb.start(), false);
		}catch(Exception ex){
			LogUtil.info("[BeanContext]", "[startup]", ex);
		}
		return null;
	}
}