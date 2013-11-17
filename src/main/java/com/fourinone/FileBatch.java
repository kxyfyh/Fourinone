package com.fourinone;

import java.util.List;

public class FileBatch{
	public Result<byte[]>[] readAllBatch(TryByteReadAdapter[] fras){
		return readAllBatch(fras, false);
	}
	
	public Result<byte[]>[] readAllBatch(TryByteReadAdapter[] fras, boolean locked){
		return readAllBatch(fras, null, locked);
	}
	
	public Result<int[]>[] readIntAllBatch(TryIntReadAdapter[] fras){
		return readIntAllBatch(fras, false);
	}
	
	public Result<int[]>[] readIntAllBatch(TryIntReadAdapter[] fras, boolean locked){
		return readAllBatch(null, fras, locked);
	}
	
	private Result[] readAllBatch(TryByteReadAdapter[] tbrs, TryIntReadAdapter[] tirs, boolean locked){
		Result[] hmarr = new Result[tbrs!=null?tbrs.length:tirs.length];
		for(int i=0,j=0;j<hmarr.length;){
			if(hmarr[i]==null){
				hmarr[i] = tbrs!=null?(locked?tbrs[i].tryReadAllSafety():tbrs[i].tryReadAll()):(locked?tirs[i].tryIntReadAllSafety():tirs[i].tryIntReadAll());
			}
			else if(hmarr[i].isReady()&&hmarr[i].getMark()){
				hmarr[i].setMark(false);
				j++;
			}
			i=i+1==hmarr.length?0:i+1;
		}
		return submit(hmarr);
	}
	
	public Result<Integer>[] writeBatch(TryByteWriteAdapter[] fwas, byte[] bts){
		return writeBatch(fwas,bts,false);
	}
	
	public Result<Integer>[] writeBatch(TryByteWriteAdapter[] fwas, byte[] bts, boolean locked){
		return writeBatch(fwas,null,bts,locked);
	}
	
	public Result<Integer>[] writeBatch(TryByteWriteAdapter[] fwas, List<byte[]> btarr, boolean locked){
		return writeBatch(fwas,btarr,null,locked);
	}
	
	private Result<Integer>[] writeBatch(TryByteWriteAdapter[] fwas, List<byte[]> btarr, byte[] bts, boolean locked){
		return writeBatch(fwas, null, btarr, bts, null, locked);
	}
	
	public Result<Integer>[] writeIntBatch(TryIntWriteAdapter[] fwas, int[] its){
		return writeIntBatch(fwas,its,false);
	}
	
	public Result<Integer>[] writeIntBatch(TryIntWriteAdapter[] fwas, int[] its, boolean locked){
		return writeIntBatch(fwas,null,its,locked);
	}
	
	public Result<Integer>[] writeIntBatch(TryIntWriteAdapter[] fwas, List<int[]> btarr, boolean locked){
		return writeIntBatch(fwas,btarr,null,locked);
	}
	
	private Result<Integer>[] writeIntBatch(TryIntWriteAdapter[] fwas, List<int[]> btarr, int[] its, boolean locked){
		return writeBatch(null, fwas, btarr, null, its, locked);
	}
	
	private Result<Integer>[] writeBatch(TryByteWriteAdapter[] fbws, TryIntWriteAdapter[] fiws, List btarr, byte[] bts, int[] its, boolean locked){
		Result[] hmarr = new Result[fbws!=null?fbws.length:fiws.length];
		for(int i=0,j=0;j<hmarr.length;){
			if(hmarr[i]==null){
				if(fbws!=null){
					byte[] curbts = bts!=null?bts:(byte[])btarr.get(i);
					hmarr[i] = locked?fbws[i].tryWriteSafety(curbts):fbws[i].tryWrite(curbts);//throws fttpexception
				}else{
					int[] curits = its!=null?its:(int[])btarr.get(i);
					hmarr[i] = locked?fiws[i].tryIntWriteSafety(curits):fiws[i].tryIntWrite(curits);
				}
			}
			else if(hmarr[i].isReady()&&hmarr[i].getMark()){
				hmarr[i].setMark(false);
				j++;
			}
			i=i+1==hmarr.length?0:i+1;
		}
		return submit(hmarr);
	}
	
	public Result<Integer>[] readWriteBatch(TryByteReadAdapter[] ras, TryByteWriteAdapter[] fwas){
		return readWriteBatch(ras, false, fwas, false);
	}
	
	public Result<Integer>[] readWriteBatch(TryByteReadAdapter[] ras, boolean readLocked, TryByteWriteAdapter[] fwas, boolean writeLocked){
		return readWriteBatch(ras, null, readLocked, fwas, null, writeLocked);
	}
	
	public Result<Integer>[] readWriteIntBatch(TryIntReadAdapter[] ras, TryIntWriteAdapter[] fwas){
		return readWriteIntBatch(ras, false, fwas, false);
	}
	
	public Result<Integer>[] readWriteIntBatch(TryIntReadAdapter[] ras, boolean readLocked, TryIntWriteAdapter[] fwas, boolean writeLocked){
		return readWriteBatch(null, ras, readLocked, null, fwas, writeLocked);
	}
	
	private Result<Integer>[] readWriteBatch(TryByteReadAdapter[] tbrs, TryIntReadAdapter[] tirs, boolean readLocked, TryByteWriteAdapter[] tbws, TryIntWriteAdapter[] tiws, boolean writeLocked){
		Result[] rdarr = new Result[tbrs!=null?tbrs.length:tirs.length];
		Result[] hmarr = new Result[tbws!=null?tbws.length:tiws.length];
		for(int i=0,j=0;j<hmarr.length;){
			if(hmarr[i]==null){
				if(rdarr[i]==null){
					if(tbrs!=null)
						rdarr[i] = readLocked?tbrs[i].tryReadAllSafety():tbrs[i].tryReadAll();
					else
						rdarr[i] = readLocked?tirs[i].tryIntReadAllSafety():tirs[i].tryIntReadAll();
				}
				else if(rdarr[i].isReady()&&rdarr[i].getMark()){
					if(tbws!=null)
						hmarr[i] = writeLocked?tbws[i].tryWriteSafety((byte[])rdarr[i].getResult()):tbws[i].tryWrite((byte[])rdarr[i].getResult());
					else
						hmarr[i] = writeLocked?tiws[i].tryIntWriteSafety((int[])rdarr[i].getResult()):tiws[i].tryIntWrite((int[])rdarr[i].getResult());
					rdarr[i].setMark(false);
				}
			}else if(hmarr[i].isReady()&&hmarr[i].getMark()){//fr.status = FileResult.EXCEPTION;
				hmarr[i].setMark(false);
				j++;
			}
			i=i+1==hmarr.length?0:i+1;
		}
		return submit(hmarr);
	}
	
	public interface TryReadAdapter extends TryByteReadAdapter,TryIntReadAdapter{}
	
	public interface TryByteReadAdapter{
		public Result<byte[]> tryReadAll();
		public Result<byte[]> tryReadAllSafety();
	}
	
	public interface TryIntReadAdapter{
		public Result<int[]> tryIntReadAll();
		public Result<int[]> tryIntReadAllSafety();
	}
	
	public interface TryWriteAdapter extends TryByteWriteAdapter,TryIntWriteAdapter{}
		
	public interface TryByteWriteAdapter{
		public Result<Integer> tryWrite(byte[] bytes);
		public Result<Integer> tryWriteSafety(byte[] bytes);
	}
	
	public interface TryIntWriteAdapter{
		public Result<Integer> tryIntWrite(int[] its);
		public Result<Integer> tryIntWriteSafety(int[] its);
	}
	
	Result[] submit(Result[] rtarr){
		for(Result rst:rtarr)
			if(rst.getStatus()==WareHouse.EXCEPTION)
				return undo(rtarr);
		return rtarr;
	}
	
	public Result[] undo(Result[] rtarr){
		return rtarr;
	}
}