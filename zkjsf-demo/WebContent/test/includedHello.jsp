<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<z:page>
<z:window title="included window" border="normal" width="200px">
	${empty arg.message ? 'Hello, World!': arg.message}
</z:window>
</z:page>
