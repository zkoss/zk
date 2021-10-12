package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B01167ConverterIdxOutOfBoundTest extends ZATSTestCase {
	@Test
	public void test() {
		try {
			connect();
		} catch(ZatsException e) {
			assertTrue(e.getCause().toString().contains("UiException: Cannot find converter:something"));
		}
	}
}
