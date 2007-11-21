<HTML>
<HEAD>
<title>Bug</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
	    #1832862  	  Content disappear in JSFComponent
		<z:page>
		<z:div>
			a text<br/>
		</z:div>
		<z:div>
			a text with jsf core component<br/>
			<h:commandButton id="commitBtn" value="click" />
		</z:div>
		<z:div>
			a text with button<br/>
			<z:button label="click"/> 
		</z:div>
		<z:div>
			a text with button and jsf core component<br/>
			<z:button label="click"/>
			<h:commandButton id="commitBtn2" value="click" /> 
		</z:div>
		</z:page>	
	</h:form>
</f:view>
</body>
</HTML>