package com.fourinone;

import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class ParkObjValue extends ObjValue
{	
	public ObjValue getNodeWidely(String nodekey){
		ObjValue obj = new ObjValue();
		Object getobj = this.getObj(nodekey);
		if(getobj!=null)
		{
			obj.put(nodekey, this.getObj(nodekey));
			for(Iterator iter=this.keySet().iterator();iter.hasNext();){
				String curkey = (String)iter.next();
				if(Pattern.matches(nodekey+"\\..*", curkey))
					obj.put(curkey, this.getObj(curkey));
			}
		}
		return obj;
	}
	
	public ObjValue getNode(String domain, String node)
	{
		ObjValue ov = new ObjValue();
		if(domain!=null)
		{
			if(node!=null)
			{
				String domainnodekey = getDomainnodekey(domain, node);
				Object obj = this.getObj(domainnodekey);
				Object version = this.getObj(ParkMeta.getYBB(domainnodekey));
				Object createby = this.getObj(ParkMeta.getYCJZ(domainnodekey));
				Object auth = this.getObj(ParkMeta.getYQX(domainnodekey));
				Object creatip = this.getObj(ParkMeta.getYCIP(domainnodekey));
				Object creattime = this.getObj(ParkMeta.getYCSJ(domainnodekey));
				Object prop = this.getObj(ParkMeta.getYSX(domainnodekey));
				Object updateby = this.getObj(ParkMeta.getYGXZ(domainnodekey));
				Object updateip = this.getObj(ParkMeta.getYGIP(domainnodekey));
				Object updatetime = this.getObj(ParkMeta.getYGSJ(domainnodekey));
							
				if(obj!=null)
					ov.setObj(domainnodekey, obj);
				if(version!=null)
					ov.setObj(ParkMeta.getYBB(domainnodekey), version);
				if(createby!=null)
					ov.setObj(ParkMeta.getYCJZ(domainnodekey), createby);
				if(auth!=null)
					ov.setObj(ParkMeta.getYQX(domainnodekey), auth);
				if(creatip!=null)
					ov.setObj(ParkMeta.getYCIP(domainnodekey), creatip);
				if(creattime!=null)
					ov.setObj(ParkMeta.getYCSJ(domainnodekey), creattime);
				if(prop!=null)
					ov.setObj(ParkMeta.getYSX(domainnodekey), prop);
				if(updateby!=null)
					ov.setObj(ParkMeta.getYGXZ(domainnodekey), updateby);
				if(updateip!=null)
					ov.setObj(ParkMeta.getYGIP(domainnodekey), updateip);
				if(updatetime!=null)
					ov.setObj(ParkMeta.getYGSJ(domainnodekey), updatetime);
			}
			else
				ov = getNodeWidely(domain);
		}
		return ov;
	}
	
	public ObjValue getParkInfo(){
		ParkObjValue obj = (ParkObjValue)this.clone();
		/*ArrayList<String> hbkeylist = new ArrayList<String>();
		
		for(Iterator iter=obj.keySet().iterator();iter.hasNext();){
			String curkey = (String)iter.next();
			if(curkey.indexOf(ParkMeta.getYSX())!=-1&&obj.get(curkey).equals(ParkMeta.getSXXT()))
				hbkeylist.add(curkey.substring(0,curkey.indexOf(ParkMeta.getYSX())));//creattime
		}
		
		for(String hbkey:hbkeylist)
			obj.removeNodeWidely(hbkey);*/
			
		return obj;
	}
	
	public List<String[]> getParkInfoExp(long exp)
	{
		//System.out.println("exp:"+exp);
		ArrayList<String[]> keyexplist = new ArrayList<String[]>();
		
		for(Iterator iter=this.keySet().iterator();iter.hasNext();){
			String curkey = (String)iter.next();
			if(curkey.indexOf(ParkMeta.getYCSJ())!=-1)
			{
				String domainnodekey = curkey.substring(0,curkey.indexOf(ParkMeta.getYCSJ()));
				String[] keyarr = getDomainNode(domainnodekey);
				if(keyarr!=null&&keyarr.length==2)
				{
					String propvalue = this.getString(ParkMeta.getYSX(domainnodekey));
					//System.out.println("propvalue:"+propvalue);
					if(propvalue==null||!propvalue.equals(ParkMeta.getSXXT()))
						if(System.currentTimeMillis()-(Long)this.get(curkey)>=exp)
							keyexplist.add(keyarr);
				}
			}
		}
		//System.out.println("keyexplist:"+keyexplist);
		return keyexplist;
	}
	
	public ObjValue removeNodeWidely(String nodekey){
		ObjValue obj = new ObjValue();
		Object node = this.remove(nodekey);
		
		if(node!=null)
		{
			obj.put(nodekey, node);
			
			List<String> keylist = new ArrayList<String>();
			for(Iterator iter=this.keySet().iterator();iter.hasNext();){
				String curkey = (String)iter.next();
				if(Pattern.matches(nodekey+"\\..*", curkey))
					keylist.add(curkey);
			}
			
			for(String ck:keylist){
				Object rvobj = this.remove(ck);
				if(rvobj!=null)obj.put(ck, rvobj);
			}
		}
		
		return obj;
	}
	
	public ObjValue removeDomain(String domain)
	{
		ObjValue ov = new ObjValue();
		if(domain!=null)
		{
			Object obj = this.remove(domain);
			Object version = this.remove(ParkMeta.getYBB(domain));
			Object createby = this.remove(ParkMeta.getYCJZ(domain));
			Object creatip = this.remove(ParkMeta.getYCIP(domain));
			Object creattime = this.remove(ParkMeta.getYCSJ(domain));
							
			if(obj!=null)
				ov.setObj(domain, obj);
			if(version!=null)
				ov.setObj(ParkMeta.getYBB(domain), version);
			if(createby!=null)
				ov.setObj(ParkMeta.getYCJZ(domain), createby);
			if(creatip!=null)
				ov.setObj(ParkMeta.getYCIP(domain), creatip);
			if(creattime!=null)
				ov.setObj(ParkMeta.getYCSJ(domain), creattime);
		}
		return ov;
	}
	
	public ObjValue removeNode(String domain, String node)
	{
		ObjValue ov = new ObjValue();
		if(domain!=null)
		{
			if(node!=null)
			{
				String domainnodekey = getDomainnodekey(domain, node);
				Object obj = this.remove(domainnodekey);
				Object version = this.remove(ParkMeta.getYBB(domainnodekey));
				Object createby = this.remove(ParkMeta.getYCJZ(domainnodekey));
				Object auth = this.remove(ParkMeta.getYQX(domainnodekey));
				Object creatip = this.remove(ParkMeta.getYCIP(domainnodekey));
				Object creattime = this.remove(ParkMeta.getYCSJ(domainnodekey));
				Object prop = this.remove(ParkMeta.getYSX(domainnodekey));
				Object updateby = this.remove(ParkMeta.getYGXZ(domainnodekey));
				Object updateip = this.remove(ParkMeta.getYGIP(domainnodekey));
				Object updatetime = this.remove(ParkMeta.getYGSJ(domainnodekey));
								
				if(obj!=null)
					ov.setObj(domainnodekey, obj);
				if(version!=null)
					ov.setObj(ParkMeta.getYBB(domainnodekey), version);
				if(createby!=null)
					ov.setObj(ParkMeta.getYCJZ(domainnodekey), createby);
				if(auth!=null)
					ov.setObj(ParkMeta.getYQX(domainnodekey), auth);
				if(creatip!=null)
					ov.setObj(ParkMeta.getYCIP(domainnodekey), creatip);
				if(creattime!=null)
					ov.setObj(ParkMeta.getYCSJ(domainnodekey), creattime);
				if(prop!=null)
					ov.setObj(ParkMeta.getYSX(domainnodekey), prop);
				if(updateby!=null)
					ov.setObj(ParkMeta.getYGXZ(domainnodekey), updateby);
				if(updateip!=null)
					ov.setObj(ParkMeta.getYGIP(domainnodekey), updateip);
				if(updatetime!=null)
					ov.setObj(ParkMeta.getYGSJ(domainnodekey), updatetime);		
			}
			else
				ov = removeNodeWidely(domain);
		}
		return ov;
	}
	
	public static boolean checkGrammar(String keyname){
		if(keyname!=null&&Pattern.matches("^[a-z0-9A-Z_-]+$", keyname))
			return true;
		else{
			LogUtil.info("[KeyName]", "[error domain or node name]", keyname);
			return false;
		}
	}
	
	public static boolean checkGrammar(String domain, String node){
		return checkGrammar(domain)&&checkGrammar(node);
	}
	
	public static boolean checkGrammar(String domain, String node, Object obj){
		if(obj==null)
			LogUtil.info("[checkGrammar]", "[error]", obj);
		return checkGrammar(domain, node)&&obj!=null;
	}
	
	public static String getDomainnodekey(String domain, String node){
		String domainnodekey = node==null?domain:domain+"."+node;
		return domainnodekey;
	}
	
	public static String[] getDomainNode(String domainnodekey){
		//String[] keyarr = domainnodekey.split("\\.");
		//System.out.println("getDomainNode domainnodekey:"+domainnodekey);
		return domainnodekey.split("\\.");
	}
	
	public static void main(String[] args)
	{
		//String[] keyarr = getDomainNode("aaa.bbb");
		//System.out.println("getDomainNode keyarr.length:"+keyarr.length);
		ParkObjValue ob = new ParkObjValue();
		ob.checkGrammar("d","a.b");
		//System.out.println(Pattern.matches("^[a-z0-9A-Z_-]+$", " d "));
		ob.setString("a"+ParkMeta.getYBB(),"0");
		ob.setString("a.b"+ParkMeta.getYBB(),"0");
		ob.setString("a.c"+ParkMeta.getYBB(),"0");
		System.out.println(ob.getWidely(ParkMeta.getYBB("a"+"\\..+")));
	}
}