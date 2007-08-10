<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/2005/zul/jsp" prefix="z" %>
<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit"/>
<z:init use="org.zkoss.jspdemo.MyInit"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Use Annotated Data Binding</title>
</head>
<body>
<%--***** remember change it to your location *****--%> 
<z:page>
<z:zscript>
	System.out.println("fire when load page..." );
</z:zscript>


<z:window id="mainPanel" width="600px"  title="RSS Reader with Aggregate Feed"  border="normal" >
<z:caption id="mainCap" label="Add New Feed">
	<z:button id="cancelBtn" label="test">
		<z:attribute name="onClick">
			System.out.println("zscript.pageVariable()= "+current_date );
		</z:attribute>
	</z:button>
</z:caption>

	<z:listbox id="feedListBox" model="@{my_list}" rows="8" selectedItem="@{selected}">

		<z:listitem self="@{bind(each='my')}">
			<z:listcell label="@{my.value}"/>
			<z:listcell label="@{my.date}"/>
		</z:listitem>
	</z:listbox>
</z:window>
<z:zscript deferred="true">
	System.out.println("Trigger Defferd ZScript...");
</z:zscript>
</z:page>
</body>
</html>