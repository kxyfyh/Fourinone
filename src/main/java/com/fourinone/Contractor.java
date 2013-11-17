package com.fourinone;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;

public abstract class Contractor extends ContractorParallel
{
	private Contractor ctor;
	WorkerLocal[] wks = null; 
	
	public Contractor toNext(Contractor ctor)
	{
		this.ctor = ctor;
		return ctor;
	}
	
	public final WareHouse giveTask(WareHouse inhouse, boolean chainProcess)
	{
		WareHouse outhouse = giveTask(inhouse);
		if(chainProcess&&ctor!=null)
			return ctor.giveTask(outhouse, chainProcess);
		return outhouse;
	}
	
	final <T> T[] getLocals(String localType, Class<T> local){
		T[] locals = null;
		WorkerLocal[] wkls = getWaitingWorkers(localType);
		if(wkls!=null){
			locals = (T[])Array.newInstance(local, wkls.length);
			for(int i=0;i<wkls.length;i++){
				locals[i]=(T)wkls[i];
			}
		}
		return locals;
	}
	/*
	final WorkerLocal[] getWorkerLocals(Class[] locals){
		WorkerLocal[] wkls = null;
		if(locals!=null){
			wkls = new WorkerLocal[locals.length];
			for(int i=0;i<locals.length;i++){
				wkls[i]=(WorkerLocal)locals[i];
			}
		}
		return wkls;
	}*/
	
	public void doProject(WareHouse inhouse)
	{
		/*WareHouse outhouse = giveTask(inhouse);
		if(ctor!=null)
			ctor.doProject(outhouse);*/
		giveTask(inhouse, true);
	}
	
	WorkerLocal[] getWaitingWorkersFromService(String workerType)
	{
		return getWaitingWorkersFromService(workerType,null);
	}
	
	WorkerLocal[] getWaitingWorkersFromService(String workerType, MigrantWorker mw)
	{
		//get host:port from ParkLocal and get WorkerService from host:port
		LogUtil.fine("", "", "getWaitingWorkersFromService:"+workerType+",MigrantWorker:"+mw);
		/*List<ObjectBean> oblist = ParkPatternExector.getWorkerTypeList(workerType);
		List<WorkerLocal> wklist = new ArrayList<WorkerLocal>();
		for(ObjectBean ob:oblist)
		{
			String[] hostport = ((String)ob.toObject()).split(":");
			wklist.add(BeanContext.getWorkerLocal(hostport[0], Integer.parseInt(hostport[1]), workerType));
		}
		return wklist.toArray(new WorkerLocal[wklist.size()]);*/
		//if(wks==null){
			List<String[]> wslist = getWorkersService(workerType);
			List<WorkerLocal> wklist = new ArrayList<WorkerLocal>();
			for(String[] wsinfo:wslist)
				wklist.add(BeanContext.getWorkerLocal(wsinfo[0], Integer.parseInt(wsinfo[1]), wsinfo[2]));
				
			/*if(mw!=null){
				BeanContext.startInetServer();
				for(WorkerLocal wl:wklist)
					((WorkerProxy)wl).setWorker(mw);
			}*/
			wks=wklist.toArray(new WorkerLocal[wklist.size()]);
		//}
		return wks;
	}
	
	WorkerLocal[] getWaitingWorkersFromPark(String workerType)
	{
		LogUtil.fine("", "", "getWaitingWorkersFromPark:"+workerType);
		//if(wks==null){
			List<ObjectBean> oblist = ParkPatternExector.getWorkerTypeList(workerType);
			//System.out.println("getWaitingWorkersFromPark oblist:"+oblist);
			List<WorkerLocal> wklist = new ArrayList<WorkerLocal>();
			for(ObjectBean ob:oblist)
				wklist.add(BeanContext.getWorkerLocal(ob.getName()));
			wks=wklist.toArray(new WorkerLocal[wklist.size()]);
		//}
		return wks;
	}
	
	protected final WareHouse[] doTaskBatch(WareHouse wh){
		return doTaskBatch(wks, wh);
	}
	
	protected final WareHouse[] doTaskBatch(WorkerLocal[] wks, WareHouse wh){
		WareHouse[] hmarr = new WareHouse[wks.length];
		for(int i=0,j=0;j<hmarr.length;){
			if(hmarr[i]==null){
				hmarr[i] = wks[i].doTask(wh);
			}
			else if(hmarr[i].isReady()&&hmarr[i].getMark()){
				hmarr[i].setMark(false);
				j++;
			}
			i=i+1==hmarr.length?0:i+1;
		}
		return hmarr;
	}
}