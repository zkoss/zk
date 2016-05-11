/** F80_ZK_3185Test.java.

 Purpose:

 Description:

 History:
 	Mon May 9 18:14:02 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 *
 */
public class F80_ZK_3185Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		JQuery validate_button = jq("$validate");
		JQuery cancel_button = jq("$cancel");

		for (int i = 0; i < 6; i++) {
			JQuery dateBox = jq("@datebox:eq(" + i + ") input");
			type(dateBox, "s");
			waitResponse();
			click(validate_button);
			waitResponse(true);
			assertEquals("false", getZKLog());
			click(cancel_button);
			waitResponse();
			closeZKLog();
			waitResponse();
		}

		//test add new one
		click(jq("$addSub"));
		waitResponse();
		JQuery newDateBox = jq("@datebox:eq(6) input");
		type(newDateBox, "s");
		waitResponse();
		click(validate_button);
		waitResponse(true);
		assertEquals("false", getZKLog());
		click(cancel_button);
		waitResponse();
		closeZKLog();
		waitResponse();

		//test serialize
		click(jq("$serialize"));
		waitResponse();
		assertFalse(getZKLog().indexOf("done deserialize") == -1);
		closeZKLog();
		waitResponse();
		validate_button = jq("$validate");
		cancel_button = jq("$cancel");
		for (int i = 6; i < 0; i--) {
			JQuery dateBox = jq("@datebox:eq(" + i + ") input");
			type(dateBox, "s");
			waitResponse();
			click(validate_button);
			waitResponse(true);
			assertEquals("false", getZKLog());
			click(cancel_button);
			waitResponse();
			closeZKLog();
			waitResponse();
		}
	}
}