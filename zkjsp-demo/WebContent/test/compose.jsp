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
		<z:ui tag="mywindow">
			<z:custom-attributes wer="qeee"/>
			<p>ewrtyuiytuuyrter</p>
			<p>aewrdtrytuyiu</p>
			<z:zscript>
				
				System.out.println("fire when load page..." );
				System.out.println(page.getId()+"'s file is: include1.jsp" );
				System.out.println(page.getId()+"'s current Page is:"+desktop );
				
			</z:zscript>
			<z:groupbox mold="3d"  closable="${bb}" apply="org.zkoss.jspdemo.MyComposerExt"> 
					<z:caption label="this is a test too!!!"/>
					<z:textbox id="include1Text" />
			</z:groupbox>
			<z:ui tag="username" label="Who" text="this is a text!"/>
		</z:ui>
	</z:page>
</body>
</html>