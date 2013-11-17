package com.fourinone;

import java.util.EventObject;

public class LastestEvent extends EventObject{

	public LastestEvent(Object source){
		super(source);
	}

	public void setSource(Object source){
		this.source = source;
	}
}