/* F102_ZK_5600Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 15 18:32:35 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.impl.UiEngineImpl;

public class F102_ZK_5600Test extends WebDriverTestCase {
	@Test
	public void test1() throws Exception {
		Logger logger = Logger.getLogger(UiEngineImpl.class.getName());
		List<LogRecord> records = new CopyOnWriteArrayList<>();
		Handler handler = capture(records);
		logger.addHandler(handler);
		try {
			String path = "/test2/F102-ZK-5600-1.zul";
			connect(path);

			click(jq("@button").eq(0));
			waitResponse();
			assertEquals(1, countErrorsAt(records, path));
			click(jq(".z-window-close"));
			waitResponse();
			click(jq("@button").eq(1));
			waitResponse();
			assertEquals(2, countErrorsAt(records, path));
		} finally {
			logger.removeHandler(handler);
		}
	}

	@Test
	public void test2() throws Exception {
		Logger logger = Logger.getLogger(DHtmlLayoutServlet.class.getName());
		List<LogRecord> records = new CopyOnWriteArrayList<>();
		Handler handler = capture(records);
		logger.addHandler(handler);
		try {
			String path = "/test2/F102-ZK-5600-2.zul";
			final String address = getAddress();
			assertEquals(500, getStatusCode(address + path));
			assertEquals(1, countErrorsAt(records, path));
		} finally {
			logger.removeHandler(handler);
		}
	}

	// ZK-6167: the logger is static final, so capture its records through JUL (slf4j-jdk14)
	private static Handler capture(List<LogRecord> records) {
		return new Handler() {
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
	}

	/** Counts the {@code log.error("at [{}]", path, exception)} records. */
	private static long countErrorsAt(List<LogRecord> records, String path) {
		return records.stream().filter(r -> r.getLevel() == Level.SEVERE && ("at [" + path + "]").equals(r.getMessage())
				&& r.getThrown() instanceof Exception).count();
	}
}
