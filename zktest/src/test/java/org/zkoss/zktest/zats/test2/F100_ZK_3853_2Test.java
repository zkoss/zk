/* F100_ZK_3853_2Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 02 18:38:34 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

public class F100_ZK_3853_2Test extends F100_ZK_3853_1Test {

	@Test
	public void test() {
		super.test();

		clickCm(2, 9);

		test_();

		/* unbind model */
		click(jq("@button"));
		waitResponse();

		/* when model is unbinded, header cm should be empty */
		assertHdcm(0);

		/* rebind model */
		click(jq("@button"));
		waitResponse();

		test_();
	}

	private void test_() {
		assertHdcm(2);
		assertCm(2, 0, 4, 5, 8);
		assertCm(1, 9);
		assertCm(0, 1, 3, 6, 7, 10, 11, 12, 13);
	}
}
