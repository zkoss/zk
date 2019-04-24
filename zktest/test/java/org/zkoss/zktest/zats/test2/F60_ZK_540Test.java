package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F60_ZK_540Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		new Select(toElement(jq("$posbox"))).selectByVisibleText("before_start");
		new Select(toElement(jq("$msgbox"))).selectByVisibleText("HTML");
		click(jq("$adcb"));

		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(jq("@notification").isVisible());
		Assert.assertThat(jq("@notification .z-notification-content").html(), containsString("<span style=\"font-weight: bold\">HTML</span>"));
		Assert.assertEquals(jq("@window").offsetLeft(), jq("@notification").offsetLeft(), 1);
		Assert.assertEquals(jq("@window").offsetTop() - jq("@notification").outerHeight(), jq("@notification").offsetTop(), 1);

		sleep(2000);
		Assert.assertFalse(jq("@notification").isVisible());
	}
}
