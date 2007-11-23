<HTML>
<HEAD>
<title>Extend</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form>
		<z:component name="mytext" extends="textbox" />
		<z:page>
			<z:window>	
				<z:ui tag="mytext" f:value="#{ActionBean.value}"/>
			</z:window>
				<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
					<h:messages></h:messages>
		</z:page>
		
	</h:form>
</f:view>
</body>
</HTML>