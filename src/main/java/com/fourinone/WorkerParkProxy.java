package com.fourinone;

final public class WorkerParkProxy
{
	private String domainnodekey;
	private RecallException rx;
	
	WorkerParkProxy(String domainnodekey)
	{
		this.domainnodekey = domainnodekey;
		rx = new RecallException();
	}
	
	@Delegate(interfaceName="com.fourinone.WorkerLocal",methodName="doTask",policy=DelegatePolicy.Implements)
	public WareHouse doTaskParkProxy(WareHouse inhouse){
		if(rx.tryRecall(inhouse)==-1)
			return null;
		
		//System.out.println("doTaskParkProxy:"+inhouse);
		WareHouse outhouse = new WareHouse(false);
		//domain,node,inhouse,outhouse->ParkPatternBean->ParkPatternExector to park and get whLastest
		String[] keyarr = ParkObjValue.getDomainNode(domainnodekey);
		ParkPatternBean ppb = new ParkPatternBean(keyarr[0],keyarr[1],inhouse,outhouse,rx);
		ParkPatternExector.append(ppb);
		return outhouse;
	}
}