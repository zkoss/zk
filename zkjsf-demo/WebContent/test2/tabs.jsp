<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form>

		<z:page>
			<z:window>
				<z:tabbox width="400px">
					<z:tabs>
						<z:tab label="Text" />
						<z:tab label="Button" />
						<z:tab label="Message" />
						<z:tab label="More Tabs"/>
					</z:tabs>
					<z:tabpanels>
						<z:tabpanel>
							<h:inputText value="#{ActionBean.value}"></h:inputText>
						</z:tabpanel>
						<z:tabpanel>
							<h:commandButton id="submit" action="#{ActionBean.doSubmit}"
								value="Submit" />

						</z:tabpanel>
						<z:tabpanel>
							<h:messages></h:messages>
						</z:tabpanel>
						<z:tabpanel>
							<z:tabbox orient="vertical">
							<z:tabs>
							<z:tab label="A" />
							<z:tab label="B" />
							<z:tab label="C" />
							<z:tab label="D" />
							<z:tab label="E" />
							</z:tabs>
							<z:tabpanels width="300px">
							<z:tabpanel>This is panel A</z:tabpanel>
							<z:tabpanel>This is panel B</z:tabpanel>
							<z:tabpanel>This is panel C</z:tabpanel>
							<z:tabpanel>This is panel D</z:tabpanel>
							<z:tabpanel>This is panel E</z:tabpanel>
							</z:tabpanels>
							</z:tabbox>
						</z:tabpanel>
					</z:tabpanels>
				</z:tabbox>
			</z:window>
		</z:page>
	</h:form>
</f:view>

</body>
</html>
