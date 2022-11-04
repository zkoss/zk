package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F0010Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l0 = jq("$l0");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery t0 = jq("$t0");
		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");

		assertEquals("A-toUI-c0", l0.text());
		assertEquals("B-toUI-c1", l1.text());
		assertEquals("C-toUI-c2", l2.text());
		assertEquals("A-toUI-c0", t0.val());
		assertEquals("B-toUI-c1", t1.val());
		assertEquals("C-toUI-c2", t2.val());

		type(t0, "I");
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("B-toUI-c1", l1.text());
		assertEquals("C-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("B-toUI-c1", t1.val());
		assertEquals("C-toUI-c2", t2.val());

		type(t1, "J");
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("J-toBean-c1-toUI-c1", l1.text());
		assertEquals("C-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("J-toBean-c1-toUI-c1", t1.val());
		assertEquals("C-toUI-c2", t2.val());

		type(t2, "K");
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("J-toBean-c1-toUI-c1", l1.text());
		assertEquals("K-toBean-c2-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("J-toBean-c1-toUI-c1", t1.val());
		assertEquals("K-toBean-c2-toUI-c2", t2.val());

		type(t1, "X");
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("X-toBean-c1-toUI-c1", l1.text());
		assertEquals("K-toBean-c2-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("X-toBean-c1-toUI-c1", t1.val());
		assertEquals("K-toBean-c2-toUI-c2", t2.val());

		click(jq("$btn1"));
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("X-toBean-c1-toUI-c3", l1.text());
		assertEquals("K-toBean-c2-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("X-toBean-c1-toUI-c3", t1.val());
		assertEquals("K-toBean-c2-toUI-c2", t2.val());

		type(t1, "X");
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("X-toBean-c3-toUI-c3", l1.text());
		assertEquals("K-toBean-c2-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("X-toBean-c3-toUI-c3", t1.val());
		assertEquals("K-toBean-c2-toUI-c2", t2.val());

		click(jq("$btn2"));
		waitResponse();
		assertEquals("I-toBean-c0-toUI-c0", l0.text());
		assertEquals("X-toBean-c3-toUI-c4", l1.text());
		assertEquals("K-toBean-c2-toUI-c2", l2.text());
		assertEquals("I-toBean-c0-toUI-c0", t0.val());
		assertEquals("X-toBean-c3-toUI-c4", t1.val());
		assertEquals("K-toBean-c2-toUI-c2", t2.val());
	}
}
