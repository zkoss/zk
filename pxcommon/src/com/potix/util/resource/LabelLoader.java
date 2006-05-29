/* LabelLoader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 13 10:09:04     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource;

import java.util.Map;
import java.net.URL;

/**
 * The label manager used to handle labels.
 *
 * <p>There is one label manager per domain.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface LabelLoader {
	/** Returns the user readable label for a field based on the locale
	 * of the current user.
	 *
	 * @param klass the class
	 * @param field the field name
	 */
	public String getFieldLabel(Class klass, String field);
	/** Returns the user readable label for a class based on the locale
	 * of the current user.
	 *
	 * @param klass the class
	 */
	public String getClassLabel(Class klass);
	/** Returns the user readable compoud label for a field based on the
	 * locale of the current user. It is a composition of
	 * {@link #getFieldLabel} and {@link #getClassLabel}.
	 *
	 * @param klass the class
	 * @param field the field name
	 */
	public String getCompoundLabel(Class klass, String field);
	/** Returns the user readable label for a object.
	 */
	public String getObjectLabel(Object o);

	/** Returns the property of the specified key based on the current user's
	 * Locale.
	 */
	public String getProperty(String key);

	/** Registers a label locator for extra resources that {@link LabelLoader}
	 * shall load from.
	 *
	 * <p>Note: the implementation might not be thread-safe, so registers
	 * locators only during startup.
	 *
	 * @exception UnsupportedOperationException if not supported
	 */
	public void register(LabelLocator locator);
	/** Resets the loader, such that it will be loaded again.
	 */
	public void reset();
}
