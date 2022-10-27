/* TemplateTest.java

	Purpose:
		
	Description:
		
	History:
		Fri May 07 16:20:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.shadow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class TemplateTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/shadow/template/template.zul");
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
		assertEquals(4, navs.eq(0).find("@navitem").length());
		assertEquals(3, navs.eq(1).find("@navitem").length());
	}

	@Test
	public void testExternal() {
		connect("/mvvm/book/shadow/template/template-external.zul");
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
		assertEquals(4, navs.eq(0).find("@navitem").length());
		assertEquals(3, navs.eq(1).find("@navitem").length());
	}

	@Test
	public void testSrc() {
		connect("/mvvm/book/shadow/template/template-src.zul");
		JQuery navitems = jq("$navbar > @navitem");
		JQuery navs = jq("$navbar > @nav");
		assertEquals(4, navitems.length());
		assertEquals(2, navs.length());
		assertEquals(4, navs.eq(0).find("@navitem").length());
		assertEquals(3, navs.eq(1).find("@navitem").length());
	}
}
