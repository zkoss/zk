/* F86_ZK_4028Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 17:39:07 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertNotNull(desktop.query("label[value='Level 3']"));
		Assertions.assertNotNull(desktop.query("label[value='AAA Profiles']"));

		desktop.query("a[label='Maintenance']").click();
		Assertions.assertNotNull(desktop.query("label[value='Maintenance']"));

		desktop.query("button[label='NavTo Configuration']").click();
		Assertions.assertNotNull(desktop.query("label[value='Level 1']"));
		Assertions.assertNotNull(desktop.query("label[value='Configuration']"));
		Assertions.assertNull(desktop.query("label[value='Level 2']"));

		// Insert / Remove
		desktop.query("button[label^='InsertBefore']").click();
		Assertions.assertNotNull(desktop.query("a[label='New Item']"));

		try {
			desktop.query("button[label^='InsertBefore']").click();
			Assertions.fail("It should throw an exception");
		} catch (ZatsException ignored) { }

		desktop.query("a[label='Diagnostics']").click();
		desktop.query("button[label='Remove Diagnostics']").click();
		Assertions.assertNull(desktop.query("a[label='Diagnostics']"));
		Assertions.assertNotNull(desktop.query("label[value='Maintenance']"));

		desktop.query("button[label='Remove Maintenance']").click();
		Assertions.assertNull(desktop.query("a[label='Maintenance']"));
		Assertions.assertNotNull(desktop.query("label[value='New Item']"));

		// Test serialize/deserialize
		desktop.query("button[label^='Direct NavTo']").click();
		Assertions.assertNotNull(desktop.query("label[value='Level 3']"));
		Assertions.assertNotNull(desktop.query("label[value='AAA Profiles']"));
		desktop.query("button[label='Test serialize/deserialize']").click();
		desktop.query("a[label='Auth Servers']").click();
		Assertions.assertNotNull(desktop.query("label[value='Level 3']"));
		Assertions.assertNotNull(desktop.query("label[value='Auth Servers']"));
	}
}
