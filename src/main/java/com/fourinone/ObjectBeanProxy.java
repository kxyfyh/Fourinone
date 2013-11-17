package com.fourinone;

class ObjectBeanProxy implements ObjectBean{
	Object obj;
	Long vid;
	String name;
	ObjectBeanProxy(){}
	/*private ObjectBeanProxy(ObjValue ov, String domainnodekey){
		vid = (Long)ov.getObj(domainnodekey+"._me_ta.version");
		obj = ov.get(domainnodekey);
		name = domainnodekey;
	}*/
	//@Delegate(interfaceName="com.fourinone.ObjectBean",methodName="toObject",policy=DelegatePolicy.Implements)
	public Object toObject(){
		return obj;
	}
	//@Delegate(interfaceName="com.fourinone.ObjectBean",methodName="getName",policy=DelegatePolicy.Implements)
	public String getName(){
		return name;
	}
	
	public String getDomain(){
		if(name!=null)
			return ParkObjValue.getDomainNode(name)[0];
		else
			return null;
	}	
		
	public String getNode(){
		if(name!=null)
		{
			String[] arr = ParkObjValue.getDomainNode(name);
			if(arr.length==2)
				return arr[1];
		}
		return null;
	}
	
	
	public String toString(){
		return name+":"+obj.toString();
	}
	/*@Delegate(interfaceName="com.fourinone.ObjectVersion",methodName="getVid",policy=DelegatePolicy.Implements)
	public Long getVid(){
		return vid;
	}*/
}