<HTML>
<HEAD>
<title>Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window title="border layout">
			<z:borderlayout height="500px">
				<z:north maxsize="300" size="50%" border="0" splittable="true" collapsible="true">
					<z:borderlayout>
						<z:west size="25%" border="none" flex="true" maxsize="550" splittable="true" collapsible="true">
							<z:window title="foo" width="300px">
									Click button A:
									<z:button id="btn" label="click me" onClick="click1()"/>
									Click button B:
									<z:zscript>
										void click1(){
											alert("click 1");
										}
									</z:zscript>
							</z:window>
						</z:west>
						<z:center border="none" flex="true">
							<z:window title="foo">
								Click button C:
								<z:button label="click me" onClick="click2()"/>
								Click button D:
								<z:zscript>
									void click2(){
										alert("click 2");
									}
								</z:zscript>
							</z:window>
						</z:center>
						<z:east size="50%" flex="true" border="none">
							<z:window title="foo">
								<z:button label="click me" onClick="click3()"/>
								<z:zscript>
									void click3(){
										alert("click 3");
									}
								</z:zscript>
							</z:window>
						</z:east>
					</z:borderlayout>
				</z:north>
				<z:center border="0">
					<z:vbox>
					<h:panelGrid columns="1">
						<z:window title="foo">
							<f:verbatim>Click button E:</f:verbatim>
							<z:button label="click me" onClick="click4()"/>
							<f:verbatim>Click button F:</f:verbatim>
							<z:zscript>
								void click4(){
									alert("click 4");
								}
							</z:zscript>
						</z:window>
					</h:panelGrid>
					</z:vbox>
				</z:center>
			</z:borderlayout>
			<h:commandButton id="commitBtn" value="Commit" />
			</z:window>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>