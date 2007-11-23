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
				<z:grid>
					<z:rows>
						<z:row>
							<h:inputText value="#{ActionBean.value}"></h:inputText>
						</z:row>
						<z:row>
							<z:textbox disabled="true" f:value="#{ActionBean.value}"></z:textbox>
						</z:row>
					</z:rows>
				</z:grid>
			</z:window>
			<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
		<h:messages></h:messages>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>