<HTML>
<HEAD>
<title>Simple Example</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<z:page>
		<z:window title="click counter" border="normal">
			<z:label id="lab"/>
			<br/>
		    <z:button label="Upload">
				<z:attribute name="onClick">
				    lab.value = "Count:"+ (++count);
				</z:attribute>
		    </z:button>
		    <z:vbox id="pics"/>
		    <z:zscript>
		    	int count = 0;
		    	lab.value = "Count:"+count;
		    </z:zscript>
		</z:window>
		<a href="../index.html">Back</a>
	</z:page>
	
</f:view>
</body>
</HTML>
