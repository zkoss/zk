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
			
		
		
		
		<z:zscript>
		import java.util.ArrayList;
		import org.zkoss.jsfdemo.test.*;
		
		ArrayList al = new ArrayList();
		int i=0;
		for(; i < 10; i++)
		{
			Object obj = ""+i;
			al.add(obj);
		}
		TestTreeModel btm = new TestTreeModel(al);	
		TestTreeRenderer btr = new TestTreeRenderer();
		</z:zscript>
		<z:tree id="tree1" f:value="#{testTreeBean.selection}">
			<z:attribute name="onCreate">
				self.setTreeitemRenderer(btr);
				self.setModel(btm);
			
			</z:attribute>
		</z:tree>
		<z:tree id="tree2" multiple="true" f:value="#{testTreeBean.selections}">
			<z:attribute name="onCreate">
				self.setTreeitemRenderer(btr);
				self.setModel(btm);
			
			</z:attribute>
		</z:tree>
	
	
				<h:commandButton id="submit" action="#{testTreeBean.doSubmit}" value="Submit" />
			</z:window>
			<h:messages/>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>
