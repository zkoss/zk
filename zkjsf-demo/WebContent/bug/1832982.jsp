<HTML>
<HEAD>
<title>BUG</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		#1832982 Output is disordered when use jsp:include
		<z:page>
		<z:window title="outter" border="normal">
		outter Before <br/>
		<!-- jsp:directive.include file="1832982-2.jsp"/-->
		<jsp:include page="1832982-2.jsp" />
		outter After<br/>
		</z:window>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>