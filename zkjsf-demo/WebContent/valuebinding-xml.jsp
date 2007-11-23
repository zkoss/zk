<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:z="http://www.zkoss.org/jsf/zul"
	>
<HTML>
<HEAD>
<title>ValueBinding Example</title>
</HEAD>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window title="ValueBinding Example" width="500px" border="normal">
				<z:textbox id="tbox" f:value="#{ValueBindingBean.text}" /><br/>
				<z:intbox id="ibox" f:value="#{ValueBindingBean.number}" /><br/>
				<z:datebox id="dbox" format="yyyy/MM/dd" f:value="#{ValueBindingBean.date}" />
				<br/>
				<z:listbox id="lbox" f:value="#{ValueBindingBean.selection}">
					<z:listitem value="A" label="Item-A" />
					<z:listitem value="B" label="Item-B" />
					<z:listitem value="C" label="Item-C" />
					<z:listitem value="D" label="Item-D" />
					<z:listitem value="E" label="Item-E" />
					<z:listitem value="F" label="Item-F" />
				</z:listbox>
				<h:commandButton id="submit" action="#{ValueBindingBean.doSubmit}" value="Submit" />				
			</z:window>
			<h:messages/>
			<a href="../index.html">Back</a>
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>
</jsp:root>
