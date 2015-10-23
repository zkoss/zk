/* B80_ZK_2908Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Oct 21, 2015 11:30:58 AM, Created by JamesChu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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