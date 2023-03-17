package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00772InterCommTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect("/mvvm/issue/F00772-inter-comm.zul");
		waitResponse();

		JQuery t11 = jq("$inc1 $inc1win $t11");
		JQuery l21 = jq("$inc2 $inc2win $l21");
		JQuery l31 = jq("$inc3 $inc3win $l31");
		JQuery l41 = jq("$inc4 $inc4win $l41");
		JQuery postx = jq("$postx");
		JQuery posty = jq("$posty");
		JQuery postz = jq("$postz");
		JQuery postmy = jq("$postmy");
		JQuery globalx = jq("$inc1 $inc1win $globalx");
		JQuery globaly = jq("$inc2 $inc2win $globaly");
		JQuery globalz = jq("$inc3 $inc3win $globalz");

		assertEquals("", t11.val());
		assertEquals("", l21.text());
		assertEquals("", l31.text());
		assertEquals("", l41.text());

		click(postx);
		waitResponse();
		assertEquals("postX-X1", l21.text());
		assertEquals("postX-X2", l31.text());
		assertEquals("", l41.text());

		click(posty);
		waitResponse();
		assertEquals("postX-X1", l21.text());
		assertEquals("postY-X2", l31.text());
		assertEquals("", l41.text());

		click(postz);
		waitResponse();
		assertEquals("postE-X1", l21.text());
		assertEquals("postZ-X3", l31.text());
		assertEquals("", l41.text());

		click(postmy);
		waitResponse();
		assertEquals("postE-X1", l21.text());
		assertEquals("postZ-X3", l31.text());
		assertEquals("postMy-XMy", l41.text());

		type(t11, "A");
		waitResponse();
		click(globalx);
		waitResponse();
		assertEquals("A-local-X1", l21.text());
		assertEquals("A-local-X2", l31.text());
		assertEquals("postMy-XMy", l41.text());

		click(globaly);
		waitResponse();
		assertEquals("A-local-X1", l21.text());
		assertEquals("A-local-X1-X2", l31.text());
		assertEquals("postMy-XMy", l41.text());

		click(globalz);
		waitResponse();
		assertEquals("postE-X1", l21.text());
		assertEquals("A-local-X1-X2-X3", l31.text());
		assertEquals("postMy-XMy", l41.text());
	}
}
