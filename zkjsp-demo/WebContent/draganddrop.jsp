<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Drag and drop</title>
</head>
<body>
	<z:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>
		<h2>Drag and Drop Demo</h2>
		<p> Same as our zkdemo userguide's drag-and-drop demo.  </p>
		<z:window title="Reorder by Drag-and-Drop" width="400px" border="normal">
			Unique Visitors of ZK:
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
			<z:image src="/img/donut.png" width="100px" height="80px" draggable="true" mold="alphafix"/>
			<z:zscript>
			void move(Component dragged) {
				if(1 < 2) System.out.println("<h1> this is an H1 tag</h1>");
				if (dragged instanceof Listitem)
					self.parent.insertBefore(dragged, self);
				else
					alert("Sorry, no food here");
			}
			</z:zscript>
		</z:window>
	</z:page>
</body>
</html>