<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.zkoss.zk.ui.http.Standalones"%>
<%@page import="org.zkoss.zul.Calendar"%>
<%@page import="org.zkoss.zul.Listbox"%>
<%@page import="org.zkoss.zul.Listitem"%>
<html>
	<head>
		<title>Test of Standalone Component</title>
	</head>
	<body style="height:auto">
		<p>This is a test of standalone component: calendar.</p>
		<%
	Calendar cal = new Calendar();
	Standalones.output(config.getServletContext(), request, response, cal, null, out);
		%>
		<p>This is another component: listbox</p>
		<%
	Listbox listbox = new Listbox();
	listbox.appendChild(new Listitem("Item 1"));
	listbox.appendChild(new Listitem("Item 2"));
	Standalones.output(config.getServletContext(), request, response, listbox, null, out);
		%>
	</body>
</html>