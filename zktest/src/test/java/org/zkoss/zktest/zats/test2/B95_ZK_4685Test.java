/* B95_ZK_4685Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B95_ZK_4685Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		List<ComponentAgent> button = desktop.queryAll("button");
		button.get(0).click();
		desktop.query("#closeBtn").click();
		button.get(1).click();
		desktop.query("#closeBtn").click();
		Assertions.assertEquals("[formContent.commonProp, formContent.prop1][formContent.commonProp, formContent.prop2]", desktop.query("#result").as(Label.class).getValue());
	}
}
