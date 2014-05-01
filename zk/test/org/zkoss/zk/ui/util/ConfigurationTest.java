/*
 * ConfigurationTest.java
 * 
 * Purpose:
 * 
 * Description:
 * 
 * Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 * 
 * {{IS_RIGHT This program is distributed under LGPL Version 2.1 in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY. }}IS_RIGHT
 */
package org.zkoss.zk.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Unit tests for the {@link Configuration} class.
 */
public final class ConfigurationTest {

	/**
	 * The class to test.
	 */
	private Configuration configuration;

	/**
	 * WebApp mock.
	 */
	private WebApp webAppMock = createMock(WebApp.class);

	/**
	 * Prepares an environment for unit tests.
	 */
	@Before
	public void setUp() {
		configuration = new Configuration();
		configuration.setWebApp(webAppMock);
	}

	/**
	 * The richlet name is required.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void removeRichletWithNullName() {
		configuration.removeRichlet(null);
	}

	/**
	 * Tries to remove a richlet which is not registered.
	 */
	@Test
	public void removeNotExistedRichletTest() {
		String richletName = "richlet";

		Object o = configuration.removeRichlet(richletName);
		assertNull(o);
	}

	/**
	 * Adds a new richlet and then removes it.
	 */
	@Test
	public void addAndRemoveRichletTest() {
		Richlet richletMock = createMock(Richlet.class);
		String richletName = "richlet";

		// expect destroying richlet
		richletMock.destroy();

		replayAll();

		// add richlet
		configuration.addRichlet(richletName, richletMock);

		// remove richlet
		Object richletClass = configuration.removeRichlet(richletName);

		verifyAll();

		// expect richlet class
		assertEquals(richletMock.getClass(), richletClass);
	}

	/**
	 * Adds two richlets with the same name. The first richlet has to be destroyed.
	 */
	@Test
	public void addTwoRichletWithSameNameTest() {
		Richlet richlet1Mock = createMock(Richlet.class);
		Richlet richlet2Mock = createMock(Richlet.class);
		String richletName = "richletName";

		// expect destroying first richlet
		richlet1Mock.destroy();

		replayAll();

		// add first richlet
		assertNull(configuration.addRichlet(richletName, richlet1Mock));

		// add second richlet and remove first richlet
		Object firstRichletClass = configuration.addRichlet(richletName, richlet2Mock);

		verifyAll();

		// returned object must be first richlet class
		assertEquals(richlet1Mock.getClass(), firstRichletClass);
	}

	/**
	 * Adds a richlet definition by providing richlet class, and then 
	 * gets its instance.
	 */
	@Test
	public void addRichletClassAndGetItTest() throws Exception {
		// expect creating a new instance
		String richletName = "richletName";

		replayAll();

		// add richlet definition
		assertNull(configuration.addRichlet(richletName, SimpleRichlet.class, null));

		// get richlet instance
		Object richlet = configuration.getRichlet(richletName);

		verifyAll();

		assertEquals(SimpleRichlet.class, richlet.getClass());
	}

	/**
	 * Adds a richlet definition by valid providing class name, and then 
	 * gets its instance.
	 */
	@Test
	public void addRichletClassNameAndGetItTest() throws Exception {
		String richletClassName = SimpleRichlet.class.getCanonicalName();
		String richletName = "richletName";

		replayAll();

		// add richlet definition
		assertNull(configuration.addRichlet(richletName, richletClassName, null));

		// get richlet instance
		Object richlet = configuration.getRichlet(richletName);

		verifyAll();

		assertEquals(SimpleRichlet.class, richlet.getClass());
	}
	
	/**
	 * Registers an invalid richlet class name.
	 * 
	 * <p>{@link Configuration#getRichlet(String)} must throw {@link UiException} 
	 * because it cannot create an instance of the invalid class name.
	 */
	@Test(expected = UiException.class)
	public void addInvalidRichletClassNameAndTryToGetItTest() {
		String richletName = "richletName";
		String invalidRichletClassName = "richlet";
		
		replayAll();
		
		// add richlet definition
		assertNull(configuration.addRichlet(richletName, invalidRichletClassName, null));
		
		try {
			// get richlet instance and expect UiException
			configuration.getRichlet(richletName);
			fail("UiException must be thrown");
		} catch (UiException e) {
			// exception is expected because of invalid richlet class name
			verifyAll();
			throw e;
		}
	}
}
