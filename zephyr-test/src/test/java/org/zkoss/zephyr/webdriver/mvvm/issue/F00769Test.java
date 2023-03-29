package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Disabled
public class F00769Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery msg = jq("$msg");
		JQuery selected = jq("$selected");
		JQuery clean1 = jq("$clean1");
		JQuery clean2 = jq("$clean2");
		JQuery select = jq("$select");
		JQuery reload = jq("$reload");
		JQuery select0 = jq("$select0");
		JQuery select1 = jq("$select1");
		JQuery showselect = jq("$showselect");

		click(jq("$A-0-1"));
		waitResponse();
		assertEquals("[A-0-1]", selected.text());
		click(showselect);
		waitResponse();
		assertEquals("[[0, 1]]", msg.text());

		click(jq("$A-1-0-1"));
		waitResponse();
		assertEquals("[A-0-1, A-1-0-1]", selected.text());
		click(showselect);
		waitResponse();
		assertEquals("[[0, 1], [1, 0, 1]]", msg.text());

		click(clean1);
		waitResponse();
		assertEquals("", selected.text());
		click(showselect);
		waitResponse();
		assertEquals("no selection", msg.text());

		click(select);
		waitResponse();
		assertEquals("[A-0-1, A-1-1-1]", selected.text());
		click(showselect);
		waitResponse();
		assertEquals("[[0, 1], [1, 1, 1]]", msg.text());

		click(jq("$A-1-0-1"));
		waitResponse();
		assertEquals("[A-0-1, A-1-0-1, A-1-1-1]", selected.text());
		click(showselect);
		waitResponse();
		assertEquals("[[0, 1], [1, 0, 1], [1, 1, 1]]", msg.text());

		click(clean2);
		waitResponse();
		assertEquals("[]", selected.text());
		click(showselect);
		waitResponse();
		assertEquals("no selection", msg.text());

		click(select);
		waitResponse();
		click(select0);
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[[0, 0], [0, 1], [1, 1, 1]]", msg.text());

		click(reload);
		waitResponse();
		assertEquals("[A-0-1, A-1-1-1]", selected.text());
		assertEquals("reloaded [A-0-1, A-1-1-1]", msg.text());
		click(showselect);
		waitResponse();
		assertEquals("[[0, 1], [1, 1, 1]]", msg.text());

		click(select1);
		waitResponse();
		click(showselect);
		waitResponse();
		assertEquals("[[0, 0, 1], [0, 1], [1, 1, 1]]", msg.text());

		click(reload);
		waitResponse();
		assertEquals("[A-0-1, A-1-1-1]", selected.text());
		assertEquals("reloaded [A-0-1, A-1-1-1]", msg.text());
		click(showselect);
		waitResponse();
		assertEquals("[[0, 1], [1, 1, 1]]", msg.text());
	}
}
