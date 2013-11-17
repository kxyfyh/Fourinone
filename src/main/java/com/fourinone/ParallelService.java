package com.fourinone;

import java.util.List;
import java.util.ArrayList;

abstract class ParallelService
{
	abstract public void waitWorking(String host, int port, String workerType);
	abstract public void waitWorking(String workerType);
	
	/*WorkerLocal[] getWorkersService(String workerType)
	{
		//LogUtil.fine("", "", "getWorkersService:"+workerType);
		List<ObjectBean> oblist = ParkPatternExector.getWorkerTypeList(workerType);
		List<WorkerLocal> wklist = new ArrayList<WorkerLocal>();
		for(ObjectBean ob:oblist)
		{
			String[] hostport = ((String)ob.toObject()).split(":");
			wklist.add(BeanContext.getWorkerLocal(hostport[0], Integer.parseInt(hostport[1]), workerType));
		}
		return wklist.toArray(new WorkerLocal[wklist.size()]);
	}
	
	Workman[] getWorkersService(String host, int port, String workerType)
	{
		//LogUtil.fine("", "", "getWorkersService:"+workerType);
		List<ObjectBean> oblist = ParkPatternExector.getWorkerTypeList(workerType);
		List<Workman> wklist = new ArrayList<Workman>();
		for(ObjectBean ob:oblist)
		{
			String[] hostport = ((String)ob.toObject()).split(":");
			if(!hostport[0].equals(host)&&!Integer.parseInt(hostport[1])!=port)
				wklist.add(BeanContext.getWorkman(hostport[0], Integer.parseInt(hostport[1]), workerType));
		}
		return wklist.toArray(new WorkerLocal[wklist.size()]);
	}*/
	
	List<String[]> getWorkersService(String host, int port, String workerType)
	{
		//LogUtil.fine("", "", "getWorkersService:"+workerType);
		List<ObjectBean> oblist = ParkPatternExector.getWorkerTypeList(workerType);
		List<String[]> wslist = new ArrayList<String[]>();
		for(ObjectBean ob:oblist)
		{
			//System.out.println("ob.toObject():"+ob.toObject());
			String[] hostport = ((String)ob.toObject()).split(":");
			if(!hostport[0].equals(host)||Integer.parseInt(hostport[1])!=port)//&&
				wslist.add(new String[]{hostport[0], hostport[1], workerType});
			
		}
		return wslist;
	}
	
	List<String[]> getWorkersService(String workerType)
	{
		return getWorkersService(null,0,workerType);
	}
}