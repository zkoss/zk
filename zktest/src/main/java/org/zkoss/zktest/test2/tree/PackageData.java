/* PackageDataUnit.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Aug 15, 2011 6:37:15 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zktest.test2.tree;

/**
 * 
 * @author simonpai
 */
public class PackageData {
	
	private static DirectoryTreeNode root;
	static {
		root = new DirectoryTreeNode(null,
				new DirectoryTreeNode[] {
						new DirectoryTreeNode(new PackageDataUnit("/doc", "Release Notes and License")),
						new DirectoryTreeNode(new PackageDataUnit("/dist", null),
								new DirectoryTreeNode[] {
										new DirectoryTreeNode(new PackageDataUnit("/lib", null),
												new DirectoryTreeNode[] {
														new DirectoryTreeNode(new PackageDataUnit("/zkforge", null),
																new DirectoryTreeNode[] {
																		new DirectoryTreeNode(new PackageDataUnit("breeze.jar", "Bresze Theme", "278KB")),
																		new DirectoryTreeNode(new PackageDataUnit("ckez.jar", "CKeditor", "1709KB")),
																		new DirectoryTreeNode(new PackageDataUnit("timelinez.jar", "Timeline", "283KB")),
																		new DirectoryTreeNode(new PackageDataUnit("timeplotz.jar", "Timeplot", "112KB")),
																		new DirectoryTreeNode(new PackageDataUnit("gmapsz.jar", "Google Maps", "95KB")),
																		new DirectoryTreeNode(new PackageDataUnit("zuljsp.jar", "JSP", "129KB")) }, true), // zkforge
																		// opened
														new DirectoryTreeNode(new PackageDataUnit("/ext",null),
																new DirectoryTreeNode[] {
																		new DirectoryTreeNode(new PackageDataUnit("commons-fileupload.jar", "Upload Features")),
																		new DirectoryTreeNode(new PackageDataUnit("commons-io.jar", "Upload Features")),
																		new DirectoryTreeNode(new PackageDataUnit("jcommon.jar", "Chart Component")),
																		new DirectoryTreeNode(new PackageDataUnit("jfreechar.jar", "Chart Component")),
																		new DirectoryTreeNode(new PackageDataUnit("jasperreports.jar", "Jasperreport related Component")),
																		new DirectoryTreeNode(new PackageDataUnit("itext.jarjxl.jar", "Jasperreport related Component")),
																		new DirectoryTreeNode(new PackageDataUnit("poi.jar", "Jasperreport related Component")),
																		new DirectoryTreeNode(new PackageDataUnit("commons-collections.jar", "Jasperreport related Component")),
																		new DirectoryTreeNode(new PackageDataUnit("commons-logging.jar", "Jasperreport related Component")),
																		new DirectoryTreeNode(new PackageDataUnit("commons-digester.jar", "Jasperreport related Component")),
																		new DirectoryTreeNode(new PackageDataUnit("bsh.jar", "Scripting in Java interpreter for zscript (BeanShell)")),
																		new DirectoryTreeNode(new PackageDataUnit("js.jar", "Scripting in JavaScript (Rhino)")),
																		new DirectoryTreeNode(new PackageDataUnit("groovy.jar", "Scripting in Groovy")),
																		new DirectoryTreeNode(new PackageDataUnit("jruby.jar", "Scripting in Ruby (JRuby)")),
																		new DirectoryTreeNode(new PackageDataUnit("jython.jar", "Scripting in Python (Jython)")),
																		new DirectoryTreeNode(new PackageDataUnit("Filters.jar", "Captcha Component.")),
																		new DirectoryTreeNode(new PackageDataUnit("mvel.jar", "Evaluate the expressions (MVEL)")),
																		new DirectoryTreeNode(new PackageDataUnit("ognl.jar", "Evaluate the expressions (OGNL)")) }), // ext 
																		// closed
														new DirectoryTreeNode(new PackageDataUnit("zcommon.jar", "ZK Core Jar File", "413KB")),
														new DirectoryTreeNode(new PackageDataUnit("zcommon-el.jar", "ZK Core Jar File", "100KB")),
														new DirectoryTreeNode(new PackageDataUnit("zhtml.jar", "ZK Core Jar File", "57KB")),
														new DirectoryTreeNode(new PackageDataUnit("zk.jar", "ZK Core Jar File", "1056KB")),
														new DirectoryTreeNode(new PackageDataUnit("zkplus.jar", "ZK Core Jar File", "122KB")),
														new DirectoryTreeNode(new PackageDataUnit("zul.jar", "ZK Core Jar File", "1311KB")),
														new DirectoryTreeNode(new PackageDataUnit("zweb.jar", "ZK Core Jar File", "196KB")) }), // lib
														// closed
										new DirectoryTreeNode(new PackageDataUnit("/src", "Jar Format Source Code")),
										new DirectoryTreeNode(new PackageDataUnit("/xsd", "XSD files for Development")),
										new DirectoryTreeNode(new PackageDataUnit("/WEB-INF", "Configuration Files")) }, true) // dist
						// opened
				});
	}
	
	public static DirectoryTreeNode getRoot() {
		return root;
	}
	
}
