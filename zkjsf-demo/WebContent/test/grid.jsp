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
			<z:window title="Grid Demo">
			  Test common grid and auxhead.
			  <z:checkbox label="sizable" onCheck="cs.sizable = self.checked"/>
				<z:grid>
					<z:auxhead>
						<z:auxheader label="A+B" colspan="2"/>
						<z:auxheader label="C"/>
					</z:auxhead>	
					<z:auxhead>
						<z:auxheader label="A"/>
						<z:auxheader label="B+C"  colspan="2"/>
					</z:auxhead>
					<z:columns id="cs">
						<z:column label="AA"/>
						<z:column label="BB"/>
						<z:column label="CC"/>
					</z:columns>
					<z:auxhead>
						<z:auxheader label="A+B+C" colspan="3"/>
					</z:auxhead>
					<z:rows>
						<z:row>
							<z:label value="AA01"/>
							<z:label value="BB01"/>
							<z:label value="CC01"/>
						</z:row>
						<z:row>
							<z:label value="AA01"/>
							<z:label value="BB01"/>
							<z:label value="CC01"/>
						</z:row>
						<z:row>
							<z:label value="AA01"/>
							<z:label value="BB01"/>
							<z:label value="CC01"/>
						</z:row>
					</z:rows>
				</z:grid>
			</z:window>	
		</z:page>
	</h:form>
	
</f:view>
</body>
</HTML>