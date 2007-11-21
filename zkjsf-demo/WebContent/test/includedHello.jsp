<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<z:window title="included window in jsf" border="normal" width="200px">
	Hello, World!
	<z:button onClick='alert("hello again")' label="click"/>
</z:window>
