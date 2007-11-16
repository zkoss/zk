
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<z:component name="mywindow" extends="window" class="org.zkoss.jspdemo.MyWindow" title="test" border="normal"/>
<z:component name="username" inline="true" macroURI="/test/macro.zul" />


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
	<z:ui tag="mywindow" >
		<z:window id="main">
		    <z:button label="Add Item">    
		        <z:attribute name="onClick">        
		    new Label("Added at "+new Date()).setParent(main);    
		    new Separator().setParent(main);    
		        </z:attribute>    
		        <z:attribute name="onCreate">        
		    new Label("Added at "+new Date()).setParent(main);    
		    new Separator().setParent(main);    
		        </z:attribute>          
		    </z:button>    
		    <z:separator bar="true"/>    
		    
		</z:window>
	</z:ui>
	</z:page>
</body>
</html>
