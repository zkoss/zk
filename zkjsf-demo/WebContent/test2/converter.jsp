<HTML>
<HEAD>
<title>Converter Example</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window z:title="Converter Example" width="500px" border="normal">
				--Convert string to date--<br/>
				<z:textbox id="tbox" f:value="#{ConverterBean.value}">
					<f:converter converterId="dateConverter"/>
				</z:textbox>
				<h:message
				style="color: red; font-style: oblique;"
					for="tbox" />
				<br/>
				<h:commandButton id="submit" action="#{ConverterBean.doSubmit}" value="Submit" />				
			</z:window>
			<h:messages/>
			<a href="../index.html">Back</a>
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>
