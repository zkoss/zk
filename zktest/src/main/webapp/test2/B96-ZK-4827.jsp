<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
B96-ZK-4827.jsp

	Purpose:

	Description:

	History:
		Fri Mar 19 10:30:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.

-->
<%@page import="org.zkoss.zk.ui.*"%>
<%@page import="org.zkoss.zul.*"%>
<%@page import="org.zkoss.zkplus.embed.Renders"%>
<%@page import="java.util.*"%>
<html>
	<head>
		<title>Test of Embedded Component</title>
	</head>
	<body style="height:auto">
		<p>This is a test of embed component: listbox. (Should be loaded successfully) </p>
		<%
	final HttpSession sess = session;
	Renders.render(config.getServletContext(), request, response,
		new GenericRichlet() {
	public void service(Page page) {
		Listbox listbox = new Listbox();
		listbox.appendChild(new Listitem("Item 1"));
		listbox.appendChild(new Listitem("Item 2"));
		listbox.setPage(page);
	}
		}, null, out);
		%>
		<p>This is a test of embed component: datebox. (Should be loaded successfully) </p>
		<%
	Datebox db = new Datebox();
	Renders.render(config.getServletContext(), request, response, db, null, out);
		%>
	</body>
</html>