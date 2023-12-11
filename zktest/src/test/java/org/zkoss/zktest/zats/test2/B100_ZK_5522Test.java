/* B100_ZK_5522Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 08 15:25:31 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.xel.fn.CommonFns;

public class B100_ZK_5522Test {
	@Test
	public void test() {
		assertEquals("", CommonFns.formatNumber(null, ""));
		assertEquals("", CommonFns.formatNumber(null, "", null));
	}
}
