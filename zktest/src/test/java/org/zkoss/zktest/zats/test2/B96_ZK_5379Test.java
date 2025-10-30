package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_5379Test extends WebDriverTestCase {
	private JQuery outer;
	private float baseOffsetTop;

	@BeforeEach
	public void setup() {
		connect();
		waitResponse();
		outer = jq("$outer");
		baseOffsetTop = getDOMOffsetTop("$anch1");
	}

	@Test
	public void test() {
		assertScrollPosition(2);
		assertScrollPosition(3);
		assertScrollPosition(1);
	}

	private void assertScrollPosition(int index) {
		final String anchorId = "$anch" + index;
		click(jq(anchorId + "Scroll"));
		waitResponse(true);
		assertEquals(getDOMOffsetTop(anchorId), outer.scrollTop() + baseOffsetTop, 2);
	}

	private float getDOMOffsetTop(String selector) {
		// `jq(selector).offsetTop()` isn't what I want. I want the raw DOM API value which is not derived by jQuery.
		return Float.parseFloat(jq(selector).toElement().get("offsetTop"));
	}
}
