<HTML>
<HEAD>
<title>Empty Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<z:page>
	<z:window title="Client side action" border="normal" >
	<z:grid>
		<z:rows>
			<z:row>
<z:label value="text1: "/>
<z:textbox action="onfocus: action.show(#{help1}); onblur: action.hide(#{help1})"/>
<z:label id="help1" visible="false" value="This is help for text1."/>
			</z:row>
			<z:row>
<z:label value="text2: "/>
<z:textbox action="onfocus: action.show(#{help2}); onblur: action.hide(#{help2})"/>
<z:label id="help2" visible="false" value="This is help for text2."/>
			</z:row>
		</z:rows>
	</z:grid>
</z:window>
	<h:form>
		
		<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
					<h:messages></h:messages>
	</h:form>
	</z:page>
</f:view>
</body>
</HTML>