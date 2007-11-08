<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<z:init use="org.zkoss.zkplus.databind.AnnotateDataBinderInit" arg0="page"/>
<z:init use="org.zkoss.zrss.samples.RssInit" arg0="http://localhost/zuljsp/publish.zxml" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ZK RSS Reader in JSP</title>
</head>
<body>

<%--***** remember change it to your location *****--%> 
<z:page>
<z:window id="mainPanel" width="1024px"  title="RSS Reader with Aggregate Feed"  border="normal" >
<z:zscript>
	import org.zkoss.zrss.RssFeed;
	import org.zkoss.zrss.RssEntry;
	RssFeed selected = (firstFeed instanceof RssFeed)? firstFeed : null;
	RssEntry selectEntry = (firstEntry instanceof RssEntry)? firstEntry : null ;
	RssFeed addRssfeed = null;
	System.out.println("->rssBinder: "+rssBinder);
	System.out.println("->firstFeed: "+firstFeed);
	System.out.println("->firstEntry: "+firstEntry);
	System.out.println("->binder: "+binder);
</z:zscript>

<z:window id="addFeedPanel" visible="false"> 
	<z:groupbox mold="3d" width="300px" closable="false">
		<z:caption label="Add New Feed"/>
		<z:hbox>
			URL:<z:textbox id="feedUrl" cols="50" value="http://slashdot.org/slashdot.rss"/>
		</z:hbox>
		<z:hbox>
			<z:button id="confirmBtn" label="Confirm" >
				<z:attribute name="onClick">
					addRssfeed = rssBinder.lookUpFeed(feedUrl.getValue());
					feedListBox.getModel().add(addRssfeed);
					selected = addRssfeed;
					selectEntry = addRssfeed.getFeedEntries().get(0);
					refreshBindingObject(mainPanel);
					leaveAddPanel();
				</z:attribute>
			</z:button>
			<z:button id="cancelBtn" label="Cancel">
				<z:attribute name="onClick">
					leaveAddPanel();
				</z:attribute>
			</z:button>
			<z:zscript>
			void leaveAddPanel()
			{
				addFeedPanel.doEmbedded();		
				addFeedPanel.setVisible(false);	
				newsPanel.setOpen(true);
			}
			</z:zscript>
		</z:hbox>
	</z:groupbox>
</z:window>

<z:hbox width="1024px" spacing="2">
<z:vbox width="420px" spacing="0">
		<z:groupbox id="feedsPanel" mold="3d" width="100%" onOpen="if(self.isOpen()){newsPanel.setOpen(true);}">
			<z:caption label="RSS Feeds.">
				<z:button id="addFeedBtn" label="Add New Feed" onClick="activeAddPanel()"/>
			</z:caption>
			<z:zscript>
			void activeAddPanel()
			{
				addFeedPanel.setVisible(true);
				addFeedPanel.doModal();
			}
			void refreshBindingObject(Component com)
			{
				binder.loadComponent(com);//binder is init defined databinder...
			}
			</z:zscript>
			
			<z:listbox id="feedListBox" model="@{feeds}" rows="4" selectedItem="@{selected}">

			    <z:listitem self="@{bind(each='feed')}">
			    	<z:attribute name="onClick"> 
			    		hideDetail();
			    		selectEntry = selected.getFeedEntries().get(0);
			    		refreshBindingObject(mainPanel);
						newsPanel.setOpen(true);
			    	</z:attribute>	    
			      <z:listcell>
			      	<z:image src="@{feed.iconLink}"/>
			      </z:listcell>
			      <z:listcell label="@{feed.feedTitle}"/>
			    </z:listitem>
			  </z:listbox>
		</z:groupbox>
		
		<!-- ***** News Title ***** -->
		<z:groupbox id="newsPanel" mold="3d" width="100%" >
			<z:caption label="News Title From Selected Feed.">
				
			</z:caption>
			  <%-- show the detail of the selected Feed --%>
			  <z:listbox id="newsListBox" rows="15" model="@{selected.feedEntries}" 
			  	 selectedItem="@{selectEntry}">

			       <z:listitem self="@{bind(each='entry')}" onClick="changeFrm()">
			      		<z:listcell label="@{entry.title}"/>
			       </z:listitem>
			  </z:listbox>
		</z:groupbox>
		<%-- ***** Detail Grid ***** --%>
		<z:groupbox id="detailPanel" mold="3d" width="100%" >
			<z:caption label="RSS and News Detail">
			</z:caption>
			  <z:grid width="99%">
			    <z:rows>
			      <z:row>Link: <z:label value="@{selected.feedLink}"/></z:row>
			      <z:row>Description: <z:label value="@{selected.feedDesc}"/></z:row>
			      <z:row>copyright: <z:label value="@{selected.copyright}"/></z:row>
			      <z:row>publish: <z:label value="@{selected.publishedDate}"/></z:row>
   			      <z:row>News Link: <z:label value="@{selectEntry.link}"/></z:row>
			    </z:rows>
			  </z:grid>
		</z:groupbox>
</z:vbox>
<z:zscript>
			void onSwitch() 
			{
			    if(ifrm1.isVisible())hideDetail();
			    else showDetail();
			}
			void showDetail()
			{
			    	ifrm1.setVisible(true);
			    	changeFrm();
			        libox.setVisible(false);
			        btnLabel.setLabel("<< Summary");
			}
			void hideDetail()
			{
					ifrm1.setVisible(false);
			        libox.setVisible(true);
			        btnLabel.setLabel("Details >>");
			}
			
			void changeFrm()
			{
				if(ifrm1.isVisible())
					ifrm1.src = selectEntry.link;
			}
</z:zscript>

	<z:groupbox id="mesgPanel" mold="3d" width="100%" closable="false">
			<z:caption label="@{selectEntry.title}">
			   <z:button  id="btnLabel" label="Details >>" onClick="onSwitch()" tooltip="detailPop"/>
			</z:caption>
			<z:listbox id="libox" rows="1">
			       <z:listitem>
			      		<z:listcell>
			      		    <z:html content="@{selectEntry.descValue}"></z:html>
			      		</z:listcell>
			       </z:listitem>
			  </z:listbox>
		    <z:iframe id="ifrm1" visible="false" width="565px" height="680px"  />
	</z:groupbox>
	<z:popup id="detailPop" width="300px">
		<z:vbox>
			Redirect to Target News:
			<z:toolbarbutton label="@{selectEntry.title}" href="@{selectEntry.link}"/>
		</z:vbox>
		
	</z:popup>




</z:hbox>

</z:window>
</z:page>
</body>
</html>