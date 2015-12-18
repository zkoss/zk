/* B80_ZK_2837Test.java

	Purpose:
		
	Description:
		
	History:
		10:49 AM 8/11/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_2837Test extends ZutiBasicTestCase {

	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect(getTestURL("B80-ZK-2837.zul"));
			ComponentAgent host = desktop.query("#host");
			List<ComponentAgent> buttons = desktop.queryAll("button");

			ComponentAgent next = host.getFirstChild();
			checkMessage(next, new String[] {"Before", "Details:",
					"Detail: initial detail", "After"});

			for (int i = 0; i < 2; i++) {
				buttons.get(0).click();
				checkMessage(next, new String[] {"Before", "Details:",
						"Detail: new detail", "After"});
			}

			// check shadow foreach
			checkVerifier(next.getNextSibling().getOwner(),
					HierarchyVerifier.class);

			// check shadow if
			checkVerifier(host.getOwner(), HierarchyVerifier.class);


			buttons.get(1).click();

			checkMessage(next, new String[] {"Before", "After"});

			// check shadow if
			checkVerifier(host.getOwner(), HierarchyVerifier.class);

			buttons.get(0).click();
			checkMessage(next,
					new String[] {"Before", "Details:", "Detail: new detail",
							"After"});

			// check shadow foreach
			checkVerifier(next.getNextSibling().getOwner(),
					HierarchyVerifier.class);

			// check shadow if
			checkVerifier(host.getOwner(), HierarchyVerifier.class);
		} catch (Exception e) {
			fail("No exception here\n" + e.getCause());
		}
	}

	private void checkMessage(ComponentAgent next, String[] messages) {
		if (messages.length < 3) {
			assertEquals(messages[0],
					next.getFirstChild().as(Label.class).getValue().trim());
			next = next.getNextSibling();
			assertEquals(messages[1],
					next.getFirstChild().as(Label.class).getValue().trim());
		} else {

			assertEquals(messages[0],
					next.getFirstChild().as(Label.class).getValue().trim());
			next = next.getNextSibling();
			assertEquals(messages[1],
					next.getFirstChild().as(Label.class).getValue().trim());

			// nested component
			next = next.getLastChild();
			assertEquals(messages[2],
					next.getFirstChild().as(Label.class).getValue().trim());

			next = next.getParent().getNextSibling();

			assertEquals(messages[3],
					next.getFirstChild().as(Label.class).getValue().trim());
		}
	}
}