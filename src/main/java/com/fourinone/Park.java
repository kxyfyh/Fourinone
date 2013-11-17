package com.fourinone;

import java.util.List;
import java.rmi.RemoteException;
import java.io.Serializable;

public interface Park extends ParkActive
{
	//public <T extends java.io.Serializable> ObjValue create(String domain, String node, T obj, String sessionid, int auth, boolean heartbeat) throws RemoteException;//acl
	//public <T extends java.io.Serializable> ObjValue update(String domain, String node, T obj, String sessionid) throws RemoteException;//acl
	public ObjValue create(String domain, String node, byte[] bts, String sessionid, int auth, boolean heartbeat) throws RemoteException,ClosetoOverException;//acl
	public ObjValue update(String domain, String node, byte[] bts, String sessionid) throws RemoteException,ClosetoOverException;//acl
	public boolean update(String domain, int auth, String sessionid) throws RemoteException;
	public ObjValue get(String domain, String node, String sessionid) throws RemoteException,ClosetoOverException;
	public ObjValue getLastest(String domain, String node, String sessionid, long version) throws RemoteException,ClosetoOverException;
	public ObjValue delete(String domain, String node, String sessionid) throws RemoteException,ClosetoOverException;
	public String getSessionId() throws RemoteException;
	public boolean heartbeat(String[] domainnodekey, String sessionid) throws RemoteException;
	public ObjValue getParkinfo() throws RemoteException;
	public boolean setParkinfo(ObjValue ov) throws RemoteException;
	public String[] askMaster() throws RemoteException;
	public boolean askLeader() throws RemoteException,LeaderException;
	//public List<ObjValue> getNodesInDomain(String domain, String sessionid) throws RemoteException;
	//putInfo(parkinfo)
	//heartbeat(String sessionid)
	//put(String domain, String node, T obj, String sessionid, ACL, heartbeat)
}