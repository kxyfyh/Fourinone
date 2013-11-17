package com.fourinone;

public class ParkPatternBean
{
	String domain,node;
	WareHouse inhouse,outhouse;
	ObjectBean thisversion;
	RecallException rx;
	ParkPatternBean(String domain, String node, WareHouse inhouse, WareHouse outhouse, RecallException rx)
	{
		this.domain = domain;
		this.node = node;
		this.inhouse = inhouse;
		this.outhouse = outhouse;
		this.rx = rx;
	}
}