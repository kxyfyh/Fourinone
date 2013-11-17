package com.fourinone;

public class FileResult<E> extends Result{
	public FileResult(){
		super();
	}
	
	public FileResult(boolean ready)
	{
		super(ready);
	}
	
	static FileResult getExceptionResult(){
		FileResult fr = new FileResult(false);
		fr.setReady(FileResult.EXCEPTION);
		return fr;
	}
}