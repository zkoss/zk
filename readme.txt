README: Build ZK


---
zul
	The ZK components implementing XUL, aka, the XUL components of ZK.
	XUL: XML user interface markup language

-----
zhtml
	The ZK components implementing XHTML, aka, the XHTML components of ZK.

----
zmul
	The ZK components implementing MUL, aka, the MUL components of ZK.
	MUL: Mobile user interface markup language

-----
zmobi

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
	all relevant files to $MIDP_HOME/apps/zmobi
		bin/deploy zmobi
