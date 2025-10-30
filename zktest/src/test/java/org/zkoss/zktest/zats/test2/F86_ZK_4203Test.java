/* F86_ZK_4203Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Aug 15, 2019 11:45:22 PM, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author jameschu
 */
public class F86_ZK_4203Test extends ZATSTestCase {
	@Test
	public void test(){
		DesktopAgent desktop = connect();
		ComponentAgent button = desktop.query("button");
		button.click();
		Label label = desktop.queryAll("label").get(1).as(Label.class);
		assertEquals("10", label.getValue());
	}
}
