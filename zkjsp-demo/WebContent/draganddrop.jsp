<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/zul" prefix="zk" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Drag and drop</title>
</head>
<body>
	<zk:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>
		<h2>Drag and Drop Demo</h2>
		<p> Same as our zkdemo userguide's drag-and-drop demo.  </p>
		<zk:window title="Reorder by Drag-and-Drop" width="400px" border="normal">
			Unique Visitors of ZK:
			<zk:listbox id="src" multiple="true" width="300px">
				<zk:listhead>
					<zk:listheader label="Country/Area"/>
					<zk:listheader align="right" label="Visits"/>
					<zk:listheader align="right" label="%"/>
				</zk:listhead>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="United States"/>
					<zk:listcell label="5,093"/>
					<zk:listcell label="19.39%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="China"/>
					<zk:listcell label="4,274"/>
					<zk:listcell label="16.27%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="France"/>
					<zk:listcell label="1,892"/>
					<zk:listcell label="7.20%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="Germany"/>
					<zk:listcell label="1,846"/>
					<zk:listcell label="7.03%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="Taiwan"/>
					<zk:listcell label="1,384"/>
					<zk:listcell label="5.27%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="United Kingdom"/>
					<zk:listcell label="1,025"/>
					<zk:listcell label="3.90%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="Italy"/>
					<zk:listcell label="921"/>
					<zk:listcell label="3.51%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="Canada"/>
					<zk:listcell label="820"/>
					<zk:listcell label="3.12%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="Brazil"/>
					<zk:listcell label="648"/>
					<zk:listcell label="2.47%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="Spain"/>
					<zk:listcell label="540"/>
					<zk:listcell label="2.06%"/>
				</zk:listitem>
				<zk:listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
					<zk:listcell label="(other)"/>
					<zk:listcell label="7,824"/>
					<zk:listcell label="29.79%"/>
				</zk:listitem>
				<zk:listfoot>
					<zk:listfooter label="Total 132"/>
					<zk:listfooter label="26,267"/>
					<zk:listfooter label="100.00%"/>
				</zk:listfoot>
			</zk:listbox>
			<zk:image src="/img/donut.png" width="100px" height="80px" draggable="true" mold="alphafix"/>
			<zk:zscript>
			void move(Component dragged) {
				if(1 < 2) System.out.println("<h1> this is an H1 tag</h1>");
				if (dragged instanceof Listitem)
					self.parent.insertBefore(dragged, self);
				else
					alert("Sorry, no food here");
			}
			</zk:zscript>
		</zk:window>
	</zk:page>
</body>
</html>