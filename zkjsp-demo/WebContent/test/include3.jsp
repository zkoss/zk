<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>include a jsp ...</title>
</head>
<body>
<z:page id="includee">
	
	<z:zscript>
	public class MyWindow extends Window
	{
	
	}
	</z:zscript>
	<z:window use="MyWindow" title="out side window" />
	<jsp:include page="/test/window.zul" />
</z:page>
</body>
</html>