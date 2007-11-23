<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Navigation</title>
</head>
<body>
<f:view>
	<h:form>
		<z:page>
			<z:window>
				<z:textbox f:value="#{MyNavigator.text}"></z:textbox>
			</z:window>
			
		</z:page>
		<h:commandButton action="#{MyNavigator.doSuccess}" value="success" >
		</h:commandButton>
		<h:commandButton action="#{MyNavigator.doFailed}" value="failed" >
		</h:commandButton>
	</h:form>
</f:view>
</body>
</html>