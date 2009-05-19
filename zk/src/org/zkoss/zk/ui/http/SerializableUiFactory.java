/* SerializableUiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 12:38:04     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.impl.AbstractUiFactory;

/**
 * The serializable implementation of {@link org.zkoss.zk.ui.sys.UiFactory}.
 * The instances returned by {@link #newSession} is serializable, such that
 * session can be stored when the Web server stops and restore after it starts.
 *
 * <p>Since ZK 5.0.0, use org.zkoss.zkmax.ui.http.SerializableUiFactory
 * instead of this class.
 * 
 * @author tomyeh
 */
public class SerializableUiFactory extends AbstractUiFactory {
	private UiFactory _uiFactory;

	/** The class of the real UI factory that this UI factory will delegate
	 * the invocation to.
	 * @since 5.0.0
	 */
	protected static Class _uiFactoryClass;

	public Session newSession(WebApp wapp, Object nativeSess, Object request) {
		if (_uiFactory == null) {
			synchronized (this) {
				if (_uiFactory == null) {
					if (_uiFactoryClass == null)
						throw new UnsupportedOperationException("Clustering supported only in the Enterprise edition");
					final UiFactory uif;
					try {
						uif = (UiFactory)_uiFactoryClass.newInstance();
					} catch (Throwable ex) {
						throw UiException.Aide.wrap(ex);
					}
					_uiFactory = uif;
				}
			}
		}

		return _uiFactory.newSession(wapp, nativeSess, request);
	}
}
