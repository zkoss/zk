<HTML>
<HEAD>
<title>Bug</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
	   #1834385  	  throws exception when setting 'rendered' attribute
		<z:page>
		<z:window title="a title" f:rendered="true">
			<z:textbox f:rendered="false"/>
			<z:datebox f:rendered="true"/>
		</z:window>
		</z:page>	
	</h:form>
</f:view>
</body>
</HTML>