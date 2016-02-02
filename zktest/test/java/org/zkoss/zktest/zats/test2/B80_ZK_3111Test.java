/* B80_ZK_3111Test.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 02/01/16, Created by christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author christopher
 */
public class B80_ZK_3111Test extends WebDriverTestCase {

	@Test
	public void testar() {
		test("ar");
	}

	@Test
	public void testbg() {
		test("bg");
	}

	@Test
	public void testca() {
		test("ca");
	}

	@Test
	public void testcs() {
		test("cs");
	}

	@Test
	public void testda() {
		test("da");
	}

	@Test
	public void testde() {
		test("de");
	}

	@Test
	public void testes() {
		test("es");
	}

	@Test
	public void testfr() {
		test("fr");
	}

	@Test
	public void testhu() {
		test("hu");
	}

	@Test
	public void testid() {
		test("id");
	}

	@Test
	public void testit() {
		test("it");
	}

	@Test
	public void testja() {
		test("ja");
	}

	@Test
	public void testko() {
		test("ko");
	}

	@Test
	public void testnl() {
		test("nl");
	}

	@Test
	public void testpl() {
		test("pl");
	}

	@Test
	public void testpt() {
		test("pt");
	}

	@Test
	public void testpt_BR() {
		test("pt_BR");
	}

	@Test
	public void testro() {
		test("ro");
	}

	@Test
	public void testru() {
		test("ru");
	}

	@Test
	public void testsk() {
		test("sk");
	}

	@Test
	public void testsl() {
		test("sl");
	}

	@Test
	public void testsv() {
		test("sv");
	}

	@Test
	public void testtr() {
		test("tr");
	}

	@Test
	public void testuk() {
		test("uk");
	}

	@Test
	public void testvi() {
		test("vi");
	}

	@Test
	public void testzh() {
		test("zh");
	}

	@Test
	public void testzh_CN() {
		test("zh_CN");
	}

	@Test
	public void testzh_SG() {
		test("zh_SG");
	}

	@Test
	public void testzh_TW() {
		test("zh_TW");
	}

	private void test(String locale) {
		connect();

		//check init label, should be english
		Iterator<JQuery> btns = jq(".z-window-close, .z-panel-close").iterator();
		while (btns.hasNext()) {
			JQuery btn = btns.next();
			assertTrue(btn.attr("title").equals("Close"));
		}

		//switch locale
		JQuery textbox = jq("@textbox");
		type(textbox, locale);
		waitResponse(true);
		click(jq("button").eq(0));
		waitResponse(true);

		//check label again, should be localized label
		btns = jq(".z-window-close, .z-panel-close").iterator();
		while (btns.hasNext()) {
			JQuery btn = btns.next();
			assertFalse(btn.attr("title").contains("27112700"));
		}
	}
}
