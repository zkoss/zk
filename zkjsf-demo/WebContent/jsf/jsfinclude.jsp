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
		outter Before A<br/>
		<!-- jsp:directive.include file="jsfinclude-2.jsp"/-->
		<jsp:include page="jsfinclude-2.jsp"/>
		outter After<br/>
			
	</h:form>
</f:view>
</body>
</HTML>