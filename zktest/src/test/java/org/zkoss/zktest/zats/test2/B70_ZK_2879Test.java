/* B70_ZK_2879Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 21 10:23:22 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2879Test extends ZATSTestCase {
	@Test(expected = ZatsException.class)
	public void test() {
		connect();
	}
}
