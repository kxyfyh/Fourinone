package com.fourinone;

import java.io.File;
import java.net.URI;

public interface FttpLocal
{
	public byte[] readByte(String f, long b, long t) throws Throwable;//FttpException
	public FileResult<byte[]> readByteAsyn(String f, long b, long t, boolean locked);
	public byte[] readByteLocked(String f, long b, long t) throws Throwable;
	public int[] readInt(String f, long b, long t) throws Throwable;
	public FileResult<int[]> readIntAsyn(String f, long b, long t, boolean locked);
	public int[] readIntLocked(String f, long b, long t) throws Throwable;
	public int writeByte(String f, long b, long t, byte[] bs) throws Throwable;
	public int writeInt(String f, long b, long t, int[] its) throws Throwable;
	public FileResult<Integer> writeIntAsyn(String f, long b, long t, int[] its, boolean locked);
	public int writeIntLocked(String f, long b, long t, int[] its) throws Throwable;
	public FileResult<Integer> writeByteAsyn(String f, long b, long t, byte[] bs, boolean locked);
	public int writeByteLocked(String f, long b, long t, byte[] bs) throws Throwable;
	public FileResult getFileMeta(String f) throws Throwable;
	public FileResult[] getChildFileMeta(String f) throws Throwable;
	public String[] getListRoots() throws Throwable;
	public String getHost();
	public File create(String f, boolean i) throws Throwable;
	public boolean delete(String f) throws Throwable;
	public boolean copy(String f, long e, URI t) throws Throwable;
	public FileResult<FttpAdapter> copyAsyn(String f, long e, URI t);
	public boolean rename(String f, String newname) throws Throwable;
}