package com.fourinone;

import java.io.Serializable;
import java.util.Map;

public interface CacheLocal
{
	/*public String add(String type, Serializable obj);
	public Object get(String type, String keyid);
	public Object putByKey(String keyId, String name, Serializable obj);
	public Object getByKey(String keyid, String name);
	*/
	public String add(String name, Serializable obj);
	public Object put(String keyid, String name, Serializable obj);
	public Object get(String keyid, String name);
	public Map get(String keyid);
	public Object remove(String keyid, String name);
	public Map remove(String keyid);
}