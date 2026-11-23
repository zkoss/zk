/* B80_ZK_2915Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 21 16:12:40 CST 2015, Created by chunfu

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author chunfu
 */
public class B80_ZK_2915Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> labels = desktop.queryAll("label");
		List<String> uuids = new LinkedList<String>();
		for (ComponentAgent ca : labels) {
			uuids.add(ca.as(Label.class).getUuid());
		}
		desktop.query("button").click();
		labels = desktop.queryAll("label");
		for (int i = 0; i < labels.size(); i++) {
			assertEquals(uuids.get(i), labels.get(i).as(Label.class).getUuid());
		}
	}
}
