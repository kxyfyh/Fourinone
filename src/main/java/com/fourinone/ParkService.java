package com.fourinone;

import java.util.Hashtable;
import java.util.List;
import java.rmi.RemoteException;//ServiceException
import java.io.Serializable;
import java.util.zip.CRC32;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Date;

public class ParkService extends MementoService implements Park
{
	private ParkObjValue parkinfo = new ParkObjValue();
	private static ObjValue hbinfo = new ObjValue();
	//private Lock lk = new ReentrantLock();
	private ReadWriteLock rwlk = new ReentrantReadWriteLock();
	private ParkLeader pl = null;
	
	public ParkService(String host, int port, String[][] servers, String parkService) throws RemoteException{
		pl = new ParkLeader(host,port,servers,parkService);
		pl.wantBeMaster(this);
	}
	
	private String checkSessionId(String sessionid){
		if(sessionid==null)
			return "se"+System.nanoTime();
		else
			return sessionid;
	}

	private Long updateDomainVersion(String domain) throws RemoteException{
		Long domainversion = (Long)parkinfo.getObj(ParkMeta.getYBB(domain));
		
		ObjValue nodeversions = parkinfo.getWidely(ParkMeta.getYBB(domain+"\\..+"));
		List vlist = nodeversions.getObjValues();
		String crcstr = "";
		for(Object obj:vlist)
			crcstr+=obj+"";
			
		Long crcversion = getObjectVersion(ObjectBytes.toBytes(crcstr));
		return (crcversion!=domainversion)?crcversion:domainversion;
	}
	
	private Long updateDomainVersion(){
		Long crcversion = getObjectVersion(ObjectBytes.toBytes(System.nanoTime()));
		return crcversion;
	}

	private Long getObjectVersion(byte[] obj){
		CRC32 crc = new CRC32();
		crc.update(obj);//ObjectBytes.toBytes(o)
		return new Long(crc.getValue());
	}
	
	public String getSessionId() throws RemoteException{
		return checkSessionId(null);
	}
	
	public ObjValue create(String domain, String node, byte[] obj, String sessionid, int auth, boolean heartbeat) throws RemoteException,ClosetoOverException
	{
		ClosetoOverException.checkMemCapacity();
		ObjValue objv = null;
		if(domain!=null&&node!=null){
			//lk.lock();
			Lock wlk = rwlk.writeLock();
			wlk.lock();
			try{
				String domainnodekey = parkinfo.getDomainnodekey(domain, node);
				
				if(!parkinfo.containsKey(domainnodekey)){
					if(!parkinfo.containsKey(domain)){
						parkinfo.setObj(domain, 0l);
						parkinfo.setObj(ParkMeta.getYBB(domain), 0l);
						parkinfo.setString(ParkMeta.getYCJZ(domain), sessionid);
						parkinfo.setString(ParkMeta.getYCIP(domain), getClientHost());
						parkinfo.setObj(ParkMeta.getYCSJ(domain), System.currentTimeMillis());
					}
					parkinfo.setObj(domainnodekey, obj);
					parkinfo.setObj(ParkMeta.getYBB(domainnodekey), getObjectVersion(obj));
					parkinfo.setObj(ParkMeta.getYBB(domain), updateDomainVersion());//updateDomainVersion(domain)
					parkinfo.setObj(ParkMeta.getYCJZ(domainnodekey), sessionid);
					parkinfo.setObj(ParkMeta.getYQX(domainnodekey), auth);
					parkinfo.setObj(ParkMeta.getYCIP(domainnodekey), getClientHost());
					parkinfo.setObj(ParkMeta.getYCSJ(domainnodekey), System.currentTimeMillis());
					Long nodenum = (Long)parkinfo.getObj(domain);
					parkinfo.setObj(domain, nodenum+1);
					
					if(heartbeat)
					{
						parkinfo.setObj(ParkMeta.getYSX(domainnodekey), ParkMeta.getSXXT());
						//HbDaemo.runGetTask(hbinfo, this);
					}
					
					pl.runCopyTask(domainnodekey, this);
					LogUtil.fine("[create]", "["+domainnodekey+"]", obj);
					
					objv = get(domain, node, sessionid);
				}
				else LogUtil.info("[Park]", "[create]", domainnodekey+" is exist!");//throw exist exception
			}catch(Exception e){
				//e.printStackTrace();
				LogUtil.info("[Park]", "[create]", e);
			}finally {
				//lk.unlock();
				wlk.unlock();
			}
		}
		return objv;
	}
	
	//synchronized
	public ObjValue update(String domain, String node, byte[] obj, String sessionid) throws RemoteException,ClosetoOverException
	{
		ClosetoOverException.checkMemCapacity();
		ObjValue objv = null;
		if(domain!=null&&node!=null){
			//lk.lock();
			Lock wlk = rwlk.writeLock();
			wlk.lock();
			try{
				String domainnodekey = parkinfo.getDomainnodekey(domain, node);
				if(checkAuth(domainnodekey, sessionid, AuthPolicy.OP_READ_WRITE)){
					if(parkinfo.containsKey(domainnodekey)){
						parkinfo.setObj(domainnodekey, obj);
						Long theversion = getObjectVersion(obj);
						if(theversion!=(Long)parkinfo.getObj(ParkMeta.getYBB(domainnodekey))){
							parkinfo.setObj(ParkMeta.getYBB(domainnodekey), theversion);
							parkinfo.setObj(ParkMeta.getYBB(domain), updateDomainVersion());//updateDomainVersion(domain)
						}
						parkinfo.setString(ParkMeta.getYGXZ(domainnodekey), sessionid);
						parkinfo.setString(ParkMeta.getYGIP(domainnodekey), getClientHost());
						parkinfo.setObj(ParkMeta.getYGSJ(domainnodekey), System.currentTimeMillis());
					
						LogUtil.fine("[update]", "["+domainnodekey+"]", obj);
						pl.runCopyTask(domainnodekey, this);
						objv = get(domain, node, sessionid);
					}
					else LogUtil.info("[Park]", "[update]", domainnodekey+" is not exist!");//throw not exist exception
				}
			}catch(Exception e){
				//e.printStackTrace();
				LogUtil.info("[Park]", "[update]", e);
			}finally {
				//lk.unlock();
				wlk.unlock();
			}
		}
		return objv;
	}
	
	public boolean update(String domain, int auth, String sessionid) throws RemoteException
	{
		boolean updateflag = false;
		if(domain!=null){
			Lock wlk = rwlk.writeLock();
			wlk.lock();
			try{
				if(parkinfo.containsKey(domain)){
					if(checkAuth(domain, sessionid, AuthPolicy.OP_READ_WRITE)){
						parkinfo.setObj(ParkMeta.getYQX(domain), auth);
						updateflag = true;
						LogUtil.fine("[update]", "["+domain+" Auth]", auth);
						pl.runCopyTask(domain, this);
					}
				}else LogUtil.info("[Park]", "[update]", domain+" is not exist!");//throw not exist exception
			}catch(Exception e){
				LogUtil.info("[Park]", "[update]", e);
			}finally {
				wlk.unlock();
			}
		}
		return updateflag;
	}
	
	public ObjValue delete(String domain, String node, String sessionid) throws RemoteException,ClosetoOverException
	{
		ObjValue objrm = null;
		//if(sessionid==Acl)
		if(domain!=null){
			if(node==null)
				ClosetoOverException.checkMemCapacity();
			
			if(checkAuth(parkinfo.getDomainnodekey(domain, node), sessionid, AuthPolicy.OP_ALL))
				objrm = delete(domain, node);
		}	
		return objrm;
	}
	
	protected ObjValue delete(String domain, String node)
	{
		ObjValue objrm = null;
		Lock wlk = rwlk.writeLock();
		wlk.lock();
		try{
			String domainnodekey = parkinfo.getDomainnodekey(domain, node);
			objrm = parkinfo.removeNode(domain, node);//removeNodeWidely(domainnodekey);
			if(!objrm.isEmpty()){
				Long nodenum = (Long)parkinfo.getObj(domain);
				//System.out.println("delete nodenum:"+nodenum);
				if(nodenum!=null){
					if(nodenum==1l)
						parkinfo.removeDomain(domain);//removeNodeWidely(domain);
					else{
						parkinfo.setObj(domain, nodenum-1);
						parkinfo.setObj(ParkMeta.getYBB(domain), updateDomainVersion());//updateDomainVersion(domain)
					}
				}
				LogUtil.fine("[delete]", "["+domainnodekey+"]", objrm);
				pl.runCopyTask(domainnodekey, this);
			}else{
				objrm = null;
				LogUtil.info("[Park]", "[delete]", domainnodekey+" cant be deleted or not exist!");//throw not exist exception
			}
		}catch(Exception e){
			//e.printStackTrace();
			LogUtil.info("[Park]", "[delete]", e);
		}finally {
			wlk.unlock();
		}
		return objrm;
	}
	
	public boolean checkAuth(String domainnodekey, String sessionid, AuthPolicy targetauth)
	{
		boolean authflag = false;
		String creator = parkinfo.getString(ParkMeta.getYCJZ(domainnodekey));
		if(creator!=null){
			if(creator.equals(sessionid))
				authflag = true;
			else{
				Object domainnodeAuth = parkinfo.getObj(ParkMeta.getYQX(domainnodekey));
				int thekeyauth = domainnodeAuth!=null?(Integer)domainnodeAuth:1;//only read for domain
				authflag = AuthPolicy.authIncluded(targetauth.getPolicy(), thekeyauth);
			}
		}
		if(!authflag)
			LogUtil.info("[Park]", "[AuthPolicy]", "No permissions to do for "+domainnodekey+"!");
		return authflag;
	}
	
	public ObjValue get(String domain, String node, String sessionid) throws RemoteException,ClosetoOverException
	{
		//String thesessionid = checkSessionId(sessionid);
		//lk.lock();
		ObjValue ov = null;
		if(domain!=null){
			if(node==null)
				ClosetoOverException.checkMemCapacity();
			
			Lock rlk = rwlk.readLock();
			rlk.lock();
			LogUtil.fine("[Park]", "[get]", parkinfo.getDomainnodekey(domain, node));
			ov = parkinfo.getNode(domain, node);//parkinfo.getNodeWidely(parkinfo.getDomainnodekey(domain, node));
			if(ov.isEmpty())
				ov = null;
			//lk.unlock();
			rlk.unlock();
		}
		return ov;
	}
	
	public ObjValue getLastest(String domain, String node, String sessionid, long version) throws RemoteException,ClosetoOverException
	{
		//lk.lock();
		Lock rlk = rwlk.readLock();
		rlk.lock();
		Long nodeversion = (Long)parkinfo.getObj(ParkMeta.getYBB(parkinfo.getDomainnodekey(domain, node)));
		if(nodeversion!=null&&nodeversion!=version)
			LogUtil.fine("[Park]", "[getLastest]", "nodeversion:"+nodeversion+";version:"+version);
		ObjValue ov = (nodeversion!=null&&nodeversion!=version)?get(domain,node,sessionid):null;
		//lk.unlock();
		rlk.unlock();
		return ov;
	}
	
	public ObjValue getParkinfo() throws RemoteException
	{
		try{
			LogUtil.fine("[Park]", "[getParkinfo]", "getParkinfo from "+getClientHost());
		}catch(Exception e){
			LogUtil.fine("[Park]", "[getParkinfo]", e.getMessage());
		}
		return getTheParkinfo();
	}
	
	ObjValue getTheParkinfo()
	{
		ObjValue ov = null;
		Lock rlk = rwlk.readLock();
		rlk.lock();
		ov = parkinfo.getParkInfo();
		rlk.unlock();
		return ov;
	}
	
	public boolean setParkinfo(ObjValue ov) throws RemoteException
	{
		LogUtil.fine("[Park]", "[setParkinfo]", ov);
		Lock wlk = rwlk.writeLock();
		wlk.lock();
		parkinfo = (ParkObjValue)ov;
		wlk.unlock();
		return true;
	}
	
	public String[] askMaster() throws RemoteException
	{
		LogUtil.info("[Park]", "[askMaster]", "receive askMaster................");
		return pl.isMaster();
	}
	
	public boolean askLeader() throws RemoteException,LeaderException
	{
		LogUtil.info("[Park]", "[askLeader]", "receive askLeader................");
		String[] sv = new String[2];
		if(pl.checkMasterPark(sv,this))
			return true;
		else throw new LeaderException(pl.getThisserver(),sv);
	}
	
	public boolean heartbeat(String[] domainnodekey, String sessionid) throws RemoteException
	{
		boolean hbback = false;
		if(domainnodekey!=null){
			for(String curkey:domainnodekey)
				hbinfo.setObj(curkey, new Date().getTime());
			hbback = true;
		}
		//System.out.println("hbinfo:"+hbinfo);
		HbDaemo.runGetTask(hbinfo, this);
		return hbback;
	}
	
	/*public List<ObjValue> getNodesInDomain(String domain, String sessionid) throws RemoteException
	{
		return null;
	}*/
	public static void main(String[] args)
	{
		/*BeanContext.setConfigFile("D:\\demo\\comutil\\test\\config.xml");
		String[][] servers = new String[args.length][];
		for(int i=0;i<args.length;i++){
			String[] theserver = new String[]{"localhost",args[i]};
			servers[i] = theserver;
		}
		try{
			ParkService ps = new ParkService(servers[0][0],Integer.parseInt(servers[0][1]), servers, "ParkService");
			Long i=new Long(0);
			System.out.println(i+","+new java.util.Date());
			while(true)
			{
				ObjValue ov = ps.createTest("d", i+"", new byte[1], "aaaaa", 7, false);
				if(i%100==0)
				{
					System.out.println(i+","+new java.util.Date());
				}
				i++;
			}
		}catch(Exception e){
			System.out.println(e);
		}*/
		//BeanContext.startPark(servers[0][0],Integer.parseInt(servers[0][1]), servers);
		//BeanContext.startPark();
	}
}