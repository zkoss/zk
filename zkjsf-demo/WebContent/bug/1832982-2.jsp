<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<f:subview id="sub1">
<z:page>
Inner Before:
<br/>
<z:window id="w1" title="inner" border="normal" rendered="true">
	Before:<br/>
	<z:button onClick='alert("hello again:"+w1.title)' label="click" /><br/>
	After:<br/>
</z:window>
Inner After:<br/>
</z:page>
</f:subview>

