package com.fourinone;

public class FileException extends ServiceException
{
	public FileException(){
		super();
	}
	
	public FileException(String msg){
		super(msg);
	}

	public FileException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	public FileException(Throwable cause){
		super(cause);
	}
}