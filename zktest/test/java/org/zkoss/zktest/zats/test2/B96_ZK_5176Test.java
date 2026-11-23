package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_5176Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		final String imageURL = jq("@image").toWidget().get("src");
		final String signatureBackgroundImageURL = jq("@signature").toWidget().get("backgroundImage");
		Assert.assertEquals(imageURL, signatureBackgroundImageURL);
	}
}
