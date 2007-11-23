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
			<z:window title="ValueBinding Example" width="500px" border="normal">
				<z:textbox id="tbox" f_value="#{ValueBindingBean.text}" /><br/>
				<z:intbox id="ibox" f_value="#{ValueBindingBean.number}" /><br/>
				<z:datebox id="dbox" format="yyyy/MM/dd" f_value="#{ValueBindingBean.date}" />
				<br/>
				<z:listbox id="lbox" f_value="#{ValueBindingBean.selection}">
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
