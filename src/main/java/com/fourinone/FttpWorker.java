package com.fourinone;

import java.rmi.RemoteException;
import java.io.File;
import java.net.URI;

public interface FttpWorker extends ParkActive{
	public byte[] read(String f, long b, long t) throws RemoteException,FttpException;
	public byte[] readLocked(String f, long b, long t) throws RemoteException,FttpException;
	public int[] readInt(String f, long b, long t) throws RemoteException,FttpException;
	public int[] readIntLocked(String f, long b, long t) throws RemoteException,FttpException;
	public int write(String f, long b, long t, byte[] bs) throws RemoteException,FttpException;
	public int writeLocked(String f, long b, long t, byte[] bs) throws RemoteException,FttpException;
	public int writeInt(String f, long b, long t, int[] its) throws RemoteException,FttpException;
	public int writeIntLocked(String f, long b, long t, int[] its) throws RemoteException,FttpException;
	public FileResult getFileMeta(String f) throws RemoteException,FttpException;
	public FileResult[] getChildFileMeta(String f) throws RemoteException,FttpException;
	public String[] listRoots() throws RemoteException,FttpException;
	public File createFile(String f, boolean isFile) throws RemoteException,FttpException;
	public boolean deleteFile(String f) throws RemoteException,FttpException;
	public boolean copyFile(String f, long e, URI t) throws RemoteException,FttpException;
	public boolean renameFile(String f, String n) throws RemoteException,FttpException;
}