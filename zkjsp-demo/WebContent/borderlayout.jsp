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
			<z:borderlayout height="500px">
				<z:north id="north" size="50%" border="0" splittable="true" collapsible="true">
					<z:borderlayout>
						<z:west id="firstWest" use="org.zkoss.jspdemo.MyWest" size="25%" border="none" 
							flex="true" maxsize="250" splittable="true" collapsible="true">
							<z:div id="div" style="background:#B8D335">
								<z:label value="25%"
									style="color:white;font-size:50px" />
									<p>This is MyWest Component!</p>
							</z:div>
						</z:west>
						<z:center border="none" flex="true">
							<z:div style="background:#E6D92C">
								<z:label value="25%"
									style="color:white;font-size:50px" />
								<p>Remember, ZK LayoutRegion can only contain one ZK Component as child.</p>
							</z:div>
						</z:center>
						<z:east size="50%" border="none" flex="true">
							<z:label value="Here is a non-border"
								style="color:gray;font-size:30px" />
							<%-- So, if you add any component here will get into trouble. --%>
						</z:east>
					</z:borderlayout>
				</z:north>
				<z:center border="0">
					<z:borderlayout>
						<z:west maxsize="600" minsize="300" size="30%" flex="true" border="0" splittable="true">
							<z:div style="background:#E6D92C">
								<z:label value="30%"
									style="color:white;font-size:50px" />
							</z:div>
						</z:west>
						<z:center>
							<z:label value="Here is a border"
								style="color:gray;font-size:30px" />
						</z:center>
						<z:east size="30%" flex="true" border="0" collapsible="true">
							<z:div style="background:#B8D335">
								<z:label value="30%"
									style="color:white;font-size:50px" />
							</z:div>
						</z:east>
					</z:borderlayout>
				</z:center>
			</z:borderlayout>
   <z:button label="enlarge north" onClick='north.setSize("500px")'/>
   <z:button label="downsize north" onClick='north.setSize("50%")'/>
   <z:button label="downsize north" onClick='north.setSize("50%")'/>
   <z:button label="collapse north" onClick='north.setOpen(false)'/>
   <z:button label="open north" onClick='north.setOpen(true)'/>
   <br/>
   <z:button label="collapse upper-west" onClick='firstWest.setOpen(false)'/>
   <z:button label="open upper-west" onClick='firstWest.setOpen(true)'/>
   <br/>
   <z:button label="change color of upper-west" onClick='div.setStyle("background: red")'/>
</z:page>
</body>
</html>