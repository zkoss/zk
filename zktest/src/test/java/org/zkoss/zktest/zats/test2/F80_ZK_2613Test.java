package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author Jameschu
 */
public class F80_ZK_2613Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse(1000);
		String log = getZKLog(); //first line in resourceURI
		assertEquals(true, log.contains(log.split("\n")[0] + "/web/") && log.contains("/img/spacer.gif"));
	}
}
