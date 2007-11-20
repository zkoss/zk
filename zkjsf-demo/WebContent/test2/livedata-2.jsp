<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Live data</title>
</head>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form>
		<z:page>
			<z:window title="Live Data Demo" border="normal">
			<z:zscript>
				String[] data = org.zkoss.jsfdemo.test2.TestDataGenerator.generateNumerousStrings(40);
				System.out.println(data.length);
				ListModel strset = new SimpleListModel(data);
			</z:zscript>
			<z:listbox id="list" width="200px" rows="10" model="${strset}">
				<z:listhead>
				<z:listheader label="Load on Demend" sort="auto" />
				</z:listhead>
			</z:listbox>
			</z:window>
		</z:page>
	</h:form>
</f:view>
</body>
</html>
