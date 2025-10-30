/* ComboboxTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:01:48 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class ComboboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-combobox-button").eq(0));
		waitResponse();
		verifyA11y();
	}
}