package com.fourinone;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.FileNotFoundException;
import java.io.File;

public class FttpException extends FileException {
	public FttpException(){
		super();
	}
	
	public FttpException(String msg){
		super(msg);
	}

	public FttpException(Throwable cause){
		super(cause);
	}

	public FttpException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	static FttpException getNewException(Throwable tw){
		return tw instanceof FttpException?(FttpException)tw:new FttpException();
	}
	
	static FttpException getNewException(Throwable tw, FileAdapter fa){
		return (!fa.exists()||!fa.isFile())?new FttpException(new FileNotFoundException()):new FttpException(tw);
	}
	
	static URI getURI(String fttpPath, String filename)throws FttpException{
		URI fl = null;
		try{
			fl = new URI(fttpPath);
			if(!fl.getScheme().toLowerCase().equals("fttp"))
				throw new FttpException("Illegal protocol character in "+fttpPath);

			if(filename!=null)
				fl = getFttpURI(fl, fl.getPath(), filename);
				
		}catch(URISyntaxException uie){
			throw new FttpException(uie.getMessage());
		}
		return fl;
	}
	
	static URI getFttpURI(URI ui, String filePath, String filename){
		/*System.out.println("ui:"+ui);
		System.out.println("filePath:"+filePath);
		System.out.println("filename:"+filename);
		System.out.println("new File(filePath,filename):"+new File(filePath));
		System.out.println("new File(filePath,filename):"+(new File(filePath)).toURI().getPath());*/
		//return ui.resolve(filePath!=null&&filePath.length()!=0?((filename==null?new File(filePath):new File(filePath,filename)).toURI().getPath()):"");
		return ui.resolve(filePath!=null&&filePath.length()!=0?(filename==null?filePath:filePath+(filePath.endsWith("\u002F")?"":"\u002F")+filename):"");//(new File(filePath,filename)).toURI().getPath()
	}
}