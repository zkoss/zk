<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">

<HTML>

<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<TITLE>Reservation of Conference Room</TITLE>
<link rel="stylesheet" type="text/css"
	href='<%= request.getContextPath() + "/stylesheet.css" %>'>
</HEAD>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>

<BODY BGCOLOR="white">

<f:view>
	<z:page>
		<h:form id="form1">
			<h:panelGrid columns="1">
				<h:outputText styleClass="bigtitle" value="Reserveation Result" />
				<z:grid>
					<z:rows>
						<z:row>
							<h:outputText styleClass="title2" value="Conference Room:" />
							<z:label value="#{ReservationBean.room}" />
						</z:row>
						<z:row>
							<h:outputText styleClass="title2" value="Employee ID:" />
							<z:label value="#{ReservationBean.eid}" />
						</z:row>
						<z:row>
							<h:outputText styleClass="title2" value="You Name:" />
							<z:label value="#{ReservationBean.name}" />
						</z:row>
						<z:row>
							<h:outputText styleClass="title2" value="Reservated Date:" />
							<z:label value="#{ReservationBean.resvDate}" />
						</z:row>
						<z:row>
							<h:outputText styleClass="title2" value="Reservated Time:" />
							<z:label value="#{ReservationBean.resvTime}" />
						</z:row>
						<z:row>
							<h:outputText styleClass="title2" value="Contact Phone:" />
							<z:label id="phone" value="#{ReservationBean.contactPhone}" />
						</z:row>
					</z:rows>
				</z:grid>
				<h:commandButton id="commitBtn" action="#{ReservationBean.doBack}"
					value="Back" />

			</h:panelGrid>

		</h:form>
	</z:page>
</f:view>
</BODY>

</HTML>
