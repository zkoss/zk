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
				<z:borderlayout height="500px">
	<z:north maxsize="300" size="50%" border="0" splittable="true" collapsible="true">
		<z:borderlayout>
			<z:west size="25%" border="none" flex="true" maxsize="250" splittable="true" collapsible="true">
				<z:div style="background:#B8D335">
					<z:label value="25%"
						style="color:white;font-size:50px" />
				</z:div>
			</z:west>
			<z:center border="none" flex="true">
				<z:div style="background:#E6D92C">
					<z:label value="25%"
						style="color:white;font-size:50px" />
				</z:div>
			</z:center>
			<z:east size="50%" border="none" flex="true">
				<z:label value="Here is a non-border"
					style="color:gray;font-size:30px" />
			</z:east>
		</z:borderlayout>
	</z:north>
	<z:center border="0">
		<z:borderlayout>
			<z:west maxsize="600" minsize="300" size="30%" flex="true" border="0" splittable="true">
				<z:div style="background:#E6D92C">
					<z:label value="30%"
						style="color:white;font-size:50px" />
				</z:div>
			</z:west>
			<z:center>
				<z:label value="Here is a border"
					style="color:gray;font-size:30px" />
			</z:center>
			<z:east size="30%" flex="true" border="0" collapsible="true">
				<z:div style="background:#B8D335">
					<z:label value="30%"
						style="color:white;font-size:50px" />
				</z:div>
			</z:east>
		</z:borderlayout>
	</z:center>
</z:borderlayout>
			</z:window>
		</z:page>
	</h:form>
</f:view>
	
</body>
</html>