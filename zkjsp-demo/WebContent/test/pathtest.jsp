<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test jsp path...</title>
</head>
<% 

request.setAttribute("bb", new Boolean(true));
%>
<body>

	<z:page id="includee">
		<z:zscript>
			String objPath = "";
		</z:zscript>
		<z:window id="win1" title="test" border="normal">
		 	......... 
		 	<z:button id="myButton" label="b1" 
		 		onCreate="objPath=new Path(self).getPath()" 
		 		onClick='alert(objPath=new Path(self).getPath())'/>
		</z:window> 

		<z:button id="pathBtn" label="test Path"  
			onClick='alert(objPath+":"+Path.getComponent(objPath))'/>
	</z:page>
</body>
</html>