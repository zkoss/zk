/** NavigationTest.java.

	Purpose:
		
	Description:
		
	History:
		9:33:00 AM Jan 26, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zhtml.Text;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;

/**
 * @author jumperchen
 *
 */
public class NavigationTest extends ZutiBasicTestCase {
	@SuppressWarnings("unchecked")
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent windowAgent = desktop.query("#window");
		ComponentAgent hostAgent = windowAgent.query("#host");
		ComponentAgent checkboxesAgent = hostAgent.getFirstChild(); 
		ComponentAgent multipleAgent = checkboxesAgent.getFirstChild();
		ComponentAgent orientAgent = multipleAgent.getNextSibling();
		
		assertTrue(multipleAgent.as(Checkbox.class).isChecked());
		assertFalse(orientAgent.as(Checkbox.class).isChecked());
		ComponentAgent navbarAgent = checkboxesAgent.getNextSibling().getNextSibling();
		ComponentAgent navitemAgent = navbarAgent.getFirstChild();
		for (String name : new String[] {"Home", "Services", "About", "Contact us"}) {
			while (navitemAgent.is(Text.class)) {
				navitemAgent = navitemAgent.getNextSibling();
			}
			assertEquals(name, navitemAgent.getFirstChild().as(Text.class).getValue().trim());
			navitemAgent = navitemAgent.getNextSibling();
		}
		navitemAgent = navbarAgent.getFirstChild();
		
		assertTrue(navbarAgent.getNextSibling().is(Hlayout.class));
		
		ComponentAgent contentAgent = null;
		for (Class contentType : new Class[]{Panel.class, Window.class, Groupbox.class, Div.class}) {
			while (navitemAgent.is(Text.class)) {
				navitemAgent = navitemAgent.getNextSibling();
			}
			navitemAgent.click();
			contentAgent = navbarAgent.getNextSibling().getLastChild();
			assertTrue(contentAgent.is(contentType));
			navitemAgent = navitemAgent.getNextSibling();
		}
		
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		orientAgent.check(true);
		
		assertTrue(orientAgent.as(Checkbox.class).isChecked());
		navbarAgent = checkboxesAgent.getNextSibling().getNextSibling();
		navitemAgent = navbarAgent.getFirstChild();
		for (String name : new String[] {"Home", "Services", "About", "Contact us"}) {
			while (navitemAgent.is(Text.class)) {
				navitemAgent = navitemAgent.getNextSibling();
			}
			assertEquals(name, navitemAgent.getFirstChild().as(Text.class).getValue().trim());
			navitemAgent = navitemAgent.getNextSibling();
		}
		navitemAgent = navbarAgent.getFirstChild();
		
		assertTrue(navbarAgent.getNextSibling().is(Vlayout.class));
		
		contentAgent = null;
		for (Class contentType : new Class[]{Panel.class, Window.class, Groupbox.class, Div.class}) {
			while (navitemAgent.is(Text.class)) {
				navitemAgent = navitemAgent.getNextSibling();
			}
			contentAgent = navbarAgent.getNextSibling().getFirstChild();
			assertTrue(contentAgent.is(contentType));
			navitemAgent.click();
			navitemAgent = navitemAgent.getNextSibling();
		}
		
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
}
