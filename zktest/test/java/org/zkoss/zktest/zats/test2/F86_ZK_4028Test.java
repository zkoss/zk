/* F86_ZK_4028Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 17:39:07 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.SelectByIndexAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Label curr = desktop.query("#curr").as(Label.class);
		Label isFirst = desktop.query("#isfirst").as(Label.class);
		Label isLast = desktop.query("#islast").as(Label.class);

		assertEquals("Current: Destination", curr.getValue());
		assertEquals("Is first: true", isFirst.getValue());
		assertEquals("Is last: false", isLast.getValue());

		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.get(0).click();
		assertEquals("Current: Destination", curr.getValue());
		assertEquals("Is first: true", isFirst.getValue());
		assertEquals("Is last: false", isLast.getValue());

		btns.get(1).click();
		assertEquals("Current: Accommodation", curr.getValue());
		assertEquals("Is first: false", isFirst.getValue());
		assertEquals("Is last: false", isLast.getValue());

		ComponentAgent selectbox = desktop.query("selectbox");
		selectbox.as(SelectByIndexAgent.class).select(4);
		assertEquals("Current: Enjoy Holiday", curr.getValue());
		assertEquals("Is first: false", isFirst.getValue());
		assertEquals("Is last: true", isLast.getValue());
	}
}
