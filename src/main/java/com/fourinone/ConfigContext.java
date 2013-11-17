package com.fourinone;

import java.util.ArrayList;
import java.util.Properties;
import java.text.DateFormat;
import java.util.Date;
import java.io.File;

public class ConfigContext
{
	private static MulBean mb = null;
	private static String QSXYSJ=null,YMMZ=null,RZDY=null,YCDYXY=null,DMY=null,AQCL=null,POLICY=null,LSML=null,SERVICEONWORKER=null;
	private static long TMOT=-1;
	static String configFile = "config.xml";
	private static ObjValue USERS = null;
	
	static MulBean getMulBean(){
		return mb!=null?mb:new MulBean("ISO-8859-1");
	}
	
	static String getQSXYSJ(){
		if(QSXYSJ==null)
			QSXYSJ = getMulBean().getString("QSXYSJ");
		return QSXYSJ;
	}

	static String getYMMZ(){
		if(YMMZ==null)
			YMMZ = getMulBean().getString("YMMZ");
		return YMMZ;
	}
	
	static String getRZDY(){
		if(RZDY==null)
			RZDY = getMulBean().getString("RZDY");
		return RZDY;
	}
	
	static String getYCDYXY(){
		if(YCDYXY==null)
			YCDYXY = getMulBean().getString("YCDYXY");
		return YCDYXY;
	}
	
	static String getDMY(){
		if(DMY==null)
			DMY = getMulBean().getString("DMY");
		return DMY;
	}
	
	static String getAQCL(){
		if(AQCL==null)
			AQCL = getMulBean().getString("AQCL");
		return AQCL;
	}
	
	static String getPOLICY(){
		if(POLICY==null)
			POLICY = getMulBean().getString("POLICY");
		return POLICY;
	}
	
	static String getLSML(){
		if(LSML==null)
			LSML = getMulBean().getString("LSML");
		return LSML;
	}
	
	static String getProp(String propstr){
		return getMulBean().getString(propstr);
	}
	
	static String getProtocolInfo(String ym, int dk, String mc){
		return getYCDYXY()+ym+":"+dk+"/"+mc;
	}
	
	static long getTMOT(){
		if(TMOT==-1)
			TMOT = getSecTime(new Double(getConfig("WORKER","TIMEOUT","TRUE","0")));
		return TMOT;
	}
	
	static boolean getServiceFlag(){
		if(SERVICEONWORKER==null)
			SERVICEONWORKER = getConfig("WORKER","SERVICE", null, "false");
		return Boolean.parseBoolean(SERVICEONWORKER);
	}	
		
	static long getSecTime(Double hours)
	{
		Double t = hours*3600*1000;
		return t.longValue();
	}
	
	static String[][] getParkConfig()
	{
		String servers = getConfig("PARK","SERVERS",null);
		return getServerFromStr(servers);
	}
	
	static String getParkService()
	{
		return getConfig("PARK","SERVICE",null);
	}
	
	static String[] getCtorService()
	{
		return getConfig("CTOR","CTORSERVERS",null).split(":");
	}
	
	static String[] getFttpConfig()
	{
		return getConfig("FTTP","SERVERS",null).split(":");
	}
	
	static String[] getInetConfig()
	{
		return getConfig("WEBAPP","SERVERS",null).split(":");
	}
	
	static ObjValue getUsersConfig()
	{
		if(USERS==null){
			String userstr = getConfig("WEBAPP","USERS",null);
			USERS = getObjFromStr(userstr);
		}
		return USERS;
	}
	
	static String getInetStrConfig(String wkjn)
	{
		String inetstr = "http://"+getConfig("WEBAPP","SERVERS",null)+"/res/";
		return wkjn!=null?inetstr+wkjn:inetstr;
	}
	
	static String getPolicyConfig(){
		String tdir = System.getProperty(getLSML());
		File fl = new File(tdir, "a.pl");
		if(!fl.exists()){
			FileAdapter fa = new FileAdapter(fl.getPath());
			fa.getWriter().write(getPOLICY().getBytes());
			fa.close();
		}
		return fl.getPath();
	}
	
	static String[] getWorkerConfig()
	{
		return getConfig("WORKER","SERVERS",null).split(":");
	}
	
	static String[][] getCacheConfig()
	{
		String servers = getConfig("CACHE","SERVERS",null);
		return getServerFromStr(servers);
	}
	
	static String getCacheService()
	{
		return getConfig("CACHE","SERVICE",null);
	}
	
	static String[] getCacheFacadeConfig()
	{
		return getConfig("CACHEFACADE","SERVERS",null).split(":");
	}
	
	public static String getCacheFacadeService()
	{
		return getConfig("CACHEFACADE","SERVICE",null);
	}
	
	static int getInitServices()
	{
		int initnum = 10;
		try{
			initnum = Integer.parseInt(getConfig("CTOR","INITSERVICES",null,"10"));
		}catch(Exception e){}
		return initnum;
	}
	
	static int getMaxServices()
	{
		int maxnum = 100;
		try{
			maxnum = Integer.parseInt(getConfig("CTOR","MAXSERVICES",null,"100"));
		}catch(Exception e){}
		return maxnum;
	}
	
	static int getParallelPattern()
	{
		return Integer.parseInt(getConfig("COMPUTEMODE","MODE","DEFAULT"));
	}
	
	static String getConfig(String cfgname, String cfgprop, String cfgdesc)
	{
		return getConfig(cfgname, cfgprop, cfgdesc, null);
	}
	
	static String getConfig(String cfgname, String cfgprop, String cfgdesc, String defvalue)
	{
		XmlUtil xu = new XmlUtil();
		ArrayList al = xu.getXmlObjectByFile(configFile,cfgname,cfgdesc);
		String v = null;
		if(al!=null&&al.size()>0)
		{
			ObjValue cfgProps = (ObjValue)al.get(0);
			v = cfgProps.getString(cfgprop);
		}
		if(v==null)
			v=defvalue;
		//LogUtil.fine("[ConfigContext]", "[getConfig]", v);
		return v;
	}
	
	static String getLogLevel(String deflevel)
	{
		XmlUtil xu = new XmlUtil();
		ArrayList al = xu.getXmlPropsByFile(configFile,"LOG","LOGLEVEL");
		Properties dbProps = (Properties)al.get(0);
		String levelName = dbProps.getProperty("LEVELNAME");
		
		return levelName!=null?levelName:deflevel;
	}
	
	static ObjValue getCacheGroupConfig()
	{
		XmlUtil xu = new XmlUtil();
		ArrayList al = xu.getXmlObjectByFile(configFile,"CACHEGROUP");
		ObjValue groups = new ObjValue();
		
		for(int i=0;i<al.size();i++)
		{
			ObjValue cacheProps = (ObjValue)al.get(i);
			ObjValue gp = new ObjValue();
			String gpcfgstr = cacheProps.getString("GROUP");
			for(String perstr:gpcfgstr.split(";"))
			{
				String[] perstrarr = perstr.split("@");
				gp.setObj(perstrarr[0],new Long(getDateLong(perstrarr[1])));
			}
			groups.put(gp, new Long(getDateLong(cacheProps.getString("STARTTIME"))));
		}
		
		LogUtil.fine("[ConfigContext]", "[getCacheConfig]", groups);
		return groups;
	}
	
	static String getDateLong(String dateStr)
	{
		if(dateStr!=null&&!dateStr.equals(""))
		{
			try
			{
				DateFormat dateFormat = DateFormat.getDateInstance();
				Date d = dateFormat.parse(dateStr);
				dateStr = d.getTime()+"";
				if(dateStr.length()==12)
					dateStr = "0"+dateStr;
			}catch(Exception e){
				System.out.println(e);
			}
		}
		return  dateStr;
	}
	
	static String[][] getServerFromStr(String servers)
	{
		String[] serverarr = servers.split(",");
		String[][] sarr = new String[serverarr.length][];
		for(int n=0;n<serverarr.length;n++)
		{
			String[] hostport=serverarr[n].split(":");
			sarr[n]=hostport;
		}
		
		return sarr;
	}
	
	private static ObjValue getObjFromStr(String strs)
	{
		String[] strarr = strs.split(",");
		ObjValue ov = new ObjValue();
		for(String thestr:strarr)
		{
			String[] str=thestr.split(":");
			ov.setString(str[0],str[1]);
		}
		
		return ov;
	}
	
	static String getRequest(String requestUrl){
		return getMulBean().getFileString(getMulBean().getString(requestUrl));
	}
	
	public static void main(String args[])
	{
		BeanContext.setConfigFile("D:\\demo\\comutil\\test\\config.xml");
		System.out.println(getParkConfig()[0][0]);
		LogUtil.fine(getCacheConfig());
		LogUtil.fine("getParallelPattern:"+getParallelPattern());
		System.out.println(getConfig("CACHEFACADE","TRYKEYSNUM",null,"500"));
	}
}