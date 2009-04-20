<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html" language="java" %>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html" />
</head>

<body>

<z:page zscriptLanguage="java">

<z:zscript>

import java.io.*;

void add2(){
Object media = Fileupload.get();
if (media != null)
System.out.println("test2:"+media.getName());

}

</z:zscript>

<ol>
<li>Click the Hi button, and a message box shall show up</li>
<li>Click the Upload button, and the upload dialog shall show up</li>
<ol>
	
<table width="150" border="0" cellspacing="0" cellpadding="0">
<tr><td>
<z:button label="Hi" onClick="alert(&quot;Hi&quot;)"/>
</td>
<td>
<z:button label="Upload" onClick="add2();" />
</td>
</tr></table>

</z:page>


</body>
</html>