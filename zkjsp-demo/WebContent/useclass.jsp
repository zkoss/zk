<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Use MyWindow Class</title>
</head>
<body>
		
	<z:page>
	<h2> Custom Component Class</h2>
	<p>this window component use <b>org.zkoss.jspdemo.MyWindow</b> as it's implementation.</p>
		<z:window id="win4" width="550px" title="My Window" border="normal" use="org.zkoss.jspdemo.MyWindow">
			<z:caption image="http://www.zkoss.org/favicon.ico" label="First Caption!!!">
				<z:button id="winBtn" self="@{ann1(opi=kjl)}" asdwer="@{ann1(selected.name) ann2(attr2a='attr2a',attr2b)}"
				image="img/yellow-trapa.png">
					<z:attribute name="label">This is a Button</z:attribute>
					<z:attribute name="onClick">
						win4.doModal();
						win4.setStyle("color:red;");
						win4.title="Mode is changed!!!";
					</z:attribute>
				</z:button>
				<z:button id="winBtn02"  
				image="img/red-trapa.png">
					<z:attribute name="label">Embedded</z:attribute>
					<z:attribute name="onClick">
						win4.setMode("embedded");
						win4.title="Mode is Changed!!!";
					</z:attribute>
				</z:button>
			</z:caption>
			(Window Component)Hello Window!!!
			<z:separator bar="true"/>
			<z:tabbox width="100%">
				<z:tabs>
					<z:tab label="Tab 1"/>
					<z:tab label="Tab 2"/>
				</z:tabs>
				
				<z:tabpanels><%-- this is Jsp valid Comment --%>
					<z:tabpanel>
						<z:zscript>
						</z:zscript>
						<z:grid>
							<z:columns sizable="true">
								<z:column label="Type" />
								<z:column label="Content"/>
							</z:columns>
							<z:rows>
								<z:row>
									<z:label value="File:"/>
									<z:textbox width="99%"/>
								</z:row>
								<z:row>
									<z:label value="Type:"/>
									<z:hbox>
										<z:listbox rows="1" mold="select">
											<z:listitem label="Java Files,(*.java)"/>
											<z:listitem label="All Files,(*.*)"/>
										</z:listbox>
										<z:button label="Browse..."/>
									</z:hbox>
								</z:row>
								<z:row>
									<z:label value="Options:"/>
									<z:textbox rows="3" width="99%"/>
								</z:row>
							</z:rows>
						</z:grid>
					</z:tabpanel>
					
					
					<z:tabpanel id="tree_panel">
					<z:tree id="tree"  rows="10">
						<z:treecols sizable="true">
							<z:treecol label="Name"/>
							<z:treecol label="Description"/>
						</z:treecols>
						<z:treechildren>
							<z:treeitem>
								<z:treerow>
									<z:treecell label="Item 1"/>
									<z:treecell label="Item 1 description"/>
								</z:treerow>
							</z:treeitem>
							<z:treeitem>
								<z:treerow>
									<z:treecell label="Item 2"/>
									<z:treecell label="Item 2 description"/>
								</z:treerow>
								<z:treechildren>
									<z:treeitem>
										<z:treerow>
											<z:treecell label="Item 2.1"/>
										</z:treerow>
										<z:treechildren>
											<z:treeitem>
												<z:treerow>
													<z:treecell label="Item 2.1.1"/>
												</z:treerow>
											</z:treeitem>
											<z:treeitem>
												<z:treerow>
													<z:treecell label="Item 2.1.2"/>
												</z:treerow>
											</z:treeitem>
											<z:treeitem>
												<z:treerow>
													<z:treecell label="Item 2.1.3"/>
												</z:treerow>
											</z:treeitem>
											<z:treeitem>
												<z:treerow>
													<z:treecell label="Item 2.1.4"/>
												</z:treerow>
											</z:treeitem>
										</z:treechildren>
									</z:treeitem>
									<z:treeitem>
										<z:treerow>
											<z:treecell label="Item 2.2"/>
											<z:treecell label="Item 2.2 is something who cares"/>
										</z:treerow>
									</z:treeitem>
								</z:treechildren>
							</z:treeitem>
							<z:treeitem label="Item 3"/>
						</z:treechildren>
					</z:tree>
					</z:tabpanel>
				</z:tabpanels>
			</z:tabbox>

		</z:window>
	</z:page>
</body>
</html>