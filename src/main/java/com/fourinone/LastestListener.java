package com.fourinone;

import java.util.EventListener;

public interface LastestListener extends EventListener
{
	public boolean happenLastest(LastestEvent le);
}