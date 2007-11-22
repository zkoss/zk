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
			<z:window z:title="Tree Test" width="500px" border="normal">
			
		<z:tree name="role1" id="tree" f:value="#{testTreeBean.selection}">
		<z:treecols>
			<z:treecol label="Name" />
			<z:treecol label="Description" />
		</z:treecols>
		<z:treechildren>
			<z:treeitem value="1">
				<z:treerow>
					<z:treecell label="Item 1" />
					<z:treecell label="Item 1 description" />
				</z:treerow>
			</z:treeitem>
			<z:treeitem value="2">
				<z:treerow>
					<z:treecell label="Item 2" />
					<z:treecell label="Item 2 description" />
				</z:treerow>
				<z:treechildren>
					<z:treeitem value="3">
						<z:treerow>
							<z:treecell label="Item 2.1" />
						</z:treerow>
						<z:treechildren>
							<z:treeitem value="4">
								<z:treerow>
									<z:treecell label="Item 2.1.1" />
								</z:treerow>
							</z:treeitem>
							<z:treeitem value="5">
								<z:treerow>
									<z:treecell label="Item 2.1.2C1" />
								</z:treerow>
							</z:treeitem>
						</z:treechildren>
					</z:treeitem>
					<z:treeitem value="6">
						<z:treerow>
							<z:treecell label="Item 2.2" />
							<z:treecell
								label="Item 2.2 is something who cares" />
						</z:treerow>
					</z:treeitem>
				</z:treechildren>
			</z:treeitem>
			<z:treeitem label="Item 3" value="7"/>
		</z:treechildren>
	</z:tree>		
	
	
	<z:tree name="role2" multiple="true" id="tree2" f:value="#{testTreeBean.selections}">
		<z:treecols>
			<z:treecol label="Name" />
			<z:treecol label="Description" />
		</z:treecols>
		<z:treechildren>
			<z:treeitem value="1">
				<z:treerow>
					<z:treecell label="Item 1" />
					<z:treecell label="Item 1 description" />
				</z:treerow>
			</z:treeitem>
			<z:treeitem value="2">
				<z:treerow>
					<z:treecell label="Item 2" />
					<z:treecell label="Item 2 description" />
				</z:treerow>
				<z:treechildren>
					<z:treeitem value="3">
						<z:treerow>
							<z:treecell label="Item 2.1" />
						</z:treerow>
						<z:treechildren>
							<z:treeitem value="4">
								<z:treerow>
									<z:treecell label="Item 2.1.1" />
								</z:treerow>
							</z:treeitem>
							<z:treeitem value="5">
								<z:treerow>
									<z:treecell label="Item 2.1.2C1" />
								</z:treerow>
							</z:treeitem>
						</z:treechildren>
					</z:treeitem>
					<z:treeitem value="6">
						<z:treerow>
							<z:treecell label="Item 2.2" />
							<z:treecell
								label="Item 2.2 is something who cares" />
						</z:treerow>
					</z:treeitem>
				</z:treechildren>
			</z:treeitem>
			<z:treeitem label="Item 3" value="7"/>
		</z:treechildren>
	</z:tree>		
	
	
	
				<h:commandButton id="submit" action="#{testTreeBean.doSubmit}" value="Submit" />
			</z:window>
			<h:messages/>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>
