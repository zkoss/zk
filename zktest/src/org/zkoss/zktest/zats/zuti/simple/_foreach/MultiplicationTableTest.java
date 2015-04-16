/** MultiplicationTableTest.java.

	Purpose:
		
	Description:
		
	History:
		12:27:10 PM Nov 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple._foreach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zhtml.Text;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class MultiplicationTableTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
			ComponentAgent xChild = xit.next();
			if (xChild.getOwner() instanceof Text)
				continue;
			List<ComponentAgent> yChildren = xChild.getChildren();
			int y = 1;
			for (Iterator<ComponentAgent> yit = yChildren.iterator(); yit
					.hasNext();) {
				ComponentAgent yChild = yit.next();
				if (yChild.getOwner() instanceof Text)
					continue;
				assertEquals(x + " X " + y + " = " + (x * y), yChild
						.getFirstChild().as(Text.class).getValue());
				y++;
			}
			x++;
		}
		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
