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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.zkoss.util.resource.ClassLocator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith (PowerMockRunner.class)
//@PrepareForTest ({LoggerFactory.class, Logger.class, ClassLocator.class})
@PrepareForTest ({LoggerFactory.class})
public class B85_ZK_3848Test {
	private Logger logger;
	private static ArrayList<String> results = new ArrayList();;
	private int calledNumber = 0;

	private ClassLocator clToTest;

	@Before
	public void setUp() throws Exception {
		mockStatic(LoggerFactory.class);
		logger = Mockito.mock(MyLogger.class);
		//logger = Mockito.mock(Logger.class); // use Mockito.mock(Logger.class also works)
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
		doCallRealMethod().when(logger).warn(anyString());
		
		clToTest = new ClassLocator();
	}

	@Test
	public void checkClientCompDenpendency() throws IOException {
		
		File p = new File(".");
		File src = new File(p, "/src/org/zkoss/zktest/test2/ZK3848_lang_addon.xml");
		File dst = new File(p, "/debug/classes/metainfo/zk/lang-addon.xml");
		
		String name = "metainfo/zk/lang-addon.xml";
		String elName = "addon-name";
		String elDepends = "depends";
		
		Path zkPath = dst.toPath().getParent();
		if (!Files.exists(zkPath))
			Files.createDirectories(zkPath);
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
		try {
			clToTest.getDependentXMLResources(name, elName, elDepends);
			verify(logger, times(2)).warn(anyString());
		} catch (Exception ex) {
			Assert.fail();
		} finally {
			Files.delete(dst.toPath());
			Path metainfoPath = zkPath.getParent();
			Files.delete(zkPath);
			Files.delete(metainfoPath);
		}
		System.out.println(results.size());
	}

	public class MyLogger implements Logger {
		
		@Override
		public String getName() {
			return "MyLogger";
		}
		
		@Override
		public boolean isTraceEnabled() {
			return true;
		}
		
		@Override
		public void trace(String s) {
		
		}
		
		@Override
		public void trace(String s, Object o) {
		
		}
		
		@Override
		public void trace(String s, Object o, Object o1) {
		
		}
		
		@Override
		public void trace(String s, Object[] objects) {
		
		}
		
		@Override
		public void trace(String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isTraceEnabled(Marker marker) {
			return true;
		}
		
		@Override
		public void trace(Marker marker, String s) {
		
		}
		
		@Override
		public void trace(Marker marker, String s, Object o) {
		
		}
		
		@Override
		public void trace(Marker marker, String s, Object o, Object o1) {
		
		}
		
		@Override
		public void trace(Marker marker, String s, Object[] objects) {
		
		}
		
		@Override
		public void trace(Marker marker, String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isDebugEnabled() {
			return true;
		}
		
		@Override
		public void debug(String s) {
		
		}
		
		@Override
		public void debug(String s, Object o) {
		
		}
		
		@Override
		public void debug(String s, Object o, Object o1) {
		
		}
		
		@Override
		public void debug(String s, Object[] objects) {
		
		}
		
		@Override
		public void debug(String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isDebugEnabled(Marker marker) {
			return true;
		}
		
		@Override
		public void debug(Marker marker, String s) {
		
		}
		
		@Override
		public void debug(Marker marker, String s, Object o) {
		
		}
		
		@Override
		public void debug(Marker marker, String s, Object o, Object o1) {
		
		}
		
		@Override
		public void debug(Marker marker, String s, Object[] objects) {
		
		}
		
		@Override
		public void debug(Marker marker, String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isInfoEnabled() {
			return true;
		}
		
		@Override
		public void info(String s) {
		
		}
		
		@Override
		public void info(String s, Object o) {
		
		}
		
		@Override
		public void info(String s, Object o, Object o1) {
		
		}
		
		@Override
		public void info(String s, Object[] objects) {
		
		}
		
		@Override
		public void info(String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isInfoEnabled(Marker marker) {
			return true;
		}
		
		@Override
		public void info(Marker marker, String s) {
		
		}
		
		@Override
		public void info(Marker marker, String s, Object o) {
		
		}
		
		@Override
		public void info(Marker marker, String s, Object o, Object o1) {
		
		}
		
		@Override
		public void info(Marker marker, String s, Object[] objects) {
		
		}
		
		@Override
		public void info(Marker marker, String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isWarnEnabled() {
			return true;
		}
		
		@Override
		public void warn(String s) {
			System.out.println(s);
			B85_ZK_3848Test temp = B85_ZK_3848Test.this;
			B85_ZK_3848Test.this.results.add(s);
		}
		
		@Override
		public void warn(String s, Object o) {
			System.out.println(s);
			B85_ZK_3848Test.this.results.add(s);
		}
		
		@Override
		public void warn(String s, Object[] objects) {
			System.out.println(s);
			B85_ZK_3848Test.this.results.add(s);
		}
		
		@Override
		public void warn(String s, Object o, Object o1) {
			System.out.println(s);
			B85_ZK_3848Test.this.results.add(s);
		}
		
		@Override
		public void warn(String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isWarnEnabled(Marker marker) {
			return true;
		}
		
		@Override
		public void warn(Marker marker, String s) {
		
		}
		
		@Override
		public void warn(Marker marker, String s, Object o) {
		
		}
		
		@Override
		public void warn(Marker marker, String s, Object o, Object o1) {
		
		}
		
		@Override
		public void warn(Marker marker, String s, Object[] objects) {
		
		}
		
		@Override
		public void warn(Marker marker, String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isErrorEnabled() {
			return true;
		}
		
		@Override
		public void error(String s) {
		
		}
		
		@Override
		public void error(String s, Object o) {
		
		}
		
		@Override
		public void error(String s, Object o, Object o1) {
		
		}
		
		@Override
		public void error(String s, Object[] objects) {
		
		}
		
		@Override
		public void error(String s, Throwable throwable) {
		
		}
		
		@Override
		public boolean isErrorEnabled(Marker marker) {
			return true;
		}
		
		@Override
		public void error(Marker marker, String s) {
		
		}
		
		@Override
		public void error(Marker marker, String s, Object o) {
		
		}
		
		@Override
		public void error(Marker marker, String s, Object o, Object o1) {
		
		}
		
		@Override
		public void error(Marker marker, String s, Object[] objects) {
		
		}
		
		@Override
		public void error(Marker marker, String s, Throwable throwable) {
		
		}
	}
}