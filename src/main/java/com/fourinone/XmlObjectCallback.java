package com.fourinone;

import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class XmlObjectCallback extends DefaultHandler
{
	private boolean textFlag = false;
	private ArrayList objAl;
	private ObjValue curObj;
	private String curKey;
	private String PROPSROW_DESC;
	private String KEY_DESC;
	private String curPROPSROW_DESC;
	private String curKEY_DESC;

	public XmlObjectCallback()
	{
	}
	
	public void startDocument() throws SAXException 
	{
		//LogUtil.fine("start parse xml");
	}
	
	public void startElement(String uri, String sName, String qName, Attributes attrs)
	{
		if(qName.equals("PROPSTABLE"))
		{
			//LogUtil.fine(attrs.getValue("DESC"));
			objAl = new ArrayList();
		}
		else if(qName.equals("PROPSROW"))
		{
			curPROPSROW_DESC = attrs.getValue("DESC");
			curObj = new ObjValue();
		}
		else
		{
			//LogUtil.fine(qName);
			curKEY_DESC = attrs.getValue("DESC");
			curKey = qName;
			textFlag = true;
		}
    }
    
    public void characters(char[] data, int start, int length)
	{
		String content = new String(data, start, length);
		if(textFlag)
		{
			//LogUtil.fine(content);
			if(KEY_DESC==null||(curKEY_DESC!=null&&curKEY_DESC.equals(KEY_DESC)))
				curObj.setString(curKey, content.trim());
		}
	}

	public void endElement(String uri, String sName, String qName)
	{
		
		if(qName.equals("PROPSTABLE"))
		{
		}
		else if(qName.equals("PROPSROW"))
		{
			if(PROPSROW_DESC==null||(curPROPSROW_DESC!=null&&curPROPSROW_DESC.equals(PROPSROW_DESC)))
				objAl.add(curObj);
		}
		else
		{
			//LogUtil.fine("/"+qName);
			textFlag = false;
		}
	}
  
	public void endDocument() throws SAXException
	{
		//LogUtil.fine("end parse xml");
	}
	
	public ArrayList getObjAl()
	{
		return objAl;
	}
	
	public void setPROPSROW_DESC(String PROPSROW_DESC)
	{
		this.PROPSROW_DESC = PROPSROW_DESC;
	}
	
	public void setKEY_DESC(String KEY_DESC)
	{
		this.KEY_DESC = KEY_DESC;
	}
}