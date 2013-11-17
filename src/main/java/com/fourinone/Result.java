package com.fourinone;

public class Result<E> extends WareHouse{
	private E res;

	public Result(){
		super();
	}
	
	public Result(boolean ready)
	{
		super(ready);
	}
	
	void setResult(E res)
	{
		this.res = res;
	}
	
	public E getResult()//throw
	{
		//if(getException()!=null)throws new FileExcpetion()
		return res;
	}
}