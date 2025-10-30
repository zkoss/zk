/* B96_ZK_4778Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 17:25:57 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4778Test extends ZATSTestCase {
	@Test
	public void testLeak1() {
		final DesktopAgent desktop = connect();

		desktop.query("#btnCreate").click();
		desktop.query("#btnDestroyLeak").click();
		desktop.query("#btnCheck").click();

		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("eq.isIdle(): true", zkLog.get(1));
	}

	@Test
	public void testLeak2() {
		final DesktopAgent desktop = connect();

		desktop.query("#btnCreate").click();
		desktop.query("#btnDestroy2").click();
		desktop.query("#btnCheck").click();

		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("eq.isIdle(): true", zkLog.get(1));
	}

	@Test
	public void testLeak3() {
		final DesktopAgent desktop = connect();

		desktop.query("#btnCreate").click();
		desktop.query("#btnDestroyNoLeak").click();
		desktop.query("#btnCheck").click();

		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("eq.isIdle(): true", zkLog.get(1));
	}
}
