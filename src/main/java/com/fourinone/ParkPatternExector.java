package com.fourinone;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;

public class ParkPatternExector
{
	private static ParkLocal pl;
	private static LinkedBlockingQueue<ParkPatternBean> bq = new LinkedBlockingQueue<ParkPatternBean>();
	private static AsyncExector aeLastest=null;
	
	static ParkLocal getParkLocal()
	{
		if(pl==null)
		{
			/*String[][] servers = {{"localhost","1888"},{"localhost","1889"}};//get from config.xml
			pl = BeanContext.getPark(servers[0][0], Integer.parseInt(servers[0][1]), servers);*/
			pl = BeanContext.getPark();
		}
		return pl;
	}
	
	static List<ObjectBean> getWorkerTypeList(String workerType)
	{
		return getParkLocal().get("_worker_"+workerType);
	}
	
	static ObjectBean createWorkerTypeNode(String workerType, String nodevalue)
	{
		return getParkLocal().create("_worker_"+workerType, ParkGroup.getKeyId(), nodevalue, AuthPolicy.OP_ALL, true);
	}
	
	static ObjectBean getLastestObjectBean(ObjectBean ob)
	{
		String[] keyarr = ParkObjValue.getDomainNode(ob.getName());
		while(true)
		{
			ObjectBean curob = getParkLocal().getLastest(keyarr[0], keyarr[1], ob);
			if(curob!=null)
				return curob;
		}
			
	}
	
	static ObjectBean updateObjectBean(ObjectBean ob, WareHouse wh)
	{
		String[] keyarr = ParkObjValue.getDomainNode(ob.getName());
		return getParkLocal().update(keyarr[0], keyarr[1], wh);
	}
	
	static void append(ParkPatternBean ppb)
	{
		try{
			ObjectBean ob = getParkLocal().update(ppb.domain, ppb.node, ppb.inhouse);
			ppb.thisversion = ob;
			bq.put(ppb);
			
			if(aeLastest==null){
				LogUtil.fine("", "", "AsyncExector aeLastest:");
				(aeLastest = new AsyncExector(){
					public void task(){
						try{
							while(true){
								ParkPatternBean curPpb = bq.take();
								//System.out.println("ParkPatternExector bq.size():"+bq.size());
								ObjectBean curversion = getParkLocal().getLastest(curPpb.domain, curPpb.node, curPpb.thisversion);
								if(curversion!=null)
								{
									curPpb.thisversion = curversion;
									curPpb.rx.setRecall(false);
									curPpb.outhouse.putAll((WareHouse)curversion.toObject());
									//curPpb.outhouse.setReady(true);
									curPpb.outhouse.setReady(FileResult.READY);
								}
								else
									bq.put(curPpb);
							}
						}catch(Exception e){
							LogUtil.info("AsyncExector", "append aeLastest", e);
							//e.printStackTrace();
						}
					}
				}).run();
			}
		}catch(Exception e){
			LogUtil.info("ParkPatternExector", "append", e);
		}
	}
}