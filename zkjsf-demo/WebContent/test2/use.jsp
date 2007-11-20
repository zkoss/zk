<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Testing Use</title>
</head>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window use="org.zkoss.jsfdemo.test2.MyWindow" title="foward testing" id="ww">
				Value:
				<z:textbox id="tb"
					f:value="#{ActionBean.value}"
				>
				</z:textbox>
				Before onCreate Called:
				<z:textbox id="tbc">
				</z:textbox>
			</z:window>
			<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
		</z:page>
		<h:messages></h:messages>
	</h:form>
</f:view>
</body>
</html>