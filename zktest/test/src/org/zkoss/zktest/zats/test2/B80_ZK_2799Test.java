/** B80_ZK_2799Test.java.

	Purpose:
		
	Description:
		
	History:
		2:58:35 PM Jul 6, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class B80_ZK_2799Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent result = desktop.query("#result");
		ComponentAgent testLabel = desktop.query("#testLabel");
		
		ComponentAgent next = result.getNextSibling().getNextSibling();
		
		int index = 0;
		for (String val : Arrays.asList("0", "1", "2", "3", "4", "5")) {
			assertEquals(val, result.getChild(index++).as(Label.class).getValue());
		}
		
		assertEquals("[0, 1, 2, 3, 4, 5]", testLabel.as(Label.class).getValue().trim());
		next.click();
		
		index = 0;
		for (String val : Arrays.asList("0", "1", "102", "103", "2", "3", "4", "5")) {
			assertEquals(val, result.getChild(index++).as(Label.class).getValue());
		}

		assertEquals("[0, 1, 102, 103, 2, 3, 4, 5]", testLabel.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		
		next.click();
		
		index = 0;
		for (String val : Arrays.asList("0", "1", "1022", "1021", "102", "103", "2", "3", "4", "5")) {
			assertEquals(val, result.getChild(index++).as(Label.class).getValue());
		}

		assertEquals("[0, 1, 1022, 1021, 102, 103, 2, 3, 4, 5]", testLabel.as(Label.class).getValue().trim());
	}

}

