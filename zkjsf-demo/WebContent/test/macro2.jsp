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
		<z:component name="mywindow" extends="window" title="MyWindow" />
		<z:component name="groupbox" extends="groupbox" mold="3d" />
		<z:component name="mybox" macroURI="/test/macro-mybox.zul" title="My Box" />
		<z:component name="mybox-inline" macroURI="/test/macro-mybox.zul" inline="true" title="My Box" />
		<z:page>
			Ui:<br/>
			<z:ui tag="mywindow"/>
			Ui With Inner:<br/>
			<z:ui tag="mywindow" border="normal" id="my1">
				<z:button label="btn1" onClick='alert(my1.title)'/>
			</z:ui>
			<z:ui tag="mywindow" title="MyWindow ext" border="normal" id="my2">
				<z:button label="btn1" onClick='alert(my2.title)'/>
			</z:ui>
			Override same component name:<br/>
			<z:groupbox>
				<z:caption label="group box 1"/>
				<z:button label="1"/>
			</z:groupbox>
			<z:ui tag="groupbox">
				<z:caption label="group box 2"/>
				<z:button label="2"/>
			</z:ui>
			<h:commandButton id="submit" value="Submit" />		
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>