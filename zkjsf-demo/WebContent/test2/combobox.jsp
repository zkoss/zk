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
			<z:window>
				<z:combobox f:value="#{ActionBean.value}">
				<z:comboitem label="Simple and Rich" />
				<z:comboitem label="Cool!" />
				<z:comboitem label="Thumbs Up!" />
				</z:combobox>
			</z:window>
			<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
			<h:messages></h:messages>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>
