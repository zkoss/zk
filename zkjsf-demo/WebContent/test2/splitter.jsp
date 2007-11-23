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
				<z:hbox spacing="0" width="100%">
	<z:vbox height="200px">
		<h:inputText value="#{ActionBean.value}">
		</h:inputText>
		<z:splitter collapse="after"/>
		<h:inputText value="#{ActionBean.value}">
		</h:inputText>
	</z:vbox>
	<z:splitter collapse="before"/>
	<h:inputText value="#{ActionBean.value}">
		</h:inputText>
</z:hbox>
			</z:window>
			<h:commandButton id="submit" action="#{ActionBean.doSubmit}"
			value="Submit" />
			<h:messages></h:messages>
		</z:page>
		
	</h:form>
</f:view>
</body>
</HTML>
