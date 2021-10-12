/* ArgsTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 26 15:41:10 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class ArgsTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/args.zul");

		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");

		Assert.assertEquals("A-Arg1", l1.text());
		Assert.assertEquals("B-myarg1", l2.text());
		Assert.assertEquals("A-Arg1", t1.val());
		Assert.assertEquals("B-myarg1", t2.val());

		type(t1, "X");
		waitResponse();
		Assert.assertEquals("X-Arg2-Arg1", l1.text());
		Assert.assertEquals("B-myarg1", l2.text());
		Assert.assertEquals("X-Arg2-Arg1", t1.val());
		Assert.assertEquals("B-myarg1", t2.val());

		type(t2, "Y");
		waitResponse();
		Assert.assertEquals("X-Arg2-Arg1", l1.text());
		Assert.assertEquals("Y-myarg2-myarg1", l2.text());
		Assert.assertEquals("X-Arg2-Arg1", t1.val());
		Assert.assertEquals("Y-myarg2-myarg1", t2.val());

		click(jq("@button:eq(0)"));
		waitResponse();

		Assert.assertEquals("X-Arg2Dennis-Arg1", l1.text());
		Assert.assertEquals("Y-myarg2Chen-myarg1", l2.text());
		Assert.assertEquals("X-Arg2Dennis-Arg1", t1.val());
		Assert.assertEquals("Y-myarg2Chen-myarg1", t2.val());

		JQuery t3 = jq("$t3");
		JQuery l3 = jq("$l3");
		type(t3, "ABC");
		waitResponse();
		Assert.assertEquals("value have to equals V1", l3.text());

		type(t3, "V1");
		waitResponse();
		Assert.assertEquals("", l3.text());
		Assert.assertEquals("V1-Arg1", l1.text());

		JQuery t4 = jq("$t4");
		JQuery l4 = jq("$l4");
		type(t4, "ABC");
		waitResponse();
		Assert.assertEquals("", l4.text());
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("value have to equals V2", l4.text());
		Assert.assertEquals("V1", t3.val());

		type(jq("$t4"), "V2");
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("execute cmd2", l4.text());
		Assert.assertEquals("V2-Arg1", l1.text());
		Assert.assertEquals("V2", t3.val());
	}
}
