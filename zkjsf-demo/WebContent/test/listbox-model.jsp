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
			<z:zscript>
				String[] data = new String[30];
				for(int j=0; j< data.length; ++j) {
					data[j] = ""+j;
				}
				ListModel strset = new SimpleListModel(data);
			
				ListitemRenderer render = new ListitemRenderer(){
					public render(Listitem item, java.lang.Object data) {
						item.setLabel("Item "+data);
						item.setValue(data);
					}
				}
			
			</z:zscript>
			<z:window width="500px" border="normal">
				<z:attribute name="title">Hello</z:attribute>
				
				<z:listbox id="role1" rows="10" multiple="true" f:value="#{testListboxBean.selection}">
					<z:attribute name="onCreate">
						self.setModel(strset);
						self.setItemRenderer(render);
					</z:attribute>
				</z:listbox>
				<z:listbox id="role2" multiple="true" mold="select" f:value="#{testListboxBean.selections}">
					<z:attribute name="onCreate">
						self.setModel(strset);
						self.setItemRenderer(render);
					</z:attribute>
				</z:listbox>
				<h:commandButton id="submit" action="#{testListboxBean.doSubmit}" value="Submit" />
			</z:window>
			<h:messages/>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>
