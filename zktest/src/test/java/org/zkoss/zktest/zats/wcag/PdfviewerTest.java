/* PdfviewerTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 14:33:03 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

/**
 * @author rudyhuang
 */
public class PdfviewerTest extends WcagTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@pdfviewer"));
		waitResponse();
		getActions().sendKeys(Keys.RIGHT, Keys.RIGHT).perform();

		verifyA11y();
	}
}
