/* B85_ZK_3935Test.java

	Purpose:
		
	Description:
		
	History:
		Fri May 25 17:42:18 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.slf4j.Logger;

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
		Logger logger = mock(Logger.class);
		setFinalStatic(DesktopImpl.class.getDeclaredField("log"), logger);

		DesktopAgent desktop = connect();
		Threads.sleep(1500);
		desktop.query("button").click();

		verify(logger, never()).error(anyString(), any(Throwable.class));
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
