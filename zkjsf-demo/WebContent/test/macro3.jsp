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
			<z:component name="mybox1" macroURI="/test/macro-mybox.zul" title="My Box 1 Title" message="My Box 1 Message" />
			<z:component name="mybox2" macroURI="/test/macro-mybox.zul" title="My Box 2 Title" />
			<z:component name="mybox3" macroURI="/test/macro-mybox.zul" inline="true" title="My Box 3 Title 1" message="My Box 3 Message" />
			<z:component name="mybox4" macroURI="/test/macro-mybox.zul" inline="true" title="My Box 4 Title 1" />
			A:<br/>
			<z:ui tag="mybox1" message="Hi 1" label="Click me 1"/>
			B:<br/>
			<z:div>
			<z:ui tag="mybox2" message="Hi 2" label="Click me 2"/>
			</z:div>
			C:<br/>
			<z:ui tag="mybox3" message="Hi 3" label="Click me 3"/>
			D:<br/>
			<z:div>
			<z:ui tag="mybox4" message="Hi 4" label="Click me 4"/>
			</z:div>
			E:
			
			<h:panelGrid columns="2">
				<z:ui tag="mybox1" message="Hi 1 panel" label="Click me 1"/>
				<z:div>
					<z:ui tag="mybox2" message="Hi 2 panel" label="Click me 2"/>
				</z:div>
				<z:ui tag="mybox3" message="Hi 3 panel" label="Click me 3"/>
				<z:div>
					<z:ui tag="mybox4" message="Hi 4 panel" label="Click me 4"/>
				</z:div>
			</h:panelGrid>
			
			<h:commandButton id="submit" value="Submit" />		
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>