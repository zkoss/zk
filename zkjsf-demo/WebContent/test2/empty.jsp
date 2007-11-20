<HTML>
<HEAD>
<title>Empty Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form>
		<z:page>
			text-A
			<z:window >
			text-B
			</z:window>	
		</z:page>
		text-C
	</h:form>
	text-D
</f:view>
</body>
</HTML>