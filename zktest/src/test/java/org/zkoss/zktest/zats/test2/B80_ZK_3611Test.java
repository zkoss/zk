/* B80_ZK_3611Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Apr 24, 2017  2:45:22 PM, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author jameschu
 */
public class B80_ZK_3611Test extends ZATSTestCase {
	@Test
	public void test(){
		DesktopAgent desktop = connect();
		ComponentAgent button = desktop.query("button");
		button.click();
		Label label = desktop.queryAll("label").get(2).as(Label.class);
		assertEquals("24", label.getValue());
	}
}
