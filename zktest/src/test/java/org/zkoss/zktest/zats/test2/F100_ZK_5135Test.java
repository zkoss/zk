/* F100_ZK_5135Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 15 17:01:07 CST 2024, Created by rebeccalai

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.impl.DesktopImpl;

@ForkJVMTestOnly
public class F100_ZK_5135Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F100-ZK-5135-zk.xml");

	@Test
	public void test() throws Exception {
		// ZK-6167: the logger is static final, so capture its records through JUL (slf4j-jdk14)
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
			connect();
			waitResponse();
			assertTrue(jq(".z-error").text().contains("custom error message"));
			// check browser log
			driver.manage().logs().get(LogType.BROWSER).getAll().stream().findFirst().ifPresent(log ->
					assertTrue(log.getMessage().contains("SimpleConstraint._init")));
			// check server log
			assertTrue(records.stream().anyMatch(r -> r.getLevel() == Level.SEVERE
					&& r.getMessage().contains("F100-ZK-5135.zul") && r.getMessage().contains("SimpleConstraint._init")));
		} finally {
			logger.removeHandler(handler);
		}
	}
}
