README

Naming Convention for abbreviation names
1) Industry standard names, e.g., XML, HTML and URI:
	We use all uppercase letters as other projects do.
	For example, XMLs for the XML utilities

2) ZK's names, e.g., ZK and ZUL:
	We use uppercase for the first letter.
	For example, ZkFns for the ZK EL functions, UiFactory for the UI factory

---
zul
	The ZK XUL component set, http://www.zkoss.org/2005/zul
	It includes ZK components implementing XUL tags
	XUL: XML user interface markup language

---
zkex
	The extension of the ZK, such as the extenstion to XUL component set,
	and server-push features.
	It includes the jFreechart-based engine.

---
zhtml
	The XHTML component set, http://www.w3c.org/1999/xhtml
	It includes ZK components implementing XHTML tags

---
zml
	The XML component set, http://www.zkoss.org/2007/xml
	It includes ZK components for generating XML ouput

---
zkmax
	The Ruby, Groovy, Rhino, MVEL and other plugins.
	Also, the performance enhancement version of zul.

---
zuljsp
	The ZUL JSP Tags

---
mil
	The ZK components implementing MIL (Mobile Interactive Language).

---
zmob

	ZK Mobile Client is a MIDlet that hat enables a user to display and interact
	with user interfaces represented in MUL.
	MUL: Mobile user interface markup language

	1.	Download Sun Java Wireless Toolkit (WTK) at

		http://java.sun.com/products/sjwtoolkit/download-2_5.html

	2.	Make a symbolic link to the directory you installed WTK, which is assumed
		to be $MIDP_HOME

		cd /usr
		ln -s $MIDP_HOME WTK


	To test with WTK, you have to execute the following command to copy
	all relevant files to $MIDP_HOME/apps/zmob
		bin/deploy zmob
