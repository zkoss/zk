/* F86_ZK_4028Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 17:39:07 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		// Navigation
		desktop.query("button[label^='Direct NavTo']").click();
		Assert.assertNotNull(desktop.query("label[value='Level 3']"));
		Assert.assertNotNull(desktop.query("label[value='AAA Profiles']"));

		desktop.query("a[label='Maintenance']").click();
		Assert.assertNotNull(desktop.query("label[value='Maintenance']"));

		desktop.query("button[label='NavTo Configuration']").click();
		Assert.assertNotNull(desktop.query("label[value='Level 1']"));
		Assert.assertNotNull(desktop.query("label[value='Configuration']"));
		Assert.assertNull(desktop.query("label[value='Level 2']"));

		// Insert / Remove
		desktop.query("button[label^='InsertBefore']").click();
		Assert.assertNotNull(desktop.query("a[label='New Item']"));

		try {
			desktop.query("button[label^='InsertBefore']").click();
			Assert.fail("It should throw an exception");
		} catch (ZatsException ignored) { }

		desktop.query("a[label='Diagnostics']").click();
		desktop.query("button[label='Remove Diagnostics']").click();
		Assert.assertNull(desktop.query("a[label='Diagnostics']"));
		Assert.assertNotNull(desktop.query("label[value='Maintenance']"));

		desktop.query("button[label='Remove Maintenance']").click();
		Assert.assertNull(desktop.query("a[label='Maintenance']"));
		Assert.assertNotNull(desktop.query("label[value='New Item']"));

		// Test serialize/deserialize
		desktop.query("button[label^='Direct NavTo']").click();
		Assert.assertNotNull(desktop.query("label[value='Level 3']"));
		Assert.assertNotNull(desktop.query("label[value='AAA Profiles']"));
		desktop.query("button[label='Test serialize/deserialize']").click();
		desktop.query("a[label='Auth Servers']").click();
		Assert.assertNotNull(desktop.query("label[value='Level 3']"));
		Assert.assertNotNull(desktop.query("label[value='Auth Servers']"));
	}
}
