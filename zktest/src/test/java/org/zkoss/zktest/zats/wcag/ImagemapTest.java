/* ImagemapTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Jul 06 17:24:18 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Test;

public class ImagemapTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();
	}
}