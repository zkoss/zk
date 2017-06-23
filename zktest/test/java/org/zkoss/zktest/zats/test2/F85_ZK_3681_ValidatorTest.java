package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public class F85_ZK_3681_ValidatorTest extends F85_ZK_3681_TestCase {
	@Test
	public void testValidatorInvalid() throws Exception {
		connect();

		String origId = jq(".z-fragment span:eq(1)").html();
		type(jq(".z-fragment input[type=\"text\"]:eq(0)"), "zK-9487");
		waitResponse();

		assertEquals("Invalid: zK-9487", jq(".z-fragment span:eq(0)").html());
		assertEquals(origId, jq(".z-fragment span:eq(1)").html());
	}

	@Test
	public void testValidatorValid() throws Exception {
		connect();

		type(jq(".z-fragment input[type=\"text\"]:eq(0)"), "AK-174");
		waitResponse();

		assertEquals("", jq(".z-fragment span:eq(0)").html());
		assertEquals("AK-174", jq(".z-fragment span:eq(1)").html());
	}
}
