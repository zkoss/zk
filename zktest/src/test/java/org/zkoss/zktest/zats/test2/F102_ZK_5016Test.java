/* F102_ZK_5016Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 11 18:07:20 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.event.Events;

@ForkJVMTestOnly
public class F102_ZK_5016Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F102-ZK-5016-zk.xml");

	@Test
	public void test() throws Exception {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		assertEquals("exception_list: " + List.of(
				new RuntimeException(Events.ON_BLUR),
				new RuntimeException(Events.ON_FOCUS)
		), jq(".z-window-content").text().trim());
	}
}
