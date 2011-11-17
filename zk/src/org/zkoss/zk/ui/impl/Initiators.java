/* Initiators.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 14 09:02:20     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Method;

import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * A helper class used to handle {@link Initiator}.
 *
 * @author tomyeh
 * @see org.zkoss.zk.ui.sys.UiEngine
 */
/*package*/ class Initiators {
	/*package(inner)*/ static final Log log = Log.lookup(Initiators.class);

	/** Invokes {@link Initiator#doInit}, if any, and returns
	 * an instance of{@link Initiators}.
	 * @param sysinits the system-level initiators
	 */
	public static final Initiators doInit(PageDefinition pagedef, Page page,
	Initiator[] sysinits) {
		if (sysinits == null) {
			sysinits = new Initiator[0];
		} else {
			try {
				for (int j = 0; j < sysinits.length; ++j)
					sysinits[j].doInit(page, Collections.EMPTY_MAP);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}

		final List inits = pagedef != null ? pagedef.doInit(page): Collections.EMPTY_LIST;
		if (sysinits.length == 0 && (inits.isEmpty()))
			return new Initiators();
		return new RealInits(sysinits, inits);
	}
	protected Initiators() {
	}

	public void doAfterCompose(Page page, Component[] comps) throws Exception {
	}
	public boolean doCatch(Throwable t) {
		return false;
	}
	public void doFinally() {
	}
}
/*package*/ class RealInits extends Initiators {
	private final Initiator[] _sysinits;
	private final List _inits;
	/**
	 * @param inits a collection of {@link Initiator}.
	 */
	/*package*/ RealInits(Initiator[] sysinits, List inits) {
		_sysinits = sysinits;
		_inits = inits;
	}
	/**
	 * Invokes {@link Initiator#doAfterCompose}.
	 * @param page
	 * @throws Exception
	 */
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		for (int j = 0; j < _sysinits.length; ++j) {
			final Initiator init = _sysinits[j];
			if (init instanceof InitiatorExt) {
				if (comps == null) comps = new Component[0];
				((InitiatorExt)init).doAfterCompose(page, comps);
			} else {
				init.doAfterCompose(page);
			}
		}
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			final Object init = it.next();
			if (init instanceof InitiatorExt) {
				if (comps == null) comps = new Component[0];
				((InitiatorExt)init).doAfterCompose(page, comps);
			} else {
				((Initiator)init).doAfterCompose(page);
			}
		}
	}
	/** Invokes {@link Initiator#doCatch}.
	 * It eats all exception without throwing one (but logging).
	 * Caller has to re-throw the exception.
	 */
	public boolean doCatch(Throwable t) {
		for (int j = 0; j < _sysinits.length; ++j) {
			final Initiator init = _sysinits[j];
			try {
				if (init.doCatch(t))
					return true;
			} catch (Throwable ex) {
				Initiators.log.error(ex);
			}
		}
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			final Initiator init = (Initiator)it.next();
			try {
				if (init.doCatch(t))
					return true; //ignore and skip all other initiators
			} catch (Throwable ex) {
				Initiators.log.error(ex);
			}
		}
		return false;
	}
	/** Invokes {@link Initiator#doFinally}.
	 */
	public void doFinally() {
		Throwable t = null;
		for (int j = 0; j < _sysinits.length; ++j) {
			final Initiator init = _sysinits[j];
			try {
				init.doFinally();
			} catch (Throwable ex) {
				Initiators.log.error(ex);
				if (t == null) t = ex;
			}
		}
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			final Initiator init = (Initiator)it.next();
			try {
				init.doFinally();
			} catch (Throwable ex) {
				Initiators.log.error(ex);
				if (t == null) t = ex;
			}
		}
		if (t != null) throw UiException.Aide.wrap(t);
	}
}