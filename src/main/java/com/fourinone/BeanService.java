package com.fourinone;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class BeanService extends PoolExector
{	
	final static void putBean(String TPYFWYM, boolean TPYRZDY, int TPYDK, String rmname, ParkActive paobj)
	{
		if(TPYFWYM!=null)
			System.setProperty(ConfigContext.getYMMZ(), TPYFWYM);
		if(TPYRZDY)
			System.setProperty(ConfigContext.getRZDY(),"true");
		try{
			LocateRegistry.getRegistry(TPYDK).list();
		}catch(Exception ex){
			try{
				UnicastRemoteObject.exportObject(paobj, 0);
				Registry rgsty = LocateRegistry.createRegistry(TPYDK);//getRegistry(TPYDK);
				rgsty.rebind(rmname,paobj);
			}
			catch(Exception e){
				LogUtil.info("[ObjectService]", "[regObject]", "[Error Exception:]", e);
			}
		}
	}
	
	final static void putBean(String TPYFWYM, boolean TPYRZDY, int TPYDK, String rmname, ParkActive paobj, String cb, String pl, SecurityManager sm){
		setSecurity(cb, pl, sm);
		putBean(TPYFWYM, TPYRZDY, TPYDK, rmname, paobj);
		/*if(TPYFWYM!=null)
			System.setProperty(ConfigContext.getYMMZ(), TPYFWYM);
		if(TPYRZDY)
			System.setProperty(ConfigContext.getRZDY(),"true");
		try{
			LocateRegistry.getRegistry(TPYDK).list();
		}catch(Exception ex){
			try{
				//UnicastRemoteObject.exportObject(paobj, 0);
				Registry rgsty = LocateRegistry.createRegistry(TPYDK);//getRegistry(TPYDK);
				rgsty.rebind(rmname,paobj);
			}
			catch(Exception e){
				LogUtil.info("[BeanService]", "[regObject]", "[Error Exception:]", e);
			}
		}*/
	}
	
	final static ParkActive getBean(String TPYYM,  int TPYDK, String rmname) throws RemoteException
	{
		try{
			if(ConfigContext.getTMOT()>0l)
				System.setProperty(ConfigContext.getQSXYSJ(), ConfigContext.getTMOT()+"");
			return (ParkActive)Naming.lookup(ConfigContext.getProtocolInfo(TPYYM,TPYDK,rmname));
		}catch(Exception e){
			//LogUtil.info("[BeanService]", "[getBean]", "getBean:"+e.getClass());
			if(e instanceof RemoteException)
				throw (RemoteException)e;
			else{
				//e.printStackTrace();
				LogUtil.info("[ObjectService]", "[getBean]", "[Error Exception:]", e);
			}
		}
		return null;
	}
	
	private static void setSecurity(String cb, String pl, SecurityManager sm){
		if(cb!=null&&pl!=null){
			System.setProperty(ConfigContext.getDMY(),cb);
			System.setProperty(ConfigContext.getAQCL(),pl);
		 	if(System.getSecurityManager()==null&&sm!=null){
		    	System.setSecurityManager(sm);//ParkManager()
		   	}
		}
	}
}