package com.fourinone;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkerService extends MementoService
{
	MigrantWorker migworker;
	private Lock lk = new ReentrantLock();
	
	public WorkerService(MigrantWorker migworker) throws RemoteException
	{
		this.migworker = migworker;
	}
	
	@Delegate(interfaceName="com.fourinone.Worker",methodName="setMigrantWorker",policy=DelegatePolicy.Implements)
	public synchronized void setExtendedWorker(MigrantWorker migworker) throws RemoteException
	{
		//System.out.println("setMigrantWorker:"+migworker);
		migworker.setHost(this.migworker.getHost());
		migworker.setWorkerType(this.migworker.getWorkerType());
		migworker.setWorkerJar(this.migworker.getWorkerJar());
		migworker.setPort(this.migworker.getPort());
		//migworker.setSelfIndex(this.migworker.getSelfIndex());
		this.migworker = migworker;
	}
	
	//synchronized
	@Delegate(interfaceName="com.fourinone.Worker",methodName="doTask",policy=DelegatePolicy.Implements)
	public WareHouse doTask(WareHouse inhouse) throws RemoteException
	{
		//System.out.println("WorkerService doTask:"+inhouse);
		//LogUtil.fine("", "", "WorkerService doTask:"+inhouse);
		WareHouse wh = null;
		try{
			if(!ConfigContext.getServiceFlag()){
				lk.lock();
				migworker.interrupted(false);
			}
			wh = migworker.doTask(inhouse);
		}finally{
			if(!ConfigContext.getServiceFlag())
				lk.unlock();
		}
		return wh;
	}
	
	@Delegate(interfaceName="com.fourinone.Worker",methodName="stopTask",policy=DelegatePolicy.Implements)
	public void stopTask() throws RemoteException,InterruptedException{
		if(!ConfigContext.getServiceFlag())
			migworker.interrupted(true);
		else throw new InterruptedException("Worker public service status cant be interrupted!");
	}
	
	@Delegate(interfaceName="com.fourinone.Worker",methodName="receiveMaterials",policy=DelegatePolicy.Implements)
	public boolean receive(WareHouse inhouse) throws RemoteException
	{
		//System.out.println("WorkerService doTask:"+inhouse);
		//LogUtil.fine("", "", "WorkerService doTask:"+inhouse);
		//synchronized
		return migworker.receiveMaterials(inhouse);
	}
	
	public static void main(String[] args)
	{
		/*try{
			BeanService.putBean("localhost",true,Integer.parseInt(args[0]),"WorkerService",new WorkerService());
			BeanService.putBean("localhost",true,Integer.parseInt(args[0]),"WorkerService2",new WorkerService());
		}catch(RemoteException e){
			e.printStackTrace();
		}*/
	}
}