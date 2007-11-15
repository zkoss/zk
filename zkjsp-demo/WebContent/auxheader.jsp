<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>A simple Auxheader Demo</title>
</head>
<body>
<z:page zscriptLanguage="java"><%-- this is Jsp valid Comment, do not use XML comment. --%>
		<h2>ZK Jsp Auxheader Demo</h2>
		
		<p>A simple Auxheader component demo. also support Tree and Grid. <br/>Same as the demo in zkdemo site.</p>
		<z:window id="win1" title="ZK is best!!!" border="normal" width="780px">

			
			<z:listbox id="libox" width="100%" multiple="true" vflex="true">
				<z:listhead  sizable="true">
					<z:listheader label="Name" sort="auto" width="25%" image="/img/coffee.gif"/>
					<z:listheader label="Gender" sort="auto" width="25%" image="/img/folder.gif"/>
					<z:listheader label="Age" sort="auto" width="25%" align="center" image="/img/cubfirs.gif"/>
					<z:listheader label="Description" width="25%" sort="auto" image="/img/home.gif"/>
				</z:listhead>
				<z:auxhead>
					<z:auxheader label="All Header" style="font-weight:bold; color:red;" align="center" colspan="4"/>
				</z:auxhead>
				<z:auxhead>
					<z:auxheader label="A+B" rowspan="2" colspan="2"/>
					<z:auxheader label="C"/>
					<z:auxheader>
						<z:attribute name="label">DE</z:attribute>
					</z:auxheader>
				</z:auxhead>	
				<z:auxhead>
					<z:auxheader label="A"/>
					<z:auxheader label="B+C+D"/>
				</z:auxhead>
				<z:auxhead>
					<z:auxheader label="A+B+C"  colspan="3"/>
					<z:auxheader label="D"/>
				</z:auxhead>
				<c:forEach var="i" begin="1" end="6" varStatus="iStatus">
					<z:listitem>
						<c:forEach var="j" begin="1" end="4" step="1" varStatus="jStatus">
							<z:listcell label="cell_${i}.${j}"/>
						</c:forEach>
					</z:listitem>
				</c:forEach>
			</z:listbox>
			
			<z:combobox> 
		       <c:forEach var="i" begin="1" end="5" varStatus="status">
		          <z:comboitem label="Hello JSTL ${i}"/>
		       </c:forEach>   
		    </z:combobox>
			
		</z:window>
</z:page>
</body>
</html>