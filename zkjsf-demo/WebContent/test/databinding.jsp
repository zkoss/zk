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
		
		<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit" />
		<z:window width="300px">
			<z:zscript>
			  	import org.zkoss.jsfdemo.test.Person;
			    
			    java.util.List persons = new java.util.ArrayList();
			    Person selected = null;
			    persons.add(new Person("Dennis","Chen"));
			    persons.add(new Person("Grace","Wu"));
			    persons.add(new Person("Jean","Lai"));
			    var i;
			</z:zscript>
			<z:listbox model="@{persons}" selectedItem="@{selected}" rows="4">
				<z:listitem self="@{each='person'}">
					<z:listcell>
						<z:textbox value="@{person.firstName}" />
					</z:listcell>
					<z:listcell label="@{person.lastName}"/>
				</z:listitem>
			</z:listbox>
			
			<!-- show the detail of the selected person -->
			<z:grid>
				<z:rows>
					<z:row>
						First Name:
						<z:textbox value="@{selected.firstName}"/>
					</z:row>
					<z:row>
						Last Name:
						<z:textbox value="@{selected.lastName}"/>
					</z:row>
					<z:row>
						Full Name:
						<z:textbox value="@{selected.fullName}"/>
					</z:row>
				</z:rows>
			</z:grid>
		</z:window>
		
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>