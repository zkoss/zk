/* B86_ZK_4029Test.java

        Purpose:

        Description:

        History:
                Tue Jan 22 15:36:48 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.test2.B86_ZK_4029VM;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4029Test extends ZATSTestCase {
	@Test
	public void test() {
		connect();
		DesktopAgent desktop = connect();
		ComponentAgent bind = desktop.query("#d");
		Binder binder = BinderUtil.getBinder(bind.getOwner());
		assertTrue(binder != null);
		try {
			int size1 = B86_ZK_4029VM.getVMNodeDependentsSize(bind.getOwner());
			desktop.query("#btn2").click();
			Assertions.assertEquals(size1, B86_ZK_4029VM.getVMNodeDependentsSize(bind.getOwner()));
		} catch (Exception e) {
			fail("no exception here");
		}

	}
}