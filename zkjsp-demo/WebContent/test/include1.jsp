<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>include a jsp ...</title>
</head>
<body>


	<z:page id="includee">
		<z:zscript>
			System.out.println("fire when load page..." );
			System.out.println(page.getId()+"'s file is: include1.jsp" );
			System.out.println(page.getId()+"'s current Page is:"+desktop );
		</z:zscript>
		<z:groupbox mold="3d"  closable="false"> 
				<z:caption label="this is a test too!!!"/>
		</z:groupbox>
		<z:textbox id="include1Text" />
	</z:page>
</body>
</html>