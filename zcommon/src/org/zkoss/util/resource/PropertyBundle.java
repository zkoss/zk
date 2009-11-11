/* PropertyBundle.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	90/12/06 20:09:36, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.util.CacheMap;
import org.zkoss.util.Cache;
import org.zkoss.util.Maps;
import org.zkoss.util.logging.Log;

/**
 * The property bundle.
 *
 * <p>It is similar to java.util.ResourceBundle, but they differ as follows.
 *
 * <ul>
 *  <li>It uses {@link Maps#load(Map, InputStream)} to load the properties.
 *  Thus, It is capable to handle UTF-16 and UTF-8 (but not ISO-8859-1).</li>
 *  <li>The locator could be any object as long as it implements
 *  <code>InputStream getResourceAsStream(String)</code>.</li>
 *  <li>It supports only property files.</li>
 *  <li>The getBundle method returns null if the resource not found,
 *  while ResourceBundle throws MissingResourceException.</li>
 * </ul>
 *
 * <p>Instances of PropertyBundle are cached, so the performance is good.
 * However, it implies the property file mapped by the giving
 * class loader, name and locale is immutable. In other words, if you
 * update the content of a property file, it might not be reflected to
 * getString unless it is cleared out of the cache.
 *
 * <p>Thread safe.
 *
 * @author tomyeh
 */
public class PropertyBundle {
	private static final Log log = Log.lookup(PropertyBundle.class);

	/** The cache to hold bundles (Key, PropertyBundle). */
	private static final Cache _cache;
	static {
		_cache = new CacheMap();
		_cache.setMaxSize(100);
	}

	/** The map of properties. */
	private final Map _map;
	/** The locale of the boundle. */
	private final Locale _locale;

	/** The key used to look up the cache. */
	private static class Key {
		private String baseName;
		private Locale locale;
		private Locator locator;
		private boolean caseInsensitive;

		private Key(String baseName, Locale locale, Locator locator,
		boolean caseInsensitive) {
			this.baseName = baseName;
			this.locale = locale;
			this.locator = locator;
			this.caseInsensitive = caseInsensitive;
		}

		//-- Object --//
		public int hashCode() {
			return baseName.hashCode()
				^ (locale != null ? locale.hashCode(): 0) ^ locator.hashCode();
		}
		public boolean equals(Object o) {
			if (!(o instanceof Key))
				return false;
			Key k = (Key)o;
			return k.baseName.equals(baseName)
			&& Objects.equals(k.locale, locale) && k.locator.equals(locator)
			&& k.caseInsensitive == caseInsensitive;
		}
	}
	/**
	 * Gets a resource bundle using the specified
	 * base name, locale, and locator.
	 *
	 * @param locator the locator (never null). See {@link Locators#getDefault}.
	 * @param caseInsensitive whether the key used to access the map
	 * is case-insensitive. If true, all keys are converted to lower cases.
	 * @return the bundle; null if not found
	 */
	public static final PropertyBundle getBundle(
	String baseName, Locale locale, Locator locator, boolean caseInsensitive) {
		if (baseName == null || locator == null)
			throw new IllegalArgumentException();

		//We don't lock the whole method, so it is possible that
		//more than one thread are loading the same property file.
		//However, it is OK since any result is correct.
		//On the other hand, it is more likely that two or more
		//threads are asking different property files at the same
		//time, so we avoid the big lock.

		final Key key = new Key(baseName, locale, locator, caseInsensitive);
		synchronized(_cache) {
			PropertyBundle bundle = (PropertyBundle)_cache.get(key);
			if (bundle != null)
				return bundle;
		}

		final PropertyBundle bundle =
			new PropertyBundle(baseName, locale, locator, caseInsensitive);
		if (bundle._map == null) //failed
			return null;

		synchronized(_cache) {
			_cache.put(key, bundle);
		}
		return bundle;
	}
	/**
	 * Gets a resource bundle using the specified
	 * base name, locale, and locator.
	 */
	public static final PropertyBundle
	getBundle(String baseName, Locale locale, Locator locator) {
		return getBundle(baseName, locale, locator, false);
	}
	/**
	 * Gets a resource bundle using the specified
	 * base name, locale, and the default locator, {@link Locators#getDefault}.
	 *
	 * @param caseInsensitive whether the key used to access the map
	 * is case-insensitive. If true, all keys are converted to lower cases.
	 * @return the bundle; null if not found
	 */
	public static final PropertyBundle
	getBundle(String baseName, Locale locale, boolean caseInsensitive) {
		return getBundle(baseName, locale, Locators.getDefault(), caseInsensitive);
	}
	/**
	 * Gets a resource bundle using the specified
	 * base name, locale, and the default locator, {@link Locators#getDefault}.
	 */
	public static final PropertyBundle
	getBundle(String baseName, Locale locale) {
		return getBundle(baseName, locale, false);
	}

	/**
	 * Constructor.
	 *
	 * @param caseInsensitive whether the key used to access the map
	 * is case-insensitive. If true, all keys are converted to lower cases.
	 */
	protected PropertyBundle(String baseName, Locale locale, Locator locator,
	boolean caseInsensitive) {
		try {
			final Locators.StreamLocation loc =
			Locators.locateAsStream(baseName + ".properties", locale, locator);
			if (loc != null) {
				_map = new HashMap(32);
				Maps.load(_map, loc.stream, caseInsensitive);
				_locale = loc.locale;
			} else {
				_map = null; //we use _map to denote failure
				_locale = null;
			}
		}catch(RuntimeException ex) {
			throw ex;
		}catch(Exception ex) {
			throw SystemException.Aide.wrap(ex, "Unable to load " + baseName + ".properties");
		}
	}

	/** Returns the property for the given key from this resource bundle
	 * or one of its parents.
	 */
	public final String getProperty(String key) {
		//It is OK not to sync because _map is immutable
		return (String)_map.get(key);
	}
	/** Returns a map of all properties, (String key , String value).
	 */
	public final Map getProperties() {
		return _map;
	}
	/** Returns the locale of the bundle, or null if it is the default.
	 * Note: it is value might not be the same as the locale being passed
	 * to the constructor, because the contructor will do some fallback.
	 */
	public final Locale getLocale() {
		return _locale;
	}
}
