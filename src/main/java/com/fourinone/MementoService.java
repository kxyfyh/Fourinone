package com.fourinone;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

class MementoService{
	String getClientHost(){
		String clienthost=null;
		try{
			clienthost = RemoteServer.getClientHost();
		}catch(ServerNotActiveException ex){
			LogUtil.fine("[MementoService]", "[getClientHost]", ex);
		}
		//System.out.println("clienthost:"+clienthost);
		return clienthost;
	}
}