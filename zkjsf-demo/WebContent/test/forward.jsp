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
			<z:window title="Apply Check Window" use="org.zkoss.jsfdemo.test.ForwardWindow">
				<z:textbox/>
				<z:button label="onOK" forward="onOK"/>
				<z:button label="onCancel" forward="onCancel"/>
			</z:window>
			<h:commandButton id="submit" value="Submit" />
		</z:page>
		
	</h:form>
</f:view>
</body>
</HTML>