var result = "";
var blankStr = "&nbsp;&nbsp;&nbsp;&nbsp;";
var loadingStr = "\u6B63\u5728\u8F7D\u5165\u002E\u002E\u002E";
if($treenum==0){
	if($fttproot&&$fttprootcode)
		result+=demarr($fttproot,$fttprootcode);
}else if($treenum>0&&$fttpchild){
	for(var i=0;i<$fttpchild.length;i++){
		/*println($fttpchild[i].getName());
		println($fttpchild[i].getPath());
		println($fttpchild[i].getPathEncode());*/
		var name = $fttpchild[i].getName();
		var pathcode = $fttpchild[i].getPathEncode();
		var readable = $fttpchild[i].canRead();
		/*var hidd = $fttpchild[i].isHidden();*/
		var fcs = $fttpchild[i].list();
		var size = $fttpchild[i].length();
		var tm = strToDate($fttpchild[i].lastModified());
		result+=$fttpchild[i].isFile()?fem(name,pathcode,size,tm):dem(geteid(i),name,pathcode,readable,fcs,tm);
		/*
		if($fttpchild[i].isFile()){
			result+="<font color=#1F6CBE>"+$fttpchild[i].getName()+"</font>";
	    	result+="<input type=radio name='treeId' value='treeId="+$fttpchild[i].getPathEncode()+"&file=true'><br>";
		}else{
			result+="<a id=parent"+$fttpchild[i].getPathEncode()+" href='javascript:menuClick(parent"+$fttpchild[i].getPathEncode()+")'><b>"+$fttpchild[i].getName()+"</b></a>";
			result+="<input type=radio name='treeId' value='treeId="+$fttpchild[i].getPathEncode()+"'>";
			result+="<br>";
			result+="<div id=child"+$fttpchild[i].getPathEncode()+" style='display:none' loaded='no'>";
			result+=blankStr+loadingStr;
			result+="</div>";
		}*/
	}
}

function fem(n,pc,size,tm){
	return getblank($treenum+1)+"<font color=#1F6CBE>"+n+"</font>"+(size?blankStr+size+"byte":"")+(tm?blankStr+tm:"")+"<input type=radio name='treeId' value='tid="+pc+"&file=true'><br>";
}

function dem(eid,n,pc,rd,fcs,tm){
	return getblank($treenum+1)+"<a id=p"+eid+" href='javascript:"+(fcs?"menuClick(p"+eid+")":"void(0)")+"' tid='"+pc+"' tn='"+($treenum+1)+"' "+(fcs?"":"disabled")+"><b>"+n+"</b></a> "+(tm?blankStr+tm:"")+"<input type=radio name='treeId' value='treeId="+pc+"' "+(fcs?"":"disabled")+"><br><div id=c"+eid+" style='display:none' loaded='no'>"+getblank($treenum+1)+blankStr+loadingStr+"</div>";
}

function demarr(ns,pcs){
	var arrstr="";
	for(var i=0;i<ns.length;i++){
		arrstr+=dem(geteid(i),ns[i],pcs[i],true,ns,null);
	}
	return arrstr;
}

function getblank(treenum){
	var spacestr = "";
	for(var i=0;i<treenum;i++){
		spacestr+=blankStr;
	}
	return spacestr;
}

function geteid(bstr){
	var d = new Date();
	return bstr+d.getHours()+""+d.getMinutes()+""+d.getSeconds()+""+d.getMilliseconds();
}

function strToDate(str){
	var d=new Date();
	d.setFullYear(1970,1,1);
	d.setTime(0);
    d.setMilliseconds(str);
	/*s=d.toLocaleString()*/;
	return d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
}