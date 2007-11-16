<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<z:component name="username" inline="true" macroURI="/test/username.zul"/>
 <z:component name="mywin" class="org.zkoss.jspdemo.MyWindow" extends="window"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>include a jsp ...</title>
</head>
<body>
	<z:page id="includee">
<z:ui tag="mywin" id="my" title="customized window"/>
<z:window title="Macro Window">
    <z:label value="change name"/>
    <z:ui tag="username" who="Ian Tsai"/>
</z:window>
<z:button label="change title" onClick='my.setTitle("Hello ZK!")'/>
	</z:page>
</body>
</html>

