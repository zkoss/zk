/* B50_ZK_328Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 08 17:43:38 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_ZK_328Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery div1 = jq(".z-div").eq(1);
		int maxHeight = div1.height();
		int maxWidth = div1.width();
		
		click(jq(".z-panel-maximize").eq(0));
		waitResponse();
		JQuery panel0 = jq(".z-panel").eq(0);
		JQuery pChildren0 = jq(".z-panelchildren").eq(0);
		JQuery panel1 = jq(".z-panel").eq(1);
		JQuery pChildren1 = jq(".z-panelchildren").eq(1);
		
		Assert.assertEquals(maxHeight, panel0.outerHeight());
		Assert.assertEquals(maxWidth, panel0.outerWidth());
		int panelChildrenHeight = panel0.innerHeight() - jq(".z-panel-header").eq(0).outerHeight();
		Assert.assertEquals(panelChildrenHeight, pChildren0.outerHeight());
		Assert.assertEquals(panel0.innerWidth(), pChildren0.outerWidth());
		
		Assert.assertEquals(maxHeight, panel1.outerHeight());
		Assert.assertEquals(maxWidth, panel1.outerWidth());
		panelChildrenHeight = panel1.innerHeight() - jq(".z-panel-header").eq(1).outerHeight();
		Assert.assertEquals(panelChildrenHeight, pChildren1.outerHeight());
		Assert.assertEquals(panel1.innerWidth(), pChildren1.outerWidth());
	}
}
