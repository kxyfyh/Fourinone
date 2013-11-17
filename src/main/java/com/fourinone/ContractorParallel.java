package com.fourinone;

public abstract class ContractorParallel extends ParallelService
{
	private int parallelPatternFlag = ConfigContext.getParallelPattern();
	
	//from workservice or park: WorkerServiceProxy/WorkerParkProxy(static ParkProxy/ParkLocal; workerType/nodename doTask)
	protected WorkerLocal[] getWaitingWorkers(String workerType)
	{
		//return parallelPatternFlag==0?getWaitingWorkersFromService(workerType):getWaitingWorkersFromPark(workerType);
		return getWaitingWorkers(workerType,null);
	}
	
	WorkerLocal[] getWaitingWorkers(String workerType, MigrantWorker mw)//protected
	{
		return parallelPatternFlag==0?getWaitingWorkersFromService(workerType, mw):getWaitingWorkersFromPark(workerType);
	}
	
	public void waitWorking(String host, int port, String workerType){
		ContractorService ctorsv = new ContractorService(this);
		ctorsv.waitWorking(host,port,workerType);
	}
	
	public void waitWorking(String workerType){
		ContractorService ctorsv = new ContractorService(this);
		ctorsv.waitWorking(workerType);
	}
	
	protected void exit(){
		PoolExector.close();
	}
	
	abstract WorkerLocal[] getWaitingWorkersFromService(String workerType, MigrantWorker mw);
	
	abstract WorkerLocal[] getWaitingWorkersFromService(String workerType);
	
	abstract WorkerLocal[] getWaitingWorkersFromPark(String workerType);
	
	abstract WareHouse[] doTaskBatch(WareHouse wh);
	
	abstract WareHouse[] doTaskBatch(WorkerLocal[] wks, WareHouse wh);
		
	public abstract WareHouse giveTask(WareHouse inhouse);
}