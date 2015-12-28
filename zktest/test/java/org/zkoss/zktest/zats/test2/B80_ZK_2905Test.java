/* B80_ZK_2905Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 28 11:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class B80_ZK_2905Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
