/* F85_ZK_3324Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jun 04 12:00:55 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F85_ZK_3324Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent textboxAgent = desktop.query("#textbox");
		textboxAgent.type("xxx");
		
		Label label = desktop.query("#label").as(Label.class);
		Assertions.assertTrue(label.getValue().isEmpty());
		Assertions.assertEquals("xxx", textboxAgent.as(Textbox.class).getValue());
		
		textboxAgent.stroke("#enter");
		Assertions.assertEquals(textboxAgent.as(Textbox.class).getValue(), label.getValue());
	}
}
