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
		<z:component name="mywindow" extends="window" title="MyWindow" />
		<z:component name="mybox" macroURI="macro-mybox.zul" label1="Dennis" />
		<z:page>
			<z:ui tag=""
			
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>