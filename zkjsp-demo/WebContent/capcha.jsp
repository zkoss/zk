<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>A simple Capcha Demo</title>
</head>
<body>
<z:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>
		<h2>ZK Jsp Capcha Demo</h2>
		
		<p>A simple Capcha component demo. <br/>Same as the demo in zkdemo site.</p>
		<z:window id="win1" title="ZK is best!!!" border="normal" width="280px">
			<z:vbox>
				<z:captcha id="cpa" length="5" width="200px" height="50px"/>
				<z:hbox>
					<z:button label="Regenerate" onClick="cpa.randomValue(); val.value=cpa.value;"/>
					:<z:label id="val"/>
				</z:hbox>
				<z:hbox>Assign one: <z:textbox onChange="cpa.value = self.value; val.value=self.value;"/></z:hbox>
				<z:zscript>val.value=cpa.value;</z:zscript>
			</z:vbox>
		</z:window>
</z:page>
</body>
</html>