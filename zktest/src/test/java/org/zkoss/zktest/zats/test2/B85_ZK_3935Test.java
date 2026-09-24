/* B85_ZK_3935Test.java

	Purpose:
		
	Description:
		
	History:
		Fri May 25 17:42:18 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.jul.JDK14LoggerFactory;

import org.zkoss.lang.Threads;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.impl.DesktopImpl;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3935Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		// ZK-6167: the logger is static final, so capture its records through JUL (slf4j-jdk14)
		assertInstanceOf(JDK14LoggerFactory.class, LoggerFactory.getILoggerFactory());
		Logger logger = Logger.getLogger(DesktopImpl.class.getName());
		List<LogRecord> records = new CopyOnWriteArrayList<>();
		Handler handler = new Handler() {
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
		logger.addHandler(handler);
		try {
			DesktopAgent desktop = connect();
			Threads.sleep(1500);
			desktop.query("button").click();

			assertTrue(records.stream().noneMatch(r -> r.getLevel() == Level.SEVERE && r.getThrown() != null));
		} finally {
			logger.removeHandler(handler);
		}
	}
}
