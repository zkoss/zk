<HTML>
<HEAD>
<title>onChanging</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form>
		<z:page>
		<z:window title="onChanging event demo" border="normal">
	<z:grid width="80%">
	<z:rows>
		<z:row>onChanging textbox: <z:textbox onChanging="copy.value = event.value"/></z:row>
		<z:row>instant copy: <z:textbox f:value="#{ActionBean.value}" id="copy" readonly="true"/></z:row>
	</z:rows>
	</z:grid>
</z:window>
</z:page>
	<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
	</h:form>
	<h:messages></h:messages>
</f:view>
</body>
</HTML>