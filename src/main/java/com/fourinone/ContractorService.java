package com.fourinone;

class ContractorService extends MigrantWorker{
	private ContractorParallel ctor = null;
	ContractorService(ContractorParallel ctor){
		this.ctor = ctor;
	}
	
	public WareHouse doTask(WareHouse inhouse){
		return ctor.giveTask(inhouse);
	}
	
	/*void waitWorking(String host, int port, String workerType){
		
	}
	
	void waitWorking(String workerType);*/
}