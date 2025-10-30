/* F100_ZK_3853_3Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 22 11:56:03 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

public class F100_ZK_3853_3Test extends F100_ZK_3853_1Test {

	@Override
	@Test
	public void test() {
		connect();

		/* Test check a close tag will update all its descendants to checked state */
		clickCm(2); // click (4)
		clickOpen(1, 2); // open (4, 5)
		assertCm(1, 2, 3, 4);

		nextPage();
		clickOpen(0);
		assertCm(1, 0, 1, 2, 3, 4);

		nextPage();
		assertCm(1, 0, 1);

		/* Test un-checked a cm inside a all-selected cm will show partial state */
		prevPage();
		clickCm(3); // un-check (10)
		assertCm(2, 1);
		assertCm(1, 0, 2, 4);
		assertCm(0, 3);

		nextPage();
		assertCm(1, 0, 1);

		prevPage();
		prevPage();
		assertCm(2, 2, 3);
		assertCm(1, 4);
		assertCm(0, 0, 1);

		/* Test another cm shows partial state in its ancestors */
		clickOpen(0); // open (0)
		clickCm(1); // check (1)
		assertCm(2, 0, 4);
		assertCm(1, 1);
		assertCm(0, 2, 3);

		/* Test all select-all cm work correctly */
		clickCm(0, 3, 4); // check (0, 3, 4)
		assertCm(1, 0, 1, 2, 3, 4);

		nextPage();
		assertCm(1, 0, 1, 2, 3, 4);

		nextPage();
		assertCm(1, 0, 1);

		prevPage();
		prevPage();

		/* Test all un-select-all cm work correctly */
		clickCm(0, 3, 4); // un-check (0, 3, 4)
		assertCm(0, 0, 1, 2, 3, 4);

		nextPage();
		assertCm(0, 0, 1, 2, 3, 4);

		nextPage();
		assertCm(0, 0, 1);
	}

	public void nextPage() {
		click(jq(".z-paging-button.z-paging-next"));
		waitResponse();
	}

	public void prevPage() {
		click(jq(".z-paging-button.z-paging-previous"));
		waitResponse();
	}
}
