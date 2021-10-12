/* B96_ZK_4832Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 8 10:50:21 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B96_ZK_4832Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		Label label1 = desktopAgent.query("#uid1").as(Label.class);
		Label label2 = desktopAgent.query("#uid2").as(Label.class);
		Label label3 = desktopAgent.query("#uid3").as(Label.class);
		ComponentAgent updBtn1 = desktopAgent.query("#updBtn1");
		ComponentAgent updBtn2 = desktopAgent.query("#updBtn2");
		ComponentAgent updBtn3 = desktopAgent.query("#updBtn3");
		String label1Value = label1.getValue();
		String label2Value = label2.getValue();
		String label3Value = label3.getValue();
		for (int i = 0; i < 5; i++) {
			//test label 1 & label 3
			updBtn1.click();
			String val1 = label1.getValue();
			assertNotEquals(label1Value, val1);
			String val3 = label3.getValue();
			assertNotEquals(label3Value, val3);
			label1Value = val1;
			label3Value = val3;

			//test label 2
			updBtn2.click();
			String val2 = label2.getValue();
			assertNotEquals(label2Value, val2);
			label2Value = val2;

			//test label 3 & label 1
			updBtn3.click();
			val1 = label1.getValue();
			assertNotEquals(label1Value, val1);
			val3 = label3.getValue();
			assertNotEquals(label3Value, val3);
			label1Value = val1;
			label3Value = val3;
		}
	}
}
