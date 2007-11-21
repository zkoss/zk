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
		<z:component name="mywindow" extends="window" useClass="org.zkoss.jsfdemo.test.ForwardWindow" title="Forward Window" />
		<z:page>
			<z:ui tag="mywindow" border="normal">
				<z:button forward="onOK" label="OK"/>
			</z:ui>
			
			<z:ui tag="mywindow" border="normal" title="Ext Title">
				<z:button forward="onCancel" label="Cancel"/>
			</z:ui>
			<h:commandButton id="submit" value="Submit" />		
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>