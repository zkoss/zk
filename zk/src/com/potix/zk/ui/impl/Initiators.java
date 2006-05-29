/* Initiators.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Dec 14 09:02:20     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import com.potix.lang.Classes;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Initiator;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.metainfo.InitiatorDefinition;

/**
 * A helper class used with {@link com.potix.zk.ui.sys.UiEngine} to process
 * {@link Initiator}
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
/*package*/ class Initiators {
	/*package(inner)*/ static final Log log = Log.lookup(Initiators.class);

	/** Invokes {@link Initiator#doInit}, if any, and returns
	 * an instance of{@link Initiators}.
	 */
	public static final Initiators doInit(PageDefinition pagedef, Page page) {
		final List initdefs =
			pagedef != null ? pagedef.getInitiatorDefinitions(): null;
		if (initdefs == null || initdefs.isEmpty()) return new Initiators();

		final List inits = new LinkedList();
		for (Iterator it = initdefs.iterator(); it.hasNext();) {
			final InitiatorDefinition def = (InitiatorDefinition)it.next();
			try {
				final Initiator init = def.newInitiator(pagedef, page);
				if (init != null) {
					init.doInit(page, def.getArguments(pagedef, page));
					inits.add(init);
				}
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		return new RealInits(inits);
	}
	protected Initiators() {
	}
	public void doCatch(Throwable t) {
	}
	public void doFinally() {
	}
}
/*package*/ class RealInits extends Initiators {
	private final List _inits;
	/**
	 * @param inits a collection of {@link Initiator}.
	 */
	/*package*/ RealInits(List inits) {
		_inits = inits;
		
	}
	/** Invokes {@link Initiator#doCatch}.
	 * It eats all exception without throwing one (but logging).
	 * Caller has to re-throw the exception.
	 */
	public void doCatch(Throwable t) {
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			final Initiator init = (Initiator)it.next();
			try {
				init.doCatch(t);
			} catch (Throwable ex) {
				Initiators.log.error(ex);
			}
		}
	}
	/** Invokes {@link Initiator#doFinally}.
	 */
	public void doFinally() {
		Throwable t = null;
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