package com.fourinone;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.io.FileNotFoundException;
import java.io.File;
import java.net.URI;

public class FttpWorkerService extends WorkerService
{
	public FttpWorkerService(MigrantWorker migworker) throws RemoteException
	{
		super(migworker);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="read",policy=DelegatePolicy.Implements)
	public byte[] readByte(String f, long b, long t) throws RemoteException,FttpException{
		return readByte(f, b, t, false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="readLocked",policy=DelegatePolicy.Implements)
	public byte[] readByteLocked(String f, long b, long t) throws RemoteException,FttpException{
		return readByte(f, b, t, true);
	}
	
	private byte[] readByte(String f, long b, long t, boolean l) throws RemoteException,FttpException
	{
		FileAdapter fa = new FileAdapter(f);
		
		byte[] bts = null;
		try{
			if(l)
				bts = (b==-1&&t==-1)?fa.getReader().readAllSafety():fa.getReader(b,t).readAllSafety();
			else
				bts = (b==-1&&t==-1)?fa.getReader().readAll():fa.getReader(b,t).readAll();
		}catch(Exception e){
			fa.close();
			throw FttpException.getNewException(e,fa);
		}
		fa.close();
		return bts;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="readInt",policy=DelegatePolicy.Implements)
	public int[] readInt(String f, long b, long t) throws RemoteException,FttpException{
		return readInt(f,b,t,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="readIntLocked",policy=DelegatePolicy.Implements)
	public int[] readIntLocked(String f, long b, long t) throws RemoteException,FttpException{
		return readInt(f,b,t,true);
	}
	
	private int[] readInt(String f, long b, long t, boolean l) throws RemoteException,FttpException
	{
		FileAdapter fa = new FileAdapter(f);
		
		int[] its = null;
		try{
			if(l)
				its = (b==-1&&t==-1)?fa.getIntReader().readIntAllSafety():fa.getIntReader(b,t).readIntAllSafety();
			else
				its = (b==-1&&t==-1)?fa.getIntReader().readIntAll():fa.getIntReader(b,t).readIntAll();
		}catch(Exception e){
			fa.close();
			throw FttpException.getNewException(e,fa);
		}
		fa.close();
		return its;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="write",policy=DelegatePolicy.Implements)
	public int writeByte(String f, long b, long t, byte[] bs) throws RemoteException,FttpException//synchronized can write diff file
	{
		return writeByte(f,b,t,bs,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="writeLocked",policy=DelegatePolicy.Implements)
	public int writeByteLocked(String f, long b, long t, byte[] bs) throws RemoteException,FttpException
	{
		return writeByte(f,b,t,bs,true);
	}
	
	private int writeByte(String f, long b, long t, byte[] bs, boolean locked) throws FttpException
	{
		int wr = 0;
		FileAdapter fa = new FileAdapter(f);
		try{
			wr = locked?fa.getWriter(b,t).writeSafety(bs):fa.getWriter(b,t).write(bs);
		}catch(Exception e){
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return wr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="writeInt",policy=DelegatePolicy.Implements)
	public int writeInt(String f, long b, long t, int[] its) throws RemoteException,FttpException
	{
		return writeInt(f,b,t,its,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="writeIntLocked",policy=DelegatePolicy.Implements)
	public int writeIntLocked(String f, long b, long t, int[] its) throws RemoteException,FttpException
	{
		return writeInt(f,b,t,its,true);
	}
	
	private int writeInt(String f, long b, long t, int[] its, boolean locked) throws FttpException
	{
		int wr = 0;
		FileAdapter fa = new FileAdapter(f);
		try{
			wr = locked?fa.getIntWriter(b,t).writeIntSafety(its):fa.getIntWriter(b,t).writeInt(its);
		}catch(Exception e){
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return wr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="getFileMeta",policy=DelegatePolicy.Implements)
	public FileResult getFileProperty(String f) throws RemoteException,FttpException
	{
		//System.out.println("f:"+f);
		FileResult fr = new FileResult();
		FileAdapter fa = new FileAdapter(f);
		try{
			fr.setObj("exists", new Boolean(fa.exists()));
			fr.setObj("isFile", new Boolean(fa.isFile()));
			fr.setObj("isDirectory", new Boolean(fa.isDirectory()));
			fr.setObj("isHidden", new Boolean(fa.isHidden()));
			fr.setObj("canRead", new Boolean(fa.canRead()));
			fr.setObj("canWrite", new Boolean(fa.canWrite()));
			fr.setString("getName", fa.getName());
			fr.setString("getParent", fa.getParentFile()!=null?fa.getParentFile().toURI().getPath():null);//fa.getParent()
			fr.setString("getPath", fa.toURI().getPath());//fa.getPath()
			//System.out.println("getPath:"+fa.toURI().getPath());
			fr.setObj("lastModified", new Long(fa.lastModified()));
			fr.setObj("length", new Long(fa.length()));
			fr.setObj("list", f.length()>0?fa.list():getListRoots());
		}catch(Exception e){
			//LogUtil.info("getFileProperty", "exception", e);
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return fr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="getChildFileMeta",policy=DelegatePolicy.Implements)
	public FileResult[] getChildFileProperty(String f) throws RemoteException,FttpException
	{
		//System.out.println(f);
		FileResult[] frs = null;
		File[] farr = f.length()>0?new FileAdapter(f).listFiles():new FileAdapter(f).listRoots();
		
		if(farr!=null&&farr.length>0){
			frs = new FileResult[farr.length];
			for(int i=0;i<farr.length;i++)
				frs[i] = getFileProperty(farr[i].getPath());
		}
		
		return frs;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="listRoots",policy=DelegatePolicy.Implements)
	public String[] getListRoots() throws RemoteException,FttpException
	{
		String[] rts = null;
		FileAdapter fa = new FileAdapter("/");
		try{
			File[] fls = fa.listRoots();
			if(fls!=null&&fls.length>0){
				rts = new String[fls.length];
				for(int i=0;i<fls.length;i++)
					rts[i]=fls[i].getPath();
			}
		}catch(Exception e){
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return rts;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="createFile",policy=DelegatePolicy.Implements)
	public File create(String fp, boolean isFile) throws RemoteException,FttpException{
		File rf = null;
		FileAdapter fa = new FileAdapter(fp);
		try{
			rf = fa.createFile(fa.getPath(),isFile);
		}catch(Exception e){
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return rf;
	}
	
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="deleteFile",policy=DelegatePolicy.Implements)
	public boolean delete(String fp) throws RemoteException,FttpException{
		boolean r=false;
		FileAdapter fa = new FileAdapter(fp);
		try{
			r = fa.delete();
		}catch(Exception e){
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return r;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="copyFile",policy=DelegatePolicy.Implements)
	public boolean copy(String frompath, long every, URI tofttppath) throws RemoteException,FttpException{
		try{
			return ((FttpMigrantWorker)migworker).copy(frompath,every,tofttppath);
		}catch(Exception e){
			throw new FttpException(e);
		}
	}
	
	@Delegate(interfaceName="com.fourinone.FttpWorker",methodName="renameFile",policy=DelegatePolicy.Implements)
	public boolean rename(String fp, String newname) throws RemoteException,FttpException{
		boolean r=false;
		FileAdapter fa = new FileAdapter(fp);
		try{
			File nf = new File(fa.getParent(),newname);
			r = fa.renameTo(nf);
		}catch(Exception e){
			fa.close();
			throw new FttpException(e);
		}
		fa.close();
		return r;
	}
}