package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Created by rudyhuang on 2017/06/05.
 */
public class F85_ZK_3681_DatabindingTest extends F85_ZK_3681_TestCase {
	@Test
	public void testAnnotationBind() throws Exception {
		connect();

		final JQuery textboxs = jq(".z-fragment input[type=\"text\"]");
		final String input = "{\"ZK\":11111111}";
		type(textboxs.eq(0), input);
		waitResponse();

		assertEquals(input, textboxs.eq(0).val(), "@bind error");
		assertEquals(input, textboxs.eq(1).val(), "@load error");
		assertEquals("", textboxs.eq(2).val(), "@save error");
		assertEquals(input, jq(".z-fragment span").eq(0).html(), "value error");
	}

	@Test
	public void testAnnotationLoad() throws Exception {
		connect();

		final JQuery textboxs = jq(".z-fragment input[type=\"text\"]");
		final String input = "1111111111";
		type(textboxs.eq(1), input);
		waitResponse();

		assertEquals("ZK", textboxs.eq(0).val(), "@bind error");
		assertEquals(input, textboxs.eq(1).val(), "@load error");
		assertEquals("", textboxs.eq(2).val(), "@save error");
		assertEquals("ZK", jq(".z-fragment span").eq(0).html(), "value error");
	}

	@Test
	public void testAnnotationSave() throws Exception {
		connect();

		final JQuery textboxs = jq(".z-fragment input[type=\"text\"]");
		final String input = "1111111111";
		type(textboxs.eq(2), input);
		waitResponse();

		assertEquals(input, textboxs.eq(0).val(), "@bind error");
		assertEquals(input, textboxs.eq(1).val(), "@load error");
		assertEquals(input, textboxs.eq(2).val(), "@save error");
		assertEquals(input, jq(".z-fragment span").eq(0).html(), "value error");
	}

	@Test
	public void testDataCoercionIntOk() throws Exception {
		connect();

		type(jq(".z-fragment input[type=\"text\"]:eq(3)"), "9487");
		waitResponse();
		assertEquals("9487", jq(".z-fragment span").eq(1).html());
	}

	@Test
	public void testDataCoercionIntError() throws Exception {
		connect();

		type(jq(".z-fragment input[type=\"text\"]:eq(3)"), "invalid number");
		waitResponse();
		assertEquals("0", jq(".z-fragment span").eq(1).html());
	}

	@Test
	public void testDataCoercionBigIntOk() throws Exception {
		connect();

		String number = (2L * Integer.MAX_VALUE) + "";
		type(jq(".z-fragment input[type=\"text\"]:eq(4)"), number);
		waitResponse();
		assertEquals(number, jq(".z-fragment span").eq(2).html());
	}

	@Test
	public void testDataCoercionBigIntError() throws Exception {
		connect();

		type(jq(".z-fragment input[type=\"text\"]:eq(4)"), "invalid number");
		waitResponse();
		assertEquals("0", jq(".z-fragment span").eq(2).html());
	}

	@Test
	public void testDataCoercionObjectProperty() throws Exception {
		connect();

		type(jq(".z-fragment input[type=\"text\"]:eq(5)"), "true");
		waitResponse();
		assertTrue(jq(".z-fragment span").eq(3).html().contains("\"done\": true"), "value error");
	}
}
