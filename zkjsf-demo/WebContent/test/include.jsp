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
		<z:page>
			A Inlucde Test,
			1==
			<z:include src="/test/empty.jsp"/>
			2==
			<z:include src="/test/includedHello.zul"/>
			3==subjsf will miss the position. a bug? wait for me find out it.
			<z:include src="/test/subjsf.jsp"/>
			4==<br/>
			For now, we cann't include a jsp page which with ZK JSF Componnent again.
			There is a multiple root component issue when include.
			<!-- 
			<z:include src="/test/includedHello.jsp"/>						
			 -->
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>