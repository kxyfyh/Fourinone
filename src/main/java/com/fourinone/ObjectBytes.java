package com.fourinone;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.CRC32;
import java.net.URLEncoder;
import java.net.URLDecoder;

final public class ObjectBytes //nopub
{
	static private char[] alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
	static private byte[] codes=new byte[256];
	static{
		for(int i=0;i<256;i++) codes[i]=-1;
		for(int i='A';i<='Z';i++) codes[i]=(byte)(i-'A');
		for(int i='a';i<='z';i++) codes[i]=(byte)(26+i-'a');
		for(int i='0';i<='9';i++) codes[i]=(byte)(52+i-'0');
		codes['+']=62;
		codes['/']=63;
	}
	
	static byte[] toBytes(Object o){
		byte[] gbt = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oout =new ObjectOutputStream(baos);//gzout
			oout.writeObject(o);
			byte[] objgbt = baos.toByteArray();
			//System.out.println(objgbt.length);
	        baos.close();
	        oout.close();
	        
			gbt = getByteFromIs(new ByteArrayInputStream(objgbt),true);
		}catch(Exception e){
			LogUtil.info("[ObjectBytes]", "[toBytes]", "[Error Exception:]", e);
		}
		return gbt;
	}
	
	static byte[] getByteFromIs(InputStream gis, boolean GZIPFlag)
	{
		byte[] gbt = null;
		try
		{
			BufferedInputStream is = new BufferedInputStream(gis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream gzout = GZIPFlag?(new GZIPOutputStream(baos)):baos;
			BufferedOutputStream os = new BufferedOutputStream(gzout);
			int ch;
			try
			{
				while((ch=is.read())!=-1)
			    	os.write(ch);
			}
			catch(EOFException eofex){/*for end the stream*/}
		    
	        is.close();
	        os.close();
	        gbt = baos.toByteArray();
	    }
	   	catch(Exception e){
			LogUtil.info("[ObjectBytes]", "[getByteFromIs]", "[Error Exception:]", e);
		}
		return gbt;
	}
	
	static Object toObject(byte[] bts){
		ByteArrayOutputStream sbos = new ByteArrayOutputStream();
		setByteToOs(bts, sbos, true);
		byte[] unzipbts = sbos.toByteArray();
		
		Object obj = null;
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(unzipbts);
			ObjectInputStream ois = new ObjectInputStream(bais);
			obj = ois.readObject();
			bais.close();
			ois.close();
	    }
	   	catch(Exception e){
			LogUtil.info("[ObjectBytes]", "[toObject]", "[Error Exception:]", e);
		}
		return obj;
	}
	
	static void setByteToOs(byte[] sbt, OutputStream sos, boolean GZIPFlag)
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(sbt);
			InputStream gzin = GZIPFlag?(new GZIPInputStream(bais)):bais;
			BufferedInputStream is = new BufferedInputStream(gzin);
			BufferedOutputStream os = new BufferedOutputStream(sos);
			
			int ch;
			try
			{
				while((ch=is.read())!=-1)
			    	os.write(ch);
			}
			catch(EOFException eofex){/*for end the stream*/}
		    
	        is.close();
	        os.close();
	    }
	   	catch(Exception e){
			LogUtil.info("[ObjectBytes]", "[setByteToOs]", "[Error Exception:]", e);
		}
	}

	static String encode(String data){
		return new String(encode(data.getBytes()));
	}
	
	static String encodeReplace(String data){
		return encode(data,"\u003D","\u005F");
	}
	
	static String encode(String data, String ostr, String rplstr){
		return encode(data).replaceAll(ostr, rplstr);
	}
	
	static String encodeurl(String data){
		return getUrlString(encode(data));
	}
	
	static char[] encode(byte[] data){
		char[] out = new char[((data.length+2)/3)*4];
		for(int i=0,index=0;i<data.length;i+=3,index+=4){
			boolean quad=false,trip=false;
			int val=(0xFF&(int)data[i]);
			val<<=8;
			if((i+1)<data.length){
				val|=(0xFF&(int)data[i+1]);
				trip = true;
			}
			val<<=8;
			if((i+2)<data.length){
				val|=(0xFF&(int)data[i+2]);
				quad = true;
			}
			out[index+3]=alphabet[(quad?(val&0x3F):64)];
			val>>=6;
			out[index+2]=alphabet[(trip?(val&0x3F):64)];
			val>>=6;
			out[index+1]=alphabet[val&0x3F];
			val>>=6;
			out[index+0]=alphabet[val&0x3F];
		}
		return out;
	}
	
	static String decode(String data){
		return new String(decode(data.toCharArray()));
	}
	
	static String decodeReplace(String data){
		return decode(data,"\u005F","\u003D");
	}
	
	static String decode(String data, String ostr, String rplstr){
		return decode(data.replaceAll(ostr,rplstr));
	}
	
	static String decodeurl(String data){
		return decode(getViewUrlString(data));
	}
	
	static byte[] decode(char[] data){
		int len=((data.length+3)/4)*3;
		if(data.length>0&&data[data.length-1]=='=') --len;
		if(data.length>1&&data[data.length-2]=='=') --len;
		byte[] out=new byte[len];
		int shift=0,accum=0,index=0;
		for(int ix=0;ix<data.length;ix++){
			int value=codes[data[ix]&0xFF];
			if(value>=0){
				accum<<=6;
				shift+=6;
				accum|=value;
				if(shift>=8){
					shift-=8;
					out[index++]=(byte)((accum>>shift)&0xff);
				}
			}
		}
		if(index!=out.length) throw new Error("miscalculated data length!");
		return out;
	}
	
	static String getUrlString(String viewStr)
	{
		String urlStr = "";
		if(viewStr!=null)
			urlStr = URLEncoder.encode(viewStr);
		return urlStr;
	}
	
	static String getViewUrlString(String urlStr)
	{
		String viewStr = "";
		if(urlStr!=null)
			viewStr = URLDecoder.decode(urlStr);//aaa = java.net.URLDecoder.decode(aaa,"UTF-8");
		return viewStr;		
	}
	
	static String getUtf8UrlString(String viewStr)
	{
		String urlStr = "";
		try
		{
			if(viewStr!=null)
				urlStr = URLEncoder.encode(viewStr,"UTF-8");//if default,can no langCode
		}catch(Exception e){
			LogUtil.fine(e);
		}
		return urlStr;
	}
	
	static String getViewUtf8UrlString(String urlStr)
	{
		String viewStr = "";
		try
		{
			if(urlStr!=null)
				viewStr = URLDecoder.decode(urlStr, "UTF-8");//new String(urlStr.getBytes("8859_1"),"UTF-8");//if default,can no langCode
		}catch(Exception e){
			LogUtil.fine(e);
		}
		return viewStr;	
	}
		
	static String getEscape(String fttpstr){
		if(fttpstr==null)
			return null;
		
		char[] chararr = fttpstr.toCharArray();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<chararr.length;i++){
			switch(chararr[i]){
				case '%': sb.append("%25");break;
				case ' ': sb.append("%20");break;
				case '[': sb.append("%5B");break;
				case ']': sb.append("%5D");break;
				case '\\': sb.append("%5C");break;
				case '{': sb.append("%7B");break;
				case '}': sb.append("%7D");break;
				case '<': sb.append("%3C");break;
				case '>': sb.append("%3E");break;
				case '|': sb.append("%7C");break;
				case '^': sb.append("%5E");break;
				case '\"': sb.append("%22");break;
				default: sb.append(chararr[i]);
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		java.util.ArrayList ls = new java.util.ArrayList();
		java.util.HashMap<String,String> mp = new java.util.HashMap<String,String>();
		mp.put("id","10168758145-1304254801723-2f222c633a82d97e3fa4766d54647604");
		mp.put("buyer.writetotaircount","2");
		mp.put("buyer.timespent","2394");
		mp.put("request.buynowjhtml.client-ip","114.82.123.229");
		mp.put("request.buynowjhtml.header.referer","http://buy.taobao.com/auction/buy_now.jhtml");
		mp.put("request.buynowjhtml.content.skuId","");
		mp.put("request.buynowjhtml.content._fma.b._0.pr","1.00");
		mp.put("request.buynowhtml.client-ip","114.82.123.229");
		mp.put("request.buynowhtml.header.referer","http://buy.taobao.com/auction/buy_now.html");
		mp.put("request.buynowhtml.content.skuId","");
		mp.put("request.buynowhtml.content._fma.b._0.pr","1.00");
		//mp.put("meta","11");
		ls.add(mp);
		
		byte[] objbts = toBytes(ls);
		System.out.println("objbts.length:"+objbts.length);
		byte[] objbts2 = toBytes(objbts);
		System.out.println("objbts2.length:"+objbts2.length);
		byte[] objbts3 = (byte[])toObject(objbts2);
		System.out.println("objbts3.length:"+objbts3.length);
		Object obj = toObject(objbts);
		System.out.println((java.util.ArrayList)obj);
		
		/*byte[] gzipbts = toGzipBytes(objbts);
		System.out.println(new String(gzipbts));
		byte[] ungzipbts = toUnGzipBytes(gzipbts);
		System.out.println(ungzipbts.length);
		*/
		
		CRC32 crc = new CRC32();
		crc.update(objbts);
		System.out.println(crc.getValue());

		java.util.Hashtable arr = new java.util.Hashtable();
		arr.put("a",1.1);
		
		java.util.Hashtable arr2 = new java.util.Hashtable();
		arr2.put("a",1.1);
		
		class A implements java.io.Serializable{
			private String a;
			public A(String a){
				this.a = a;//arr2.get("a");
			}
		}
		
		A a0 = new A("0");
		A a1 = new A("0");
				
		System.out.println(a0.equals(a1));
		
		CRC32 crc2 = new CRC32();
		crc2.update(toBytes(a0));
		System.out.println(crc2.getValue());
		
		CRC32 crc3 = new CRC32();
		crc3.update(toBytes(a1));
		System.out.println(crc3.getValue());
		
		CRC32 crc4 = new CRC32();
		crc4.update(toBytes(System.nanoTime()));
		System.out.println("crc4:"+crc4.getValue());
	}
}