<HTML>
<HEAD>
<title>Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			
<z:window title="textbox with constraints" border="normal">
	<z:grid width="90%">
	<z:rows>
		<z:row>
			textbox: <z:textbox f:value="text..."/>
		</z:row>
		<z:row>int box:<z:intbox/></z:row>
		<z:row>decimal box:<z:decimalbox format="#,##0.##"/></z:row>
		<z:row>date box:
			<z:hbox>
			<z:datebox id="db"/>
			<z:listbox onSelect='db.setFormat(self.selectedItem.value)' mold="select" rows="1">
				<z:listitem label="Default" value=""/>
				<z:listitem label="yyyy/MM/dd" value="yyyy/MM/dd"/>
				<z:listitem label="MM-dd-yy" value="MM-dd-yy"/>
			</z:listbox>
			</z:hbox>
		</z:row>
		<z:row>positive int box:<z:intbox constraint="no negative,no zero"/></z:row>
		<z:row>e-mail:<z:textbox constraint="/.+@.+\.[a-z]+/: Please enter an e-mail address"/></z:row>
		<z:row>
			multiple lines: 
			<z:textbox rows="5" cols="40">
				<z:attribute name="value">
text line1...
text line2...
 				</z:attribute>
 			</z:textbox>
		</z:row>		
		<z:row>
			Selection event: 
			<z:vbox>
				<z:textbox id="sel" rows="5" cols="40" >
					<z:attribute name="value">Welcome John !</z:attribute>
					<z:attribute name="onSelection">
						int start = event.start;
						int end = event.end;
						msg.setValue("Start: "+start+" , End:"+end);
					</z:attribute>
				</z:textbox>
				<z:button label="Replace" onClick="replace()"/>
				<z:label id="msg"/>
			</z:vbox>
		</z:row>
	</z:rows>
	</z:grid>
<z:zscript>
	int start,end;
	void replace(){
		sel.setSelectedText(start,end,"Mary",true);
	}
</z:zscript>
</z:window>
							
			
			
			
			
			
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>