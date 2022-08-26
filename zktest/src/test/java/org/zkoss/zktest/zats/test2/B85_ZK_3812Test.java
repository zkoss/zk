/* B85_ZK_3812Test.java

	Purpose:

	Description:

	History:
		Wed Jan 17 11:42:38 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B85_ZK_3812Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		verifyResult(desktop, "d1");
		desktop.queryAll("button").get(0).click();
		verifyResult(desktop, "d2");
		desktop.queryAll("button").get(0).click();
		verifyResult(desktop, "d3");
		desktop.queryAll("button").get(0).click();
		verifyResult(desktop, "d4");
		desktop.queryAll("button").get(0).click();
		verifyResult(desktop, "d5");
	}

	public void verifyResult(DesktopAgent desktop, String result) {
		List<Label> labels = new ArrayList<>();
		labels.add(desktop.query("#h1 label").as(Label.class));
		labels.add(desktop.query("#h2 label").as(Label.class));
		labels.add(desktop.query("#h3 label").as(Label.class));
		labels.add(desktop.query("#h4 label").as(Label.class));
		for (int i = 0; i < labels.size(); i++) {
			Assertions.assertEquals(result, labels.get(i).getValue());
		}
	}
}
