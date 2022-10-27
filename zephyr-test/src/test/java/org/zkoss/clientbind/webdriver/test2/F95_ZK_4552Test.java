/* F95_ZK_4552Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 12:03:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F95_ZK_4552Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/test2/F95-ZK-4552-syntax.zul");
		sleep(2000);
		JQuery btns = jq("@button");
		for (JQuery btn : btns) {
			click(btn);
			waitResponse();
		}
		List<String> zkLog = Arrays.asList(getZKLog().split("\n"));
		Assertions.assertEquals(2, zkLog.size());
		Assertions.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assertions.assertEquals("do test!!aa,12,2", zkLog.get(1));
		JQuery label1 = jq("$l1");
		Assertions.assertEquals("a", label1.text());
		JQuery label2 = jq("$l2");
		Assertions.assertEquals("bbb", label2.text());
		JQuery div = jq("$list");
		Assertions.assertEquals(6, div.children().length());
	}

	@Test
	public void testNested() {
		connect("/test2/F95-ZK-4552-nested.zul");
		sleep(2000);
		JQuery btns = jq("@button");
		for (JQuery btn : btns) {
			click(btn);
			waitResponse();
		}
		List<String> zkLog = Arrays.asList(getZKLog().split("\n"));
		Assertions.assertEquals(3, zkLog.size());
		Assertions.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assertions.assertEquals("do test!!aa,12,2", zkLog.get(1));
		Assertions.assertEquals("nested clicked!", zkLog.get(2));
		JQuery label1 = jq("$l1");
		Assertions.assertEquals("a", label1.text());
		JQuery label2 = jq("$l2");
		Assertions.assertEquals("bbb", label2.text());
		JQuery div = jq("$list");
		Assertions.assertEquals(6, div.children().length());
		JQuery label3 = jq("$l3");
		Assertions.assertEquals("a1", label3.text());
		JQuery nested = jq("$nested");
		Assertions.assertEquals(8, nested.children().length());
	}

	@Test
	public void testException() {
		connect("/test2/F95-ZK-4552-syntax-exception.zul");
		assertNotEquals("-1", getEval("document.body.innerHTML.indexOf('HTTP ERROR 500 org.zkoss.zk.ui.UiException: Not allowed to use named parameters before un-named parameters')"));
	}
}
