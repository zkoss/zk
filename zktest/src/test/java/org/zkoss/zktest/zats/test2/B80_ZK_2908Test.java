/* B80_ZK_2908Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Oct 21, 2015 11:30:58 AM, Created by JamesChu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_2908Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktopAgent = connect();
		} catch(ZatsException e) {
			fail();
		}
	}
}