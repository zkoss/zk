/* B85_ZK_3848Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Mar 05 12:43 PM:32 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.ClassLocator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith (PowerMockRunner.class)
@PrepareForTest ({LoggerFactory.class})
public class B85_ZK_3848Test {
	private static final Logger slf4logger = LoggerFactory.getLogger(ClassLocator.class);
	private static Logger logger;
	private static ArrayList<String> results = new ArrayList();
	private static ClassLocator clToTest;
	
	@BeforeClass
	public static void setUp() throws Exception {
		mockStatic(LoggerFactory.class);
		logger = Mockito.mock(Logger.class);
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
		clToTest = new ClassLocator();
	}
	
	@Test
	public void test1() throws Exception {
		
		File src = new File("src/org/zkoss/zktest/test2/ZK3848_lang_addon_Test1.xml");
		File dst = new File("debug/classes/metainfo/zk/lang-addon.xml");
		
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
			Assert.assertThat(ex.getMessage(), containsString("you are extending component decimalbox which is defined in 'zkplus', 'zkbind', please define <depends> element"));
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
		
		File src = new File("src/org/zkoss/zktest/test2/ZK3848_lang_addon_Test2.xml");
		File dst = new File("debug/classes/metainfo/zk/lang-addon.xml");
		
		String name = "metainfo/zk/lang-addon.xml";
		String elName = "addon-name";
		String elDepends = "depends";
		
		Path zkPath = dst.toPath().getParent();
		if (!Files.exists(zkPath))
			Files.createDirectories(zkPath);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				Object[] arguments = invocationOnMock.getArguments();
				String[] args = (String[]) arguments[1];
				slf4logger.warn((String) arguments[0], args);
				String f = ((String) arguments[0]).replace("{}", "%s");
				String result = String.format(f, args[0], args[1], args[2]);
				results.add(result);
				return null;
			}
		}).when(logger).warn(anyString(), any(String[].class));
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
		try {
			clToTest.getDependentXMLResources(name, elName, elDepends);
			verify(logger, times(1)).warn(anyString(), any(String[].class));
			Assert.assertThat(results.get(0), containsString("you are extending component decimalbox which is defined in 'zkplus', 'zkbind', please define <depends> element"));
		} catch (Exception ex) {
			throw ex;
		} finally {
			Files.delete(dst.toPath());
			Path metainfoPath = zkPath.getParent();
			Files.delete(zkPath);
			Files.delete(metainfoPath);
		}
	}
}