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
			<z:button label="click me"/>
			<z:ui tag="window" title="Macro Window" width="300px"/>
			<z:ui tag="button" label="click me 1" onClick="doClick()"/>
			<z:window title="W" border="normal">
			<z:ui tag="button" label="click me 2" onClick="doClick()"/>
			</z:window>
			<z:zscript>
				void doClick(){
					alert("click");
				}
			</z:zscript>
			A
			<z:button label="click me"/>
			B
			<z:ui tag="window" title="Macro Window" width="300px"/>
			C
			<z:ui tag="button" label="click me 1" onClick="doClick()"/>
			D
			<z:window title="W" border="normal">
			E
			<z:ui tag="button" label="click me 2" onClick="doClick()"/>
			F
			</z:window>
			G
			<h:commandButton id="submit" value="Submit" />
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>