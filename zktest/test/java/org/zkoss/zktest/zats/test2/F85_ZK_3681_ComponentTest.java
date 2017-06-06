package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public class F85_ZK_3681_ComponentTest extends F85_ZK_3681_TestCase {
	@Test
	public void testInvalidate() throws Exception {
		connect();

		String origId = jq(".z-fragment input[type=\"text\"]:eq(0)").val();
		click(jq("@button:eq(0)"));
		waitResponse();

		assertEquals(origId, jq(".z-fragment input[type=\"text\"]:eq(0)").val());
	}

	@Test
	public void testSetSrcNonExists() throws Exception {
		connect();

		click(jq("@button:eq(1)"));
		waitResponse();

		assertEquals("Editing selected issue", jq("h1:eq(0)").text());
		assertTrue("Error window not shown", jq("@window").exists());
	}

	@Test
	public void testSetSrc() throws Exception {
		connect();

		click(jq("@button:eq(2)"));
		waitResponse();

		assertEquals("Replaced", jq("h1").text());
		assertFalse("ID not loaded", "##-####".equals(jq("#i_id").text()));
		assertFalse("Description not loaded", "____".equals(jq("#i_desc").text()));
	}

	@Test
	public void testSerializeDeserialize() throws Exception {
		connect();

		click(jq("@button:eq(3)"));
		waitResponse();

		type(jq(".z-fragment input[type=\"text\"]:eq(0)"), "zz-123");
		waitResponse();

		assertEquals("Invalid: zz-123", jq(".z-fragment span:eq(0)").text());
	}
}
