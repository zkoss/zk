<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<%
	//prevent cache.
	response.addHeader("Pragma","no-cache");
	response.addHeader("Cache-Control","no-cache");
	response.addHeader("Cache-Control","no-store");
%>
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
			<h:panelGrid columns="1" >
				<h:outputText styleClass="bigtitle" value="Reserve the Conference Room" />
				<z:hbox >
				<h:outputText styleClass="title" value="Conference Room:" />
				<z:listbox id="room" mold="select" onSelect="changeRoom(self)"
					f:value="#{ReservationBean.room}"
					f:validator="#{ReservationBean.validateRoom}">
					<z:listitem value="" label="--select conference room--"/>
					<z:listitem value="Mannheim" label="Mannheim"/>
					<z:listitem value="Balears" label="Balears"/>
				</z:listbox>
				<h:message style="color: red; font-style: oblique;" for="room" />				
				</z:hbox>
				<h:panelGrid columns="2" style="border:solid 1px">
					<z:vbox>
						<z:label value="Details"/>
						<z:grid >
							<z:columns>
								<z:column width="200px"/>
								<z:column width="100px"/>
							</z:columns>
							<z:rows>
								<z:row>
									<z:label value="Seat number:"/>
									<z:label id="seat" value=""/>
								</z:row>
								<z:row>
									<z:label value="Projector:"/>
									<z:label id="projector" value=""/>
								</z:row>
								<z:row>
									<z:label value="Video-Conference"/>
									<z:label id="vconference" value=""/>
								</z:row>
								<z:row>
									<z:label value="Network"/>
									<z:label id="network" value=""/>
								</z:row>
								<z:row>
									<z:label value="Telephone"/>
									<z:label id="telephone" value=""/>
								</z:row>
							</z:rows>
						</z:grid>
					</z:vbox>
					<z:image id="roomimg" src="/images/empty.jpg" />
				</h:panelGrid>
				<h:panelGrid columns="3" style="border:solid 1px">
					<h:outputText styleClass="title" value="*Employee ID:"/>
					<z:textbox id="eid"  f:required="true" f:value="#{ReservationBean.eid}" onChange="eidChange()"/>
					<h:message style="color: red; font-style: oblique;" for="eid" />
					
					<h:outputText styleClass="title" value="*You Name:" />
					<z:textbox id="name" f:value="#{ReservationBean.name}" f:required="true"/>
					<h:message style="color: red; font-style: oblique;" for="name" />
					
					<h:outputText styleClass="title" value="*Date to reserve:" />
					
					<z:datebox id="date" format="yyyy/MM/dd" f:required="true" f:value="#{ReservationBean.resvDate}"/>

					<z:hbox>
					<h:message style="color: red; font-style: oblique;" for="date" />
					<z:button label="check free time" id="cfbtn" onClick="checkFree()"/>
					</z:hbox>
					
					<h:outputText styleClass="title" value="*Time to reserve:" />
					<z:listbox id="time" mold="select" f:required="true" f:value="#{ReservationBean.resvTime}">
						<z:listitem value="08" label="08:00"/>
						<z:listitem value="10" label="10:00"/>
						<z:listitem value="12" label="12:00"/>
						<z:listitem value="14" label="14:00"/>
						<z:listitem value="16" label="16:00"/>
						<z:listitem value="18" label="18:00"/>
					</z:listbox>
					<h:message style="color: red; font-style: oblique;" for="time" />
					
					
					
					<h:outputText styleClass="title" value="Contact PhoneExt:" />
					<z:textbox id="phoneext" f:value="#{ReservationBean.contactPhone}" />
					<h:message style="color: red; font-style: oblique;" for="phoneext" />
				
					
				</h:panelGrid>
				
				<h:message style="color: red; font-style: oblique;" for="commitBtn" />
				<h:commandButton id="commitBtn" action="#{ReservationBean.doReservation}" value="Commit" />
				
			</h:panelGrid>

		</h:form>
		<z:zscript>
			
			void changeRoom(lb){
				if("Mannheim".equals(lb.selectedItem.value)){
					seat.value = "12";
					projector.value = "EMP TWD3";
					vconference.value = "YES";
					network.value = "Wire,Wireless";
					telephone.value = "#1011";
					roomimg.src = "/images/Mannheim.jpg";
				}else if("Balears".equals(lb.selectedItem.value)){
					seat.value = "6";
					projector.value = "NO";
					vconference.value = "NO";
					network.value = "Wireless";
					telephone.value = "#1012";
					roomimg.src = "/images/Balears.jpg";
				}else{
					seat.value = "";
					projector.value = "";
					vconference.value = "";
					network.value = "";
					telephone.value = "";				
					roomimg.src = "/images/empty.jpg";
				} 
			}
			void eidChange(){
				if("001".equals(eid.value)){
					name.value = "Dennis";
				}else if("002".equals(eid.value)){
					name.value  = "Robbie";
				}else if("003".equals(eid.value)){
					name.value  = "Jumper";
				}else if("004".equals(eid.value)){
					name.value  = "Jeff";
				}
			}
			
			void checkFree(){
				if(room.selectedItem==null || room.selectedItem.value.equals("")){
					Messagebox.show("Select a conference room first!","",
					Messagebox.OK, Messagebox.INFORMATION);
					return;
				}
				
				java.util.HashMap args = new java.util.HashMap();
				args.put("calbox",date);
				args.put("timebox",time);
				args.put("room",room.selectedItem.value);
				Executions.createComponents("checkfree.zul",box,args).doModal();
			}
		</z:zscript>
		<z:vbox id="box" style="visible:none"/>
		<a href="../index.html">Back</a>
	</z:page>
	
</f:view>
</BODY>

</HTML>
