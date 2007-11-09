<HTML>
<HEAD>
<title>Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<body>
<f:view>
	<h:form id="helloForm">
		<h:panelGrid columns="1">
				<f:verbatim>Click Button</f:verbatim>
				<h:commandButton id="commitBtn" value="Commit" />
		</h:panelGrid>
	</h:form>
</f:view>
</body>
</HTML>