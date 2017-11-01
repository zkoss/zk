package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author bob peng
 */
public class B85_ZK_3580Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("Should see SUCCESS!!!", "SUCCESS!!!", jq(".z-div span:eq(0)").text());
	}
}