/* ChosenboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 23 14:15:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
public class ChosenboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
		click(jq("input"));
		waitResponse();
		verifyA11y();
	}
}
