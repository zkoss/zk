package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4221Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent da = connect();
		ComponentAgent cb = da.query("checkbox");
		try {
			cb.check(true);
			cb.check(false);
		} catch (Exception e) {
			fail();
		}
	}
}
