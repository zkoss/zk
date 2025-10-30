<?xml version="1.0" encoding="UTF-8"?>

<!-- book.xsl
	Purpose:
		
	Description:
		
	History:
		Tue Aug 28 15:56:40     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.
-->

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Book Info</title>
			</head>
			<body>
				<h1>Book Info</h1>
				<xsl:apply-templates select="book"/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="book">
		<dl>
			<dt>Title:</dt>
			<dd><xsl:value-of select="title"/></dd>
			<dt>Who is this book for:</dt>
			<dd><xsl:value-of select="for-who"/></dd>
			<dt>Authors</dt>
			<dd><xsl:value-of select="author"/></dd>
		</dl>
	</xsl:template>
</xsl:stylesheet>
