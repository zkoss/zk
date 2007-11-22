<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">

<HTML>

<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<TITLE>Reservation of Conference Room</TITLE>
<link rel="stylesheet" type="text/css"
	href='<%= request.getContextPath() + "/stylesheet.css" %>'>
</HEAD>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>

<BODY BGCOLOR="white">

<f:view>
	<z:page>
		<h:form id="form1">
			<z:window title="foo" width="300px">
					
					
			</z:window>
			<h:commandButton id="commitBtn" value="Commit" action="#{testCommandBean.doSubmit1}" actionListener="#{testCommandBean.onSubmit1}"/>
			<z:button id="btn1" label="click me" f:action="#{testCommandBean.doSubmit2}" f:actionListener="#{testCommandBean.onSubmit2}"/>
			<z:toolbar>
				<z:toolbarbutton id="tbtn1" label="click me 2" f:action="#{testCommandBean.doSubmit3}" f:actionListener="#{testCommandBean.onSubmit3}"/>
			</z:toolbar>
		<z:menubar id="menubar">
			<z:menu label="File">
				<z:menupopup>
					<z:menuitem label="New" onClick="alert(self.label)"/>
					<z:menuitem label="Open" onClick="alert(self.label)"/>
					<z:menuitem label="Save" onClick="alert(self.label)"/>
					<z:menuseparator/>
					<z:menuitem label="Submit" f:action="#{testCommandBean.doSubmit4}" f:actionListener="#{testCommandBean.onSubmit4}"/>
				</z:menupopup>
			</z:menu>
		</z:menubar>
		<h:messages/>
		</h:form>
	</z:page>
</f:view>
</BODY>

</HTML>
