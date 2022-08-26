/* DestroyTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 10 16:43:44 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.init;

import static org.hamcrest.Matchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class DestroyTest extends ZATSTestCase {
	@Test
	public void testDestroy() {
		connect("/bind/viewmodel/init/destroy.zul");
		Assertions.assertEquals("InitVM.cleanup was called\n", captureStdOut());
	}

	private String captureStdOut() {
		PrintStream stdout = System.out;
		try {
			ByteArrayOutputStream outContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));
			env.cleanup();
			return outContent.toString();
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	public void testDestroyChildOverrideSuper() {
		connect("/bind/viewmodel/init/destroy-child-override-super.zul");
		Assertions.assertEquals("ChildInitOverrideVM.cleanup was called twice\n" +
				"ChildInitOverrideVM.cleanup was called twice\n", captureStdOut());
	}

	@Test
	public void testDestroyChildWithoutSuper() {
		connect("/bind/viewmodel/init/destroy-child-without-super.zul");
		Assertions.assertEquals("ChildInitNoSuperVM.childDestroy was called\n", captureStdOut());
	}

	@Test
	public void testDestroyChildWithSuper() {
		connect("/bind/viewmodel/init/destroy-child-with-super.zul");
		Assertions.assertEquals("ChildInitSuperVM.childDestroy was called\n" +
				"InitVM.cleanup was called\n", captureStdOut());
	}

	@Test
	public void testDestroyChildWithSuperClass() {
		connect("/bind/viewmodel/init/destroy-child-with-super-class.zul");
		Assertions.assertEquals("InitVM.cleanup was called\n", captureStdOut());
	}

	@Test
	public void testDestroyChildWithSuperNotExist() {
		connect("/bind/viewmodel/init/destroy-child-with-super-notexist.zul");
		Assertions.assertEquals("ChildInitSuperNotExistVM.childDestroy was called\n", captureStdOut());
	}

	@Test
	public void testMultipleDestroy() {
		connect("/bind/viewmodel/init/multiple-destroy.zul");

		final Logger logger = Logger.getLogger("org.zkoss.zk.ui.impl.DesktopImpl");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		Handler testHandler = new StreamHandler(new PrintStream(outContent), new SimpleFormatter());
		try {
			logger.addHandler(testHandler);
			env.cleanup();
			testHandler.flush();
			MatcherAssert.assertThat(outContent.toString(),
					containsString("org.zkoss.zk.ui.UiException: more than one [@Destroy] in the class"));
		} finally {
			logger.removeHandler(testHandler);
		}
	}
}
