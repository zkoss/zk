/* Labels.java

{{IS_NOTE
	$Id: Labels.java,v 1.3 2006/02/27 03:42:06 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Sep 21 10:55:09     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource;

import com.potix.comp.ComponentManager;

/**
 * Shortcuts to acces {@link LabelLoader}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:42:06 $
 */
public class Labels {
	/** Returns the {@link LabelLoader} component for this domain.
	 */
	public static final LabelLoader the() {
		return (LabelLoader)ComponentManager.the().get(LabelLoader.class.getName());
	}
	/** Returns the user readable label for a field based on the locale
	 * of the current user.
	 *
	 * <p>Shortcut to {@link LabelLoader#getFieldLabel}.
	 *
	 * @param klass the class
	 * @param field the field name
	 */
	public static final String getFieldLabel(Class klass, String field) {
		return the().getFieldLabel(klass, field);
	}
	/** Returns the user readable label for a class based on the locale
	 * of the current user.
	 *
	 * <p>Shortcut to {@link LabelLoader#getClassLabel}.
	 *
	 * @param klass the class
	 */
	public static final String getClassLabel(Class klass) {
		return the().getClassLabel(klass);
	}
	/** Returns the user readable label for a class based on the locale
	 * of the current user, or empty if null.
	 *
	 * <p>Shortcut to {@link LabelLoader#getClassLabel}.
	 *
	 * @param obj the object
	 */
	public static final String getClassLabel(Object obj) {
		return obj != null ? the().getClassLabel(obj.getClass()): "";
	}
	/** Returns the user readable compoud label for a field based on the
	 * locale of the current user.
	 *
	 * <p>Shortcut to {@link LabelLoader#getCompoundLabel}.
	 *
	 * @param klass the class
	 * @param field the field name
	 */
	public static final String getCompoundLabel(Class klass, String field) {
		return the().getCompoundLabel(klass, field);
	}
	/** Returns the user readable label for a object.
	 *
	 * <p>Shortcut to {@link LabelLoader#getObjectLabel}.
	 */
	public static final String getObjectLabel(Object o) {
		return the().getObjectLabel(o);
	}

	/** Returns the property of the specified key based on the current user's
	 * Locale.
	 */
	public static final String getProperty(String key) {
		return the().getProperty(key);
	}
}
