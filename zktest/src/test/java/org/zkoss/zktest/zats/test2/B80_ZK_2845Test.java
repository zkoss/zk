/* B80_ZK_2845Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Sep  2, 2015  6:01:24 PM, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;

import junit.framework.Assert;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_2845Test extends ZutiBasicTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect(getTestURL("B80-ZK-2845.zul"));
			// 3 labels explaining test case, two customComps producing 4 labels, total = 7 labels
			Assert.assertEquals(7, desktop.queryAll("label").size());
		} catch (Throwable e) {
			fail("not expecting any exceptions");
		}
	}
}
