<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/zul" prefix="zk" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Use MyWindow Class</title>
</head>
<body>
		
	<zk:page>
	<h2> Custom Component Class</h2>
	<p>this window component use <b>org.zkoss.jspdemo.MyWindow</b> as it's implementation.</p>
		<zk:window id="win4" width="550px" title="My Window" border="normal" use="org.zkoss.jspdemo.MyWindow">
			<zk:caption image="http://www.zkoss.org/favicon.ico" label="First Caption!!!">
				<zk:button id="winBtn"  
				image="img/yellow-trapa.png">
					<zk:attribute name="label">This is a Button</zk:attribute>
					<zk:attribute name="onClick">
						win4.doModal();
						win4.setStyle("color:red;");
						win4.title="Mode is changed!!!";
					</zk:attribute>
				</zk:button>
				<zk:button id="winBtn02"  
				image="img/red-trapa.png">
					<zk:attribute name="label">Embedded</zk:attribute>
					<zk:attribute name="onClick">
						win4.setMode("embedded");
						win4.title="Mode is Changed!!!";
					</zk:attribute>
				</zk:button>
			</zk:caption>
			(Window Component)Hello Window!!!
			<zk:separator bar="true"/>
			<zk:tabbox width="100%">
				<zk:tabs>
					<zk:tab label="Tab 1"/>
					<zk:tab label="Tab 2"/>
				</zk:tabs>
				
				<zk:tabpanels><%-- this is Jsp valid Comment --%>
					<zk:tabpanel>
						<zk:zscript>
						</zk:zscript>
						<zk:grid>
							<zk:columns sizable="true">
								<zk:column label="Type" />
								<zk:column label="Content"/>
							</zk:columns>
							<zk:rows>
								<zk:row>
									<zk:label value="File:"/>
									<zk:textbox width="99%"/>
								</zk:row>
								<zk:row>
									<zk:label value="Type:"/>
									<zk:hbox>
										<zk:listbox rows="1" mold="select">
											<zk:listitem label="Java Files,(*.java)"/>
											<zk:listitem label="All Files,(*.*)"/>
										</zk:listbox>
										<zk:button label="Browse..."/>
									</zk:hbox>
								</zk:row>
								<zk:row>
									<zk:label value="Options:"/>
									<zk:textbox rows="3" width="99%"/>
								</zk:row>
							</zk:rows>
						</zk:grid>
					</zk:tabpanel>
					
					
					<zk:tabpanel id="tree_panel">
					<zk:tree id="tree"  rows="10">
						<zk:treecols sizable="true">
							<zk:treecol label="Name"/>
							<zk:treecol label="Description"/>
						</zk:treecols>
						<zk:treechildren>
							<zk:treeitem>
								<zk:treerow>
									<zk:treecell label="Item 1"/>
									<zk:treecell label="Item 1 description"/>
								</zk:treerow>
							</zk:treeitem>
							<zk:treeitem>
								<zk:treerow>
									<zk:treecell label="Item 2"/>
									<zk:treecell label="Item 2 description"/>
								</zk:treerow>
								<zk:treechildren>
									<zk:treeitem>
										<zk:treerow>
											<zk:treecell label="Item 2.1"/>
										</zk:treerow>
										<zk:treechildren>
											<zk:treeitem>
												<zk:treerow>
													<zk:treecell label="Item 2.1.1"/>
												</zk:treerow>
											</zk:treeitem>
											<zk:treeitem>
												<zk:treerow>
													<zk:treecell label="Item 2.1.2"/>
												</zk:treerow>
											</zk:treeitem>
											<zk:treeitem>
												<zk:treerow>
													<zk:treecell label="Item 2.1.3"/>
												</zk:treerow>
											</zk:treeitem>
											<zk:treeitem>
												<zk:treerow>
													<zk:treecell label="Item 2.1.4"/>
												</zk:treerow>
											</zk:treeitem>
										</zk:treechildren>
									</zk:treeitem>
									<zk:treeitem>
										<zk:treerow>
											<zk:treecell label="Item 2.2"/>
											<zk:treecell label="Item 2.2 is something who cares"/>
										</zk:treerow>
									</zk:treeitem>
								</zk:treechildren>
							</zk:treeitem>
							<zk:treeitem label="Item 3"/>
						</zk:treechildren>
					</zk:tree>
					</zk:tabpanel>
				</zk:tabpanels>
			</zk:tabbox>

		</zk:window>
	</zk:page>
</body>
</html>