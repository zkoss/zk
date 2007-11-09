<HTML>
<HEAD>
<title>Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window title="Grid Demo">
				<z:captcha id="cpa" length="5" width="200px" height="50px"/>
			</z:window>	
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>