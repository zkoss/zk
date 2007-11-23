<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Apply Attribute</title>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
</head>
<body>
<f:view>
	<h:form>
		For testing composer and submit
		<z:page>
			<z:window id="myWin" apply="org.zkoss.jsfdemo.test2.MyApplier">
				<z:grid>
					<z:rows>
						<z:row>
							<z:div>
							<z:checkbox id="dac" f:value="#{MyApplierBean.doAfterComposeFlag}" /> 
							doAfterCompose
							</z:div>
						</z:row>
						<z:row>
							<z:div>
							<z:checkbox id="df" f:value="#{MyApplierBean.value}" /> 
							normal checkbox
							</z:div>
						</z:row>
						<z:row>
							<h:commandButton id="submit" action="#{MyApplierBean.doSubmit}"
					value="Submit" />
						</z:row>
						<z:row>	<h:messages></h:messages>
						</z:row>
					</z:rows>
				</z:grid>
			</z:window>
		</z:page>

	</h:form>
</f:view>
</body>
</html>
