package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public class F85_ZK_3681_Validator_FormTest extends F85_ZK_3681_TestCase {
	@Test
	public void testValidatorInvalid() throws Exception {
		connect();

		String origId = jq(".z-fragment span:eq(2)").html();
		type(jq(".z-fragment input[type=\"text\"]:eq(0)"), "zK-9487");
		type(jq(".z-fragment input[type=\"text\"]:eq(1)"), "ZK");
		click(jq(".z-fragment button"));
		waitResponse();

		assertEquals("ID is invalid.", jq(".z-fragment span:eq(0)").html());
		assertEquals("Description is too short (minimum is 3 characters)", jq(".z-fragment span:eq(1)").html());
		assertEquals(origId, jq(".z-fragment span:eq(2)").html());
	}

	@Test
	public void testValidatorValid() throws Exception {
		connect();

		type(jq(".z-fragment input[type=\"text\"]:eq(0)"), "AK-174");
		type(jq(".z-fragment input[type=\"text\"]:eq(1)"), "ZK is awesome.");
		click(jq(".z-fragment button"));
		waitResponse();

		assertEquals("", jq(".z-fragment span:eq(0)").html());
		assertEquals("", jq(".z-fragment span:eq(1)").html());
		assertEquals("AK-174", jq(".z-fragment span:eq(2)").html());
		assertEquals("ZK is awesome.", jq(".z-fragment span:eq(3)").html());
	}
}
