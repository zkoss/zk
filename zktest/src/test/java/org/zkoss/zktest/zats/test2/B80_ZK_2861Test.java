package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_2861Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("{\"id1\":\"<value1>\",\"id2\":\"value2\",\"id3\":\"value2\"}", getZKLog());
	}
}