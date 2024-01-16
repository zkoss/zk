/* F100_ZK_5135Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 15 17:01:07 CST 2024, Created by rebeccalai

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;

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
		Logger logger = mock(Logger.class);
		setFinalStatic(DesktopImpl.class.getDeclaredField("log"), logger);
		connect();
		waitResponse();
		assertTrue(jq(".z-error").text().contains("custom error message"));
		// check browser log
		driver.manage().logs().get(LogType.BROWSER).getAll().stream().findFirst().ifPresent(log ->
				assertTrue(log.getMessage().contains("SimpleConstraint._init")));
		// check server log
		verify(logger, atLeastOnce()).error(anyString(), contains("F100-ZK-5135.zul"),
				contains("SimpleConstraint._init"));
	}

	// https://stackoverflow.com/a/30703932
	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, newValue);
	}
}
