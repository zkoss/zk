<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/zul" prefix="zk" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>A simple Capcha Demo</title>
</head>
<body>
<zk:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>
		<h2>ZK Jsp Capcha Demo</h2>
		
		<p>A simple Capcha component demo. <br/>Same as the demo in zkdemo site.</p>
		<zk:window id="win1" title="ZK is best!!!" border="normal" width="280px">
			<zk:vbox>
				<zk:captcha id="cpa" length="5" width="200px" height="50px"/>
				<zk:hbox>
					<zk:button label="Regenerate" onClick="cpa.randomValue(); val.value=cpa.value;"/>
					:<zk:label id="val"/>
				</zk:hbox>
				<zk:hbox>Assign one: <zk:textbox onChange="cpa.value = self.value; val.value=self.value;"/></zk:hbox>
				<zk:zscript>val.value=cpa.value;</zk:zscript>
			</zk:vbox>
		</zk:window>
</zk:page>
</body>
</html>