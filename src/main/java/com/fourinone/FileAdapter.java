package com.fourinone;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import com.fourinone.FileBatch.TryByteReadAdapter;
import com.fourinone.FileBatch.TryByteWriteAdapter;
import com.fourinone.FileBatch.TryIntReadAdapter;
import com.fourinone.FileBatch.TryIntWriteAdapter;

public final class FileAdapter extends File
{
	//private File fl = null;
	private RandomAccessFile raf = null;
	private FileChannel fc = null;//tryLock()
	private ByteBuffer mbread=null,mbwrite=null;

	public static long k(long num)
	{
		return 0x400*num;
	}
	
	public static long m(long num)
	{
		return 0x400*k(num);
	}
	
	public static long g(long num)
	{
		return 0x400*m(num);
	}
	
	public FileAdapter(String filePath)
	{
		super(filePath);
		//fl = new File(filePath);
	}
	
	public FileAdapter(String parentPath, String filePath)
	{
		super(parentPath, filePath);
		//fl = parentPath!=null?new File(parentPath, filePath):new File(filePath);
	}

	public static ByteReadParser getByteReadParser(byte[] array){
		FileAdapter fa = new FileAdapter("");
		fa.mbread = MappedByteBuffer.wrap(array);
		return fa.getReader(Long.MIN_VALUE,Long.MAX_VALUE);
	}

	public interface ByteReadParser{
		public byte[] read(int totalnum);
		public byte[] readLine();
		public byte[] read(byte[] split);
		public byte[] readLast(byte[] split);
	}
	
	public interface ByteReadAdapter extends ByteReadParser,TryByteReadAdapter{
		public byte[] readAll();
		public byte[] readAllSafety() throws FileException;
	}

	public interface IntReadAdapter extends TryIntReadAdapter{
		public int[] readIntAll();
		public int[] readIntAllSafety() throws FileException;
		public int[] readInt(int totalnum);
		public int readInt();
		public List<Integer> readListIntAll();
		public List<Integer> readListInt(int totalnum);
	}
	
	public interface ReadAdapter extends ByteReadAdapter,IntReadAdapter{}
	
	private boolean initRead(long beginIndex, long bytesNum)
	{
		if(beginIndex==Long.MIN_VALUE&&bytesNum==Long.MAX_VALUE)
			return true;
		
		try{
			if(raf==null)
			{
				raf = new RandomAccessFile(this, "r");//fl
				fc = raf.getChannel();
			}
			
			long readnum = Math.min(this.length()-beginIndex, bytesNum);//fl
			//if(readnum==0)return false;
			mbread = fc.map(MapMode.READ_ONLY, beginIndex, readnum);
		}catch(Exception e){
			//System.out.println(e);
			LogUtil.info("[FileAdapter]", "[ReadAdapter]", e.getMessage());
			return false;
		}
		return true;
	}	
	
	public interface IntWriteAdapter extends TryIntWriteAdapter{
		public int writeInt(int[] its);
		public int writeIntSafety(int[] its) throws FileException;
		public int writeListInt(List<Integer> ls);
	}
	
	public interface ByteWriteAdapter extends TryByteWriteAdapter{
		public int write(byte[] bytes);
		public int writeSafety(byte[] bytes) throws FileException;
	}
	
	public interface WriteAdapter extends ByteWriteAdapter,IntWriteAdapter{}
	
	private void initWrite()
	{
		try{
			if(raf==null)
			{
				if(!this.exists())//fl
					createFile(this.getPath());//fl
				raf = new RandomAccessFile(this, "rw");//fl
				fc = raf.getChannel();
			}		
		}catch(Exception e){
			//System.out.println(e);
			LogUtil.info("[FileAdapter]", "[WriteAdapter]", e.getMessage());
		}
	}
	
	public ByteReadAdapter getByteReader(){
		return getReader();
	}
	
	public IntReadAdapter getIntReader(){
		return getReader();
	}
	
	public ReadAdapter getReader(){
		return getReader(0, this.length());//byte.length
	}
	
	public ByteReadAdapter getByteReader(long beginIndex, long bytesNum){
		return getReader(beginIndex, bytesNum);
	}
	
	public IntReadAdapter getIntReader(long beginIndex, long intNum){
		return getReader(beginIndex*4, intNum*4);
	}
	
	public ReadAdapter getReader(final long beginIndex, final long bytesNum){
		if(!initRead(beginIndex, bytesNum))
			return null;
		else return new ReadAdapter(){
			public byte[] readAll(){
				//System.out.println((int)Math.min(bytesNum, Integer.MAX_VALUE));
				return read((int)Math.min(bytesNum, Integer.MAX_VALUE));
			}
			
			public byte[] readAllSafety() throws FileException{
				byte[] rbts = null;
				FileLock fl = null;
				try{
					fl = fc.lock(beginIndex,bytesNum,true);
					//System.out.println(Thread.currentThread()+" isShared fl:"+fl.isShared()+"\n");
					//System.out.println(Thread.currentThread()+" overlaps fl:"+fl.overlaps(0,50)+"\n");
					rbts = readAll();
					//while(true);
					//Thread.sleep(20000);
					fl.release();
				}catch(Exception ex){
					throw new FileException(ex);
				}
				return rbts;
			}
			
			public Result<byte[]> tryReadAll(){
				return tryReadAll(false);
			}
			
			public Result<byte[]> tryReadAllSafety(){
				return tryReadAll(true);
			}
			
			private Result<byte[]> tryReadAll(final boolean locked){
				final FileResult<byte[]> fr = new FileResult<byte[]>(false);
				PoolExector.tpe().execute(new Runnable(){
					public void run(){
						try{
							byte[] wh = locked?readAllSafety():readAll();
							if(wh!=null)
								fr.setResult(wh);
							//fr.setReady(true);
							fr.setReady(FileResult.READY);
						}catch(Throwable e){
							LogUtil.info("tryReadAll", "exception", e);
							//fr.status = FileResult.EXCEPTION;
							fr.setReady(FileResult.EXCEPTION);
						}
					}
				});
				return fr;
			}
			
			public byte[] read(int totalnum)
			{
				int readnum = Math.min(mbread.remaining(),totalnum);
				byte[] bt = null;
				try{
					if(readnum>0){
						bt = new byte[readnum];
						mbread.get(bt);
					}
				}catch(Exception e){
					System.out.println(e);
					LogUtil.info("[ReadAdapter]", "[read]", e.getMessage());
				}
				return bt;
			}
			
			public byte[] readLine()
			{
				byte[] bts = new byte[]{0xD,0xA};
				return read(bts);
			}
			
			public byte[] read(byte[] split)
			{
				if(split==null||mbread.remaining()==0)
					return null;
				
				int i=0,p=mbread.position(),n=0;
				while(mbread.hasRemaining()&&i<split.length){
					byte b = mbread.get();
					if(b==split[i]){
						if(i++==0)
							mbread.mark();
					}else{
						if(i>0){
							mbread.reset();
							i=0;
						}
						n++;
					}
				}
				
				if(i<split.length){
					n+=i;
					i=0;
				}
				
				mbread.position(p);
				byte[] rbts = new byte[n];
				mbread.get(rbts);
				mbread.position(mbread.position()+i);
		 		return rbts;
			}
			
			public byte[] readLast(byte[] split){
				byte[] bts = readAll();
				
				if(split==null||bts==null)
					return null;
				
				int i=bts.length-1,j=split.length-1,m=-1;
				while(i>=0&&j>=0){
					if(bts[i--]==split[j]){
						if(j--==split.length-1)
							m=i;
					}else if(m>0){
						i=m-1;
						j=split.length-1;
						m=-1;
					}
				}
				//System.out.println("i:"+(i+1));
				return i>0?Arrays.copyOf(bts, i+1):bts;
			}
			
			public int[] readIntAll(){
				return readInt((int)(bytesNum/4));
			}
			
			public int[] readIntAllSafety() throws FileException{
				int[] rits = null;
				FileLock fl = null;
				try{
					fl = fc.lock(beginIndex,bytesNum,true);
					rits = readIntAll();
					fl.release();
				}catch(Exception ex){
					throw new FileException(ex);
				}
				return rits;
			}
			
			public Result<int[]> tryIntReadAll(){
				return tryIntReadAll(false);
			}
			
			public Result<int[]> tryIntReadAllSafety(){
				return tryIntReadAll(true);
			}
			
			private Result<int[]> tryIntReadAll(final boolean locked){
				final FileResult<int[]> fr = new FileResult<int[]>(false);
				PoolExector.tpe().execute(new Runnable(){
					public void run(){
						try{
							int[] wh = locked?readIntAllSafety():readIntAll();
							if(wh!=null)
								fr.setResult(wh);
							fr.setReady(FileResult.READY);
						}catch(Throwable e){
							LogUtil.info("tryIntReadAll", "exception", e);
							fr.setReady(FileResult.EXCEPTION);
						}
					}
				});
				return fr;
			}
			
			public int[] readInt(int totalnum){
				int readnum = Math.min(mbread.remaining()/4,totalnum);
				int[] its = null;
				if(readnum>0){
					its = new int[readnum];
					for(int i=0;i<its.length;i++)
						its[i]=readInt();
				}
				return its;
			}
			
			public int readInt(){
				return mbread.getInt();
			}
			
			public List<Integer> readListIntAll(){
				return readListInt((int)(bytesNum/4));
			}
			
			public List<Integer> readListInt(int totalnum){
				int size = Math.min(mbread.remaining()/4,totalnum);
				List<Integer> ls = null;
				if(size>0){
					ls = new ArrayList<Integer>(size);
					for(int i=0;i<size;i++)
						ls.add(readInt());
				}
				return ls;
			}
		};
	}	
	
	public File createFile()
	{
		return createFile(this.getPath());
	}
	
	public File createDirectory()
	{
		return createFile(this.getPath(),false);
	}
	
	public File createFile(String fileUrl)
	{
		return createFile(fileUrl,true);
	}
	
	public File createFile(String fileUrl, boolean fileflag)
	{
		File newFile = new File(fileUrl);
		try{
			if(fileflag){
				String parentStr = newFile.getParent();
				//System.out.println(parentStr);
				String fileName = newFile.getName();
				File newDir = parentStr!=null?createFile(parentStr, false):new File("");
				File theFile = new File(newDir,fileName);
				theFile.createNewFile();
				/*if(!theFile.createNewFile())
					System.out.println(fileUrl+" already existed!");*/
			}else{
				if(!newFile.exists())
					newFile.mkdirs();
			}
		}catch(Exception e){
			//System.out.println(e);
			LogUtil.info("[FileAdapter]", "[createFile]", e.getMessage()+":"+fileUrl);
		}
		return 	newFile;
	}
	
	public ByteWriteAdapter getByteWriter(){
		return getWriter();
	}
	
	public IntWriteAdapter getIntWriter(){
		return getWriter();
	}
	
	public WriteAdapter getWriter(){
		return getWriter(-1, -1);
	}
	
	public ByteWriteAdapter getByteWriter(long beginIndex, long bytesNum){
		return getWriter(beginIndex, bytesNum);
	}	
	
	public IntWriteAdapter getIntWriter(long beginIndex, long intNum){
		return getWriter(beginIndex*4, intNum*4);
	}
	
	public WriteAdapter getWriter(final long beginIndex, final long bytesNum){
		initWrite();
		final long filesize = this.length();
		return new WriteAdapter(){
			private long index,num;
			private void initIndexNum(long t){
				index = beginIndex<0?filesize:beginIndex;
				num = bytesNum<0?t:bytesNum;
			}
			
			private void initWriteBuffer(int total) throws IOException{
				//System.out.println("total:"+total);
				//System.out.println("beginIndex:"+beginIndex+",super.length():"+super.length());
				initIndexNum(total);
				//System.out.println("index:"+index+",num:"+num);
				mbwrite = fc.map(MapMode.READ_WRITE, index, num);
				//System.out.println("index:"+index+",num:"+num);
			}
			
			public int write(byte[] bytes)
			{
				int t=0;
				try{
					initWriteBuffer(bytes.length);
					if(bytes.length>num)//mbread.remaining()
						bytes = Arrays.copyOf(bytes, (int)num);
					mbwrite.put(bytes);
					t=bytes.length;
				}catch(Exception e){
					//System.out.println(e);
					LogUtil.info("[WriteAdapter]", "[write]", e.getMessage());
				}
				return t;
			}
			
			/*public int writeSafety(byte[] bytes) throws FileException{
				int t = 0;
				FileLock fl = null;
				try{
					initIndexNum(bytes.length);
					fl = fc.lock(index,num,false);//
					t = write(bytes);//raf.write(1);
					fl.release();//
				}catch(Exception ex){
					throw new FileException(ex);
				}
				return t;
			}*/
			
			public int writeSafety(byte[] bytes) throws FileException{
				return writeSafety(bytes, null);
			}
			
			private int writeSafety(byte[] bytes, int[] its) throws FileException{
				int t = 0;
				FileLock fl = null;
				try{
					if(bytes!=null)
						initIndexNum(bytes.length);
					else
						initIndexNum(its.length*4);
					fl = fc.lock(index,num,false);
					t = bytes!=null?write(bytes):writeInt(its);
					fl.release();
				}catch(Exception ex){
					throw new FileException(ex);
				}
				return t;
			}
			
			private int write(byte[] bytes, int[] its){
				return bytes!=null?write(bytes):writeInt(its);
			}
			
			public Result<Integer> tryWrite(byte[] bytes){
				return tryWrite(bytes, null, false);
			}
			
			public Result<Integer> tryWriteSafety(byte[] bytes){
				return tryWrite(bytes, null, true);
			}
			
			/*private Result<Integer> tryWrite(final byte[] bytes, final boolean locked)
			{
				final FileResult<Integer> fr = new FileResult<Integer>(false);
				PoolExector.tpe().execute(new Runnable(){
					public void run(){
						try{
							int bl = locked?writeSafety(bytes):write(bytes);
							fr.setResult(new Integer(bl));
							//fr.setReady(true);
							fr.setReady(FileResult.READY);
						}catch(Throwable e){
							LogUtil.info("tryWrite", "exception", e);
							//fr.status = FileResult.EXCEPTION;
							fr.setReady(FileResult.EXCEPTION);
						}
					}
				});
				return fr;
			}*/
			
			private Result<Integer> tryWrite(final byte[] bytes, final int[] its, final boolean locked)
			{
				final FileResult<Integer> fr = new FileResult<Integer>(false);
				PoolExector.tpe().execute(new Runnable(){
					public void run(){
						try{
							int bl = locked?writeSafety(bytes, its):write(bytes, its);
							fr.setResult(new Integer(bl));
							//fr.setReady(true);
							fr.setReady(FileResult.READY);
						}catch(Throwable e){
							LogUtil.info("tryWrite", "exception", e);
							//fr.status = FileResult.EXCEPTION;
							fr.setReady(FileResult.EXCEPTION);
						}
					}
				});
				return fr;
			}
			
			public int writeInt(int[] its)
			{
				int i=0;
				try{
					initWriteBuffer(its.length*4);
					//System.out.println(its.length);
					int wt = Math.min((int)(num/4),its.length);
					for(;i<wt;i++)
						mbwrite.putInt(its[i]);
				}catch(Exception e){
					//System.out.println(e);
					LogUtil.info("[WriteAdapter]", "[writeInt]", e.getMessage());
				}
				return i;
			}
			
			/*public int writeIntSafety(int[] its) throws FileException
			{
				int t = 0;
				FileLock fl = null;
				try{
					initIndexNum(its.length*4);
					fl = fc.lock(index,num,false);
					t = writeInt(its);
					fl.release();
				}catch(Exception ex){
					throw new FileException(ex);
				}
				return t;
			}*/
			
			public int writeIntSafety(int[] its) throws FileException{
				return writeSafety(null, its);
			}
			
			public Result<Integer> tryIntWrite(int[] its){
				return tryWrite(null, its, false);
			}
			
			public Result<Integer> tryIntWriteSafety(int[] its){
				return tryWrite(null, its, true);
			}
			
			/*private Result<Integer> tryIntWrite(final int[] its, final boolean locked)
			{
				final FileResult<Integer> fr = new FileResult<Integer>(false);
				PoolExector.tpe().execute(new Runnable(){
					public void run(){
						try{
							int bl = locked?writeIntSafety(its):writeInt(its);
							fr.setResult(new Integer(bl));
							fr.setReady(FileResult.READY);
						}catch(Throwable e){
							LogUtil.info("tryIntWrite", "exception", e);
							fr.setReady(FileResult.EXCEPTION);
						}
					}
				});
				return fr;
			}*/
			
			public int writeListInt(List<Integer> ls)
			{
				int i=0;
				try{
					initWriteBuffer(ls.size()*4);
					int wt = Math.min((int)(num/4),ls.size());
					for(;i<wt;i++)
						mbwrite.putInt(ls.get(i));
				}catch(Exception e){
					//System.out.println(e);
					LogUtil.info("[WriteAdapter]", "[writeListInt]", e.getMessage());
				}
				return i;
			}
		};
	}
	
	/*public boolean exists()
	{
		return fl.exists();
	}
	
	public boolean isFile()
	{
		return fl.isFile();
	}
	
	public String getPath()
	{
		return fl.getPath();
	}
	
	
	public boolean delete()
	{
		close();
		boolean b = false;
		try{
			b = fl.delete();
		}catch(Exception e){
			System.out.println(e);
		}
		return b;
	}*/
	
	public int copyTo(String toFilePath){
		return copyTo(toFilePath, FileAdapter.m(8));
	}
	
	public int copyTo(String toFilePath, long every){
		int c=0;
		FileAdapter fa = new FileAdapter(toFilePath);
		fa.createFile();
		byte[] bts = null;
		long begin=0;
		while((bts=this.getReader(begin, every).readAll())!=null){
			c+=fa.getWriter().write(bts);
			begin+=bts.length;
		}
		fa.close();
		//System.out.println("copy length:"+c);
		return c;
	}
	
	public Result<Integer> tryCopyTo(String toFilePath){
		return tryCopyTo(toFilePath, FileAdapter.m(8));
	}
	
	public Result<Integer> tryCopyTo(final String toFilePath, final long every)
	{
		final FileResult<Integer> fr = new FileResult<Integer>(false);
		PoolExector.tpe().execute(new Runnable(){
			public void run(){
				try{
					int bl = copyTo(toFilePath, every);
					fr.setResult(new Integer(bl));
					fr.setReady(FileResult.READY);
				}catch(Throwable e){
					LogUtil.info("tryCopyFile", "exception", e);
					fr.setReady(FileResult.EXCEPTION);
				}
			}
		});
		return fr;
	}
	
	public final void closeBuffer(final Buffer buffer){
		if(null==buffer)
			return;
		AccessController.doPrivileged(new PrivilegedAction<Object>(){
			public Object run() {
				try {
					Method cleanerMethod = buffer.getClass().getMethod("cleaner");
					if (null==cleanerMethod)
						return null;
					cleanerMethod.setAccessible(true);
					Object cleanerObj = cleanerMethod.invoke(buffer);
					if(null==cleanerObj)
						return null;
					Method cleanMethod = cleanerObj.getClass().getMethod("clean");
					if (null==cleanMethod)
						return null;
					cleanMethod.invoke(cleanerObj);
				}catch(Throwable e){}
				return null;
			}
		});
	}
	
	public void closeExit(){
		PoolExector.close();
		close();
	}
	
	public void close()
	{
		try{
			if(fc!=null)fc.close();
			if(raf!=null)raf.close();
			closeBuffer(mbread);
			closeBuffer(mbwrite);
		}catch(Exception e){
			//System.out.println(e);
			LogUtil.info("[FileAdapter]", "[close]", e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		//byte[] bbb = new byte[1024*1024*256];
		/*StringBuilder wordlist = new StringBuilder();
		//for(int j=0;j<16*4;j++)
			wordlist.append("ccccc world good dddfg word googl booy aaa dddgggd sfdafs addcc ");
		
		FileAdapter fa = new FileAdapter("D:\\demo\\parallel\\a\\six.txt");
		WriteAdapter wa = fa.getWriter();//64,64
		//for(int i=0;i<5;i++)
			wa.write(wordlist.toString().getBytes());
		fa.close();*/	
		//byte[] bts = fa.readAll();
		//System.out.println(new String(bts));
		
		
		
		//System.out.println(Integer.toHexString(1024));
		/*System.out.println(Integer.toHexString(10));
		byte[] bb = new byte[]{0xD,0XA};
		System.out.println(bb[1]);*/
		
		/*FileAdapter f = new FileAdapter("D:\\demo\\parallel\\a\\five.txt");
		byte[] bs = f.getReader().readAll();
		for(byte b:bs)
			System.out.print(b+",");
		System.out.println("");*/
		
		/*FileAdapter fa = new FileAdapter("D:\\demo\\parallel\\a\\five.txt");//FileAdapter.m(8)
		ReadAdapter ra = fa.getReader();
		while(true)
		{
			byte[] bts = ra.readUntil("fd".getBytes());//ra.readLine();
			if(bts!=null){
				System.out.println("bts.length:"+bts.length);
				System.out.println(new String(bts));
			}else break;
		}*/
		
		
		/*FileAdapter fa = new FileAdapter("D:\\demo\\parallel\\a\\five.txt");
		byte[] bts = fa.getReader().read(70);
		//byte[] bts = fa.read(5);
		System.out.println(bts);
		bts = fa.readLine();
		System.out.println(new String(bts));*/
		/*FileAdapter fa = new FileAdapter("D:\\demo\\parallel\\a\\five.txt");
		ReadAdapter ra = fa.getReader(0,10);
		byte[] bts = ra.read(10);
		System.out.println(new String(bts));
		ra = fa.getReader(10,10);
		bts = ra.read(10);
		System.out.println(new String(bts));
		ra = fa.getReader(20,10);
		bts = ra.read(10);
		System.out.println(new String(bts));
		fa.close();*/
		//for(byte b:bts)
		//	System.out.print(b+",");
		//fa = new FileAdapter("D:\\demo\\parallel\\a\\ten.txt", FileAdapter.m(8), FileAdapter.m(8));
		//bts = fa.read((int)FileAdapter.m(8));
		//System.out.println(bts.length);
		/*for(byte b:bts)
			System.out.print(b+",");*/
		//fa.close();
		
		/*FileAdapter fa = new FileAdapter("D:\\demo\\parallel\\a\\ten5.txt");
		ReadAdapter ra = null;
		long begin = (new java.util.Date()).getTime();
		for(int i=0;i<128;i++)
		{
			ra = fa.getReader(i*FileAdapter.m(8), FileAdapter.m(8));
			//System.out.print(fa.readAll().length+",");
			ra.readAll();
		}
		long end = (new java.util.Date()).getTime();
		System.out.println("time:"+(end-begin)/1000+"s");
		fa.close();*/
		
		/*FileAdapter fa = new FileAdapter("D:\\demo\\parallel\\a\\five.txt");
		ReadAdapter ra = fa.getReader();
		byte[] bts = ra.read(5);
		System.out.println(new String(bts));*/
	}
}