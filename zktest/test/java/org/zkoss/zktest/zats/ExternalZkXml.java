/* ExternalZkXml.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 10:51:03 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import org.junit.rules.ExternalResource;

import org.zkoss.lang.Library;

/**
 * Load an external zk.xml for testing.
 *
 * @author rudyhuang
 */
public class ExternalZkXml extends ExternalResource {
	private final String configPath;

	/**
	 * Pass a path to zk.xml manually.
	 *
	 * @param configPath a path to zk.xml
	 */
	public ExternalZkXml(String configPath) {
		this.configPath = configPath;
	}

	/**
	 * Pass a test class and will find zk.xml by a convention rule.
	 * <p>
	 * e.g. F95_ZK_1234Test => /test2/F95-ZK-1234-zk.xml
	 *
	 * @param testClass a test class (WebDriverTestCase only)
	 */
	public ExternalZkXml(Class<? extends WebDriverTestCase> testClass) {
		String className = testClass.getName()
				.replace(this.getClass().getPackage().getName(), "")
				.replace('.', '/')
				.replace('_', '-');
		int lastTest = className.lastIndexOf("Test");
		this.configPath = className.substring(0, lastTest) + "-zk.xml";
	}

	@Override
	protected void before() {
		Library.setProperty("org.zkoss.zk.config.path", configPath);
	}

	@Override
	protected void after() {
		Library.setProperty("org.zkoss.zk.config.path", null);
	}
}
