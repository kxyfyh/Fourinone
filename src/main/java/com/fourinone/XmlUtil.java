package com.fourinone;

import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.xml.sax.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
//import com.file.*;
//import com.base.*;
//import com.log.LogUtil;
//import javax.servlet.*;
//import javax.servlet.http.*;
//import com.lang.MulLangBean;
import java.util.List;

public class XmlUtil
{
	public ArrayList getXmlPropsByFile(String filePath)
	{
		return getXmlPropsByFile(filePath, null, null);	
	}
	
	public ArrayList getXmlPropsByFile(String filePath, String PROPSROW_DESC)
	{
		return getXmlPropsByFile(filePath, PROPSROW_DESC, null);	
	}
	
	public ArrayList getXmlPropsByFile(String filePath, String PROPSROW_DESC, String KEY_DESC)
	{
		/*filePath += ".xml";
		
		FileUse fu = new FileUse();
		BaseBean base = new BaseBean();
		if(!fu.checkFile(filePath))
			filePath = base.getClassPath()+fu.getSeparator()+filePath;
		*/
		ArrayList al = new ArrayList();
		try
		{
			XmlCallback handler = 	new XmlCallback();
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser saxParser = factory.newSAXParser();
				
			InputSource src = new InputSource(new FileInputStream(filePath));
			//LogUtil.fine(filePath);
			if(PROPSROW_DESC!=null)
				handler.setPROPSROW_DESC(PROPSROW_DESC);
			if(KEY_DESC!=null)
				handler.setKEY_DESC(KEY_DESC);
			saxParser.parse(src ,handler);
			al = handler.getPropsAl();
		} 
		catch(Throwable t) 
		{
			//LogUtil.fine("[XmlUtil]", "[getXmlPropsByFile]", "[Error Exception:"+filePath+"]", t);
			System.err.println("[XmlConfig][Error:get XmlProps From File]"+t);
		}
		return al;	
	}
	
	public ArrayList getXmlPropsByTable()
	{
		ArrayList al = new ArrayList();
		return al;
	}
	
	public ArrayList getXmlPropsByObject()
	{
		ArrayList al = new ArrayList();
		return al;		
	}
	
	public void getXmlFileByTable()
	{
	}
	
	public ArrayList getXmlObjectByFile(String filePath)
	{
		return getXmlObjectByFile(filePath, null, null);	
	}
	
	public ArrayList getXmlObjectByFile(String filePath, String PROPSROW_DESC)
	{
		return getXmlObjectByFile(filePath, PROPSROW_DESC, null);	
	}
	
	public ArrayList getXmlObjectByFile(String filePath, String PROPSROW_DESC, String KEY_DESC)
	{
		/*filePath += ".xml";
		
		FileUse fu = new FileUse();
		BaseBean base = new BaseBean();
		if(!fu.checkFile(filePath))
			filePath = base.getClassPath()+fu.getSeparator()+filePath;
		*/
		ArrayList al = new ArrayList();
		try
		{
			XmlObjectCallback handler = 	new XmlObjectCallback();
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser saxParser = factory.newSAXParser();
			//LogUtil.fine(filePath);
			InputSource src = new InputSource(new FileInputStream(filePath));
			//LogUtil.fine(filePath);
			if(PROPSROW_DESC!=null)
				handler.setPROPSROW_DESC(PROPSROW_DESC);
			if(KEY_DESC!=null)
				handler.setKEY_DESC(KEY_DESC);
			saxParser.parse(src ,handler);
			al = handler.getObjAl();
		} 
		catch(Throwable t) 
		{
			//LogUtil.fine("[XmlUtil]", "[getXmlObjectByFile]", "[Error Exception:"+filePath+"]", t);
			System.err.println("[XmlConfig][Error:get XmlObject From File]"+t);
		}
		return al;	
	}
	/*
	public void getXmlFileByObject(ArrayList objArray, String filePath) throws Exception
	{
		getXmlOutByObject(objArray, new FileOutputStream(filePath));//FileWriter bad code
	}
	
	
	public void getExportByObject(List objArray, HttpServletResponse response, MulLangBean mull, int exportType) throws Exception
	{
		if(exportType==1)//excel
		{
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition","attachment; filename=data.xls");
			response.getWriter().println(getTableStrByObject(objArray, mull));
		}
		else if(exportType==2)//word
		{
			response.setContentType("application/msword");
			response.setHeader("Content-disposition","attachment; filename=data.doc");
			response.getWriter().println(getTableStrByObject(objArray, mull));
		}
		else if(exportType==3)//html
		{
			response.setContentType("text/html");
			response.setHeader("Content-disposition","attachment; filename=data.html");
			response.getWriter().println(getTableStrByObject(objArray, mull));
		}
		else if(exportType==4)//xml
		{
			getXmlExportByObject((ArrayList)objArray, response, mull);
		}
	}
	
	public String getTableStrByObject(List objArray, MulLangBean mull)
	{
		StringBuffer tableSb = new StringBuffer();
		BaseBean base = new BaseBean();
		tableSb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		tableSb.append("<table border=1>");
		for(int i=0;i<objArray.size();i++)
	    {
	    	ObjValue obj = (ObjValue)objArray.get(i);
	    	ArrayList objNames = obj.getObjNames();
	    	
	    	if(i==0)
	    	{
	    		tableSb.append("<tr>");
		    	for(int j=0;j<objNames.size();j++)
			    {
			    	String tagName = (String)objNames.get(j);
				    tableSb.append("<td>");
				    tableSb.append(mull!=null?mull.getString(tagName):tagName);
				    tableSb.append("</td>");
				}
				tableSb.append("</tr>");
	    	}
	    	
	    	tableSb.append("<tr>");
	    	for(int j=0;j<objNames.size();j++)
		    {
		    	String tagName = (String)objNames.get(j);
			    String tagValue = base.getString(obj.get(tagName));
			    if(tagName!=null&&(tagName.equals("DATE_CREATE")||tagName.equals("DATE_UPDATE")))
			    	tagValue = base.getDateViewDesc(obj.get(tagName));
			    tableSb.append("<td>");
			    tableSb.append(tagValue);
			    tableSb.append("</td>");
			}
			tableSb.append("</tr>");
		}
		tableSb.append("</table>");
		
		return tableSb.toString();
	}
	
	public void getXmlOutByObject(ArrayList objArray, OutputStream out)//Writer
	{
		try
		{
			Document doc = getXmlDocByObject(objArray);
			xmlTrans(doc, out);
		}
		catch(Exception e)
		{
			LogUtil.fine("[XmlUtil]", "[getXmlOutByObject]", "[Error Exception:]", e);
		}
	}
	
	public void getXmlOutByObject(ArrayList objArray, HttpServletResponse response)
	{
		try
		{
			//for return xml doc
		    response.setHeader("Cache-Control", "no-store");
		    response.setDateHeader("Expires", 0);
		    response.setContentType("text/xml; charset=UTF-8");
			
			//send
			Document doc = getXmlDocByObject(objArray);
			xmlTrans(doc, response.getWriter());//getOutputStream() has already been called for
		}
		catch(Exception e)
		{
			LogUtil.fine("[XmlUtil]", "[getXmlOutByObject]", "[Error Exception:]", e);
		}
	}
	
	public void getXmlExportByObject(ArrayList objArray, HttpServletResponse response, MulLangBean mull)
	{
		try
		{
			response.setContentType("application");
			response.setHeader("Content-disposition","attachment; filename=data.xml");
			Document doc = getXmlDocByObject(objArray, mull);
			xmlTrans(doc, response.getWriter());
		}
		catch(Exception e)
		{
			LogUtil.fine("[XmlUtil]", "[getXmlExportByObject]", "[Error Exception:]", e);
		}
	}
	
	public Document getXmlDocByObject(ArrayList objArray) throws Exception
	{
		return getXmlDocByObject(objArray, null);
	}
	
	public Document getXmlDocByObject(ArrayList objArray, MulLangBean mull) throws Exception
	{
	    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	    Element PROPSTABLE = doc.createElement(mull!=null?mull.getString("PROPSTABLE"):"PROPSTABLE");//MUST HAVA A ROOT ELEMT
	    BaseBean base = new BaseBean();
	    
	    for(int i=0;i<objArray.size();i++)
	    {
	    	Element PROPSROW = doc.createElement(mull!=null?mull.getString("PROPSROW"):"PROPSROW");
	    	
	    	ObjValue obj = (ObjValue)objArray.get(i);
	    	ArrayList objNames = obj.getObjNames();
	    	for(int j=0;j<objNames.size();j++)
		    {
			    String tagName = (String)objNames.get(j);
			    String tagValue = base.getString(obj.get(tagName));
			    Element tagNameElem = doc.createElement(mull!=null?mull.getString(tagName,""):tagName);
			    tagNameElem.appendChild(doc.createTextNode(tagValue));
			    PROPSROW.appendChild(tagNameElem);
			}
			
			PROPSTABLE.appendChild(PROPSROW);
		}
		
		doc.appendChild(PROPSTABLE);
		return doc;
	}
	
	public void xmlTrans(Document doc, Writer out) throws Exception
	{
		DOMSource doms = new DOMSource(doc);
	   	StreamResult sr = new StreamResult(out);//response.getOutputStream()
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer t = tf.newTransformer();
	    t.setOutputProperty("encoding", "UTF-8");
	    t.transform(doms, sr);
	}
	
	public void xmlTrans(Document doc, OutputStream out) throws Exception
	{
		DOMSource doms = new DOMSource(doc);
	   	StreamResult sr = new StreamResult(out);//response.getOutputStream()
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer t = tf.newTransformer();
	    t.setOutputProperty("encoding", "UTF-8");
	    t.transform(doms, sr);
	}
	*/
	public static void main(String args[])
	{
		XmlUtil xu = new XmlUtil();
		ArrayList al = xu.getXmlPropsByFile("db","SQLSERVER");//
		System.out.println(al);
	}
}