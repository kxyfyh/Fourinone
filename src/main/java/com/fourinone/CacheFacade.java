package com.fourinone;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

public class CacheFacade extends MementoService//implements Cache
{
	private ParkGroup pg = null;//input Groups
	private String cacheService = "CacheService";
	private int keysnum = 100;
	
	public CacheFacade(String cacheService, ObjValue groups) throws RemoteException 
	{
		this.cacheService = cacheService;
		pg = new ParkGroup(groups);
		keysnum = Integer.parseInt(ConfigContext.getConfig("CACHEFACADE","TRYKEYSNUM",null,"100"));
	}
	
	private ParkLocal getParkLocalFromKid(String kid)
	{
		String[][] servers = pg.getServers(kid);
		return BeanContext.getPark(servers[0][0], Integer.parseInt(servers[0][1]), cacheService, servers);
	}
	
	@Delegate(interfaceName="com.fourinone.Cache",methodName="add",policy=DelegatePolicy.Implements)
	public String addObj(String name, byte[] obj) throws RemoteException
	{
		String kid = null;
		ObjectBean ob = null;
		
		int i=0;
		while(ob==null&&i<keysnum)//try to this keys, keys small if group big, keys big if group small
		{
			kid = ParkGroup.getKeyId();//attach 'cache' or 'ca' to begin
			ob = getParkLocalFromKid(kid).create(kid, name, obj);
			i++;
			//System.out.println("I..........."+i+",keysnum:"+keysnum);
		}
		return kid;
	}
	
	@Delegate(interfaceName="com.fourinone.Cache",methodName="put",policy=DelegatePolicy.Implements)
	public ObjectBean putObj(String keyid, String name, byte[] obj) throws RemoteException
	{
		ParkLocal pl = getParkLocalFromKid(keyid);
		ObjectBean ob = pl.create(keyid, name, obj);
		if(ob==null)
			ob = pl.update(keyid, name, obj);
		return ob;
	}
	
	@Delegate(interfaceName="com.fourinone.Cache",methodName="get",policy=DelegatePolicy.Implements)
	public ObjectBean getObj(String keyid, String name) throws RemoteException
	{
		return getParkLocalFromKid(keyid).get(keyid, name);
	}
	
	@Delegate(interfaceName="com.fourinone.Cache",methodName="get",policy=DelegatePolicy.Implements)
	public List<ObjectBean> getObj(String keyid) throws RemoteException
	{
		return getParkLocalFromKid(keyid).get(keyid);
	}	
	
	@Delegate(interfaceName="com.fourinone.Cache",methodName="remove",policy=DelegatePolicy.Implements)
	public ObjectBean removeObj(String keyid, String name) throws RemoteException
	{
		return getParkLocalFromKid(keyid).delete(keyid, name);
	}
	
	@Delegate(interfaceName="com.fourinone.Cache",methodName="remove",policy=DelegatePolicy.Implements)
	public List<ObjectBean> removeObj(String keyid) throws RemoteException
	{
		return getParkLocalFromKid(keyid).delete(keyid);
	}
	
	/*
	public String add(String type, byte[] obj) throws RemoteException
	{
		String kid = ParkGroup.getKeyId();
		String[][] servers = pg.getServers(kid);
		ParkLocal pl = BeanContext.getPark(servers[0][0], Integer.parseInt(servers[0][1]), servers);//servers
		pl.create(type, kid, obj);
		return kid;
		//if(!)
			//pl.update("Cache", String node, Serializable obj);
	}
	
	public Object get(String type, String keyid) throws RemoteException
	{
		String[][] servers = pg.getServers(keyid);
		ParkLocal pl = BeanContext.getPark(servers[0][0], Integer.parseInt(servers[0][1]), servers);
		return pl.get(type, keyid).toObject();
	}
	
	public Object putByKey(String keyId, String name, byte[] obj) throws RemoteException
	{
		String[][] servers = pg.getServers(keyId);
		ParkLocal pl = BeanContext.getPark(servers[0][0], Integer.parseInt(servers[0][1]), servers);//servers
		ObjectBean ob = pl.create(keyId, name, obj);
		if(ob==null)
			ob = pl.update(keyId, name, obj);
		return ob.toObject();
	}
	
	public Object getByKey(String keyid, String name) throws RemoteException
	{
		String[][] servers = pg.getServers(keyid);
		ParkLocal pl = BeanContext.getPark(servers[0][0], Integer.parseInt(servers[0][1]), servers);
		return pl.get(keyid, name).toObject();
	}
	*/
	
	public static void main(String[] args)
	{
		BeanContext.startCache();
		//BeanContext.startCache("localhost",Integer.parseInt(args[0]));
		/*String kid = pg.getKeyId();
		System.out.println(kid);
		System.out.println(turnKeyId(kid));
		
		for(int i=0;i<5;i++){
			System.out.println((System.currentTimeMillis()+"").length());
			System.out.println(System.currentTimeMillis());
			//String hs = Long.toHexString(System.currentTimeMillis());
			//System.out.println(hs);
			System.out.println();
		}
		for(int i=0;i<5;i++)
			System.out.println(System.nanoTime());
		//for(int i=0;i<5;i++){
			System.out.println(System.currentTimeMillis()+""+System.nanoTime());
			String hexstr = Double.toHexString(Double.valueOf(System.currentTimeMillis()+""+System.nanoTime()));
			System.out.println(hexstr);
			System.out.println(Double.valueOf(hexstr).toString());
		//}*/
	}
}