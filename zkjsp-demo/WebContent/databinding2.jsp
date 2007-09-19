<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.zkoss.jspdemo.MyValue" %>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Use Annotated Data Binding</title>
</head>
<body>

<% 
//JSP scriplet to generate test data "my_list", can be easily wrapped to normal JSP tags...
final ArrayList my_list = new ArrayList();
final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
for(int i=0;i<10;i++)
{
	final String ref = "index: "+i;
	my_list.add(new MyValue(){
		public String getDate() {
			return format.format(new Date());
		}
		public String getValue() {
			return ref;
		}
	});
}
request.setAttribute("my_list",my_list);// use Request to store and pass data.

%>
<z:page>
<z:zscript>
	System.out.println("fire when load page..." );
	my_list = requestScope.get("my_list");// get data from request and declare page variable "my_list" implicitly.
	String main_name = "test Variable";
</z:zscript>

<z:window id="mainPanel" width="600px"  title="RSS Reader with Aggregate Feed"  border="normal" >
<z:caption id="mainCap" label="Add New Feed">
	<z:button id="cancelBtn" label="test">
		<z:attribute name="onClick">
			System.out.println("zscript.pageVariable()= "+current_date );
		</z:attribute>
	</z:button>
			<z:zscript>
			cancelBtn.setLabel(main_name);
			System.out.println(page.getVariable("main_name"));
		</z:zscript>
</z:caption>
	<%-- same as original ZK databinding...--%>
	<z:listbox id="feedListBox" model="@{my_list}" rows="8" selectedItem="@{selected}">
		<z:listitem self="@{each='my'}">
			<z:listcell label="@{my.value}"/>
			<z:listcell label="@{my.date}"/>
		</z:listitem>
	</z:listbox>
</z:window>
</z:page>
</body>
</html>