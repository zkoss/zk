/* F110_ZK_6056LibraryPropertiesTest.java

        Purpose:

        Description:

        History:
                Tue Oct 13 21:24:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@ForkJVMTestOnly
public class F110_ZK_6056LibraryPropertiesTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F110-ZK-6056-zk.xml");

	@Test
	public void testFilterLogicContainsLibraryProperty() {
		connect("/test2/F110-ZK-6056LibraryProperties.zul");
		waitResponse();
		JQuery cb = jq("@chosenbox").eq(0);

		click(cb);
		waitResponse();
		sendKeys(cb.find(".z-chosenbox-input"), "US");
		sleep(500);
		waitResponse();

		JQuery options = jq(".z-chosenbox-select").find(".z-chosenbox-option");
		int visible = 0;
		for (int i = 0; i < options.length(); i++) {
			JQuery option = options.eq(i);
			if (!"none".equals(option.css("display")) && !"hidden".equals(option.css("visibility"))) {
				visible++;
				assertTrue(option.text().toUpperCase().contains("US"), "visible option should contain 'US': " + option.text());
			}
		}
		// no Locale.toString() starts with "US", so the SubModel's startsWith pre-filter shows nothing
		assertTrue(visible > 0, "library property filterLogic=contains should match locales containing 'US'");
		assertNoJSError();
	}
}
