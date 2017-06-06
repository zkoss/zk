package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public class F85_ZK_3681_ShadowTest extends F85_ZK_3681_TestCase {
	@Test
	public void testShadow() throws Exception {
		connect();

		assertEquals(5, jq(".z-fragment li").length());
		assertEquals(2, jq(".z-fragment input:checked").length());
		assertEquals("STRIKE", jq(".z-fragment input:checked:eq(0)").next().toElement().get("nodeName"));
	}
}
