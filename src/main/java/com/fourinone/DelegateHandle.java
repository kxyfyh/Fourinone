package com.fourinone;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class DelegateHandle implements InvocationHandler
{
	private Class[] bs;
	private DelegateHandle(Class[] bs)
	{
		this.bs = bs;
	}
	
	public static Object bind(Class[] as, Class[] bs)
	{
		return Proxy.newProxyInstance(as[0].getClassLoader(), as, new DelegateHandle(bs));
	}
	
	public static <I> I bind(Class<I> a, Class b)
	{
		return (I)bind(new Class[]{a}, new Class[]{b});
	}
	
	public static <I> I bind(Class<I> a, Class[] bs)
	{
		return (I)bind(new Class[]{a}, bs);
	}

	/*
	public static <I> I bind(Class<I>[] a, Class b)
	{
		return (I)Proxy.newProxyInstance(a[0].getClassLoader(), a, new DelegateHandle(b));//new Class[]
	}
	*/
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Object result = null;
		//System.out.println("A:"+proxy.getClass().getSuperclass().getName());
		//System.out.println("A:"+proxy.getClass().getInterfaces()[0]);
		Method mbegin=null,mimpl=null,mend=null;
		
		for(int j=0;j<bs.length;j++)
		{
			Method[] bms = bs[j].getMethods();
			for(int i=0;i<bms.length;i++)
			{
				boolean anflag = bms[i].isAnnotationPresent(Delegate.class);
				if(anflag)
				{
					Delegate dl = bms[i].getAnnotation(Delegate.class);
					//System.out.println("dl.interfaceName():"+dl.interfaceName());
					Class dlifl = Class.forName(dl.interfaceName());
					if(dlifl.isAssignableFrom(proxy.getClass())&&dl.methodName().equals(method.getName())&&Arrays.equals(method.getParameterTypes(),bms[i].getParameterTypes())&&method.getReturnType().equals(bms[i].getReturnType()))
					{
						DelegatePolicy dp = dl.policy();
						if(dp==DelegatePolicy.Begin)
							mbegin = bms[i];
						else if(dp==DelegatePolicy.Implements)
							mimpl = bms[i];
						else if(dp==DelegatePolicy.End)
							mend = bms[i];
					}
				}
			}
		}
		//System.out.println("mimpl111111:"+mimpl);
		if(mimpl!=null)
		{
			if(mbegin!=null)
				mbegin.invoke(mbegin.getDeclaringClass().newInstance(), args);
			//System.out.println("11111111:"+mimpl.getDeclaringClass());
			result = mimpl.invoke(mimpl.getDeclaringClass().newInstance(), args);
			if(mend!=null)
				mend.invoke(mend.getDeclaringClass().newInstance(), args);
		}

		//result = bms[i].invoke(b.newInstance(), args);
		/*
		if(method.getName().equals("getStr"))
			result = (Object)ClientMain.getStrUtil((String)args[0]);
		else if(method.getName().equals("getSomething"))
			result = method.invoke(new ClientMain(), args);
		//System.out.println("end invoke");
		*/
		return result;
	}

}