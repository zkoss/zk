/** B80_ZK_2792Test.java.

	Purpose:
		
	Description:
		
	History:
		1:09:17 PM Jun 17, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class B80_ZK_2792Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent bind = desktop.query("#bind");
		
		ComponentAgent next = bind.getFirstChild().getNextSibling();
		
		for (String val : Arrays.asList("1", "2", "3", "4")) {
			assertEquals(val, next.getFirstChild().as(Label.class).getValue());
			next = next.getNextSibling();
		}
		
		bind.getFirstChild().click();
		next = bind.getFirstChild().getNextSibling();
		for (String val : Arrays.asList("1", "2", "2.5", "3", "4")) {
			assertEquals(val, next.getFirstChild().as(Label.class).getValue());
			next = next.getNextSibling();
		}
	}

}
