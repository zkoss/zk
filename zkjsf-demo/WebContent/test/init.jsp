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
			<z:init use="org.zkoss.jsfdemo.test.TestInit" arg0="Dennis" arg1="Mary"/>
			<z:window title="Initial Check Window" id="ww">
			   Arg0:<z:label id="arg0"/><z:separator/>
			   Arg1:<z:label id="arg1"/><z:separator/>
			   After Compose:<z:label id="afco"/><z:separator/>
			   Do Catch:<z:label id="doca"/><z:separator/>
			   Do Finally:<z:label id="fina"/><z:separator/>
			</z:window>
			
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>