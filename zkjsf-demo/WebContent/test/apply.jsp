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
			<z:window title="Apply Check Window" id="ww" apply="org.zkoss.jsfdemo.test.TestApply">
				zscript:<z:label id="zscript" /><br/>
				doBeforeComposeChildren:<z:label id="doBeforeComposeChildren" /><br/>
				doCatch:<z:label id="doCatch" /><br/>
				doFinally:<z:label id="doFinally" /><br/>
				doAfterCompose:<z:label id="doAfterCompose" /><br/>
			</z:window>
			<z:zscript>
				Path.getComponent("/ww/zscript").setValue("doScript");
				Path.getComponent("/ww/doBeforeComposeChildren").setValue(ww.getAttribute("doBeforeComposeChildren"));
				Path.getComponent("/ww/doCatch").setValue(ww.getAttribute("doCatch"));
				Path.getComponent("/ww/doFinally").setValue(ww.getAttribute("doFinally"));
				Path.getComponent("/ww/doAfterCompose").setValue(ww.getAttribute("doAfterCompose"));
			</z:zscript>
			<h:commandButton id="submit" value="Submit" />
		</z:page>
		
	</h:form>
</f:view>
</body>
</HTML>