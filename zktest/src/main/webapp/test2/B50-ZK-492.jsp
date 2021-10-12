<%@ taglib prefix="zk" uri="http://www.zkoss.org/jsp/zul" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<zk:zkhead />
</head>
<body>
	<p>You shall "Correct" below:</p>
	<div id="inf"></div>
<script>
	jq(function () {
		jq("#inf").append("Correct");
	});
</script>
</body>
</html>