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
	<h:form>
		<z:page>
			<z:window>
				<z:progressmeter f:value="#{ActionBean.value}" id="pm" value="20" />
				<z:button label="0" onClick="pm.value = 0" />
				<z:button label="50" onClick="pm.value = 50" />
				<z:button label="100" onClick="pm.value = 100" />
					<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
					<h:messages></h:messages>
			</z:window>
		</z:page>
	</h:form>
</f:view>
Progress meter:

</body>
</html>
