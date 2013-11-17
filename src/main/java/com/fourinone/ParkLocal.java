package com.fourinone;

import java.util.List;
import java.io.Serializable;

public interface ParkLocal
{
	public ObjectBean create(String domain, Serializable obj);
	public ObjectBean create(String domain, String node, Serializable obj);
	public ObjectBean create(String domain, String node, Serializable obj, AuthPolicy auth);
	public ObjectBean create(String domain, String node, Serializable obj, boolean heartbeat);
	public ObjectBean create(String domain, String node, Serializable obj, AuthPolicy auth, boolean heartbeat);
	public ObjectBean update(String domain, String node, Serializable obj);
	public ObjectBean get(String domain, String node);
	public ObjectBean getLastest(String domain, String node, ObjectBean ob);
	public List<ObjectBean> get(String domain);
	public List<ObjectBean> getLastest(String domain, List<ObjectBean> oblist);
	public ObjectBean delete(String domain, String node);
	public boolean setDeletable(String domain);
	public List<ObjectBean> delete(String domain);
	public void addLastestListener(String domain, String node, ObjectBean ob, LastestListener liser);
	public void addLastestListener(String domain, List<ObjectBean> oblist, LastestListener liser);
}