<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>foreach Example</title>
</head>
<body>
      <z:page zscriptLanguage="java">
         <h3>Iteration using JSTL Tags</h3>
		 <p>use JSTL Tag: "forEach" Tag.<p>
      
      		<z:window id="win2" title="ZK is best!!!" width="75%" border="normal">
      		<p>Use JSTL forEach Tag to create Treeitem Components.</p>
      		<z:tree id="tree" width="70%" rows="30">
				<z:treecols sizable="true">
					<z:treecol label="Name"/>
				</z:treecols>
				<z:treechildren>
				<c:forEach var="i" begin="1" end="3" step="1" varStatus="status">
					<z:treeitem>
						<z:treerow>
							<z:treecell label="Item ${i}"/>
						</z:treerow>
						<z:treechildren>
							<c:forEach var="j" begin="1" end="3" step="1" varStatus="status">
							<z:treeitem>
								<z:treerow>
									<z:treecell label="Item ${i}.${j}"/>
								</z:treerow>
								<z:treechildren>
									<c:forEach var="k" begin="1" end="5" step="1" varStatus="status">
									<z:treeitem>
										<z:treerow>
											<z:treecell label="Item ${i}.${j}.${k}"/>
										</z:treerow>
									</z:treeitem>
									</c:forEach>
								</z:treechildren>
							</z:treeitem>
							</c:forEach>
						</z:treechildren>
					</z:treeitem>
					</c:forEach>
				</z:treechildren>
			</z:tree>
			</z:window>
      	</z:page>
      
</body>
</html>