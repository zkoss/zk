package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
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
