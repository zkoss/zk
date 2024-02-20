/* B100_ZK_5650Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Feb 20 10:02:46 CST 2024, Created by rebeccalai

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B100_ZK_5650Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B100-ZK-5650-zk.xml");

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		dropUploadFile(jq("@dropupload"), Paths.get("src/main/webapp/test2/img/sun.jpg"));
		waitResponse();
		assertEquals("sun.jpg", getZKLog());
	}
}
