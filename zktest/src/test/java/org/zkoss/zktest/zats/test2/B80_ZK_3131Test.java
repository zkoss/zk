/* B80_ZK_3131Test.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 02/15/16, Created by christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author christopher
 */
public class B80_ZK_3131Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		int length = Integer.parseInt(getEval("document.styleSheets.length"));
		for (int i = 0 ; i < length ; i++){
			String sheet = getEval("document.styleSheets[" + i + "].href");
			if (sheet.contains("googleapis")) {
				assertEquals("http://fonts.googleapis.com/css?family=Open+Sans:400,700", stripJsessionid(sheet));
			}
		}
	}
}
