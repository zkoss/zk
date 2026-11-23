package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
