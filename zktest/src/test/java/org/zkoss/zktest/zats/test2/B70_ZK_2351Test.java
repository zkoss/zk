/* B70_ZK_2351Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 17:49:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.TouchWebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class B70_ZK_2351Test extends TouchWebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();

		Element btnUp = widget("@doublespinner:eq(1)").$n("btn-up");
		tapCenter(btnUp);
		waitResponse();
		tapCenter(btnUp);
		waitResponse();
		assertEquals("2", jq("@doublespinner:eq(0) input").val());

		Element btnDown = widget("@doublespinner:eq(0)").$n("btn-down");
		tapCenter(btnDown);
		waitResponse();
		tapCenter(btnDown);
		waitResponse();
		tapCenter(btnDown);
		waitResponse();
		assertEquals("-1.0", jq("@doublespinner:eq(1) input").val());

		click(jq("@button"));
		waitResponse();
		assertEquals("1.1", jq("@doublespinner:eq(1) input").val());
	}

	// tap(WebElement) hits the corner, which Chrome's touch adjustment can retarget to the adjacent input
	private void tapCenter(Element element) {
		Rectangle rect = toElement(element).getRect();
		tap(new Point(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2));
	}
}
