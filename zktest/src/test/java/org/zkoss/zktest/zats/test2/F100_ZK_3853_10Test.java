/* F100_ZK_3853_10Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 16 18:18:39 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

public class F100_ZK_3853_10Test extends F100_ZK_3853_1Test{
	@Test
	public void test() {
		connect();

		switchToModel1();
		clickOpen(0, 1, 2, 3); // open (0, 4, 5, 8)
		clickCm(5); // check (5)
		assertModel1();


		switchToModel2();
		clickOpen(0, 1, 2, 3); // open (0, 4, 5, 8)
		clickCm(10); // check (10)
		assertModel2();

		switchToModel1();
		assertModel1();

		switchToModel2();
		assertModel2();
	}

	public void switchToModel1() {
		click(jq("@button:eq(0)"));
		waitResponse();
	}

	public void switchToModel2() {
		click(jq("@button:eq(1)"));
		waitResponse();
	}

	public void assertModel1() {
		assertHdcm(2);
		assertCm(0, 0, 1, 2, 3, 12, 13);
		assertCm(1, 5, 6, 7, 8, 9, 10, 11);
		assertCm(2, 4);
	}

	public void assertModel2() {
		assertHdcm(2);
		assertCm(0, 0, 1, 2, 3, 6, 7, 9, 11, 12, 13);
		assertCm(1, 10);
		assertCm(2, 4, 5, 8);
	}
}
