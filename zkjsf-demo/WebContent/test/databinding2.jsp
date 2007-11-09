<HTML>
<HEAD>
<title>ValueBinding Example</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit" />
			<z:zscript>
				String text;
			</z:zscript>
			<z:window z:title="ValueBinding Example" width="500px" border="normal">
				<z:textbox id="tbox" value="@{text}" f:value="#{ValueBindingBean.text}" /><br/>
				<z:textbox value="@{text}"/><br/>
				<h:commandButton id="submit" action="#{ValueBindingBean.doSubmit}" value="Submit" />				
			</z:window>
			<h:messages/>
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>
