/* B80_ZK_2855Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Sep  1, 2015  2:29:13 PM, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zul.Label;

import junit.framework.Assert;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_2855Test extends ZutiBasicTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect(getTestURL("B80-ZK-2855.zul"));
		
		ComponentAgent host1 = desktop.query("#host1");
		Label label1 = host1.getFirstChild().getFirstChild().as(Label.class);
		Assert.assertEquals("include 1", label1.getValue());
		Label label2 = host1.getLastChild().getFirstChild().as(Label.class);
		Assert.assertEquals("include 2", label2.getValue());
		
		checkVerifier(host1.getOwner(), HierarchyVerifier.class);
		
		ComponentAgent host2 = desktop.query("#host2");
		Label label3 = host2.getFirstChild().getFirstChild().as(Label.class);
		Assert.assertEquals("include 1", label3.getValue());
		
		checkVerifier(host2.getOwner(), HierarchyVerifier.class);

		ComponentAgent host3 = desktop.query("#host3");
		Label label4 = host3.getFirstChild().getFirstChild().as(Label.class);
		Assert.assertEquals("include 2", label4.getValue());

		checkVerifier(host3.getOwner(), HierarchyVerifier.class);
		
		ComponentAgent host4 = desktop.query("#host4");
		Label label5 = host4.getFirstChild().getFirstChild().as(Label.class);
		Assert.assertEquals("include 1", label5.getValue());
		Label label6 = host4.getLastChild().getFirstChild().as(Label.class);
		Assert.assertEquals("include 1", label6.getValue());
		
		checkVerifier(host4.getOwner(), HierarchyVerifier.class);
	}
}
