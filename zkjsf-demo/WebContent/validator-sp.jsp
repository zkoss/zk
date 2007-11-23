<HTML>
<HEAD>
<title>Validator Example</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window title="Validator Example" width="500px" border="normal">
				--Validate input day must in weekend--<br/>
				<z:datebox id="dbox" format="yyyy/MM/dd"
					f_value="#{ValidatorBean.value}" 
					f_validator="#{ValidatorBean.validateDate}"/>
				<h:message
				style="color: red; font-style: oblique;"
					for="dbox" />
				<br/>
				<h:commandButton id="submit" action="#{ValidatorBean.doSubmit}" value="Submit" />				
			</z:window>
			<h:messages/>
			<a href="../index.html">Back</a>
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>
