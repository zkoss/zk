/* B80_ZK_3104Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 28 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class B80_ZK_3104_1Test extends ZATSTestCase {

	@Test
	public void test() throws IOException {
		try {
			DesktopAgent desktop = connect();
			ComponentAgent btn = desktop.query("@button");
			btn.click();
		} catch(ZatsException e) {
			return;
		}
		fail();
	}
}
