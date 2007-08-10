<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Window Mode Demo</title>
</head>
<body>
	<z:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>

		
		<h2>Window Mode Demo</h2>
		<p>Same as zkdemo userguide's Window Component Demo</p>
		<z:window id="win2" title="ZK is best!!!" width="600px" border="normal">
			
				<z:window id="win3" border="normal" width="350px" sizable="true">
					<z:caption image="/img/yellow-trapa.png" label="Hi there!"/>
					<z:checkbox label="Hello, Wolrd!"/>
					<z:separator bar="true"/>
						Auto-position (applicable if not embedded)
					<z:separator/>
					<z:button label="left,center" onClick="win3.position = &quot;left,center&quot;;"/>
					<z:button label="right,bottom" onClick="win3.position = &quot;right,bottom&quot;;"/>
					<z:button label="center" onClick="win3.position = &quot;center&quot;;"/>
				</z:window>
				<z:button label="Overlap" onClick="win3.doOverlapped();"/>
				<z:button label="Popup" onClick="win3.doPopup();"/>
				<z:button label="Embbeded" onClick="win3.doEmbedded();"/>
		</z:window>
	</z:page>
</body>
</html>