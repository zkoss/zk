README: Build ZK


-----
zkmob

	ZK Mobile Client is a MIDlet depending on MIDP.

	1.	Download Sun Java Wireless Toolkit (WTK) at

		http://java.sun.com/products/sjwtoolkit/download-2_5.html

	2.	Make a symbolic link to the directory you installed WTK, which is assumed
		to be $MIDP_HOME

		cd /usr
		ln -s $MIDP_HOME WTK


	To test with WTK, you have to execute the following command to copy
	all relevant files to $MIDP_HOME/apps/zkmob
		bin/deploy zkmob
