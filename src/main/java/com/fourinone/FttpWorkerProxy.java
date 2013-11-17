package com.fourinone;

import java.rmi.RemoteException;
import java.io.File;
import java.net.URI;

public class FttpWorkerProxy extends WorkerServiceProxy
{
	private FttpWorker fw;
	private String host;
	//private int port;
	FttpWorkerProxy(String host, int port, String sn)
	{
		super(host, port, sn);
		this.host = host;
		//this.port = port;
		fw = (FttpWorker)wk;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="readByte",policy=DelegatePolicy.Implements)
	public byte[] read(String f, long b, long t) throws Throwable{
		return read(f,b,t,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="readByteAsyn",policy=DelegatePolicy.Implements)
	public FileResult<byte[]> readAsyn(final String f, final long b, final long t, final boolean locked){
		final FileResult<byte[]> fr = new FileResult<byte[]>(false);
		tpe().execute(new Runnable(){
			public void run(){
				try{
					//System.out.println(fr);
					byte[] wh = read(f,b,t,locked);
					if(wh!=null)
						fr.setResult(wh);
					//fr.status = FileResult.READY;
					fr.setReady(FileResult.READY);
				}catch(Throwable e){//Exception
					//System.out.println("doTaskServiceProxy:"+e);
					LogUtil.info("readAsyn", "exception", e);
					//e.printStackTrace();
					//fr.status = FileResult.EXCEPTION;
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="readByteLocked",policy=DelegatePolicy.Implements)
	public byte[] readLocked(String f, long b, long t) throws Throwable{
		return read(f,b,t,true);
	}
	
	private byte[] read(String f, long b, long t, boolean locked) throws Throwable{
		byte[] bts = null;
		try{
			bts = locked?fw.readLocked(f,b,t):fw.read(f,b,t);
			//System.out.println("end fw.read(f,b,t)");
		}catch(Throwable e){
			LogUtil.info("read", "exception", e);
			throw e;
		}
		return bts;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="readInt",policy=DelegatePolicy.Implements)
	public int[] readInt(String f, long b, long t) throws Throwable{
		return readInt(f,b,t,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="readIntLocked",policy=DelegatePolicy.Implements)
	public int[] readIntLocked(String f, long b, long t) throws Throwable{
		return readInt(f,b,t,true);
	}
	
	private int[] readInt(String f, long b, long t, boolean locked) throws Throwable{
		int[] its = null;
		try{
			its = locked?fw.readIntLocked(f,b,t):fw.readInt(f,b,t);
		}catch(Throwable e){
			LogUtil.info("readInt", "exception", e);
			throw e;
		}
		return its;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="readIntAsyn",policy=DelegatePolicy.Implements)
	public FileResult<int[]> readIntAsyn(final String f, final long b, final long t, final boolean locked){
		final FileResult<int[]> fr = new FileResult<int[]>(false);
		tpe().execute(new Runnable(){
			public void run(){
				try{
					int[] wh = readInt(f,b,t,locked);
					if(wh!=null)
						fr.setResult(wh);
					fr.setReady(FileResult.READY);
				}catch(Throwable e){
					LogUtil.info("readIntAsyn", "exception", e);
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="writeByte",policy=DelegatePolicy.Implements)
	public int write(String f, long b, long t, byte[] bs) throws Throwable{
		return write(f,b,t,bs,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="writeByteAsyn",policy=DelegatePolicy.Implements)
	public FileResult<Integer> writeAsyn(final String f, final long b, final long t, final byte[] bs, final boolean locked)
	{
		final FileResult<Integer> fr = new FileResult<Integer>(false);
		tpe().execute(new Runnable(){
			public void run(){
				try{
					int bl = write(f,b,t,bs,locked);
					fr.setResult(new Integer(bl));
					fr.setReady(FileResult.READY);
				}catch(Throwable e){
					LogUtil.info("writeAsyn", "exception", e);
					//fr.status = FileResult.EXCEPTION;
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="writeByteLocked",policy=DelegatePolicy.Implements)
	public int writeLocked(String f, long b, long t, byte[] bs) throws Throwable{
		return write(f,b,t,bs,true);
	}
	
	private int write(String f, long b, long t, byte[] bs, boolean locked) throws Throwable{
		int brs = 0;
		try{
			brs = locked?fw.writeLocked(f,b,t,bs):fw.write(f,b,t,bs);
		}catch(Throwable e){
			LogUtil.info("write", "exception", e);
			throw e;
		}
		return brs;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="writeInt",policy=DelegatePolicy.Implements)
	public int writeInt(String f, long b, long t, int[] its) throws Throwable{
		return writeInt(f,b,t,its,false);
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="writeIntLocked",policy=DelegatePolicy.Implements)
	public int writeIntLocked(String f, long b, long t, int[] its) throws Throwable{
		return writeInt(f,b,t,its,true);
	}
	
	private int writeInt(String f, long b, long t, int[] its, boolean locked) throws Throwable{
		int brs = 0;
		try{
			brs = locked?fw.writeIntLocked(f,b,t,its):fw.writeInt(f,b,t,its);
		}catch(Throwable e){
			LogUtil.info("writeInt", "exception", e);
			throw e;
		}
		return brs;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="writeIntAsyn",policy=DelegatePolicy.Implements)
	public FileResult<Integer> writeIntAsyn(final String f, final long b, final long t, final int[] its, final boolean locked)
	{
		final FileResult<Integer> fr = new FileResult<Integer>(false);
		tpe().execute(new Runnable(){
			public void run(){
				try{
					int bl = writeInt(f,b,t,its,locked);
					fr.setResult(new Integer(bl));
					fr.setReady(FileResult.READY);
				}catch(Throwable e){
					LogUtil.info("writeIntAsyn", "exception", e);
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="getFileMeta",policy=DelegatePolicy.Implements)
	public FileResult getResultProperty(String f) throws Throwable{
		FileResult fres = null;
		try{
			fres = fw.getFileMeta(f);
		}catch(Throwable e){
			LogUtil.info("getResultProperty", "exception", e);
			throw e;
		}
		return fres;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="getChildFileMeta",policy=DelegatePolicy.Implements)
	public FileResult[] getChildResultProperty(String f) throws Throwable{
		FileResult[] res = null;
		try{
			res = fw.getChildFileMeta(f);
		}catch(Throwable e){
			LogUtil.info("getChildResultProperty", "exception", e);
			throw e;
		}
		return res;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="getListRoots",policy=DelegatePolicy.Implements)
	public String[] listRoots() throws Throwable{
		try{
			return fw.listRoots();
		}catch(Throwable e){
			LogUtil.info("listRoots", "exception", e);
			throw e;
		}
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="getHost",policy=DelegatePolicy.Implements)
	public String getHost(){
		return host;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="create",policy=DelegatePolicy.Implements)
	public File createFile(String f, boolean i) throws Throwable{
		try{
			return fw.createFile(f, i);
		}catch(Throwable e){
			LogUtil.info("createFile", "exception", e);
			throw e;
		}
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="delete",policy=DelegatePolicy.Implements)
	public boolean deleteFile(String f) throws Throwable{
		try{
			return fw.deleteFile(f);
		}catch(Throwable e){
			LogUtil.info("deleteFile", "exception", e);
			throw e;
		}
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="copy",policy=DelegatePolicy.Implements)
	public boolean copyFile(String f, long ev, URI t) throws Throwable{
		try{
			/*WareHouse inhouse = new WareHouse();
			inhouse.setString("command","copy");
			inhouse.setString("frompath",f);
			inhouse.setString("topath",t);
			WareHouse wh =((WorkerLocal)fw).doTask(inhouse);
			Boolean b = (Boolean)wh.getObj("result");
			return b.booleanValue();*/
			return fw.copyFile(f,ev,t);
		}catch(Throwable e){
			LogUtil.info("copyFile", "exception", e);
			throw e;
		}
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="copyAsyn",policy=DelegatePolicy.Implements)
	public FileResult<FttpAdapter> copyFileAsyn(final String f, final long ev, final URI t)
	{
		final FileResult<FttpAdapter> fr = new FileResult<FttpAdapter>(false);
		tpe().execute(new Runnable(){
			public void run(){
				try{
					boolean b = copyFile(f, ev, t);
					fr.setResult(b?new FttpAdapter(t.toString()):null);
					fr.setReady(FileResult.READY);
				}catch(Throwable e){
					LogUtil.info("copyFileAsyn", "exception", e);
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	
	@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="rename",policy=DelegatePolicy.Implements)
	public boolean renameFile(String f, String newname) throws Throwable{
		try{
			return fw.renameFile(f, newname);
		}catch(Throwable e){
			LogUtil.info("renameFile", "exception", e);
			throw e;
		}
	}
	
	/*@Delegate(interfaceName="com.fourinone.FttpLocal",methodName="getPort",policy=DelegatePolicy.Implements)
	public int getPort(){
		this.port = port;
	}*/
}