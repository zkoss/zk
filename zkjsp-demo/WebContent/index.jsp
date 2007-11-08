<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ZK JSP Tag Library Demo</title>
</head>
<body>
<h1>ZK JSP Tag Library Demo</h1>

	<z:page zscriptLanguage="java"><%-- this is JSP valid Comment, do not use XML comment. --%>

		
		<h3>ZK Upload Demo:</h3>
		
		<z:window id="win3" title="Fileupload Demo" border="normal" width="550px">
		<p>Same as zkdemo userguide's Upload demo. press upload key to upload an image.</p>
				<z:button label="Upload">
					<z:attribute name="onClick">{
						Object media = Fileupload.get();
						if (media instanceof org.zkoss.image.Image) {
							Image image = new Image();
							image.setContent(media);
							image.setParent(pics);
						} else if (media != null)
							Messagebox.show("Not an image: "+media, "Error", Messagebox.OK, Messagebox.ERROR);
					}</z:attribute>
				</z:button>
				<z:vbox id="pics"/>
		</z:window>
		
		<h3>Other Demos:</h3>
		
		<z:window id="win" title="ZK JSP demo" width="550px" border="normal">
			<z:vbox>
		Here is the list of ZK JSP Tag Library demos. 
			<div style="margin-top: 10px">
				<z:hbox>
					JSTL forEach works with ZK: <z:toolbarbutton href="./foreach.jsp" label="forEach Demo"/>
				</z:hbox>
			</div>
			<div style="margin-top: 10px">
				<z:hbox>
					Use class to define customized ZK Component: <z:toolbarbutton href="./useclass.jsp" label="use class Demo"/>
				</z:hbox>
			</div>
			<div style="margin-top: 10px">
			
				<z:hbox>
					ZK Drag and Drop features: <z:toolbarbutton href="./draganddrop.jsp" label="Drag and Drop Demo"/>
				</z:hbox>
			</div>
			<div style="margin-top: 10px">
				<z:hbox>
					ZK Capcha Component in jsp: <z:toolbarbutton href="./capcha.jsp" label="Capcha Demo"/>
				</z:hbox>
			</div>			
			<div style="margin-top: 10px">
				<z:hbox>
					ZK Window Component Mode setting in zscript: <z:toolbarbutton href="./windowmode.jsp" label="Window Mode Demo"/>
				</z:hbox>
			</div>		
			<div style="margin-top: 10px">
				<z:hbox>
					ZK Borderlayout component set from ZKEX: <z:toolbarbutton href="./borderlayout.jsp" label="Borderlayout Demo"/>
				</z:hbox>
			</div>	
			<div style="margin-top: 10px">
				<z:hbox>
					ZK Auxheader Demo from ZKEX: <z:toolbarbutton href="./auxheader.jsp" label="Auxheader Demo"/>
				</z:hbox>
			</div>	
			</z:vbox>
		</z:window>		
		
	</z:page>
</body>
</html>