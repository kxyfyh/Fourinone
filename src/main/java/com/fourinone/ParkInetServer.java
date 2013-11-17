package com.fourinone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Compilable;
import com.fourinone.FttpAdapter.FileProperty;

public class ParkInetServer//not public
{
	static void start(String i, int p, int q){
		try{
		    HttpServer hs = HttpServer.create(new InetSocketAddress(i,p),q);
		    hs.createContext(ConfigContext.getProp("REQROOT"), new HandlerAll());
		    //hs.createContext(ConfigContext.getProp("REQECHO"), new HandlerEcho());
		    hs.createContext(ConfigContext.getProp("REQRES"), new HandlerRes());
		    hs.createContext(ConfigContext.getProp("REQADMIN"), new HandlerAuth());
		    hs.setExecutor(Executors.newCachedThreadPool());//PoolExector
		    hs.start();
		    //hs.stop();
		    LogUtil.info("", "", "startWebapp("+i+":"+p+")");
		}catch(IOException e) {
		   LogUtil.info("[InetServer]", "[StartError]", e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		/*try{
			String fttps = ConfigContext.getRequest("/WEB-INFO/fttp.js");
			System.out.println(fttps);
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
			
			int treenum = 2;
			String[] fttproot=null,fttprootcode=null;
			FileProperty[] fttpchild = null;
			if(treenum==0){
				fttproot = FttpAdapter.fttpRoots();
				fttprootcode = FttpAdapter.fttpRootsPathEncode(fttproot);
			}else if(treenum==1){
				FttpAdapter fa = new FttpAdapter("fttp://localhost");
				//rootlist = fa.listRoots();
				fttpchild = fa.getChildProperty();
			}else if(treenum>1){
				FttpAdapter fa = new FttpAdapter("fttp://localhost/d:/");
				fttpchild = fa.getChildProperty();
			}
			
			engine.put("$treenum", treenum);
			engine.put("$fttproot", fttproot);
			engine.put("$fttprootcode", fttprootcode);
			engine.put("$fttpchild", fttpchild);
			//if(engine instanceof Invocable){
			
			if(engine instanceof Compilable){
				Compilable ce = (Compilable)engine;
				ce.compile(fttps).eval();
			}
			//System.out.println(o);
			String result = (String)engine.get("result");
			System.out.println("result:"+result);
		}catch(Exception e){
			System.out.println(e);
		}
		try{
			//System.out.println(new File("/C:/Program Files").isHidden());
			//System.out.println(new File("/C:/Program Files").listFiles());
			//System.out.println(new File("/C:/Program Files").listFiles().length);
			//FttpAdapter fa = new FttpAdapter("fttp://a.b.c///d/\\out\\aa\\bb/% [d]*?<>|^&#$@!+-=,;:'\"()~.htm");//
			//FttpAdapter fa = new FttpAdapter("fttp://a.b.c/d:\\.htm");
			FttpAdapter fa = new FttpAdapter("fttp://localhost/C:/Program Files");
			FileProperty[] fttpchild = fa.getChildProperty();
			System.out.println("fttpchild................"+fttpchild);
			System.out.println("fttpchild................"+fttpchild.length);
		}catch(Exception e){
			System.out.println(e);
		}*/
		start(args[0], 8888, 0);
	}
}

class HandlerAll implements HttpHandler
{
	public void handle(HttpExchange exchange) throws IOException
	{
	    /*InputStream is = t.getRequestBody();
	    String response = "response ok";
	    t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
	    OutputStream os = t.getResponseBody();
	    os.write(response.getBytes());
	    os.close();
	    */
	    Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/html");
		String response = ConfigContext.getRequest(ConfigContext.getProp("RSPE404"));
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}

class HandlerEcho implements HttpHandler
{
	public void handle(HttpExchange exchange) throws IOException
	{
		String requestMethod = exchange.getRequestMethod();
		if(requestMethod.equalsIgnoreCase("GET"))
		{
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			
			OutputStream responseBody = exchange.getResponseBody();
			Headers requestHeaders = exchange.getRequestHeaders();
			Set<String> keySet = requestHeaders.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()){
				String key = iter.next();
				List values = requestHeaders.get(key);
				String s = key + " = " + values.toString() + "\n";
				responseBody.write(s.getBytes());
			}
			responseBody.close();
		}
	}
}

class HandlerRes implements HttpHandler
{
	public void handle(HttpExchange exchange) throws IOException
	{
		String uri = exchange.getRequestURI().getPath().substring(5);
		//System.out.println(exchange.getRemoteAddress()+" getPath:"+uri);
		File fl = new File(uri);
		//System.out.println(fl);
		//System.out.println(fl.exists());
		
		String requestMethod = exchange.getRequestMethod();
		if(requestMethod.equalsIgnoreCase("GET"))
		{	
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "application/x-download");
			//responseHeaders.set("Content-Disposition", "attachment; filename=abcd.bat");
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, fl.length());//HttpURLConnection.HTTP_OK,responseMsg.length();
			OutputStream responseBody = exchange.getResponseBody();
			FileAdapter fa = new FileAdapter(fl.getPath());
			byte[] bts = null;
			long begin=0,every=FileAdapter.k(512);
			while((bts=fa.getReader(begin, every).readAll())!=null){
				responseBody.write(bts);
				begin+=bts.length;
			}
			fa.close();
			responseBody.close();
		}
	}
}

class HandlerAuth implements HttpHandler
{
	String getResponse(String treeId, int treenum, String eid){
		String result = "";
		try{
			String fttps = ConfigContext.getRequest(ConfigContext.getProp("RSPFTTPJS"));
			//System.out.println(fttps);
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
			
			String[] fttproot=null,fttprootcode=null;
			FileProperty[] fttpchild = null;
			if(treenum==0){
				fttproot = FttpAdapter.fttpRoots();
				//for(int i=0;i<fttproot.length;i++)System.out.println("fttproot:"+fttproot[i]);
				fttprootcode = FttpAdapter.fttpRootsPathEncode(fttproot);
			}else if(treenum>0){
				//System.out.println("getResponse treeId................"+treeId);
				FttpAdapter fa = new FttpAdapter(treeId);//ObjectBytes.getViewUtf8UrlString(treeId)getViewUrlStringdecodeReplace"fttp://localhost"
				//rootlist = fa.listRoots();
				fttpchild = fa.getChildProperty();
				/*System.out.println("getResponse fttpchild................"+fttpchild);
				if(fttpchild!=null)
					System.out.println("getResponse fttpchild length................"+fttpchild.length);*/
			}
			
			engine.put("$treenum", treenum);
			engine.put("$fttproot", fttproot);
			engine.put("$fttprootcode", fttprootcode);
			engine.put("$fttpchild", fttpchild);
			//if(engine instanceof Invocable){
			
			if(engine instanceof Compilable){
				Compilable ce = (Compilable)engine;
				ce.compile(fttps).eval();
			}
			//System.out.println(o);
			result = (String)engine.get("result");
			//result = new String(result.getBytes("iso-8859-1"),"utf-8");
			//System.out.println("result:"+result);
		}catch(Exception e){
			//System.out.println(e);
			 LogUtil.info("[getResponse]", "[HandlerAuth]", e);
		}
		return "<script>parent."+eid+".innerHTML=\""+result+"\";</script>";
	}
	
	public void handle(HttpExchange exchange) throws IOException
	{
		try{
			boolean authflag = false;
			Headers hds = exchange.getRequestHeaders();
			List<String> auth = hds.get("Authorization");
			String requesturi = exchange.getRequestURI().getPath();
			String requestparam = exchange.getRequestURI().getQuery();
			//System.out.println(requesturi+",param:"+requestparam);//new String(.getBytes("UTF-8"))
			if(auth!=null){
				String authstr = new String(ObjectBytes.decode(auth.get(0).split("\u0020")[1].toCharArray()));
				//System.out.println("Authorization:"+authstr);
				//System.out.println(ObjectBytes.encodeurl("root"));
				//System.out.println(ObjectBytes.decodeurl("cm9vdA%3D%3D"));
				if(authstr!=null){
					String[] autharr = authstr.split("\u003A");
					//System.out.println("autharr.length:"+autharr.length);
					if(autharr.length==2){
						String authpwd = ConfigContext.getUsersConfig().getString(autharr[0]);
						if(authpwd!=null&&authpwd.equals(autharr[1]))
							authflag = true;
					}
				}
				//System.out.println("authflag:"+authflag);
			}
			
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/html;charset=UTF-8");
			String response = "";
			if(authflag){
				if(requesturi.equals(ConfigContext.getProp("REQFTTP")))
					response = ConfigContext.getRequest(ConfigContext.getProp("RSPFTTPJSP"));
				else if(requesturi.equals(ConfigContext.getProp("REQGETFTTP"))&&requestparam!=null){
					String[] rqsarrstr = requestparam.split("&");
					String rqtid = rqsarrstr[0].replaceAll("tid=","");
					String tid = ObjectBytes.getViewUtf8UrlString(rqtid);//getViewUrlString ObjectBytes.decodeReplace(tid);
					//System.out.println("requesttreeId:"+rqtid);
					//System.out.println("treeId:"+tid);
					int tn = Integer.parseInt(rqsarrstr[1].replaceAll("tn=",""));
					//System.out.println("tn:"+tn);
					String eid = rqsarrstr[2].replaceAll("eid=","");
					response =  getResponse(tid, tn, eid);
				}
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			}else{
				response = ConfigContext.getRequest(ConfigContext.getProp("RSPE401"));
				responseHeaders.set("WWW-Authenticate", "Basic realm='Fttp Admin'");
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, response.length());
			}
			OutputStream os = exchange.getResponseBody();
		    os.write(response.getBytes("UTF-8"));
		    os.close();
		}catch(Exception e){
		   LogUtil.info("[InetServer]", "[HandlerAuth]", e.getMessage());
		   throw new IOException(e);
		}  
	}
}