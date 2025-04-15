/* F102_ZK_5600Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 15 18:32:35 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.impl.UiEngineImpl;

public class F102_ZK_5600Test extends WebDriverTestCase {
	@Test
	public void test1() throws Exception {
		Logger logger = mock(Logger.class);
		setFinalStatic(UiEngineImpl.class.getDeclaredField("log"), logger);
		String path = "/test2/F102-ZK-5600-1.zul";
		connect(path);

		click(jq("@button").eq(0));
		waitResponse();
		verify(logger, times(1)).error(eq("at [{}]"), eq(path), any(Exception.class));
		click(jq(".z-window-close"));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		verify(logger, times(2)).error(eq("at [{}]"), eq(path), any(Exception.class));
	}

	@Test
	public void test2() throws Exception {
		Logger logger = mock(Logger.class);
		setFinalStatic(DHtmlLayoutServlet.class.getDeclaredField("log"), logger);
		String path = "/test2/F102-ZK-5600-2.zul";
		final String address = getAddress();
		assertEquals(500, getStatusCode(address + path));
		verify(logger, times(1)).error(eq("at [{}]"), eq(path), any(Exception.class));
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
