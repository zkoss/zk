<HTML>
<HEAD>
<title>Validator</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form >
		<z:page>
			<z:window>
				<z:textbox id="tbox" f:value="#{ActionBean.value}">
					<f:validator validatorId="MyStringValidator"/>
				</z:textbox>
				<h:message
				style="color: red; font-style: oblique;"
					for="tbox" />
				<br/>
				<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />				
			</z:window>
			<h:messages/>
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>
