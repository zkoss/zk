/* B101_ZK_5764Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 02 12:50:22 CST 2024, Created by jameschu

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B101_ZK_5764Test extends ZATSTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B101-ZK-5764-zk.xml");

	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		desktopAgent.query("button").click();
		assertEquals("org.zkoss.zktest.test2.B101_ZK_5764ViewModelAnnotationHandler", desktopAgent.getZkLog().get(0));
	}
}
