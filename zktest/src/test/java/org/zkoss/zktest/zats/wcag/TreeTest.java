/* TreeTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 23 14:15:01 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author jameschu
 */
@Disabled("Needs aXe fix this issue, https://github.com/dequelabs/axe-core/issues/2720")
public class TreeTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
