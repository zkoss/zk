/* Apps.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 30 21:07:29     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.prefs;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Locale;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import com.potix.lang.Classes;
import com.potix.lang.Strings;
import com.potix.lang.SystemException;
import com.potix.util.Maps;
import com.potix.io.Files;
import com.potix.util.prefs.impl.PreferenceManager;
import com.potix.util.logging.Log;
import com.potix.comp.ComponentManager;

/**
 * Utilities to access the application properties.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.11 $ $Date: 2006/05/29 04:27:24 $
 */
public class Apps {
	//private static final Log log = Log.lookup(Apps.class);

	private static final Properties _props = new Properties();
	/** The preference manager used for this app. */
	private static PreferenceManager _prefman = newPrefManager();

	protected Apps() {}

	/** Returns the application.
	 */
	public static App getApp() {
		return (App)ComponentManager.the().get(App.class.getName());
	}

	/** Returns the current locale; never null.
	 * This is the locale that every other objects shall use,
	 * unless they have special consideration.
	 *
	 * <p>Default: If {@link #setThreadLocale} was called with non-null,
	 * the value is returned. Otherwise, Locale.getDefault() is returned,
	 * unless {@link #setPreferenceManager} is called to override its meaning.
	 */
	public static final Locale getCurrentLocale() {
		return _prefman.getCurrentLocale();
	}
	/** Returns the current time zone; never null.
	 * This is the time zone that every other objects shall use,
	 * unless they have special consideration.
	 *
	 * <p>Default: TimeZone.getDefault() is returned, unless {@link #setPreferenceManager}
	 * is called to override its meaning.
	 */
	public static final TimeZone getCurrentTimeZone() {
		return _prefman.getCurrentTimeZone();
	}

	/** Returns the current doamin; never null.
	 *
	 * <p>Default: "localdomain" is returned, unless {@link #setPreferenceManager}
	 * is called to override its meaning.
	 */
	public static final String getCurrentDomain() {
		return _prefman.getCurrentDomain();
	}
	/** Returns the current user name, or null if no one logins
	 * the current execution.
	 *
	 * <p>Default: null is returned, unless {@link #setPreferenceManager}
	 * is called to override its meaning.
	 */
	public static final String getCurrentUsername() {
		return _prefman.getCurrentUsername();
	}
	/** Returns the current theme.
	 *
	 * <p>Default: "Normal" is returned, unless {@link #setPreferenceManager}
	 * is called to override its meaning.
	 */
	public static final String getCurrentTheme() {
		return _prefman.getCurrentTheme();
	}
	/**
	 * Sets the thread locale.
	 *
	 * <p>Each thread could have an independent locale, called
	 * the thread locale.
	 *
	 * <p>When Invoking this method under a thread that serves requests,
	 * remember to clean up the setting upon completing each request.
	 *
	 * <pre><code>Locale old = Apps.setThreadLocale(newValue);
	 *try { 
	 *  ...
	 *} finally {
	 *  Apps.setThreadLocale(old);
	 *}</code></pre>
	 *
	 * @param locale the thread locale; null to denote no thread locale
	 * @return the previous thread locale
	 */
	public static final Locale setThreadLocale(Locale locale) {
		return _prefman.setThreadLocale(locale);
	}
	/** Keeps the current login session, if any, alive (prevents it from timeout).
	 */
	public static final void keepAlive() {
		_prefman.keepAlive();
	}
	/** Sets the preference manager used to retrieve {@link #getCurrentLocale},
	 * {@link #getCurrentUsername} and so on.
	 *
	 * @param prefman the new preference manager.
	 * If null is specified, the default one is used.
	 * @return the previous prefernce manager (never null).
	 */
	public static final
	PreferenceManager setPreferenceManager(PreferenceManager prefman) {
		final PreferenceManager old = _prefman;
		_prefman = prefman != null ? prefman: newPrefManager();
		return old;
	}
	private static PreferenceManager newPrefManager() {
		return new PreferenceManager() {
			private final InheritableThreadLocal
				_thdLocale = new InheritableThreadLocal();
			public Locale getCurrentLocale() {
				final Locale l = (Locale)_thdLocale.get();
				return l != null ? l: Locale.getDefault();
			}
			public TimeZone getCurrentTimeZone() {
				return TimeZone.getDefault();
			}
			public String getCurrentDomain() {
				return "localdomain";
			}
			public String getCurrentUsername() {
				return null;
			}
			public String getCurrentTheme() {
				return "Normal";
			}
			public Locale setThreadLocale(Locale locale) {
				final Locale old = (Locale)_thdLocale.get();
				_thdLocale.set(locale);
				return old;
			}
			public void keepAlive() {
			}
		};
	}

	/** Sets the property of the specified name belonging to this
	 * applications.
	 *
	 * @param value the value of this property. If null, the property is
	 * removed.
	 */
	public static final String setProperty(String name, String value) {
		return value != null ?
			(String)_props.setProperty(name, value):
			(String)_props.remove(name);
	}
	/** Returns all properties.
	 */
	public static final Properties getProperties() {
		return _props;
	}
	/** Returns the property of the specified name belonging to this
	 * application. If not found, it will try System.getProperty.
	 * If not found either, return null.
	 */
	public static final String getProperty(String name) {
		final String s = _props.getProperty(name);
		return s != null ? s: System.getProperty(name);
	}
	/** Returns the property of the specified name in String,
	 * or defValue if not found.
	 *
	 * @param defValue the default value used if not found (in both
	 * Apps and System).
	 */
	public static final String getProperty(String name, String defValue) {
		final String s = getProperty(name);
		return s != null ? s: defValue;
	}

	/** Returns the property of the specified name in integer,
	 * or defValue if not found.
	 *
	 * <p>To convert a string to boolean, Integer.parseInt() is used.
	 *
	 * @param defValue the default value used if not found.
	 */
	public static final int getInteger(String name, int defValue) {
		final String s = getProperty(name);
		return s != null ? Integer.parseInt(s): defValue;
	}
	/** Returns the property of the specified name in integer,
	 * or 0 if not found.
	 *
	 * <p>To convert a string to boolean, Integer.parseInt() is used.
	 */
	public static final int getInteger(String name) {
		final String s = getProperty(name);
		return s != null ? Integer.parseInt(s): 0;
	}
	/** Returns the property of the specified name in Integer,
	 * or defValue if not found.
	 *
	 * <p>To convert a string to boolean, Integer.valueOf() is used.
	 *
	 * @param defValue the default value used if not found (might null).
	 */
	public static final Integer getInteger(String name, Integer defValue) {
		final String s = getProperty(name);
		return s != null ? Integer.valueOf(s): defValue;
	}

	/** Returns the property of the specified name in boolean,
	 * or defValue if not found.
	 *
	 * <p>To convert a string to boolean, Boolean.valueOf() is used.
	 *
	 * @param defValue the default value used if not found.
	 */
	public static final boolean getBoolean(String name, boolean defValue) {
		final String s = getProperty(name);
		return s != null ? Boolean.valueOf(s.trim()).booleanValue(): defValue;
	}
	/** Returns the property of the specified name in boolean,
	 * or false if not found.
	 *
	 * <p>To convert a string to boolean, Boolean.valueOf() is used.
	 */
	public static final boolean getBoolean(String name) {
		final String s = getProperty(name);
		return s != null && Boolean.valueOf(s.trim()).booleanValue();
	}
	/** Returns the property of the specified name in Boolean,
	 * or defValue if not found.
	 *
	 * <p>To convert a string to boolean, Boolean.valueOf() is used.
	 *
	 * @param defValue the default value used if not found (might be null).
	 */
	public static final Boolean getBoolean(String name, Boolean defValue) {
		final String s = getProperty(name);
		return s != null ? Boolean.valueOf(s.trim()): defValue;
	}

	/** Sets the fields of the specified object with the application
	 * properties.
	 *
	 * <p>Assumes prefix is "com.potix.util.logging." and there are
	 * a property called "com.potix.util.logging.Filename", then
	 * obj.setFilename(propValue) will be called.
	 *
	 * <p>Note: if the property following another '.' after prefix, it is
	 * ignore. Thus, you could use '.' to exclude certain properties
	 *
	 * <p>It detects what the method argument is and convert string to
	 * the proper type befor calling.
	 */
	public static final void setFieldByProperties(Object bean, String prefix) {
		final int len = prefix.length();
		synchronized (_props) {
			final Class klass = bean.getClass();
			final Object[] args = new Object[1];
			for (Iterator it = _props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				String name = (String)me.getKey();
				if (!name.startsWith(prefix))
					continue;
				name = name.substring(len);
				if (name.indexOf('.') >= 0)
					continue; //skip
				final String value = (String)me.getValue();

				Class argType = null;
				try {
					final Method m = Classes.getCloseMethod(klass,
						Classes.toMethodName(name, "set"), new Class[] {null});
					argType = m.getParameterTypes()[0];
					args[0] =
						value != null ? Strings.toObject(argType, value): null;
					m.invoke(bean, args);
				} catch (NoSuchMethodException ex) {
					System.out.println("Ingored: the property does not have the corresponding method in "+klass+": "+name);
				} catch (NumberFormatException ex) {
					System.out.println("Ignored: Failed to convert "+value+" to "+argType+" for "+name+" in "+klass);
				} catch (Exception ex) {
					throw SystemException.Aide.wrap(ex);
				}
			}
		}
	}

	/** Load the application properties from the specified file.
	 */
	public static final void loadProperties(File file) {
		try {
			if (!file.exists())
				return; //nothing to do

			System.out.println("[i3] Loading application properties from "+file);
			FileInputStream strm = null;
			try {
				strm = new FileInputStream(file);
				Maps.load(_props, new BufferedInputStream(strm));
					//NOTE: don't use _props.load(strm) because we want
					//handle multiple-lines properties
			} catch (IOException ex) {
				throw new SystemException("[i3] Unable to read "+file, ex);
			} finally {
				Files.close(strm);
			}
		} catch (java.security.AccessControlException ex) { //ignore it
			System.out.println("Ignored: no right to access "+ file);
		}
	}
}
