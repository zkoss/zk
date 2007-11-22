<HTML>
<HEAD>
<title>Listbox test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>

<body bgcolor="white">
<f:view>
	<h:form id="helloForm" >
		
		<z:page>
			<z:window z:title="Listbox Test" width="500px" border="normal">
				<z:listbox id="role1" f:value="#{testListboxBean.selection}">
					<z:listitem value="1" label="Determine need" />
					<z:listitem value="2" label="Evaluate products/sesrvices" />
					<z:listitem value="3" label="Recommend products/sesrvices" />
					<z:listitem value="4" label="Implement products/sesrvices" />
					<z:listitem value="5" label="Techinical decision maker" />
					<z:listitem value="6" label="Financial decision maker" />
				</z:listbox>
				<z:listbox id="role2" multiple="true" mold="select" f:value="#{testListboxBean.selections}">
					<z:listitem value="1" label="Determine need" />
					<z:listitem value="2"
						label="Evaluate products/sesrvices" />
					<z:listitem value="3"
						label="Recommend products/sesrvices" />
					<z:listitem value="4"
						label="Implement products/sesrvices" />
					<z:listitem value="5"
						label="Techinical decision maker" />
					<z:listitem value="6"
						label="Financial decision maker" />
				</z:listbox>

				<h:commandButton id="submit" action="#{testListboxBean.doSubmit}" value="Submit" />
			</z:window>
			<h:messages/>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>
