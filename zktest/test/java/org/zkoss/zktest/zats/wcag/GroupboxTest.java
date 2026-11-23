/* GroupboxTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:23:25 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

public class GroupboxTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}