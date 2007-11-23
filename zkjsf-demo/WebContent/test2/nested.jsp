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
				<h:panelGrid>
					<h:inputText value="#{ActionBean.value}">
					</h:inputText>
					<h:inputText value="#{ActionBean.value}">
					</h:inputText>
					<h:inputText value="#{ActionBean.value}">
					</h:inputText>
					<z:textbox  f:value="#{ActionBean.value}">
					</z:textbox>
					<z:grid>
						<z:rows>
							<z:row>
								<z:textbox  f:value="#{ActionBean.value}">
					</z:textbox>
							</z:row>
							<z:row>
								<z:textbox  f:value="#{ActionBean.value}">
					</z:textbox>
							</z:row>
						</z:rows>
					</z:grid>
				</h:panelGrid>
				<z:grid>
					<z:rows>
						<z:row>
							<h:panelGrid>
								<z:textbox f:value="#{ActionBean.value}">
								</z:textbox>
								<z:textbox f:value="#{ActionBean.value}">
								</z:textbox>
								<z:textbox f:value="#{ActionBean.value}">
								</z:textbox>
								<z:textbox f:value="#{ActionBean.value}">
								</z:textbox>
							</h:panelGrid>
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