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
				<z:textbox id="now" 	f:value="#{ActionBean.value}" >
					<f:converter converterId="MyConverter"/>
				</z:textbox> 
				<z:timer id="timer" delay="1000" repeats="true"
					onTimer="now.setValue(new Date().toString())" 
					/> 
					<separator bar="true" />
				<z:button label="Stops timer" onClick="timer.stop()" />
				<z:button label="Starts timer" onClick="timer.start()" />
			</z:window>
		</z:page>
		<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
	</h:form>
	<h:messages></h:messages>
</f:view>
</window>
</body>
</html>
