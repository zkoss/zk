/* B95_ZK_4756Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 24 14:34:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zkex.zul.Pdfviewer;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4756Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();

		final Pdfviewer pdfviewer = desktop.query("pdfviewer").as(Pdfviewer.class);
		Assert.assertEquals(1, pdfviewer.getActivePage());
	}
}
