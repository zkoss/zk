/* SearchboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 23 14:15:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author jameschu
 */
public class SearchboxTest extends WcagTestCase {
	@Test
	public void test() {
		//TODO: focusable region issue
		connect();
		verifyA11y();
		click(jq("@searchbox"));
		waitResponse();
		verifyA11y();
	}
}
