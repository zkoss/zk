<HTML>
<HEAD>
<title>Command Example</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window z:title="Command Example" width="500px" border="normal">
				--input day must in weekend--<br/>
				<z:datebox id="dbox" format="yyyy/MM/dd"
					f:value="#{CommandBean.value}" 
					f:validator="#{CommandBean.validateDate}"/>
				<h:message
				style="color: red; font-style: oblique;"
					for="dbox" />
				<br/>
				<z:button id="submit1" label="Submit 1" f:action="#{CommandBean.actionPerform}"  />
				<z:toolbar>
					<z:toolbarbutton id="submit2" label="Submit 2" f:actionListener="#{CommandBean.onAction}"/>
				</z:toolbar>
				<z:menubar id="menubar">
					<z:menu label="Command">
						<z:menupopup>
							<z:menuitem label="Submit Immediate" f:action="#{CommandBean.actionPerformImmediate}" f:immediate="true"/>
						</z:menupopup>
					</z:menu>
				</z:menubar>
			</z:window>
			<h:messages/>
			<a href="../index.html">Back</a>
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>
