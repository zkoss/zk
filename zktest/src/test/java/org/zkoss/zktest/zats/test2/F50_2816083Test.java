/* F50_2816083Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 15:56:29 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F50_2816083Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> labels = desktop.queryAll("label");
		Assert.assertEquals("mydiv vs div", labels.get(labels.size() - 1).as(Label.class).getValue());
	}
}
