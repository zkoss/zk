/* BandboxTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Jun 23 17:50:52 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class BandboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@bandbox > .z-bandbox-button"));
		waitResponse();
		verifyA11y();
	}
}