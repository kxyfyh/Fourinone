package com.fourinone;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

final public class CacheProxy
{
	private Cache cc;
	
	protected CacheProxy(String host, int port)
	{
		cc = BeanContext.getCacheFacade(host, port);
	}
	
	private Object getObjFromBean(ObjectBean ob)
	{
		return ob!=null?ObjectBytes.toObject((byte[])ob.toObject()):null;
	}
	
	private Map getMapFromBeanList(List<ObjectBean> oblist)
	{
		if(oblist!=null)
		{
			ParkObjValue ov = new ParkObjValue();
			for(ObjectBean ob:oblist)
				ov.put(ov.getDomainNode(ob.getName())[1],getObjFromBean(ob));
			return ov;
		}else return null;
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="add",policy=DelegatePolicy.Implements)
	public String add(String name, Serializable obj)
	{
		String keystr = null;
		try{
			keystr = cc.add(name, ObjectBytes.toBytes(obj));
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[add]", e);
		}
		return keystr;
	}	
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="put",policy=DelegatePolicy.Implements)
	public Object put(String keyid, String name, Serializable obj)
	{
		ObjectBean objt = null;
		try{
			objt = cc.put(keyid, name, ObjectBytes.toBytes(obj));
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[put]", e);
		}
		return getObjFromBean(objt);
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="get",policy=DelegatePolicy.Implements)
	public Object get(String keyid, String name)
	{
		ObjectBean obj = null;
		try{
			obj = cc.get(keyid, name);
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[get]", e);
		}
		return getObjFromBean(obj);
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="get",policy=DelegatePolicy.Implements)
	public Map get(String keyid)
	{
		List<ObjectBean> objlist = null;
		try{
			objlist = cc.get(keyid);
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[get]", e);
		}
		return getMapFromBeanList(objlist);
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="remove",policy=DelegatePolicy.Implements)
	public Object remove(String keyid, String name)
	{
		ObjectBean obj = null;
		try{
			obj = cc.remove(keyid, name);
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[remove]", e);
		}
		return getObjFromBean(obj);
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="remove",policy=DelegatePolicy.Implements)
	public Map remove(String keyid)
	{
		List<ObjectBean> objlist = null;
		try{
			objlist = cc.remove(keyid);
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[remove]", e);
		}
		return getMapFromBeanList(objlist);
	}
	
	/*
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="add",policy=DelegatePolicy.Implements)
	public String add(String type, Serializable obj)
	{
		String addstr = null;
		try{
			addstr = cc.add(type, ObjectBytes.toBytes(obj));
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[add]", e);
		}
		return addstr;
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="get",policy=DelegatePolicy.Implements)
	public Object get(String type, String keyid)
	{
		Object obj = null;
		try{
			obj = ObjectBytes.toObject((byte[])cc.get(type, keyid));
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[get]", e);
		}
		return obj;
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="putByKey",policy=DelegatePolicy.Implements)
	public Object putByKey(String keyid, String name, Serializable obj)
	{
		Object objt = null;
		try{
			objt = cc.putByKey(keyid, name, ObjectBytes.toBytes(obj));
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[putByKey]", e);
		}
		return objt;
	}
	
	@Delegate(interfaceName="com.fourinone.CacheLocal",methodName="getByKey",policy=DelegatePolicy.Implements)
	public Object getByKey(String keyid, String name)
	{
		Object obj = null;
		try{
			obj = ObjectBytes.toObject((byte[])cc.getByKey(keyid, name));
		}catch(Exception e){
			LogUtil.info("[CacheProxy]", "[getByKey]", e);
		}
		return obj;
	}*/
}