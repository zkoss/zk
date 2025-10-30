package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class F96_ZK_4761_1Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4761-zk.xml");
	@Test
	public void test() {
		connect("/test2/F96-ZK-4761.zul");
		waitResponse();
		click(jq("@button"));
		waitResponse();
		assertEquals("0", jq("$resultNum").text());
	}
}