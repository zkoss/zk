/* Namespaces.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 16 00:01:09     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import java.util.Map;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.Scopes;

/**
 * @deprecated As of release 5.0, replaced with {@link Scopes}.
 * @author tomyeh
 */
public class Namespaces {
	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 */
	public static final
	Namespace beforeInterpret(Component comp) {
		return null;
	}
	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 */
	public static final Namespace beforeInterpret(Page page) {
		return null;
	}
	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 */
	public static final void afterInterpret() {
	}

	/** @deprecated As of release 3.6.1, it is replaced with {@link #beforeInterpret(Component)}.
	 */
	public static final Namespace beforeInterpret(Map backup, Component comp,
	boolean pushNS) {
		return beforeInterpret(comp);
	}
	/** @deprecated As of release 3.6.1, it is replaced with {@link #beforeInterpret(Page)}.
	 */
	public static final Namespace beforeInterpret(Map backup, Page page,
	boolean pushNS) {
		return beforeInterpret(page);
	}
	/** @deprecated As of release 3.6.1, it is replaced with {@link #afterInterpret}.
	 */
	public static final void afterInterpret(Map backup, Namespace ns,
	boolean popNS) {
		afterInterpret();
	}

	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 * Sets an implicit object.
	 * It can be called only between {@link #beforeInterpret} and
	 * {@link #afterInterpret}.
	 *
	 * @since 3.6.1
	 */
	public static void setImplicit(String name, Object value) {
		Scopes.setImplicit(name, value);
	}
	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 * Returns the implict object.
	 *
	 * @param name the variable to retrieve
	 * @param defValue the default vale that is used if the implicit
	 * object is not defined.
	 * @since 3.6.1
	 */
	public static Object getImplicit(String name, Object defValue) {
		return Scopes.getImplicit(name, defValue);
	}

	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 */
	public static final Namespace getCurrent(Page page) {
		return null;
	}
	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 */
	private static final void push(Namespace ns) {
	}
	/** @deprecated As of release 5.0, replaced with {@link Scopes}.
	 */
	private static final void pop() {
	}
}
