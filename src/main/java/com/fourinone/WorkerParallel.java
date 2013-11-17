package com.fourinone;

public abstract class WorkerParallel extends ParallelService
{
	protected int parallelPatternFlag = ConfigContext.getParallelPattern();
	
	//start workerservice or getlastest from park by keyid&workertype and invoke doTask get WareHouse to park and getlastest;
	public void waitWorking(String host, int port, String workerType)
	{
		if(parallelPatternFlag==1)
			waitWorkingByPark(workerType);
		else
			waitWorkingByService(host, port, workerType);
	}
	
	public void waitWorking(String workerType)
	{
		if(parallelPatternFlag==1)
			waitWorkingByPark(workerType);
		else
			waitWorkingByService(workerType);
	}
	
	abstract void waitWorkingByService(String workerType);
	
	abstract void waitWorkingByService(String host, int port, String workerType);
	
	abstract void waitWorkingByPark(String workerType);
	
	protected abstract Workman[] getWorkerElse();
	
	protected abstract Workman getWorkerIndex(int index);
	
	protected abstract Workman[] getWorkerElse(String workerType);
	
	protected abstract Workman getWorkerIndex(String workerType, int index);
	
	protected abstract Workman getWorkerElse(String workerType, String host, int port);
	
	protected abstract Workman[] getWorkerAll();
	
	protected abstract Workman[] getWorkerAll(String workerType);
	
	protected abstract int getSelfIndex();
	
	protected abstract boolean receive(WareHouse inhouse);

}