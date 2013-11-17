<script>
	function listWith(url){
		location = url;
	}
	function editWith(url){
		hiddenframe.location=url;
	}
	function editWithSelect(url){
		var tmp = getEditSelValue(url);
		if(tmp == "")
			return;
		else
		{
			if (url.indexOf("?")>0)
				url+="&"+tmp;
			else
				url+="?"+tmp;
		}
		
		editWith(url);
	}
	function popWindowWithSelect(url)
	{
		var tmp = getEditSelValue(url);
		if(tmp == "")
			return;
		else
		{
			if (url.indexOf("?")>0)
				url+="&"+tmp;
			else
				url+="?"+tmp;
		}
		
		popWindow(url);
	}
	function getEditSelValue(url)
	{
		var num=0;
		var selValue="";
		for(var i=0;i<document.MyForm.elements.length;i++)
	    {
			var e = document.MyForm.elements[i];
			if(e.type=="radio"&&e.checked == true)
			{
				num++;
				
				if(e.value.indexOf("isRoot=true")>=0)
				{
					alert("\u4E0D\u80FD\u7F16\u8F91\u548C\u5220\u9664\u6839\u76EE\u5F55\uFF01");
					return "";
				}
		      	
		      	if(url.indexOf("actionType=1")>0||url.indexOf("actionType=2")>0)
              	{
					if(e.value.indexOf("file")<0)
					{
						var dirUrl = prompt("enter dir or file name","");
						if(dirUrl==null||dirUrl=="")
							return "";
						
						var fileUrlStr = "fileUrl="+dirUrl;
						selValue += e.value + "&" + fileUrlStr;
					}
					else
					{
						alert("please select a folder!");
						return "";
					}				
                }
                else if(url.indexOf("actionType=3")>0||url.indexOf("actionType=4")>0)
                {
                	var dirUrl = prompt("enter dir or file name","");
					if(dirUrl==null||dirUrl=="")
						return "";
					
					var fileUrlStr = "fileUrl="+dirUrl;
					selValue += e.value + "&" + fileUrlStr;
                }
                else if(url.indexOf("actionType=5")>0)
                {
                	if(confirm("delete?"))
                		selValue += e.value;
                }
                else if(url.indexOf("actionType=6")>0)
                {
                	if(e.value.indexOf("file")<0)
                	{
                		alert("please select a file!");
						return "";
                	}	
                	else
                		selValue += e.value;
                }
                else if(url.indexOf("actionType=7")>0)
                {
                	if(e.value.indexOf("file")<0)
						selValue += e.value;
					else
					{
						alert("please select a folder!");
						return "";
					}
                }
                else if(url.indexOf("actionType=8")>0)
                	selValue += e.value;
                else if(url.indexOf("actionType=9")>0)
                	selValue += e.value;
                else if(url.indexOf("actionType=0")>0)
                	selValue += e.value;   
		    }
	    }
	    if(num==0)
	    {
	    	alert("\u8BF7\u9009\u62E9\u4E00\u4E2A\u8282\u70B9\uFF01");
	    	return "";
	    }
	    else if(num==1)
	    {
	    	return selValue;
	   	}
	   	else if(num>1)
	   	{
	   		alert("\u8BF7\u53EA\u9009\u62E9\u4E00\u4E2A\u8282\u70B9\uFF01");
	    	return "";
	   	}
	}
	function exeAction()
	{
		if(document.MyForm.actionName.options.length>0)
			eval(document.MyForm.actionName.options[document.MyForm.actionName.selectedIndex].value);
	}
	function popWindow(urlto,windowName)
	{
		var  strFeatures = "scrollbars=yes,resizable=yes,status=yes,width=650,height=400";
		window.open(urlto, windowName, strFeatures);
	}
</script>
<html>
<style>
BODY {
	MARGIN-TOP: 0px; FONT-SIZE: 9pt; COLOR: #000000; LINE-HEIGHT: 160%; FONT-FAMILY: Arial;background-color:#F0FFF9;
	scrollbar-face-color:#007325; scrollbar-shadow-color:#E2FBC4; scrollbar-highlight-color:#F0FFF9; scrollbar-3dlight-color:#a4b6d7; scrollbar-darkshadow-color:#a4b6d7; scrollbar-track-color:#F0FFF9; scrollbar-arrow-color:#ffffff
}
TD {
	MARGIN-TOP: 0px; FONT-SIZE: 9pt; COLOR: #000000; LINE-HEIGHT: 160%; FONT-FAMILY: Arial
}
A:link {
	COLOR: #005700; FONT-FAMILY: Arial; TEXT-DECORATION: none
}
A:visited {
	COLOR: #074101; FONT-FAMILY: Arial; TEXT-DECORATION: none
}
A:hover {
	COLOR: #ff0000; FONT-FAMILY: Arial; TEXT-DECORATION: underline
}
A:active {
	COLOR: #c0c0c0; FONT-FAMILY: Arial
}

.tdbgcolor{
	background-color:#54C600;
}

.calendarbgcolor{
	background-color:#D9FFD9;
}

.searchtdbgcolor{
	background-color:#33CC00;
}

.searchtdbgcolorMinus{
	background-color:#00B500;
}

.searchTr{
	background-color:#D8FBCC;
	display:none;	
}

.searchTd{
	color:#007325;font-weight:bolder;
}

.searchTrTitlePlus{
	background-color:#DAFAD6;
	cursor:hand;
}

.searchTrTitleMinus{
	background-color:#A5F786;
	cursor:hand;
}

.tdlist{
	color:#007325;
}

.tdlistbreak{
	color:#007325;word-break:break-all;
}

.tdfgcolor{
	background-color:#F0FFF9;
}

.tdfgcoloranother{
	background-color:#F0FFF9;
}

.tdfieldcolor{
	FONT-WEIGHT: bold;
    background-color: #B6F4B0;
}
.trmouseovercolor{
	background-color: #E2FBC4;
}

.textedit {
	BORDER-RIGHT: #007325 1px solid; BORDER-TOP: #007325 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #007325 1px solid;  BORDER-BOTTOM: #007325 1px solid; background-color:#F0FFF9;
	color:#007325;
}
.searchtextedit {
	BORDER-RIGHT: #007325 1px solid; BORDER-TOP: #007325 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #007325 1px solid;  BORDER-BOTTOM: #007325 1px solid; background-color:#E8FFE8;
	color:#007325;WIDTH: 200px;
}
.textlist{
	BORDER-RIGHT: #006666 1px solid; BORDER-TOP: #006666 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #006666 1px solid;  BORDER-BOTTOM: #006666 1px solid; WIDTH: 50px; 
	background-color:#F0FFF9;color:#006666;
}
.searchtexteditdate {
	BORDER-RIGHT: #007325 1px solid; BORDER-TOP: #007325 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #007325 1px solid;  BORDER-BOTTOM: #007325 1px solid; background-color:#E8FFE8;
	color:#007325;WIDTH: 140px;
}

.textareaedit {
	BORDER-RIGHT: #c0c0c0 1px solid; BORDER-TOP: #c0c0c0 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #c0c0c0 1px solid; WIDTH: 300px; BORDER-BOTTOM: #c0c0c0 1px solid;background-color:#F0FFF9;
	color:#006666;
}
.button {
	BORDER-RIGHT: #007325 1px solid; BORDER-TOP: #007325 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #007325 1px solid; BORDER-BOTTOM: #007325 1px solid; BACKGROUND-COLOR: #D8FBCC;
}
.wfbutton{
	BORDER-RIGHT: #1F6CBE 1px solid; BORDER-TOP: #1F6CBE 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #1F6CBE 1px solid; BORDER-BOTTOM: #1F6CBE 1px solid; BACKGROUND-COLOR: #e6f3ff;
}
.searchbutton {
	BORDER-RIGHT: #007325 1px solid; BORDER-TOP: #007325 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #007325 1px solid; BORDER-BOTTOM: #007325 1px solid; BACKGROUND-COLOR: #E8FDF0;
	color:#007325;
}
.select {
	BORDER-RIGHT: #c0c0c0 1px solid; BORDER-TOP: #c0c0c0 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #c0c0c0 1px solid; BORDER-BOTTOM: #c0c0c0 1px solid; background-color:#F0FFF9;
	color:#007325;
}
.searchselect {
	BORDER-RIGHT: #007325 1px solid; BORDER-TOP: #007325 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #007325 1px solid; BORDER-BOTTOM: #007325 1px solid; background-color:#E8FFE8;
	color:#007325;
}
.searchareamarkgin {
	margin-top: 3px; 
	margin-bottom: 3px; 
	margin-left: 1px; 
	margin-right: 1px;
}
.pl{
	font-size: 15px; color: #000000;
}
.pdef{
	margin-top:5;
}
.listedit{
	WIDTH:100%;
}
</style>
<title>Fourinone Fttp Explorer</title>
<head>
</head>
<body>
<form name=MyForm  method="post">
	<table cellpadding="0" cellspacing="0" border="0" align=center width="100%">
		<tr>
			<td align=left>
			</td>
			<td align=right>
				<select style='width:120' name="actionName" class="select" disabled>
					<option value="editWithSelect('update_comFile.jsp?actionType=0');" >VIEW PATH</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=1');" >CREATE DIR</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=2');" >CREATE FILE</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=3');" >RENAME</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=4');" >MOVETO</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=5');" >DELETE</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=6');" >DOWNLOAD FILE</option>
					<option value="popWindowWithSelect('upload_comFile.jsp?actionType=7');" >UPLOAD FILE</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=8');" >DOWNLOAD JZIP</option>
					<option value="editWithSelect('update_comFile.jsp?actionType=9');" >UNJZIP FILE</option>
					<option value="popWindowWithSelect('ejp.jsp?actionType=6&desFlag=true');" >EDIT</option>
					<option value="popWindow('edit_comExe.jsp');" >EXEC CMD</option>
				</select>
				<input id=bt type=button onclick='exeAction();' value='EXE' class=button disabled>
				<iframe width="0" height="0" id="hiddenframe"></iframe>
			</td>
		</tr>
	</table>

<script>
	function menuClick(parentId)
	{
		/*var parentId = document.getElementById(pId);*/
		var eid = (parentId.id).substring(1);
		var childId = eval("c"+eid);
		if(childId)
		{
			if(childId.style.display == "block")
			{
				childId.style.display = "none";
			}
			else
			{
				childId.style.display = "block";
				if(childId.attributes.loaded.value=="no")
				{
					var hf = document.getElementById("hiddenframe");
					var tid = parentId.attributes.tid.value;
					var tn = parentId.attributes.tn.value;
					hf.src='getFttp.jsp?tid='+tid+"&tn="+tn+"&eid="+childId.id;
					childId.attributes.loaded.value = "yes";
				}
			}
		 }
	}
</script>

<div nowrap>

<a id=proot href="javascript:menuClick(proot)" tid="root" tn="0"><b>Fttp NameNode</b></a><input type=radio name="treeId" value="isRoot=true&tid=root">
<div id=croot style="display:none" loaded="no">
	&nbsp;&nbsp;&nbsp;&nbsp;loading...
</div>


</div>
<script>
	menuClick(proot);
</script>

</form>
</body>

</html>