/* F70_ZK_2002Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 21 15:00:38 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

/**
 * @author rudyhuang
 */
public class F70_ZK_2002Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> tabboxes = desktop.queryAll("tabbox");

		Assertions.assertEquals(0, tabboxes.get(0).as(Tabbox.class).getSelectedIndex());
		Assertions.assertEquals(1, tabboxes.get(1).as(Tabbox.class).getSelectedIndex());

		final Tabbox tabbox3 = tabboxes.get(2).as(Tabbox.class);
		Assertions.assertEquals(2, tabbox3.getSelectedIndex());
		final Caption caption = tabbox3.getSelectedTab().getCaption();
		Assertions.assertNotNull(caption, "Should have a caption in a tab");
		MatcherAssert.assertThat("Should have a button in a caption", caption.getLastChild(), instanceOf(Button.class));
		Assertions.assertEquals("background:green", ((Div) tabbox3.getSelectedPanel().getFirstChild()).getStyle());

		final Tabbox tabbox4 = tabboxes.get(3).as(Tabbox.class);
		MatcherAssert.assertThat(tabbox4.getSelectedTab().getLabel(), startsWith("New --"));
		MatcherAssert.assertThat(((Label) tabbox4.getSelectedPanel().getFirstChild()).getValue(), startsWith("New --"));

		final List<ComponentAgent> buttons = desktop.queryAll("hlayout ~ button");
		buttons.get(0).click();
		Assertions.assertEquals(4, tabbox4.getTabs().getChildren().size());
		Assertions.assertEquals("New -- Tab4", ((Tab) tabbox4.getTabs().getLastChild()).getLabel());
		buttons.get(1).click();
		Assertions.assertEquals(3, tabbox4.getTabs().getChildren().size());
		Assertions.assertEquals("New -- Tab2", ((Tab) tabbox4.getTabs().getFirstChild()).getLabel());
		buttons.get(2).click();
		Assertions.assertEquals(2, tabbox4.getTabs().getChildren().size());
		Assertions.assertEquals("New -- Model 1", ((Tab) tabbox4.getTabs().getFirstChild()).getLabel());
		buttons.get(3).click();
		Assertions.assertEquals(1, tabbox4.getSelectedIndex());
		tabboxes.get(3).query("tab:first-child").select();
		buttons.get(4).click();
		Assertions.assertEquals("[Model 1]", desktop.getZkLog().get(0));
	}
}
