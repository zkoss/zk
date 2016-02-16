/* B80_ZK_3131Test.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 02/15/16, Created by christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author christopher
 */
public class B80_ZK_3131Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Integer length = Integer.parseInt(getEval("document.styleSheets.length"));
		for (int i = 0 ; i < length ; i++){
			String sheet = getEval("document.styleSheets[" + i + "].href");
			if (sheet.contains("googleapis")) {
				assertTrue("was expecting //fonts.googleapis.com/css?family=Open+Sans:400,700, got: " + sheet, "//fonts.googleapis.com/css?family=Open+Sans:400,700".equals(sheet));
			}
		}
	}
}
