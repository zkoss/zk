/* B101_ZK_5707Test.java

	Purpose:

	Description:

	History:
		12:48 PM 2024/9/19, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.jul.JDK14LoggerFactory;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
@ForkJVMTestOnly
public class B101_ZK_5707Test extends ZATSTestCase {
	// ZK-6167: the logger is static final, so capture its records through JUL (slf4j-jdk14)
	private static final Logger logger = Logger.getLogger(AbstractComponent.class.getName());
	private static final List<LogRecord> records = new CopyOnWriteArrayList<>();
	private static final Handler handler = new Handler() {
		@Override
		public void publish(LogRecord record) {
			records.add(record);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}
	};

	@BeforeAll
	public static void beforeAll() throws Exception {
		assertInstanceOf(JDK14LoggerFactory.class, LoggerFactory.getILoggerFactory());
		logger.addHandler(handler);
	}

	@AfterAll
	public static void afterAll() {
		logger.removeHandler(handler);
	}

	@Test
	public void test() throws Exception {
		DesktopAgent desktopAgent = connect("/test2/B101-ZK-5707.zul");
		desktopAgent.query("button").click();
		// ZK-5707: AbstractComponent.service warns "No page is available in ..." or "Page ... was destroyed"
		assertTrue(records.stream().map(LogRecord::getMessage)
				.noneMatch(m -> m.startsWith("No page is available in ") || m.startsWith("Page ") && m.contains(" was destroyed")));
	}
}
