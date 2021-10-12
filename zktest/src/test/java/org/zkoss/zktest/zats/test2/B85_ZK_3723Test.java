package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author bob peng
 */
public class B85_ZK_3723Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("Input type should be password", "password", jq(".z-combobox-input:eq(0)").attr("type"));
		assertEquals("Input type should be password", "password", jq(".z-bandbox-input:eq(0)").attr("type"));
	}
}