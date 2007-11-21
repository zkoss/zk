<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<f:subview id="sub1">
<h:panelGrid columns="1">
	<z:page>
	<f:verbatim>Before A:</f:verbatim>
	<h:commandButton id="commitBtn" value="Commit" />
	<f:verbatim>After:</f:verbatim>
	<z:button label="click"/>
	</z:page>
</h:panelGrid>
</f:subview>

