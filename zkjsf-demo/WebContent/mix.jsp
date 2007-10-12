<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">

<HTML>

<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<TITLE>Mix Example</TITLE>
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
			--No mix--
			<blockquote>
			<z:window title="foo" width="300px">
					Click button A:
					<z:button id="btn" label="click me" onClick="click1()"/>
					Click button B:
					<z:zscript>
						void click1(){
							alert("click 1");
						}
					</z:zscript>
			</z:window>
			</blockquote>
			--Mix--
			<blockquote>
			<h:panelGrid columns="1">
				<z:window title="foo">
					Click button C:
					<z:button label="click me" onClick="click2()"/>
					Click button D:
					<z:zscript>
						void click2(){
							alert("click 2");
						}
					</z:zscript>
				</z:window>
			</h:panelGrid>
			equals:
			<h:panelGrid columns="1">
				<z:window title="foo">
					<z:button label="click me" onClick="click3()"/>
					<z:zscript>
						void click3(){
							alert("click 3");
						}
					</z:zscript>
				</z:window>
			</h:panelGrid>
			</blockquote>
			--Use Verbatim--
			<blockquote>
			<h:panelGrid columns="1">
				<z:window title="foo">
					<f:verbatim>Click button E:</f:verbatim>
					<z:button label="click me" onClick="click4()"/>
					<f:verbatim>Click button F:</f:verbatim>
					<z:zscript>
						void click4(){
							alert("click 4");
						}
					</z:zscript>
				</z:window>
			</h:panelGrid>
			</blockquote>
			<h:commandButton id="commitBtn" value="Commit" />
		</h:form>
		<a href="../index.html">Back</a>
	</z:page>
</f:view>
</BODY>

</HTML>
