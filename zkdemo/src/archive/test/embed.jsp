<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.zkoss.zk.ui.*"%>
<%@page import="org.zkoss.zul.*"%>
<%@page import="org.zkoss.zkplus.embed.Renders"%>
<%@page import="java.util.*"%>
<html>
	<head>
		<title>Test of Embedded Component</title>
	</head>
	<body style="height:auto">
		<p>This is a test of embed component: listbox.</p>
		<%
	final HttpSession sess = session;
	Renders.render(config.getServletContext(), request, response,
		new GenericRichlet() {
	public void service(Page page) {
		Listbox listbox = new Listbox();
		listbox.appendChild(new Listitem("Item 1"));
		listbox.appendChild(new Listitem("Item 2"));
		listbox.setPage(page);

		Map map = (Map)sess.getAttribute("desktops");
		if (map == null)
			sess.setAttribute("desktops", map = new HashMap());
		Desktop desktop = page.getDesktop();
		map.put(desktop.getId(), desktop);
			//stupid way to pass to Servlet, but it's for test purpose
	}
		}, null, out);
		%>
		<script>
		function ajaxClick() {
		 jq.ajax({
			   type: 'POST',
			   url: zk.ajaxURI('/embed'),
			   dataType: 'text',
			   data: 'desktop=' + zk.Desktop.$().id,
			   success: function (jscode) {
					eval(jscode);
				}
			 });
	}
		</script>

		This is regular button to send Ajax thru jQuery (not AU)
		<button type="button" onclick="ajaxClick()">click me</button>

		<p>This is a test of embed component: datebox.</p>
		<%
	Datebox db = new Datebox();
	Renders.render(config.getServletContext(), request, response, db, null, out);
		%>
	</body>
</html>