/* B110_ZK_6241Test.java

		Purpose:

		Description:

		History:
				Fri Nov 13 16:05:33 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B110_ZK_6241Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B110-ZK-6241.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		assertTrue(jq("@cropper .z-cropper-holder").exists(), "Jcrop initialized");
		assertNoJSError();
	}
}
