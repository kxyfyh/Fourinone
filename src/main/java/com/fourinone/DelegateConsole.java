package com.fourinone;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DelegateConsole implements InvocationHandler
{
	private Object[] bs;
	private DelegateConsole(Object[] bs){
		this.bs = bs;
	}
	/*
	public static Object bind(Object... abs)
	{
		List aslist = new ArrayList();
		List bslist = new ArrayList();
		for(Object obj:abs){
			if(obj instanceof Class)
				aslist.add(obj);
			else
				bslist.add(obj);
		}
		//System.out.println(as[0].getClass().getName());
		//System.out.println(bs[0].getClass().getName());
		return Proxy.newProxyInstance(aslist.get(0).getClassLoader(), aslist.toArray(), new DelegateConsole(bslist.toArray()));
	}
	*/
	public static Object bind(Class[] as, Object... bs)
	{
		//System.out.println(as[0].getClass().getName());
		//System.out.println(bs[0].getClass().getName());
		return Proxy.newProxyInstance(as[0].getClassLoader(), as, new DelegateConsole(bs));
	}
	
	public static <I> I bind(Class<I> a, Object... bs)
	{
		return (I)bind(new Class[]{a}, bs);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Object result = null;
		Object mbeginObj=null,mimplObj=null,mendObj=null;
		Method mbegin=null,mimpl=null,mend=null;
		
		for(int j=0;j<bs.length;j++)
		{
			Method[] bms = bs[j].getClass().getMethods();
			for(int i=0;i<bms.length;i++)
			{
				boolean anflag = bms[i].isAnnotationPresent(Delegate.class);
				if(anflag)
				{
					Delegate dl = bms[i].getAnnotation(Delegate.class);
					Class dlifl = Class.forName(dl.interfaceName());
					//System.out.println("method.getName():"+method.getName());
					//System.out.println("method.getParameterTypes():"+method.getParameterTypes());
					//System.out.println("method.getReturnType():"+method.getReturnType());
					
					if(dlifl.isAssignableFrom(proxy.getClass())&&dl.methodName().equals(method.getName())&&Arrays.equals(method.getParameterTypes(),bms[i].getParameterTypes())&&method.getReturnType().equals(bms[i].getReturnType())){
						DelegatePolicy dp = dl.policy();
						if(dp==DelegatePolicy.Begin){
							mbeginObj = bs[j];
							mbegin = bms[i];
						}
						else if(dp==DelegatePolicy.Implements){
							mimplObj = bs[j];
							mimpl = bms[i];
						}
						else if(dp==DelegatePolicy.End){
							mendObj = bs[j];
							mend = bms[i];
						}
					}
				}
			}
		}
		
		if(mimpl!=null)
		{
			if(mbegin!=null)
				mbegin.invoke(mbeginObj, args);
			result = mimpl.invoke(mimplObj, args);
			if(mend!=null)
				mend.invoke(mendObj, args);
		}
		return result;
	}

}