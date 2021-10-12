package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4354Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(jq("$hd").width(), jq("$hdn").width());
		assertEquals(jq("$vd").height(), jq("$vdn").height());
	}
}
