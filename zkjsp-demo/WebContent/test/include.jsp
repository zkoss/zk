<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Use Annotated Data Binding</title>
</head>
<body>


<z:page id="includeMom">
<z:zscript>
	System.out.println("fire when load page..." );
	System.out.println(page.getId()+"'s file is: include.jsp" );
	System.out.println(page.getId()+"'s current desktop is:"+desktop );
	
</z:zscript>

<z:window id="mainPanel" width="600px"  title="include demo"  border="normal" >
<z:caption id="mainCap" label="Add New Feed">
	<z:button id="cancelBtn" label="test">
		<z:attribute name="onClick">
			System.out.println("zscript.pageVariable()= "+current_date );
		</z:attribute>
	</z:button>
</z:caption>

Hello, World!
<z:include src="/test/include1.zul"/>

<z:include src="/test/include1.jsp"/>

</z:window>
</z:page>
</body>
</html>