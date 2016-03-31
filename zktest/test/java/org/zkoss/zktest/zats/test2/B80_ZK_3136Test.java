/* B80_ZK_3136Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 16:01:32 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;
import static org.junit.Assert.assertEquals;

/**
 * @author jameschu
 */
public class B80_ZK_3136Test extends ZATSTestCase {
	@Test public void test() {
		connect();
		DesktopAgent desktop = connect();
		ComponentAgent textbox = desktop.query("textbox");
		assertEquals("*", textbox.as(Textbox.class).getValue());
	}
}
