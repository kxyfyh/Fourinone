package com.fourinone;

import java.security.Permission;

public class ParkPermission extends Permission
{
	private String action;
	public ParkPermission(String target, String anAction){
		super(target);
		action = anAction;
	}

	public String getActions(){
		return action;
	}
	
	public boolean equals(Object other){
		return false;
	}
	
	public int hashCode(){
		return getName().hashCode() + action.hashCode();
	}
	
	public boolean implies(Permission other){
		//System.out.println("implies");
		return true;
	}
}