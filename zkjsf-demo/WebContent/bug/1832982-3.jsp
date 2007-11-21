<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<f:subview id="sub1">
	<z:page>
Inner Before:
<br/>
<z:window title="inner" border="normal">
	<h:panelGrid columns="1">
	<f:verbatim>Before:<br/></f:verbatim>
	<h:commandButton id="commitBtn" value="Commit" />
	<z:button onClick='alert("hello again")' label="click" /><br/>
	<f:verbatim>After:<br/></f:verbatim>
	
	</h:panelGrid>
</z:window>
Inner After:<br/>
<hr/>
</z:page>
</f:subview>

