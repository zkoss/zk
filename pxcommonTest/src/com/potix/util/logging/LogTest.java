/* LogTest.java

{{IS_NOTE

	Purpose: Test MultiValues
	Description:
	History:
	 2001/4/12, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.logging;

import java.util.Properties;
import java.util.logging.Logger;
import junit.framework.*;

import com.potix.lang.*;

public class LogTest extends TestCase {
	public static final Log log = Log.lookup(LogTest.class);

	public LogTest(String name) throws Exception {
		super(name);
	}
	public static Test suite() {
		return new TestSuite(LogTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testA() throws Exception {
		assertTrue(Log.ERROR == LogService.levelOf("error"));
		assertTrue(Log.WARNING == LogService.levelOf("warning"));
		assertTrue(Log.INFO == LogService.levelOf("INFO"));
		assertTrue(Log.DEBUG == LogService.levelOf("debug"));
		assertTrue(null == LogService.levelOf("DEBUGX"));

		Properties props = new Properties();
		props.setProperty("com.potix.test", "ERROR");
		LogService.init("com.potix").configure(props);
		assertEquals(Log.ERROR, Logger.getLogger("com.potix.test").getLevel());
			//configure use getLogger, so don't use Log.lookup
	}
	public void testB() throws Exception {
		Log.lookup("com.potix.util.logging").setLevel(Log.INFO);
		log.info("test logger: you must see this message");
		log.debug("!!WRONG!! You MUST NOT see this message.");
	}
}
