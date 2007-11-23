<HTML>
<HEAD>
<title>Empty Test</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>
<body>
<f:view>
	<h:form>
		<z:page>
			<z:window title="Reorder by Drag-and-Drop" border="normal">
	<z:listbox id="src" multiple="true" width="300px">
		<z:listhead>
			<z:listheader label="Country/Area"/>
			<z:listheader align="right" label="Visits"/>
			<z:listheader align="right" label="%"/>
		</z:listhead>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="United States"/>
			<z:listcell label="5,093"/>
			<z:listcell label="19.39%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="China"/>
			<z:listcell label="4,274"/>
			<z:listcell label="16.27%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="France"/>
			<z:listcell label="1,892"/>
			<z:listcell label="7.20%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="Germany"/>
			<z:listcell label="1,846"/>
			<z:listcell label="7.03%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="Taiwan"/>
			<z:listcell label="1,384"/>
			<z:listcell label="5.27%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="United Kingdom"/>
			<z:listcell label="1,025"/>
			<z:listcell label="3.90%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="Italy"/>
			<z:listcell label="921"/>
			<z:listcell label="3.51%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="Canada"/>
			<z:listcell label="820"/>
			<z:listcell label="3.12%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="Brazil"/>
			<z:listcell label="648"/>
			<z:listcell label="2.47%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="Spain"/>
			<z:listcell label="540"/>
			<z:listcell label="2.06%"/>
		</z:listitem>
		<z:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
			<z:listcell label="(other)"/>
			<z:listcell label="7,824"/>
			<z:listcell label="29.79%"/>
		</z:listitem>
		<z:listfoot>
			<z:listfooter label="Total 132"/>
			<z:listfooter label="26,267"/>
			<z:listfooter label="100.00%"/>
		</z:listfoot>
	</z:listbox>
	
	<z:zscript>
	void move(Component dragged) {
			self.parent.insertBefore(dragged, self);
	}
	</z:zscript>
			</z:window>
			<h:commandButton id="submit" action="#{ActionBean.doSubmit}" value="Submit" />
		<h:messages></h:messages>
		</z:page>
		
	</h:form>
</f:view>
</body>
</HTML>