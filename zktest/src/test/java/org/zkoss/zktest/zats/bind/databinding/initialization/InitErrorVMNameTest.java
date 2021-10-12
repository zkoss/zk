/* InitErrorVMNameTest.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.initialization;

import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class InitErrorVMNameTest extends ZATSTestCase {
	@Test(expected = ZatsException.class)
	public void test() {
		connect();
	}
}
