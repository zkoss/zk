/* B96_ZK_4832Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 8 10:50:21 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_4832Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery label1 = jq("$uid1");
		JQuery label2 = jq("$uid2");
		JQuery label3 = jq("$uid3");
		JQuery updBtn1 = jq("$updBtn1");
		JQuery updBtn2 = jq("$updBtn2");
		JQuery updBtn3 = jq("$updBtn3");
		String label1Value = label1.text();
		String label2Value = label2.text();
		String label3Value = label3.text();
		for (int i = 0; i < 5; i++) {
			//test label 1 & label 3
			click(updBtn1);
			waitResponse();
			String val1 = label1.text();
			assertNotEquals(label1Value, val1);
			String val3 = label3.text();
			assertNotEquals(label3Value, val3);
			label1Value = val1;
			label3Value = val3;

			//test label 2
			click(updBtn2);
			waitResponse();
			String val2 = label2.text();
			assertNotEquals(label2Value, val2);
			label2Value = val2;

			//test label 3 & label 1
			click(updBtn3);
			waitResponse();
			val1 = label1.text();
			assertNotEquals(label1Value, val1);
			val3 = label3.text();
			assertNotEquals(label3Value, val3);
			label1Value = val1;
			label3Value = val3;
		}
	}
}
