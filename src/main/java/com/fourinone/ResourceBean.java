package com.fourinone;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class ResourceBean 
{
	protected String resourcesName;
	protected ResourceBundle bundle;
	
	public ResourceBean(){}
	
	public ResourceBean(String resourcesName)
	{
		bundle = ResourceBundle.getBundle(resourcesName);
	}
	
	public String getString(String keyWord)
	{		
		String str = "";
		try
		{
			str = bundle.getString(keyWord);
		}
		catch(MissingResourceException ex)
		{
			System.err.println(ex);
		}		
		return str;		
	}
	
	public static void main(String[] args)
	{
		ResourceBean rb = new ResourceBean("config");
		System.out.println(rb.getString("QSXYSJ"));
		
		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
        System.out.println(ResourceBean.class.getClassLoader().getResource(""));
        System.out.println(ClassLoader.getSystemResource(""));
        System.out.println(ResourceBean.class.getResource(""));
        System.out.println(ResourceBean.class.getResource("/"));
        System.out.println(new java.io.File("").getAbsolutePath());
        System.out.println(System.getProperty("user.dir"));
	}
}
