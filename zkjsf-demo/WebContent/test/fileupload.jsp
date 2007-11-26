<HTML>
<HEAD>
<title>Fileupload Example</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
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
