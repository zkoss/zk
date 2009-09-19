/* Labels.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 21 10:55:09     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import org.zkoss.lang.SystemException;
import org.zkoss.util.resource.impl.LabelLoader;
import org.zkoss.text.MessageFormats;
import org.zkoss.xel.VariableResolver;

/**
 * Utilities to access labels. A label is a Locale-dependent string
 * that is stored in i3-label*properties.
 *
 * @author tomyeh
 */
public class Labels {
	private Labels() {} //prevent form misuse

	private static final LabelLoader _loader = new LabelLoader();

	/** Returns the label of the specified key based
	 * on the current Locale, or null if no found.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 */
	public static final String getLabel(String key) {
		return _loader.getLabel(key);
	}
	/** Returns the label of the specified key and formats it
	 * with the specified argument, or null if not found.
	 *
	 * <p>It first uses {@link #getLabel(String)} to load the label.
	 * Then, it, if not null, invokes {@link MessageFormats#format} to format it.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 * @since 3.0.6
	 */
	public static final String getLabel(String key, Object[] args) {
		final String s = getLabel(key);
		return s != null ? MessageFormats.format(s, args, null): null;
	}

	/** Returns the label of the specified key based
	 * on the current Locale, or the default value if no found.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 *
	 * @param defValue the value being returned if the key is not found
	 * @since 3.6.0
	 */
	public static final String getLabel(String key, String defValue) {
		final String s = _loader.getLabel(key);
		return s != null ? s: defValue;
	}
	/** Returns the label of the specified key and formats it
	 * with the specified argument, or the default value if not found.
	 *
	 * <p>It first uses {@link #getLabel(String, String)} to load the label.
	 * Then, it, if not null, invokes {@link MessageFormats#format} to format it.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 * @param defValue the value being returned if the key is not found
	 * @since 3.6.0
	 */
	public static final String getLabel(String key, String defValue, Object[] args) {
		final String s = getLabel(key, defValue);
		return s != null ? MessageFormats.format(s, args, null): null;
	}

	/** Returns the label of the specified key based on the current locale.
	 * Unlike {@link #getLabel(String)}, it throws an exception if not found.
	 *
	 * @exception SystemException if no such label
	 * @since 3.0.6
	 */
	public static final String getRequiredLabel(String key)
	throws SystemException {
		final String s = getLabel(key);
		if (s == null)
			throw new SystemException("label not found: "+key);
		return s;
	}
	/** Returns the label of the specified key and formats it
	 * with the specified argument, or null if not found.
	 * Unlike {@link #getLabel(String, Object[])}, it throws an exception if not found.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 * @exception SystemException if no such label
	 * @since 3.0.6
	 */
	public static final String getRequiredLabel(String key, Object[] args) {
		final String s = getLabel(key);
		if (s == null)
			throw new SystemException("label not found: "+key);
		return MessageFormats.format(s, args, null);
	}

	/** Resets all cached labels and next call to {@link #getLabel(String)}
	 * will cause re-loading i3-label*.proerties.
	 */
	public static final void reset() {
		_loader.reset();
	}
	/** Sets the variable resolver, which is used if an EL expression
	 * is specified.
	 *
	 * <p>Default: no resolver at all.
	 *
	 * @return the previous resolver, or null if no resolver.
	 */
	public static final
	VariableResolver setVariableResolver(VariableResolver resolv) {
		return _loader.setVariableResolver(resolv);
	}
	/** Registers a locator which is used to load i3-label*.properties
	 * from other resource, such as servlet contexts.
	 */
	public static final void register(LabelLocator locator) {
		_loader.register(locator);
	}
}
