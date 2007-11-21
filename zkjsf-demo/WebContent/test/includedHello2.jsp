<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<f:subview id="sub100">
<z:page>
	<z:window title="include 2">
		Button:
		<z:button onClick='alert("hello again 2")' label="click" />
	</z:window>
</z:page>
</f:subview>
