package com.fourinone;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import com.fourinone.FileBatch.TryByteReadAdapter;
import com.fourinone.FileBatch.TryByteWriteAdapter;
import com.fourinone.FileBatch.TryIntReadAdapter;
import com.fourinone.FileBatch.TryIntWriteAdapter;
import com.fourinone.FileAdapter;

public final class FttpAdapter
{
	//private URI fl = null;
	private FttpContractor fc;
	
	public FttpAdapter(String fttpPath) throws FttpException{
		//fl = FttpException.getURI(fttpPath);
		//fc = FttpContractor.getContractor(fttpPath);
		this(fttpPath, null);
	}
	
	public FttpAdapter(String fttpPath, String filename) throws FttpException{
		//System.out.println("fttpPath:"+fttpPath);
		fc = FttpContractor.getContractor(fttpPath, filename);
		//System.out.println("fc:"+fc);
	}
	
	public interface ByteFttpReadAdapter extends TryByteReadAdapter{
		public byte[] readAll() throws FttpException;//lockflag?setLock(true)
		public byte[] readAllSafety() throws FttpException;
	}
	
	public interface IntFttpReadAdapter extends TryIntReadAdapter{
		public int[] readIntAll() throws FttpException;
		public int[] readIntAllSafety() throws FttpException;
	}
	
	public interface FttpReadAdapter extends ByteFttpReadAdapter,IntFttpReadAdapter{}
	
	public interface IntFttpWriteAdapter extends TryIntWriteAdapter{
		public int writeInt(int[] its) throws FttpException;
		public int writeIntSafety(int[] its) throws FttpException;
		/*public void writeListInt(List<Integer> ls) throws FttpException;*/
	}
	
	public interface ByteFttpWriteAdapter extends TryByteWriteAdapter{
		public int write(byte[] bytes) throws FttpException;
		public int writeSafety(byte[] bytes) throws FttpException;
	}
	
	public interface FttpWriteAdapter extends ByteFttpWriteAdapter,IntFttpWriteAdapter{}
	
	public interface FileProperty{
		public boolean exists();
		public boolean isFile();
		public boolean isDirectory();
		public boolean isHidden();
		public boolean canRead();
		public boolean canWrite();
		public String getName();
		public String getParent();
		public String getPath();
		public long lastModified();
		public Date lastModifiedDate();
		public long length();
		public String[] list();
		public String getPathEncode();
	}
	
	public interface FttpAdapterOperate extends FttpReadAdapter,FttpWriteAdapter,FileProperty{}
	
	public FttpReadAdapter getFttpReader() throws FttpException{//param lock
		//return new FttpContractor(fl);//.getFttpReadAdapter(f);
		return fc;//fc.object();
	}
	
	public ByteFttpReadAdapter getByteFttpReader() throws FttpException{
		return getFttpReader();
	}
	
	public ByteFttpReadAdapter getByteFttpReader(long beginIndex, long bytesNum) throws FttpException{
		return getFttpReader(beginIndex, bytesNum);
	}
	
	public IntFttpReadAdapter getIntFttpReader() throws FttpException{
		return getFttpReader();
	}
	
	public IntFttpReadAdapter getIntFttpReader(long beginIndex, long intNum) throws FttpException{
		return getFttpReader(beginIndex, intNum);
	}
	
	public FttpReadAdapter getFttpReader(long beginIndex, long bytesNum) throws FttpException{
		//return new FttpContractor(fl,beginIndex,bytesNum);
		/*FttpContractor fcread = fc.object();
		fcread.setReadArea(beginIndex, bytesNum);
		return fcread;*/
		fc.setReadArea(beginIndex, bytesNum);
		return fc;
	}
	
	public ByteFttpWriteAdapter getByteFttpWriter() throws FttpException{
		return getFttpWriter();
	}
	
	public IntFttpWriteAdapter getIntFttpWriter() throws FttpException{
		return getFttpWriter();
	}
	
	public ByteFttpWriteAdapter getByteFttpWriter(long beginIndex, long bytesNum) throws FttpException{
		return getFttpWriter(beginIndex, bytesNum);
	}
	
	public IntFttpWriteAdapter getIntFttpWriter(long beginIndex, long intNum) throws FttpException{
		return getFttpWriter(beginIndex, intNum);
	}
	
	public FttpWriteAdapter getFttpWriter() throws FttpException{
		return fc;//fc.object();//getFttpWriter(-1, -1);//lock
	}
	
	
	public FttpWriteAdapter getFttpWriter(long beginIndex, long bytesNum) throws FttpException{
		//return new FttpContractor(fl,beginIndex,bytesNum);
		/*FttpContractor fcwrite = fc.object();
		fcwrite.setWriteArea(beginIndex, bytesNum);
		return fcwrite;*/
		fc.setWriteArea(beginIndex, bytesNum);
		return fc;
	}
	
	public FileProperty getProperty() throws FttpException{
		fc.acquireProperty();
		return fc;
	}
	
	public FileProperty[] getChildProperty() throws FttpException{
		return fc.getChildProperty();
	}
	
	/*public FileProperty[] getChildProperty() throws FttpException{
		FileResult[] frarr = fc.getChildProperty();
		FttpContractor[] fcs = null;
		if(frarr!=null&&frarr.length>0){
			fcs = new FttpContractor[frarr.length];
			for(int i=0;i<frarr.length;i++){
				FttpContractor fc = getContractor();
				fc.acquireProperty(frarr[i]);
				fcs[i]=fc;
			}
		}
		return fcs;
	}*/
	
	public String[] listRoots() throws FttpException{
		return fc.listRoots();
	}
	
	public static String[] fttpRoots(){
		return FttpContractor.getContractor().fttpRoots();
	}
	
	public static String[] fttpRootsPath(String[] roots){
		String[] rootspath = null;
		if(roots!=null){
			rootspath = new String[roots.length];
			for(int i=0;i<roots.length;i++)
				rootspath[i]="fttp://"+roots[i];
		}
		return rootspath;
	}
	
	public static String[] fttpRootsPathEncode(String[] roots){
		String[] rootspath = fttpRootsPath(roots);
		if(rootspath!=null){
			for(int i=0;i<rootspath.length;i++)
				rootspath[i]=ObjectBytes.getUtf8UrlString(rootspath[i]);//getUrlString encodeReplace
		}
		return rootspath;
	}
	
	public FttpAdapter createFile() throws FttpException{
		fc.create(true);
		return this;
	}
	
	public FttpAdapter createDirectory() throws FttpException{
		fc.create(false);
		return this;
	}
	
	public boolean delete() throws FttpException{
		return fc.delete();
	}
	
	public FttpAdapter copyTo(String topath)throws FttpException{
		return copyTo(topath,FileAdapter.m(1));
	}
	
	public FttpAdapter copyTo(String topath, long every)throws FttpException{
		return fc.copy(topath,every)?new FttpAdapter(topath):null;//tryCopy
	}
	
	public Result<FttpAdapter> tryCopyTo(String topath){
		return tryCopyTo(topath,FileAdapter.m(1));
	}
	
	public Result<FttpAdapter> tryCopyTo(String topath, long every){
		return fc.tryCopy(topath,every);//tryCopy
	}
	
	public FttpAdapter rename(String newname)throws FttpException{
		return fc.rename(newname)?new FttpAdapter(this.getProperty().getParent(),newname):null;
	}
	
	//close() close pool
	public void close(){
		fc = null;
	}
	
	public void closeExit(){
		fc.exit();
		close();
	}
	
	public static void main(String[] args)
	{
	}
}