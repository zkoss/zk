/* CombobuttonTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:20:07 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class CombobuttonTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-combobutton-button").eq(0));
		waitResponse();
		verifyA11y();
	}
}