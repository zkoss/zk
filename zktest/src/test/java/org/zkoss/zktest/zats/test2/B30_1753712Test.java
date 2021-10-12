package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class B30_1753712Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B30_1753712Test.class);

	@Test
	public void test() {
		connect();
		Assert.assertEquals("n/a", jq("$la").text());
		driver.navigate().refresh(); // trigger rmDesktop
		sleep(1000);
		waitResponse();
		Assert.assertNotEquals("n/a", jq("$la").text());
	}
}
