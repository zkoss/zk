<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>

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
  <z:window id="win" use="org.zkoss.jspdemo.MyWindow2">
    <z:button label="OK" forward="onClick=win.onOK"/>
    <z:button label="Cancel" forward="onClick=win.onCancel"/>
  </z:window>
</z:page>
</body>
</html>