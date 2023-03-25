package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F80_ZK_3300Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		testMVVM();
	}

	@Test
	public void testZHTML() {
		connect("/test2/F80-ZK-3300.zhtml");
		testMVVM();
	}

	private void testMVVM() {
		//checkbox
		JQuery jqChkLabel = jq("$chk_label");
		JQuery jqChkBind = jq("$chk_bind input");
		JQuery jqInpChkInit = jq("$inp_chk_init");
		JQuery jqInpChkLoad = jq("$inp_chk_load");
		JQuery jqInpChkBind = jq("$inp_chk_bind");
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(false, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		click(jqChkBind);
		waitResponse();
		assertEquals("true", jqChkLabel.text());
		assertEquals(true, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(true, jqInpChkLoad.is(":checked"));
		assertEquals(true, jqInpChkBind.is(":checked"));
		click(jqChkBind);
		waitResponse();
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(false, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		click(jqInpChkInit);
		waitResponse();
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(true, jqInpChkInit.is(":checked"));
		assertEquals(false, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		click(jqInpChkInit);
		waitResponse();
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(false, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		click(jqInpChkLoad);
		waitResponse();
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(true, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		click(jqInpChkLoad);
		waitResponse();
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(false, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		click(jqInpChkBind);
		waitResponse();
		assertEquals("true", jqChkLabel.text());
		assertEquals(true, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(true, jqInpChkLoad.is(":checked"));
		assertEquals(true, jqInpChkBind.is(":checked"));
		click(jqInpChkBind);
		waitResponse();
		assertEquals("false", jqChkLabel.text());
		assertEquals(false, jqChkBind.is(":checked"));
		assertEquals(false, jqInpChkInit.is(":checked"));
		assertEquals(false, jqInpChkLoad.is(":checked"));
		assertEquals(false, jqInpChkBind.is(":checked"));
		//textbox
		JQuery jqTbLabel = jq("$tb_label");
		JQuery jqTbBind = jq("$tb_bind");
		JQuery jqInpTbInit = jq("$inp_tb_init");
		JQuery jqInpTbLoad = jq("$inp_tb_load");
		JQuery jqInpTbBind = jq("$inp_tb_bind");
		assertEquals("ddd", jqTbLabel.text());
		assertEquals("ddd", jqTbBind.val());
		assertEquals("ddd", jqInpTbInit.val());
		assertEquals("ddd", jqInpTbLoad.val());
		assertEquals("ddd", jqInpTbBind.val());
		type(jqTbBind, "a");
		waitResponse();
		assertEquals("a", jqTbLabel.text());
		assertEquals("a", jqTbBind.val());
		assertEquals("ddd", jqInpTbInit.val());
		assertEquals("a", jqInpTbLoad.val());
		assertEquals("a", jqInpTbBind.val());
		type(jqInpTbInit, "s");
		waitResponse();
		assertEquals("a", jqTbLabel.text());
		assertEquals("a", jqTbBind.val());
		assertEquals("s", jqInpTbInit.val());
		assertEquals("a", jqInpTbLoad.val());
		assertEquals("a", jqInpTbBind.val());
		type(jqInpTbLoad, "d");
		waitResponse();
		assertEquals("a", jqTbLabel.text());
		assertEquals("a", jqTbBind.val());
		assertEquals("s", jqInpTbInit.val());
		assertEquals("d", jqInpTbLoad.val());
		assertEquals("a", jqInpTbBind.val());
		type(jqInpTbBind, "f");
		waitResponse();
		assertEquals("f", jqTbLabel.text());
		assertEquals("f", jqTbBind.val());
		assertEquals("s", jqInpTbInit.val());
		assertEquals("f", jqInpTbLoad.val());
		assertEquals("f", jqInpTbBind.val());
		//a
		JQuery jqAInitDownload = jq("$a_init_download");
		JQuery jqAInitMedia = jq("$a_init_media");
		JQuery jqAInitTab = jq("$a_init_tab");
		JQuery jqALoadDownload = jq("$a_load_download");
		JQuery jqALoadMedia = jq("$a_load_media");
		JQuery jqALoadTab = jq("$a_load_tab");
		assertEquals("123", jqAInitDownload.attr("download"));
		assertEquals("./F80-ZK-3300.zul", jqAInitDownload.attr("href"));
		assertEquals("print and (resolution:300dpi)", jqAInitMedia.attr("media"));
		assertEquals("./F80-ZK-3300.zul", jqAInitMedia.attr("href"));
		assertEquals("nofollow", jqAInitTab.attr("rel"));
		assertEquals("_blank", jqAInitTab.attr("target"));
		assertEquals("text/html", jqAInitTab.attr("type"));
		assertEquals("./F80-ZK-3300.zul", jqAInitTab.attr("href"));
		assertEquals("123", jqALoadDownload.attr("download"));
		assertEquals("./F80-ZK-3300.zul", jqALoadDownload.attr("href"));
		assertEquals("print and (resolution:300dpi)", jqALoadMedia.attr("media"));
		assertEquals("./F80-ZK-3300.zul", jqALoadMedia.attr("href"));
		assertEquals("nofollow", jqALoadTab.attr("rel"));
		assertEquals("_blank", jqALoadTab.attr("target"));
		assertEquals("text/html", jqALoadTab.attr("type"));
		assertEquals("./F80-ZK-3300.zul", jqALoadTab.attr("href"));
		click(jq("$chg_a_btn"));
		waitResponse();
		assertEquals("321", jqALoadDownload.attr("download"));
		assertEquals("https://www.zkoss.org", jqALoadDownload.attr("href"));
		assertEquals("print", jqALoadMedia.attr("media"));
		assertEquals("https://www.zkoss.org", jqALoadMedia.attr("href"));
		assertEquals("nofollow", jqALoadTab.attr("rel"));
		assertEquals("_self", jqALoadTab.attr("target"));
		assertEquals("text/html", jqALoadTab.attr("type"));
		assertEquals("https://www.zkoss.org", jqALoadTab.attr("href"));
	}
}
