package org.zkoss.zktest.zats.test2;

import static junit.framework.TestCase.fail;

import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4306Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent d = connect();
		try {
			d.query("button").click();
		} catch (Exception e) {
			fail();
		}
	}
}
