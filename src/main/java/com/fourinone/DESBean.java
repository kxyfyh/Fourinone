package com.fourinone;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.util.*;

public class DESBean
{
	private SecretKey deskey;
	private Cipher cipher;
	private String algorithm = "DES";
	private String initKeyData = "tianxingjian";

	public DESBean()
	{
    	init();
    }
	
	public void init() 
	{
	    try 
        {
		    /*
		    ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream("secret.key"));
			deskey = (SecretKey)keyIn.readObject();
			keyIn.close();
			*/
			
			byte[] keyData = initKeyData.getBytes();
         	DESKeySpec keySpec = new DESKeySpec(keyData);
         	SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
         	deskey = skf.generateSecret(keySpec);
			
			cipher = Cipher.getInstance(algorithm);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getEncryptor(String str) 
	{
        String encryptorStr = "";
        try
        {
        	//init
        	cipher.init(Cipher.ENCRYPT_MODE, deskey);
        	
        	//Encryptor
            byte[] cipherByte = cipher.doFinal(str.getBytes());
            //turn String
            encryptorStr = byte2hex(cipherByte);
        }
        catch(Exception e)
		{
			e.printStackTrace();
		}
        return encryptorStr;
	}
	
	public String getDecryptor(String str)
	{
		String decryptorStr = "";
        try 
        {
        	//init
        	cipher.init(Cipher.DECRYPT_MODE, deskey);
        	
        	//parse encryptorStr to byte[]
        	byte[] outt = hex2byte(str);
			
			//Encryptor
            byte[] cipherByte = cipher.doFinal(outt);
			//turn String
			decryptorStr = new String(cipherByte);
        }
        catch(Exception e)
		{
			e.printStackTrace();
		}
        return decryptorStr;
	}
	
	public String byte2hex(byte[] b) 
	{
		StringBuffer hs = new StringBuffer("");
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
		 stmp = (Integer.toHexString(b[n] & 0xFF));
		 if (stmp.length() == 1) {
		    hs.append("0" + stmp);
		 } else {
		    hs.append(stmp);
		 }
		
		}
		return hs.toString().toUpperCase();
	}
	
	public byte[] hex2byte(String hex) 
	{
		int len = hex.length();
		if ( (len % 2) != 0) {
		 return null;
		}
		int size = len / 2;
		byte[] b = new byte[size];
		for (int i = 0; i < size; i++) {
		 b[i] = (Integer.decode("0X" + hex.substring(i * 2, i * 2 + 2))).
		         byteValue();
		}
		return b;
	}
	
	public static void main(String[] args)
	{
		try
		{
			DESBean des = new DESBean();
			String s = "\u5929\u884C\u952Eabcd888888";
				System.out.println(s);
			
			String sEnc = des.getEncryptor(s);
				System.out.println(sEnc);
			
			String sDec = des.getDecryptor(sEnc);
				System.out.println(sDec);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}