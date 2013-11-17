package com.fourinone;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.rmi.RemoteException;
//import java.rmi.ConnectException;

final public class ParkProxy{
	private static String sid = null;
	private Park pk;
	private ParkLeader pl = null;
	
	public ParkProxy(String host, int port, String sn)
	{
		pl = new ParkLeader(host,port,sn);
		pk = pl.getLeaderPark();
		init();
	}
	
	public ParkProxy(String host, int port, String[][] servers, String sn)//all server host and port:string[][]
	{
		pl = new ParkLeader(host,port,servers,sn);
		pk = pl.getLeaderPark();//(Park)BeanService.getBean(host,port,"ParkService");//try change pk if catch exception
		//new ParkLeader(host,port,String[][])
		//pl.getMasterPark(){catch remoteexception and try next until get one};
		init();
	}
	
//	private class ObjectBeanProxy implements ObjectBean{
//		private Object obj;
//		private Long vid;
//		private String name;
//		private ObjectBeanProxy(){}
		/*private ObjectBeanProxy(ObjValue ov, String domainnodekey){
			vid = (Long)ov.getObj(domainnodekey+"._me_ta.version");
			obj = ov.get(domainnodekey);
			name = domainnodekey;
		}*/
		//@Delegate(interfaceName="com.fourinone.ObjectBean",methodName="toObject",policy=DelegatePolicy.Implements)
//		public Object toObject(){
//			return obj;
//		}
		//@Delegate(interfaceName="com.fourinone.ObjectBean",methodName="getName",policy=DelegatePolicy.Implements)
//		public String getName(){
//			return name;
//		}
		
//		public String toString(){
//			return name+":"+obj.toString();
//		}
		/*@Delegate(interfaceName="com.fourinone.ObjectVersion",methodName="getVid",policy=DelegatePolicy.Implements)
		public Long getVid(){
			return vid;
		}*/
//	}
	
//	private class ObjectBeanList<E> extends ArrayList implements List{
//		private Long vid;
//	}
	
	private void init(){
		try{
			if(sid==null)
				sid = pk.getSessionId();
		}catch(Exception e){
			//e.printStackTrace();
			LogUtil.info("[Park]", "[init]", e.toString());
		}
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="create",policy=DelegatePolicy.Implements)
	public ObjectBean create(String domain, Serializable obj){
		return put(domain, System.nanoTime()+"", obj);
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="create",policy=DelegatePolicy.Implements)
	public ObjectBean put(String domain, String node, Serializable obj){
		return put(domain, node, obj, AuthPolicy.OP_ALL);
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="create",policy=DelegatePolicy.Implements)
	public ObjectBean put(String domain, String node, Serializable obj, AuthPolicy auth){
		return put(domain, node, obj, auth, false);
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="create",policy=DelegatePolicy.Implements)
	public ObjectBean create(String domain, String node, Serializable obj, boolean heartbeat){
		return put(domain, node, obj, AuthPolicy.OP_ALL, heartbeat);
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="create",policy=DelegatePolicy.Implements)
	public ObjectBean put(String domain, String node, Serializable obj, AuthPolicy auth, boolean heartbeat){
		return put(domain, node, obj, auth, heartbeat, 0);
	}
	
	public ObjectBean put(String domain, String node, Serializable obj, AuthPolicy auth, boolean heartbeat, int i)
	{
		ObjectBean ob=null;
		if(ParkObjValue.checkGrammar(domain, node, obj)){
			try{
				ObjValue ov = pk.create(domain, node, ObjectBytes.toBytes(obj), sid, auth.getPolicy(), heartbeat);
				ob = OvToBean(ov,domain,node);
				if(ob!=null&&heartbeat)
					HbDaemo.runPutTask(pk, pl, domain, node, sid);
				//System.out.println("created...");
			}catch(Exception e){
				//e.printStackTrace();
				LogUtil.info("[Park]", "[put]", e.getMessage());
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							ob = put(domain, node, obj, auth, heartbeat, i+1);
					//}
				}
				//if(e=LeaderException or java.rmi.ConnectException)
				//{pk=getNextMaster;ob = put(...);
				
				if(e instanceof ClosetoOverException){
					LogUtil.info("[Park]", "[put]", ((ClosetoOverException)e).print());
				}
			}
		}
		return ob;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="update",policy=DelegatePolicy.Implements)
	public ObjectBean update(String domain, String node, Serializable obj){
		return update(domain, node, obj, 0);
	}
	
	public ObjectBean update(String domain, String node, Serializable obj, int i)
	{
		ObjectBean ob=null;
		if(ParkObjValue.checkGrammar(domain, node, obj)){
			try{
				ObjValue ov = pk.update(domain, node, ObjectBytes.toBytes(obj), sid);
				ob = OvToBean(ov, domain,node);
			}catch(Exception e){
				LogUtil.info("[Park]", "[update]", e.getMessage());
				//e.printStackTrace();
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							ob = update(domain, node, obj,i+1);
					//}
				}
				
				if(e instanceof ClosetoOverException){
					LogUtil.info("[Park]", "[update]", ((ClosetoOverException)e).print());
				}
			}
		}
		return ob;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="get",policy=DelegatePolicy.Implements)
	public ObjectBean get(String domain, String node){
		return get(domain, node, 0);
	}
	
	public ObjectBean get(String domain, String node, int i)
	{
		ObjectBean ob=null;
		if(ParkObjValue.checkGrammar(domain, node)){
			try{
				ObjValue ov = pk.get(domain, node, sid);//getTestObj();
				//ob = new ObjectBeanProxy(ov, domain,node);
				ob = OvToBean(ov, domain,node);
			}catch(Exception e){
				LogUtil.info("[Park]", "[get]", e.getMessage());
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							ob = get(domain, node, i+1);
					//}
				}
			}
		}
		return ob;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="getLastest",policy=DelegatePolicy.Implements)
	public ObjectBean getLastest(String domain, String node, ObjectBean obold){
		return 	getLastest(domain, node, obold, 0);
	}
		
	public ObjectBean getLastest(String domain, String node, ObjectBean obold, int i){
		ObjectBean ob=null;
		if(ParkObjValue.checkGrammar(domain, node)){
			try{
				long vid = obold!=null?((ObjectBeanProxy)obold).vid:0l;//ObjectVersion
				//System.out.println("ob.vid:"+vid);
				ObjValue ov = pk.getLastest(domain, node, sid, vid);
				//System.out.println(ov);
				ob = OvToBean(ov, domain,node);
				//System.out.println(ob);
			}catch(Exception e){
				LogUtil.info("[Park]", "[getLastest]", e.getMessage());
				
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							ob = getLastest(domain, node, obold, i+1);
					//}
				}
			}
		}
		return ob;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="get",policy=DelegatePolicy.Implements)
	public List<ObjectBean> getNodes(String domain){
		return getNodes(domain, 0);
	}
	
	public List<ObjectBean> getNodes(String domain, int i)
	{
		List<ObjectBean> objlist = null;
		if(ParkObjValue.checkGrammar(domain)){
			try{
				ObjValue ov = pk.get(domain, null, sid);//getTestObj();
				objlist = OvToBeanList(ov, domain);
			}catch(Exception e){
				LogUtil.info("[Park]", "[getNodes]", e.getMessage());
				
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							objlist = getNodes(domain, i+1);
					//}
				}
				
				if(e instanceof ClosetoOverException){
					LogUtil.info("[Park]", "[getNodes]", ((ClosetoOverException)e).print());
				}
			}
		}
		return objlist;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="getLastest",policy=DelegatePolicy.Implements)
	public List<ObjectBean> getNodesLastest(String domain, List<ObjectBean> oblist){
		return getNodesLastest(domain, oblist, 0);
	}
	
	public List<ObjectBean> getNodesLastest(String domain, List<ObjectBean> oblist, int i){
		List<ObjectBean> objlist = null;
		if(ParkObjValue.checkGrammar(domain)){
			try{
				long vid = oblist!=null?((ObjectBeanList)oblist).vid:0l;
				ObjValue ov = pk.getLastest(domain, null, sid, vid);
				//System.out.println("getNodesLastest:"+ov);
				objlist = OvToBeanList(ov, domain);
			}catch(Exception e){
				LogUtil.info("[Park]", "[getNodesLastest]", e.getMessage());
				
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							objlist = getNodesLastest(domain, oblist, i+1);
					//}
				}
				
				if(e instanceof ClosetoOverException){
					LogUtil.info("[Park]", "[getNodesLastest]", ((ClosetoOverException)e).print());
				}
			}
		}
		return objlist;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="delete",policy=DelegatePolicy.Implements)
	public ObjectBean remove(String domain, String node)
	{
		return remove(domain, node, 0);
	}
	
	public ObjectBean remove(String domain, String node, int i)
	{
		ObjectBean ob=null;
		//System.out.println("remove(String domain, String node):"+domain);
		if(ParkObjValue.checkGrammar(domain,node)){
			try{
				ObjValue ov = pk.delete(domain, node, sid);
				ob = OvToBean(ov, domain,node);
			}catch(Exception e){
				//e.printStackTrace();
				LogUtil.info("[Park]", "[delete]", e.getMessage());
				
				if(e instanceof RemoteException){
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							ob = remove(domain, node, i+1);
					//}
				}
			}
		}
		return ob;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="delete",policy=DelegatePolicy.Implements)
	public List<ObjectBean> remove(String domain){
		return remove(domain,0);
	}
	
	public List<ObjectBean> remove(String domain, int i)
	{
		List<ObjectBean> objlist = null;
		if(ParkObjValue.checkGrammar(domain)){
			try{
				ObjValue ov = pk.delete(domain, null, sid);
				//System.out.println(ov);
				objlist = OvToBeanList(ov, domain);
			}catch(Exception e){
				//e.printStackTrace();
				LogUtil.info("[Park]", "[delete]", e.getMessage());
				
				if(e instanceof RemoteException){//ConnectException
					//if(i<pl.groupserver.length)
					//{
						pk = pl.getNextLeader();
						if(pk!=null)
							objlist = remove(domain,i+1);
					//}
				}
				
				if(e instanceof ClosetoOverException){
					LogUtil.info("[Park]", "[delete]", ((ClosetoOverException)e).print());
				}
			}
		}
		return objlist;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="setDeletable",policy=DelegatePolicy.Implements)
	public boolean updateDomainAuth(String domain){
		return updateDomainAuth(domain,0);
	}
	
	public boolean updateDomainAuth(String domain, int i){
		boolean setflag = false;
		if(ParkObjValue.checkGrammar(domain)){
			try{
				setflag = pk.update(domain, AuthPolicy.OP_ALL.getPolicy(), sid);
			}catch(Exception e){
				LogUtil.info("[Park]", "[setDeletable]", e.getMessage());
				if(e instanceof RemoteException){//ConnectException
					pk = pl.getNextLeader();
						if(pk!=null)
							setflag = updateDomainAuth(domain,i+1);
				}
			}
		}
		return setflag;
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="addLastestListener",policy=DelegatePolicy.Implements)
	public void addLastestListener(String domain, String node, ObjectBean ob, LastestListener liser)
	{
		final String dm = domain;
		final String nd = node;
		final ObjectBean oob = ob;
		final LastestListener lis = liser;
		new AsyncExector(){
			public void task(){
				try{
					/*ObjectBean newob = null;
					while((newob=getLastest(dm, nd, oldob))==null);
					LogUtil.fine("[Park]","[Trim LastestEvent]","[obj]");
					LastestEvent le = new LastestEvent(newob);
					lis.happenLastest(le);*/
					ObjectBean oldob = oob;
					while(true){
						ObjectBean newob = getLastest(dm, nd, oldob);
						if(newob!=null){
							LogUtil.fine("[Park]","[Trim LastestEvent]","[obj]");
							LastestEvent le = new LastestEvent(newob);
							if(lis.happenLastest(le))
								break;
							oldob = (ObjectBean)le.getSource();
						}
					}
				}catch(Exception e){
					LogUtil.info("[Park]","[addLastestListener]",e);
				}
			}
		}.run();//ScheduledExecutorService or Thread.sleep(1) but time delay, so add time param
	}
	
	@Delegate(interfaceName="com.fourinone.ParkLocal",methodName="addLastestListener",policy=DelegatePolicy.Implements)
	public void addLastestListener(String domain, List<ObjectBean> oblist, LastestListener liser)
	{
		final String dm = domain;
		final List<ObjectBean> ols = oblist;
		final LastestListener lis = liser;
		new AsyncExector(){
			public void task(){
				try{
					/*List<ObjectBean> newls = null;
					while((newls=getNodesLastest(dm, oldls))==null);
					LogUtil.fine("[Park]","[Trim LastestEvent]","[list]");
					LastestEvent le = new LastestEvent(newls);
					lis.happenLastest(le);*/
					List<ObjectBean> oldls = ols;
					while(true){
						List<ObjectBean> newls = getNodesLastest(dm, oldls);
						if(newls!=null){
							LogUtil.fine("[Park]","[Trim LastestEvent]","[list]");
							LastestEvent le = new LastestEvent(newls);
							if(lis.happenLastest(le))
								break;
							oldls = (List<ObjectBean>)le.getSource();
						}
					}
				}catch(Exception e){
					LogUtil.info("[Park]","[addLastestListener]",e);
				}
			}
		}.run();
	}
	
	public ObjectBean OvToBean(ObjValue ov, String domain, String node){
		if(ov!=null&&!ov.isEmpty())
		{
			//System.out.println("OvToBean:"+ov);
			ObjectBeanProxy obp = new ObjectBeanProxy();
			//ObjectBean ob = (ObjectBean)DelegateConsole.bind(new Class[]{ObjectBean.class,ObjectVersion.class}, new ObjectBeanProxy(ov, domainnodekey));
			//ObjectBeanProxy obp = (ObjectBeanProxy)ob;
			String domainnodekey = ParkObjValue.getDomainnodekey(domain, node);
			obp.vid = (Long)ov.getObj(ParkMeta.getYBB(domainnodekey));
			obp.obj = ObjectBytes.toObject((byte[])ov.get(domainnodekey));
			obp.name = domainnodekey;
			return obp;
		}else return null;
	}
	
	public List<ObjectBean> OvToBeanList(ObjValue ov, String domain){
		if(ov!=null&&!ov.isEmpty())
		{
			ObjectBeanList<ObjectBean> objlist = new ObjectBeanList<ObjectBean>();
			objlist.vid = (Long)ov.getObj(ParkMeta.getYBB(domain));
			ObjValue nodeversion = ov.getWidely(ParkMeta.getYBB(domain+"..*"));
			ArrayList<String> nvnames = nodeversion.getObjNames();
			for(String nvname:nvnames){
				ObjectBeanProxy obp = new ObjectBeanProxy();
				obp.vid = (Long)nodeversion.getObj(nvname);
				obp.name = nvname.substring(0,nvname.indexOf(ParkMeta.getYSJ()));
				obp.obj = ObjectBytes.toObject((byte[])ov.getObj(obp.name));
				//ObjectBean ob = (ObjectBean)DelegateConsole.bind(new Class[]{ObjectBean.class,ObjectVersion.class}, obp);
				objlist.add(obp);
			}
			return objlist;
		}else return null;
	}
	/*
	private ObjValue getTestObj(){
		ObjValue ov = new ObjValue();
		ov.set("d","2");
		ov.setObj("d._me_ta.version",11l);
		ov.set("d.n","aaa");
		ov.setObj("d.n._me_ta.version",111l);
		ov.set("d.m","bbb");
		ov.setObj("d.m._me_ta.version",222l);
		return ov;
	}
	*/
	public static void main(String[] args){
		try{
			//Park pk = (Park)BeanService.getBean("localhost",1888,"ParkService");
			//System.out.println(pk.put(args[0], args[1], args[2], sid));
			//ParkProxy pp = new ParkProxy();
			
			//ParkLocal pp = DelegateHandle.bind(ParkLocal.class, ParkProxy.class);
			ParkLocal pp = BeanContext.getPark();
			pp.create(args[0],args[1],args[2],AuthPolicy.OP_ALL,true);
			//pp.create("d","m","b",true);
			//pp.create("d","x","c",true);
			
			/*pp.create("d","n","a");
			pp.create("d","m","b");
			pp.create("d","x","c");
			pp.create("d","y","d");
			//System.out.println("ob_put:"+ob_put.toObject());
			
			ObjectBean ob_get = pp.get("d","g");
			System.out.println("ob_get:"+ob_get);
			if(ob_get!=null){
				System.out.println("ob_get.toObject:"+ob_get.toObject());	
				System.out.println("obp.vid:"+((ObjectBeanProxy)ob_get).vid);
			}
			
			List<ObjectBean> oblist = pp.get("d");
			for(ObjectBean obean:oblist)
				System.out.println("obean:"+obean.getName());
			
			System.out.println(pp.getLastest("d","n",ob_get));
			System.out.println(pp.getLastest("d",oblist));
			
			System.out.println(pp.delete("d","n"));
			System.out.println(pp.delete("d"));
			*/
			/*
			ObjectBean ob = pp.get("d","n");
			System.out.println("ob:"+ob.toObject());
			System.out.println("ob:"+ob.getName());
			ObjectBeanProxy obp = (ObjectBeanProxy)ob;
			System.out.println("obp:"+obp.vid);
			//System.out.println(pp.getLastest("d","n",null));
			
			List<ObjectBean> lob = pp.getNodes(null);//"d"
			System.out.println("lob:"+lob);
			//for(ObjectBean obean:lob)
				//System.out.println("obean:"+obean.getName());
			*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}