/* AudioTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 11 09:50:32 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.Test;

/**
 * @author rudyhuang
 */
public class AudioTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
