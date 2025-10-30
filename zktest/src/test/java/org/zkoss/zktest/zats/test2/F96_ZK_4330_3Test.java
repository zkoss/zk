/* F96_ZK_4330_3Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 28 15:47:08 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import org.zkoss.zktest.zats.ZATSTestCase;

public class F96_ZK_4330_3Test extends ZATSTestCase {
	@Test
	@Timeout(5)
	public void test() {
		connect("/test2/B70-ZK-2589.zul");
	}
}
