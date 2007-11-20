<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:textbox id="tb"></z:textbox>
			<z:window title="foward testing" id="ww">
				...
				<z:button label="OK" onClick="tb.setValue('ok');" forward="onOK" />
				<z:button label="Cancel" onClick="tb.setValue('cancel');" forward="onCancel" />
			</z:window>
			<h:commandButton id="submit" value="Submit" />
		</z:page>
	</h:form>
</f:view>
</body>
</html>
