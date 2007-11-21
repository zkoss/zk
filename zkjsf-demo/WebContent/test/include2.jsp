<HTML>
<HEAD>
<title>Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<h:commandButton id="submit" value="Submit" />
		<z:page>
		This case test the include function<br/>
		<z:label/><!--  insert this tag is because of bug 1832862 -->
		1.JSP directive.include,<br/>
		<jsp:directive.include file="empty.jsp"/>
		<hr/>
		
		2.JSP directive.include , sub jsf,<br/>
		<jsp:directive.include file="subjsf.jsp"/>
		<hr/>
		
		3.JSP directive.include , zuljsf,<br/>
		<jsp:directive.include file="includedHello.jsp"/>
		<hr/>
		
		4.JSP include , zuljsf, (fail this one, why?)<br/>
		<jsp:include page="includedHello2.jsp"/>
		<hr/>
		
		5.JSP include , zul, (use include when zul file)<br/>
		<jsp:include page="includedHello.zul"/>
		<hr/>
		</z:page>	
	</h:form>
</f:view>
</body>
</HTML>