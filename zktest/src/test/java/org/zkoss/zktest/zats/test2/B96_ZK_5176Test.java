package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class B96_ZK_5176Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		final String imageURL = jq("@image").toWidget().get("src");
		final String signatureBackgroundImageURL = jq("@signature").toWidget().get("backgroundImage");
		assertEquals(imageURL, signatureBackgroundImageURL);
	}
}
