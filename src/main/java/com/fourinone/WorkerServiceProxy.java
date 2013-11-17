package com.fourinone;

import java.rmi.RemoteException;

public class WorkerServiceProxy extends PoolExector
{
	Worker wk;
	private RecallException rx;
	private static boolean InetFlag = true;
	private String host=null;
	private int port;
	
	protected WorkerServiceProxy(String host, int port, String sn)
	{
		this.host = host;
		this.port = port;
		wk = BeanContext.getWorker(host, port, sn);
		rx = new RecallException();
	}
	
	@Delegate(interfaceName="com.fourinone.Workman",methodName="receive",policy=DelegatePolicy.Implements)
	public boolean receiveMaterials(WareHouse inhouse)
	{
		boolean received = false;
		try{
			received = wk.receiveMaterials(inhouse);
		}catch(Exception e){
			LogUtil.info("receiveMaterials", "exception", e);
		}
		return received;
	}
	
	@Delegate(interfaceName="com.fourinone.Workman",methodName="getHost",policy=DelegatePolicy.Implements)
	public String getHost(){
		return host;
	}
	
	@Delegate(interfaceName="com.fourinone.Workman",methodName="getPort",policy=DelegatePolicy.Implements)
	public int getPort(){
		return port;
	}
	
	@Delegate(interfaceName="com.fourinone.WorkerLocal",methodName="setWorker",policy=DelegatePolicy.Implements)
	public void setWorkerObject(MigrantWorker mwobj){
		try{
			//System.out.println("setWorkerObject:"+mwobj);
			InetStart();
			wk.setMigrantWorker(mwobj);
		}catch(Exception e){
			LogUtil.info("setWorkerObject", "exception", e);
		}
	}
	
	@Delegate(interfaceName="com.fourinone.CtorLocal",methodName="giveTask",policy=DelegatePolicy.Implements)
	public WareHouse giveTaskServiceProxy(WareHouse inhouse){
		return doTaskServiceProxy(inhouse);
	}
	
	@Delegate(interfaceName="com.fourinone.WorkerLocal",methodName="interrupt",policy=DelegatePolicy.Implements)
	public void cancel(){
		try{
			wk.stopTask();
		}catch(Exception e){
			LogUtil.info("Interrupt", "exception", e);
		}
	}
	
	@Delegate(interfaceName="com.fourinone.WorkerLocal",methodName="doTask",policy=DelegatePolicy.Implements)
	public WareHouse doTaskServiceProxy(WareHouse inhouse){
		return doTaskServiceProxy(inhouse, 0);
	}
	
	@Delegate(interfaceName="com.fourinone.WorkerLocal",methodName="doTask",policy=DelegatePolicy.Implements)
	public WareHouse doTaskServiceProxy(final WareHouse inhouse, long t)
	{
		if(!ConfigContext.getServiceFlag()&&rx.tryRecall(inhouse)==-1)
			return null;
			
		final WareHouse outhouse = new WareHouse(false);
		execute(new Runnable(){
			public void run(){
				try{
					//System.out.println(inhouse);
					WareHouse wh = wk.doTask(inhouse);
					if(wh!=null)
					{
						if(!ConfigContext.getServiceFlag())
							rx.setRecall(false);
						outhouse.putAll(wh);
					}
					//System.out.println(outhouse);
					outhouse.setReady(FileResult.READY);
				}catch(Exception e){
					//System.out.println("doTaskServiceProxy:"+e);
					LogUtil.info("doTaskServiceProxy", "exception", e);
					//e.printStackTrace();
					outhouse.setReady(FileResult.EXCEPTION);
				}
				//outhouse.setReady(true);
			}
		},new Runnable(){
             public void run(){
             	cancel();
             }
        },t);
		
		return outhouse;
	}
	
	public WareHouse dotaskimpl(final WareHouse inhouse){
		final WareHouse outhouse = new WareHouse();
		//final WareHouse aehouse = new WareHouse("doTask",new WareHouse());
		new AsyncExector(){
			public void task(){
				try{
					outhouse.putAll(wk.doTask(inhouse));
				}catch(RemoteException e){
					LogUtil.info("AsyncExector", "WareHouse", e);
				}
			}
		}.run();
		return outhouse;//(WareHouse)aehouse.getObj("doTask");
	}
	
	private static void InetStart(){
		if(InetFlag){
			BeanContext.startInetServer();
			InetFlag = false;
		}
	}
}