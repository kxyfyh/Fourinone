package com.fourinone;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class MulBean extends ResourceBean
{
	private String nativeLangCode;
	
	public MulBean(String langCode)
	{
		super();
		resourcesName = "META-INF/config";
		init(langCode);
	}
	
	public void init(String langCode)
	{
		if(langCode==null)
		{
			bundle = ResourceBundle.getBundle(resourcesName, Locale.getDefault());
		}
		else if(langCode.toUpperCase().equals("ISO-8859-1"))
		{
			nativeLangCode = "ISO-8859-1";
			bundle = ResourceBundle.getBundle(resourcesName, Locale.US);
		}
		else if(langCode.toUpperCase().equals("GB2312"))
		{
			nativeLangCode = "GB2312";
			bundle = ResourceBundle.getBundle(resourcesName, Locale.PRC);
		}
		else if(langCode.toUpperCase().equals("BIG5"))
		{
			nativeLangCode = "BIG5";
			bundle = ResourceBundle.getBundle(resourcesName, Locale.TAIWAN);//new Locale("zh", "TW");
		}
	}
	
	public String getString(String keyWord)
	{		
		return getString(keyWord, "");
	}
	
	public String getString(String keyWord, String topStr)
	{		
		String str = "";
		try
		{
			str = bundle.getString(keyWord);
					
		}
		catch(MissingResourceException ex)
		{
			str = topStr+keyWord;
			//System.err.println(ex);	
		}		
		return str;		
	}
	
	public String getSpace()
	{
		String space = "";
		if(nativeLangCode!=null&&nativeLangCode.equals("ISO-8859-1"))
			space = "&nbsp;";
	
		return space;
	}
	
	public String getFileString(String relativeUri){
		StringBuffer sb = new StringBuffer();
		try{
			Reader f = new InputStreamReader(this.getClass().getResourceAsStream(relativeUri));
			BufferedReader fb = new BufferedReader(f);
			String s = "";
			while((s=fb.readLine())!=null){
				sb = sb.append(s);
			}
			f.close();
			fb.close();
		}catch(IOException ex){
			//System.err.println(ex);
		}
		//System.out.println(sb);
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		MulBean rb = new MulBean("ISO-8859-1");
		//try{Thread.sleep(10000L);}catch(Exception ex){}
		/*System.out.println(rb.getString("QSXYSJ"));
		System.out.println(rb.getString("YBB"));
		System.out.println(rb.getString("YGSJ"));*/
	}
}
