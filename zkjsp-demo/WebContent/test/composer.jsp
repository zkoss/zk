<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<z:component name="mywindow" extends="window" title="test" border="normal"/>
<z:component name="username" inline="true" macroURI="/test/test.zul" />


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>include a jsp ...</title>
</head>
<% 

request.setAttribute("bb", new Boolean(true));
%>
<body>
	<z:page id="includee">
  <z:window id="win" apply="org.zkoss.jspdemo.MyComposer" width="400px"/>
  <z:button label="change window title" onClick='win.setTitle("Hello ZK")'/>
	</z:page>
</body>
</html>


