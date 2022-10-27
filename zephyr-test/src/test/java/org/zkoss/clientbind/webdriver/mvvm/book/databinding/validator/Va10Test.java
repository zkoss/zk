/* Va10Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.validator;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author jameschu
 */
public class Va10Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery start = jq("$start input");
		JQuery end = jq("$end input");
		JQuery okButton = jq("$okButton");
		JQuery resultStartDb = jq("$resultStartDb input");
		JQuery resultEndDb = jq("$resultEndDb input");

		type(start, "2011/11/02");
		waitResponse();
		type(end, "2011/11/03");
		waitResponse();
		//check input is correct
		assertEquals("2011/11/02", start.val());
		assertEquals("2011/11/03", end.val());
		click(okButton);
		waitResponse();
		assertEquals(start.val(), resultStartDb.val());
		assertEquals(end.val(), resultEndDb.val());

		type(end, "2011/11/01");
		waitResponse();
		assertEquals("2011/11/01", end.val());
		click(okButton);
		waitResponse();
		assertEquals("2011/11/02", resultStartDb.val());
		assertEquals("2011/11/03", resultEndDb.val());
	}
}
