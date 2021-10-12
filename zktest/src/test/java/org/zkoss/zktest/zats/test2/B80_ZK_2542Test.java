/* B80_ZK_2542Test.java

	Purpose:
		
	Description:
		
	History:
		10:08 AM 9/8/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Test;
import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.sys.tracker.TrackerNode;
import org.zkoss.lang.Classes;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2542Test extends ZATSTestCase {
	@Test public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent bind = desktop.query("#bind");
		Binder binder = BinderUtil.getBinder(bind.getOwner());
		assertTrue(binder != null);

		Tracker tracker = ((BinderCtrl) binder).getTracker();
		try {
			final AccessibleObject acs = Classes
					.getAccessibleObject(tracker.getClass(), "allTrackerNodesByBean", new Class[]{Object.class},
							Classes.B_GET | Classes.B_METHOD_ONLY);
			if (acs instanceof Method) {
				acs.setAccessible(true);
				Collection<TrackerNode> allTrackerNodes = (Collection<TrackerNode>) ((Method)acs).invoke(tracker, binder.getViewModel());
				assertFalse(allTrackerNodes == null);
				assertEquals(1, allTrackerNodes.size());
				assertEquals(2, allTrackerNodes.iterator().next().getDependents().size());
			} else {
				fail("cannot run here");
			}
		} catch (Exception e) {
			fail("no exception here");
		}
	}
}