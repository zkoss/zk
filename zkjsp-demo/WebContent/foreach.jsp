<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/zul" prefix="zk" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>foreach Example</title>
</head>
<body>
      <zk:page zscriptLanguage="java">
         <h3>Iteration using JSTL Tags</h3>
		 <p>use JSTL Tag: "forEach" Tag.<p>
      
      		<zk:window id="win2" title="ZK is best!!!" width="75%" border="normal">
      		<p>Use JSTL forEach Tag to create Treeitem Components.</p>
      		<zk:tree id="tree" width="70%" rows="30">
				<zk:treecols sizable="true">
					<zk:treecol label="Name"/>
				</zk:treecols>
				<zk:treechildren>
				<c:forEach var="i" begin="1" end="3" step="1" varStatus="status">
					<zk:treeitem>
						<zk:treerow>
							<zk:treecell label="Item ${i}"/>
						</zk:treerow>
						<zk:treechildren>
							<c:forEach var="j" begin="1" end="3" step="1" varStatus="status">
							<zk:treeitem>
								<zk:treerow>
									<zk:treecell label="Item ${i}.${j}"/>
								</zk:treerow>
								<zk:treechildren>
									<c:forEach var="k" begin="1" end="5" step="1" varStatus="status">
									<zk:treeitem>
										<zk:treerow>
											<zk:treecell label="Item ${i}.${j}.${k}"/>
										</zk:treerow>
									</zk:treeitem>
									</c:forEach>
								</zk:treechildren>
							</zk:treeitem>
							</c:forEach>
						</zk:treechildren>
					</zk:treeitem>
					</c:forEach>
				</zk:treechildren>
			</zk:tree>
			</zk:window>
      	</zk:page>
      
</body>
</html>