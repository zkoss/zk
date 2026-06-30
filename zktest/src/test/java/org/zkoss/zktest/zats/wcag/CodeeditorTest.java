/* CodeeditorTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:05:39 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class CodeeditorTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}
