<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:z="http://www.zkoss.org/jsf/zul"
	>
<HTML>
<HEAD>
<title>Simple Example</title>
</HEAD>
<body>
<f:view>
	<z:page>
		<z:window title="fileupload demo" border="normal">
		    <z:button label="Upload">
			<z:attribute name="onClick">{
			    Object media = Fileupload.get();
			    if (media instanceof org.zkoss.image.Image) {
				Image image = new Image();
				image.setContent(media);
				image.setParent(pics);
			    } else if (media != null)
				Messagebox.show("Not an image: "+media, "Error",
				    Messagebox.OK, Messagebox.ERROR);
			    }
			</z:attribute>
		    </z:button>
		    <z:vbox id="pics"/>
		</z:window>		
		<a href="../index.html">Back</a>
	</z:page>
	
</f:view>
</body>
</HTML>
</jsp:root>
