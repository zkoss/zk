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
			<z:window title="Attributes" id="ww">
				<z:custom-attributes main.rich="simple" very-simple="intuitive">
				JUNK TEXT
				</z:custom-attributes>
			  	<z:button label="click">
			  		<z:attribute name="onClick">
			  			alert("att1:"+ww.getAttribute("main.rich")+",att2:"+ww.getAttribute("very-simple"));
			  		</z:attribute>
			  	</z:button>
			</z:window>	
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>