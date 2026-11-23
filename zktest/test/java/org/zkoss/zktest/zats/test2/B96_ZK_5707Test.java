/* B96_ZK_5707Test.java

	Purpose:

	Description:

	History:
		12:48 PM 2024/9/19, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
@Category(ForkJVMTestOnly.class)
public class B96_ZK_5707Test extends ZATSTestCase {
	private static Logger logger;

	@BeforeClass
	public static void beforeAll() throws Exception {
		logger = mock(Logger.class);
		setFinalStatic(AbstractComponent.class.getDeclaredField("log"), logger);
	}

	@Test
	public void test() throws Exception {
		DesktopAgent desktopAgent = connect("/test2/B96-ZK-5707.zul");
		desktopAgent.query("button").click();
		verify(logger, never()).warn(any());
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
