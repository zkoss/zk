<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/zul" prefix="zk" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Window Mode Demo</title>
</head>
<body>
	<zk:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>

		
		<h2>Window Mode Demo</h2>
		<p>Same as zkdemo userguide's Window Component Demo</p>
		<zk:window id="win2" title="ZK is best!!!" width="600px" border="normal">
			
				<zk:window id="win3" border="normal" width="350px" sizable="true">
					<zk:caption image="/img/yellow-trapa.png" label="Hi there!"/>
					<zk:checkbox label="Hello, Wolrd!"/>
					<zk:separator bar="true"/>
						Auto-position (applicable if not embedded)
					<zk:separator/>
					<zk:button label="left,center" onClick="win3.position = &quot;left,center&quot;;"/>
					<zk:button label="right,bottom" onClick="win3.position = &quot;right,bottom&quot;;"/>
					<zk:button label="center" onClick="win3.position = &quot;center&quot;;"/>
				</zk:window>
				<zk:button label="Overlap" onClick="win3.doOverlapped();"/>
				<zk:button label="Popup" onClick="win3.doPopup();"/>
				<zk:button label="Embbeded" onClick="win3.doEmbedded();"/>
		</zk:window>
	</zk:page>
</body>
</html>