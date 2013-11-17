package com.fourinone;

public class ServiceException extends Exception
{
	public ServiceException(){
		super();
	}
	
	public ServiceException(String msg){
		super(msg);
	}

	public ServiceException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	public ServiceException(Throwable cause){
		super(cause);
	}
}