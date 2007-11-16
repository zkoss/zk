<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<z:component name="mywin" class="org.zkoss.jspdemo.MyWindow2" extends="window" border="normal"/>
<z:component name="username" inline="true" macroURI="/username.zul"/>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<z:page id="includee">
<z:window title="There?" width="600px"/>
<z:ui tag="mywin" id="win" title="Customized Window" width="500px">
    <z:button label="change name" onClick='win.setTitle("I am here!")'/>
    <z:ui tag="username" who="Bill Gates"/>
</z:ui>
	</z:page>
</body>
</html>

