<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/zul" prefix="zk" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ZK Jsp Tag Library 0.9.0</title>
</head>
<body>
<h1>First zul + jsp!!!</h1>

	<zk:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>

		
		<h3>ZK Upload Demo</h3>
		
		<zk:window id="win3" title="ZK is best!!!" border="normal" width="550px">
		<p>Same as zkdemo userguide's Upload demo. press upload key to upload an image.</p>
				<zk:button label="Upload">
					<zk:attribute name="onClick">{
						Object media = Fileupload.get();
						if (media instanceof org.zkoss.image.Image) {
							Image image = new Image();
							image.setContent(media);
							image.setParent(pics);
						} else if (media != null)
							Messagebox.show("Not an image: "+media, "Error", Messagebox.OK, Messagebox.ERROR);
					}</zk:attribute>
				</zk:button>
				<zk:vbox id="pics"/>
		</zk:window>
		
		<h3>Other Demos</h3>
		
		<zk:window id="win" title="ZK jsp demo" width="550px" border="normal">
			<zk:vbox>
		Here is the list of ZK Jsp Tag Library demos. you can click links bellow or 
		view source code to see how it works.
			<div style="margin-top: 10px">
				<zk:hbox>
					JSTL forEach works with ZK: <zk:toolbarbutton href="./foreach.jsp" label="forEach Demo"/>
				</zk:hbox>
			</div>
			<div style="margin-top: 10px">
				<zk:hbox>
					Use class to define customized ZK Component: <zk:toolbarbutton href="./useclass.jsp" label="use class Demo"/>
				</zk:hbox>
			</div>
			<div style="margin-top: 10px">
			
				<zk:hbox>
					ZK Drag and Drop features: <zk:toolbarbutton href="./draganddrop.jsp" label="Drag and Drop Demo"/>
				</zk:hbox>
			</div>
			<div style="margin-top: 10px">
				<zk:hbox>
					ZK Capcha Component in jsp: <zk:toolbarbutton href="./capcha.jsp" label="Capcha Demo"/>
				</zk:hbox>
			</div>			
			<div style="margin-top: 10px">
				<zk:hbox>
					ZK Window Component Mode setting in zscript: <zk:toolbarbutton href="./windowmode.jsp" label="Window Mode Demo"/>
				</zk:hbox>
			</div>
			</zk:vbox>
		</zk:window>		
		
	</zk:page>
</body>
</html>