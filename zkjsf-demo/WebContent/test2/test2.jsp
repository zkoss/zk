<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
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
				<z:textbox id="tb" width="99%" 
					f:value="#{ActionBean.value}" 
					f:validator="#{ActionBean.validateString}"
				 />
				 <h:message
				style="color: red; font-style: oblique;"
					for="tb" />
				<h:messages/>
				<h:commandButton id="submit" action="#{ActionBean.doSubmit}"
					value="Submit" />
			</z:window>
		</z:page>
	</h:form>
</f:view>
</body>
</html>
