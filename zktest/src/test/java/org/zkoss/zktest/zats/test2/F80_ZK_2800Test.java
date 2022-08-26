/** F80_ZK_2800Test.java.

	Purpose:
		
	Description:
		
	History:
		3:32:58 PM Jul 6, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
@Disabled
public class F80_ZK_2800Test extends ZATSTestCase {
	
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
		List<Component> oldIntances = new LinkedList<Component>();
		
		for (ComponentAgent ca : result.getChildren())
			oldIntances.add(ca.getOwner());
		
		assertEquals("[0, 1, 2, 3, 4, 5]", testLabel.as(Label.class).getValue().trim());
		next.click();
		
		int checkIndex = 0;
		for (Component c : oldIntances) {
			if (checkIndex == 2) {
				assertFalse(result.getChild(checkIndex).getOwner() == c,
						"The instance should not be the same!");
			} else {
				assertTrue(result.getChild(checkIndex).getOwner() == c,
						"The instance should be the same!");
			}
			checkIndex++;
		}
		
		index = 0;
		for (String val : Arrays.asList("0", "1", "22", "3", "4", "5")) {
			assertEquals(val, result.getChild(index++).as(Label.class).getValue());
		}

		assertEquals("[0, 1, 22, 3, 4, 5]", testLabel.as(Label.class).getValue().trim());
	}

}