package com.fourinone;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Date;

abstract class FttpContractor extends Contractor implements FttpAdapter.FttpAdapterOperate,Cloneable
{
	private URI fli;
	//private long b=-1,t=-1;//
	private long rb=-1,rt=-1,wb=-1,wt=-1;
	private static FttpLocal[] Locals;
	private FileResult res = null;
	private FileResult[] resarr = null;
	
	/*FttpContractor(String fttpPath) throws FttpException{
		//this.fli = fli;
		this(fttpPath, null);
	}*/
	
	FttpContractor(){
		super();
	}
	
	FttpContractor(String fttpPath, String filename) throws FttpException{
		/*System.out.println("FttpContractor fttpPath:"+ObjectBytes.getEscape(fttpPath));
		System.out.println("FttpContractor filename:"+ObjectBytes.getEscape(filename));*/
		this.fli = FttpException.getURI(ObjectBytes.getEscape(fttpPath), ObjectBytes.getEscape(filename));
		//System.out.println("this.fli:"+this.fli);
	}
	
	/*
	FttpContractor(URI fli, long b, long t){
		this.fli = fli;
		this.b = b;
		this.t = t;
	}
	*/
	void setReadArea(long rb, long rt){
		this.rb = rb;
		this.rt = rt;
	}
	
	void setWriteArea(long wb, long wt){
		this.wb = wb;
		this.wt = wt;
	}
	
	/*public WareHouse giveTask(WareHouse inhouse){//throws NoSuchMethodException
		return null;
		//throw new NoSuchMethodException();
	}*/
	
	FttpContractor object() throws FttpException{
		try{
			return (FttpContractor)clone();
		}catch(Throwable tw){
			//tw.printStackTrace();
			throw FttpException.getNewException(tw);
		}
	}
	
	WorkerLocal[] getWaitingWorkersFromService(String workerType, MigrantWorker mw)
	{
		LogUtil.fine("", "", "getWaitingWorkersFromService:"+workerType+",MigrantWorker:"+mw);
		if(wks==null){
			List<String[]> wslist = getWorkersService(workerType);
			List<WorkerLocal> wklist = new ArrayList<WorkerLocal>();
			for(String[] wsinfo:wslist)
				wklist.add(BeanContext.getFttpLocal(wsinfo[0], Integer.parseInt(wsinfo[1]), wsinfo[2]));
			wks=wklist.toArray(new WorkerLocal[wklist.size()]);
		}
		return wks;
	}
	
	FttpLocal getFttpLocal() throws FttpException{
		FttpLocal flf = null;
		
		setFttpLocal();
		
		if(Locals!=null&&Locals.length!=0)
			for(FttpLocal fl:Locals)
				if(fl.getHost().equals(fli.getHost()))
					flf=fl;
		
		if(flf==null)
			throw new FttpException(new NullPointerException("get null from fttp "+fli.getHost()));
		
		return flf;
	}
	
	void setFttpLocal(){
		/*if(Locals==null)
			Locals = getLocals(FttpMigrantWorker.FTTPSN, FttpLocal.class);*/
		setFttpLocal(false);
	}
	
	private void setFttpLocal(boolean flush){
		if(flush||Locals==null)
			Locals = getLocals(FttpMigrantWorker.FTTPSN, FttpLocal.class);
	}
	
	public byte[] readAll() throws FttpException{
		try{
			return getFttpLocal().readByte(fli.getPath(),rb,rt);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public Result<byte[]> tryReadAll(){
		//try{
			//return getFttpLocal().readByteAsyn(fli.getPath(),rb,rt);
		/*}catch(FttpException tw){
			throw FttpException.getNewException(tw);
		}*/
		return tryReadAll(false);
	}
	
	public byte[] readAllSafety() throws FttpException{
		try{
			return getFttpLocal().readByteLocked(fli.getPath(),rb,rt);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public Result<byte[]> tryReadAllSafety(){
		return tryReadAll(true);
	}
	
	private Result<byte[]> tryReadAll(boolean locked){
		try{
			return getFttpLocal().readByteAsyn(fli.getPath(),rb,rt,locked);
		}catch(FttpException fe){
			LogUtil.info("tryReadAll", "exception", fe);
			return FileResult.getExceptionResult();
		}
	}
	
	/*public Result<byte[]>[] readAllBatch(FttpReadAdapter[] fras, boolean locked){
		
	}*/
	
	public int[] readIntAll() throws FttpException{
		try{
			return getFttpLocal().readInt(fli.getPath(),rb,rt);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public int[] readIntAllSafety() throws FttpException{
		try{
			return getFttpLocal().readIntLocked(fli.getPath(),rb,rt);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public Result<int[]> tryIntReadAll(){
		return  tryIntReadAll(false);
	}
		
	public Result<int[]> tryIntReadAllSafety(){
		return  tryIntReadAll(true);
	}
	
	private Result<int[]> tryIntReadAll(boolean locked){
		try{
			return getFttpLocal().readIntAsyn(fli.getPath(),rb,rt,locked);
		}catch(FttpException fe){
			LogUtil.info("tryIntReadAll", "exception", fe);
			return FileResult.getExceptionResult();
		}
	}
	
	public int write(byte[] bytes) throws FttpException
	{
		try{
			return getFttpLocal().writeByte(fli.getPath(),wb,wt,bytes);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public Result<Integer> tryWrite(byte[] bytes)
	{
		//return getFttpLocal().writeByteAsyn(fli.getPath(),wb,wt,bytes);
		return tryWrite(bytes, false);
	}
	
	public int writeSafety(byte[] bytes) throws FttpException
	{
		try{
			return getFttpLocal().writeByteLocked(fli.getPath(),wb,wt,bytes);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//new FttpWriteException
		}
	}
	
	public Result<Integer> tryWriteSafety(byte[] bytes){
		return tryWrite(bytes, true);
	}
	
	private Result<Integer> tryWrite(byte[] bytes, boolean locked){
		try{
			return getFttpLocal().writeByteAsyn(fli.getPath(),wb,wt,bytes,locked);
		}catch(FttpException fe){
			LogUtil.info("tryWrite", "exception", fe);
			return FileResult.getExceptionResult();
		}
	}
	
	public int writeInt(int[] its) throws FttpException{
		try{
			return getFttpLocal().writeInt(fli.getPath(),wb,wt,its);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public int writeIntSafety(int[] its) throws FttpException{
		try{
			return getFttpLocal().writeIntLocked(fli.getPath(),wb,wt,its);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);
		}
	}
	
	public Result<Integer> tryIntWrite(int[] its){
		return tryIntWrite(its, false);
	}
	
	public Result<Integer> tryIntWriteSafety(int[] its){
		return tryIntWrite(its, true);
	}
	
	private Result<Integer> tryIntWrite(int[] its, boolean locked){
		try{
			return getFttpLocal().writeIntAsyn(fli.getPath(),wb,wt,its,locked);
		}catch(FttpException fe){
			LogUtil.info("tryIntWrite", "exception", fe);
			return FileResult.getExceptionResult();
		}
	}
	
	private void acquireChildProperty() throws FttpException{
		try{
			resarr = getFttpLocal().getChildFileMeta(fli.getPath());
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//new FttpPropertyException
		}
	}
	
	void acquireProperty() throws FttpException{
		try{
			res = getFttpLocal().getFileMeta(fli.getPath());
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//new FttpPropertyException
		}
	}
	
	private void acquireProperty(FileResult res){
		this.res = res;
	}
	
	FttpContractor[] getChildProperty() throws FttpException{
		FttpContractor[] fcs = null;
		if(resarr==null)
			acquireChildProperty();
		
		if(resarr!=null&&resarr.length>0){
			//System.out.println("resarr.length"+resarr.length);
			fcs = new FttpContractor[resarr.length];
			for(int i=0;i<resarr.length;i++){
				//System.out.println("this.fli:"+this.fli);
				//System.out.println("resarr[i]:"+resarr[i]);//.getString("getName")
				FttpContractor fc = getContractor(this.fli.toString(), resarr[i].getString("getName"));
				//System.out.println("fc.fli:"+fc.fli);
				fc.acquireProperty(resarr[i]);
				fcs[i]=fc;
			}
		}
		return fcs;
	}
	
	//FileResult[] getChildProperty(){
	//laze acquireChildProperty and return resarr
	//}
	
	public boolean isFile(){
		//System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
		return getBoolValue("isFile");
	}
	
	public boolean exists(){
		return getBoolValue("exists");
	}
	
	public boolean isDirectory(){
		return getBoolValue("isDirectory");
	}
	
	public boolean isHidden(){
		return getBoolValue("isHidden");
	}
		
	public boolean canRead(){
		return getBoolValue("canRead");
	}
	
	public boolean canWrite(){
		return getBoolValue("canWrite");
	}
	
	private boolean getBoolValue(String mn){
		return ((Boolean)res.getObj(mn)).booleanValue();
	}
	
	public String getName(){
		String fname = res.getString("getName");
		return fname.equals("")?res.getString("getPath"):fname;//.replaceAll("/","")
	}
	
	public String getParent(){
		return getFttpPath("getParent");
	}
	
	public String getPath(){
		return getFttpPath("getPath");
	}
	
	private String getFttpPath(String mn){
		/*System.out.println("fli:"+fli);
		System.out.println("res.getString(mn):"+res.getString(mn));
		System.out.println("getfttpPath:"+ObjectBytes.getEscape((new File(res.getString(mn)).toURI().getPath())));*/
		//return res.getString(mn)!=null?FttpException.getFttpURI(fli, ObjectBytes.getEscape(new File(res.getString(mn)).toURI().getPath()), null).toString():null;
		return res.getString(mn)!=null?FttpException.getFttpURI(fli, ObjectBytes.getEscape(res.getString(mn)), null).toString():null;
	}
	
	public String getPathEncode(){
		return ObjectBytes.getUtf8UrlString(getPath());//getUrlString,encodeReplace
	}
	
	public long lastModified(){
		return getLongValue("lastModified");
	}
	
	public Date lastModifiedDate(){
		return new Date(lastModified());
	}
	
	public long length(){
		return isDirectory()?0l:getLongValue("length");
	}
		
	private long getLongValue(String mn){
		return ((Long)res.getObj(mn)).longValue();
	}
	
	public String[] list(){
		return (String[])res.getObj("list");
	}
	
	public String[] listRoots() throws FttpException{
		try{
			return getFttpLocal().getListRoots();
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//new FttpException method name
		}
	}
	
	public String[] fttpRoots(){
		String[] frs = null;
		setFttpLocal(true);
		if(Locals!=null&&Locals.length!=0){
			frs = new String[Locals.length];
			for(int i=0;i<Locals.length;i++)
				frs[i]=Locals[i].getHost();
		}
		return frs;
	}
	
	public File create(boolean isFile) throws FttpException{
		try{
			//System.out.println("fli.getPath():"+fli.getPath());
			return getFttpLocal().create(fli.getPath(), isFile);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//create
		}
	}
	
	public boolean delete() throws FttpException{
		try{
			return getFttpLocal().delete(fli.getPath());
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//delete
		}
	}
	
	public boolean copy(String topath, long every) throws FttpException{
		try{
			return getFttpLocal().copy(fli.getPath(),every,FttpException.getURI(ObjectBytes.getEscape(topath),null));
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//copy
		}
	}
	
	public Result<FttpAdapter> tryCopy(String topath, long every){
		try{
			return getFttpLocal().copyAsyn(fli.getPath(),every,FttpException.getURI(ObjectBytes.getEscape(topath),null));
		}catch(FttpException fe){
			LogUtil.info("tryCopy", "exception", fe);//trycopy
			return FileResult.getExceptionResult();
		}
	}
	
	public boolean rename(String newname) throws FttpException{
		try{
			return getFttpLocal().rename(fli.getPath(),newname);
		}catch(Throwable tw){
			throw FttpException.getNewException(tw);//rename
		}
	}
	
	static FttpContractor getContractor(){
		return new FttpContractor(){
			public WareHouse giveTask(WareHouse inhouse){
				return null;
				//throw new NoSuchMethodException();
			}
		};
	}
	
	static FttpContractor getContractor(String parentPath, String filename) throws FttpException{
		return new FttpContractor(parentPath, filename){
			public WareHouse giveTask(WareHouse inhouse){
				return null;
				//throw new NoSuchMethodException();
			}
		};
	}
}