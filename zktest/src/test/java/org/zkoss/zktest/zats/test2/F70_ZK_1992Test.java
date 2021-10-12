/* F70_ZK_1992Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 11:01:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.matchesRegex;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F70_ZK_1992Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testChosenbox();
		testSelectbox();
		testListbox();
		testTree();
	}

	private void testChosenbox() {
		click(jq("@chosenbox input"));
		waitResponse(true);
		click(jq(".z-chosenbox-popup .z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$cb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: []", jq("$cb_pso").text());

		click(jq("@chosenbox input"));
		waitResponse(true);
		click(jq(".z-chosenbox-popup .z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$cb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: [test1]", jq("$cb_pso").text());

		click(jq(".z-chosenbox-item:eq(0) .z-chosenbox-delete"));
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$cb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: [test1, test2]", jq("$cb_pso").text());
	}

	private void testSelectbox() {
		Select sb = new Select(toElement(jq("@selectbox")));
		sb.selectByVisibleText("test1");
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$sb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: []", jq("$sb_pso").text());

		sb.selectByVisibleText("test2");
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$sb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: [test1]", jq("$sb_pso").text());

		sb.selectByVisibleText("test1");
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$sb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: [test2]", jq("$sb_pso").text());
	}

	private void testListbox() {
		click(jq("@listitem:eq(0)"));
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$lb_psi").text());
		Assert.assertEquals("Previous SelectionObjects: []", jq("$lb_pso").text());

		click(jq("@listitem:eq(1)"));
		waitResponse();
		Assert.assertThat(jq("$lb_psi").text(), matchesRegex("Previous SelectionItems: \\[<Listitem \\w{5,}>]"));
		Assert.assertEquals("Previous SelectionObjects: [test1]", jq("$lb_pso").text());

		click(jq("@listitem:eq(0)"));
		waitResponse();
		Assert.assertThat(jq("$lb_psi").text(), matchesRegex("Previous SelectionItems: \\[<Listitem \\w{5,}>]"));
		Assert.assertEquals("Previous SelectionObjects: [test2]", jq("$lb_pso").text());
	}

	private void testTree() {
		click(widget("@treerow:eq(0)").$n("open"));
		waitResponse();

		click(jq("@treerow:eq(0)"));
		waitResponse();
		Assert.assertEquals("Previous SelectionItems: []", jq("$tr_psi").text());
		Assert.assertEquals("Previous SelectionObjects: []", jq("$tr_pso").text());

		click(jq("@treerow:eq(1)"));
		waitResponse();
		Assert.assertThat(jq("$tr_psi").text(), matchesRegex("Previous SelectionItems: \\[<Treeitem \\w{5,}>]"));
		Assert.assertEquals("Previous SelectionObjects: [root]", jq("$tr_pso").text());

		click(jq("@treerow:eq(0)"));
		waitResponse();
		Assert.assertThat(jq("$tr_psi").text(), matchesRegex("Previous SelectionItems: \\[<Treeitem \\w{5,}>]"));
		Assert.assertEquals("Previous SelectionObjects: [test1]", jq("$tr_pso").text());
	}
}
