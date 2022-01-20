/* F100_ZK_5024Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 28 09:45:32 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Test;

import org.junit.experimental.categories.Category;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class F100_ZK_5024Test extends ZATSTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F100-ZK-5024-zk.xml");

	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		assertEquals("custom bind composer", desktopAgent.getZkLog().get(0).trim());
	}
}
