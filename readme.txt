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
	The ZK components implementing XUL, aka, the XUL components of ZK.
	XUL: XML user interface markup language

-----
zhtml
	The ZK components implementing XHTML, aka, the XHTML components of ZK.

---
zuljsp
	The ZUL JSP Tags

----
mil
	The ZK components implementing MIL (Mobile Interactive Language).

-----
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
