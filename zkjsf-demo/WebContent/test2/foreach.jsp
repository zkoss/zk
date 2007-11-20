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
				<z:zscript>
					String[] contacts = new String[] {"Monday", "Tuesday", "Wednesday"};
				</z:zscript>
				<z:listbox width="100px">
					<z:listitem label="${each}" forEach="${contacts}" />
				</z:listbox>
			</z:window>
		</z:page>
	</h:form>
</f:view>
</body>
</html>
