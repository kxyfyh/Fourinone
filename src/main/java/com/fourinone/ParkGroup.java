package com.fourinone;

import java.util.ArrayList;

public class ParkGroup{
	private ObjValue groups = new ObjValue();
	//key--->server(bk)
	public ParkGroup(ObjValue groups){
		//initGroups();//input Groups
		this.groups = groups;
	}
	
	private void initGroups()
	{
		ObjValue group1 = new ObjValue();
		Long t1 = new Long(ConfigContext.getDateLong("2010-01-01"));
		group1.setObj("localhost:1888,localhost:1889",t1);
		group1.setObj("localhost:2000,localhost:2001",t1);
		Long t2 = new Long(ConfigContext.getDateLong("2010-05-01"));//2012
		group1.setObj("localhost:2002,localhost:2003",t2);
		Long t3 = new Long(ConfigContext.getDateLong("2012-05-01"));//2012
		group1.setObj("localhost:2004,localhost:2005",t3);
		group1.setObj("localhost:2006,localhost:2007",t3);
		/*group1.setObj("localhost:2008,localhost:2009",t2);
		group1.setObj("localhost:2010,localhost:2011",t2);
		group1.setObj("localhost:2012,localhost:2013",t2);
		group1.setObj("localhost:1890,localhost:1891",t2);
		group1.setObj("localhost:1892,localhost:1893",t2);*/
		groups.put(group1, t1);
		/*
		ObjValue group2 = new ObjValue();
		Long t3 = new Long(getDateLong("2010-08-01"));
		group2.setObj("localhost:2000,localhost:2001",t3);
		group2.setObj("localhost:2002,localhost:2003",t3);
		group2.setObj("localhost:2004,localhost:2005",t3);
		groups.put(group2, t3);
		*/
	}
	/*
	private int getTimeIndex(ObjValue ov, Long kt)
	{
		ArrayList ls = ov.getObjValues();
		for(int i=0;i<ls.size();i++)
			if(kt<(Long)ls.get(i))
				break;
		return i;
	}
	*/
	public static String getKeyId(){
		return Long.toHexString(System.currentTimeMillis()).toUpperCase()+"-"+Long.toHexString(System.nanoTime()).toUpperCase();
	}
	
	/*public String parseKeyId(String keyid)
	{
		String[] keyidarr = keyid.split("-");
		return Long.decode("0x"+keyidarr[0])+"-"+Long.decode("0x"+keyidarr[1]);
	}*/
	
	public String[][] getServers(String keyId){
		String[] keyidarr = keyId.split("-");
		Long kt = Long.decode("0x"+keyidarr[0]);
		LogUtil.fine("[getServers]", "[kt]", kt);
		ArrayList gpskeys = groups.getObjNames();
		ObjValue keygroup = null;
		for(int i=0;i<gpskeys.size();i++){
			if(kt>(Long)groups.get(gpskeys.get(i)))
				keygroup = (ObjValue)gpskeys.get(i);
		}
		
		LogUtil.fine("[getServers]", "[keygroup]", keygroup);
		ArrayList servers = keygroup.getObjNames();
		int j=0;
		for(;j<servers.size();j++){
			if(kt<(Long)keygroup.get(servers.get(j)))
				break;
		}
		
		LogUtil.fine("[getServers]", "[j]", "j:"+j+",kt%j:"+kt%j);
		String serverStr = (String)servers.get((int)(kt%j));
		
		return ConfigContext.getServerFromStr(serverStr);
	}
	
	public static void main(String[] args){
		/*ParkGroup pg = new ParkGroup();
		String kid = ParkGroup.getKeyId();
		System.out.println(kid);
		System.out.println(pg.getServers(kid)[0][1]);*/
		//
		//System.out.println(groups.get(group2));
		//ObjValue<>
	}
}