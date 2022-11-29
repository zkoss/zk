package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.init;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class DestroyTest extends WebDriverTestCase {
	@Test
	public void testDestroy() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		connect("/mvvm/book/viewmodel/init/destroy.zul");
		sleep(1000);
		connect("/mvvm/book/viewmodel/init/destroy.zul");
		assertEquals("InitVM.cleanup was called\n", outContent.toString());
	}

	@Test
	public void testDestroyChildOverrideSuper() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		connect("/mvvm/book/viewmodel/init/destroy-child-override-super.zul");
		sleep(1000);
		connect("/mvvm/book/viewmodel/init/destroy-child-override-super.zul");
		assertEquals("ChildInitOverrideVM.cleanup was called twice\nChildInitOverrideVM.cleanup was called twice\n", outContent.toString());
	}

	@Test
	public void testDestroyChildWithoutSuper() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		connect("/mvvm/book/viewmodel/init/destroy-child-without-super.zul");
		sleep(1000);
		connect("/mvvm/book/viewmodel/init/destroy-child-without-super.zul");
		assertEquals("ChildInitNoSuperVM.childDestroy was called\n", outContent.toString());
	}

	@Test
	public void testDestroyChildWithSuper() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		connect("/mvvm/book/viewmodel/init/destroy-child-with-super.zul");
		sleep(1000);
		connect("/mvvm/book/viewmodel/init/destroy-child-with-super.zul");
		assertEquals("ChildInitSuperVM.childDestroy was called\nInitVM.cleanup was called\n", outContent.toString());
	}

	@Test
	public void testDestroyChildWithSuperClass() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		connect("/mvvm/book/viewmodel/init/destroy-child-with-super-class.zul");
		sleep(1000);
		connect("/mvvm/book/viewmodel/init/destroy-child-with-super-class.zul");
		assertEquals("InitVM.cleanup was called\n", outContent.toString());
	}

	@Test
	public void testDestroyChildWithSuperNotExist() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		connect("/mvvm/book/viewmodel/init/destroy-child-with-super-notexist.zul");
		sleep(1000);
		connect("/mvvm/book/viewmodel/init/destroy-child-with-super-notexist.zul");
		assertEquals("ChildInitSuperNotExistVM.childDestroy was called\n", outContent.toString());
	}


	@Test
	public void testMultipleDestroy() {
		connect("/mvvm/book/viewmodel/init/multiple-destroy.zul");
		final Logger logger = Logger.getLogger("org.zkoss.zk.ui.impl.DesktopImpl");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		Handler testHandler = new StreamHandler(new PrintStream(outContent), new SimpleFormatter());
		try {
			logger.addHandler(testHandler);
			connect("/mvvm/book/viewmodel/init/multiple-destroy.zul");
			testHandler.flush();
			MatcherAssert.assertThat(outContent.toString(), containsString("org.zkoss.zk.ui.UiException: more than one [@Destroy] in the class"));
		} finally {
			logger.removeHandler(testHandler);
		}
	}
}
