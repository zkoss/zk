/* B85_ZK_3848Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Mar 05 12:43 PM:32 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.util.resource.ClassLocator;

@ForkJVMTestOnly
public class B85_ZK_3848Test {
	private static Logger logger;
	private static ClassLocator clToTest;

	private static MockedStatic<LoggerFactory> utilities;

	@BeforeAll
	public static void setUp() {
		utilities = Mockito.mockStatic(LoggerFactory.class);
		logger = Mockito.mock(Logger.class);
		utilities.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
		clToTest = new ClassLocator();
	}
	@AfterAll
	public static void tearDown() {
		utilities.close();
	}
	
	@Test
	public void test1() throws Exception {
		File src = new File("src/test/java/org/zkoss/zktest/zats/test2/ZK3848_lang_addon_Test1.xml");
		File dst = new File("build/resources/main/metainfo/zk/lang-addon.xml");

		String name = "metainfo/zk/lang-addon.xml";
		String elName = "addon-name";
		String elDepends = "depends";

		Path zkPath = dst.toPath().getParent();
		if (!Files.exists(zkPath))
			Files.createDirectories(zkPath);
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
		boolean throwError = false;
		try {
			clToTest.getDependentXMLResources(name, elName, elDepends);
		} catch (Exception ex) {
			assertThat(ex.getMessage(), containsString("you are extending component decimalbox which is defined in 'zkbind', please define <depends> element"));
			throwError = true;
		} finally {
			Files.delete(dst.toPath());
			Path metainfoPath = zkPath.getParent();
			Files.delete(zkPath);
			Files.delete(metainfoPath);
		}
		if (!throwError) {
			throw new Exception("An exception is expected");
		}
	}

	@Test
	public void test2() throws IOException {
		File src = new File(
				"src/test/java/org/zkoss/zktest/zats/test2/ZK3848_lang_addon_Test2.xml");
		File dst = new File(
				"build/resources/main/metainfo/zk/lang-addon.xml");

		String name = "metainfo/zk/lang-addon.xml";
		String elName = "addon-name";
		String elDepends = "depends";

		Path zkPath = dst.toPath().getParent();
		if (!Files.exists(zkPath))
			Files.createDirectories(zkPath);

		List<String> results = new ArrayList<>();
		doAnswer(invocationOnMock -> {
			Object[] args = invocationOnMock.getArguments();
			String f = ((String) args[0]).replace("{}", "%s");
			String result = String.format(f, args[1], args[2], args[3]);
			results.add(result);
			return null;
		}).when(logger).warn(anyString(), anyString(), anyString(), anyString());
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
		try {
			clToTest.getDependentXMLResources(name, elName, elDepends);
			verify(logger, times(1)).warn(anyString(), anyString(),
					anyString(), anyString());
			assertThat(results.get(0), containsString(
					"you are extending component decimalbox which is defined in 'zkbind', please define <depends> element"));
		} finally {
			Files.delete(dst.toPath());
			Path metainfoPath = zkPath.getParent();
			Files.delete(zkPath);
			Files.delete(metainfoPath);
		}
	}
}