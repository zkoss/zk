/* Initiators.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed August 08 14:27:47     2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;

/**
 *  A helper class used to handle {@link Initiator} in jsp.
 * 
 * @author Ian Tsai
 *
 */
/*package*/ class Initiators{
	static final Log log = Log.lookup(Initiators.class);
	private final List _inits;
	private final List _argsList;
	
	/**
	 * 
	 *
	 */
	public Initiators(){
		_inits = new ArrayList(5);
		_argsList = new ArrayList(5);
	}
	/**
	 * Add new initiator and it's args into this handler. 
	 * @param init the initiator
	 * @param args the args
	 */
	public void addInitiator(Initiator init, List args)	{
		_inits.add(init);
		_argsList.add(args);
	}
	
	/** Invokes {@link Initiator#doInit}, if any, and returns
	 * an instance of{@link Initiators}.
	 */
	public void doInit(Page page){
		Initiator initiator;
		List args;
		for (int i=0,j=_argsList.size();i<j;i++) {
			initiator = (Initiator) _inits.get(i);
			args = (List) _argsList.get(i);
			try {
				initiator.doInit(page, getArguments(page, args));
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	
	/** Returns the arguments array (by evaluating EL if necessary).
	 */
	private Object[] getArguments(Page page, List _args) {
		final Object[] args = new Object[_args.size()];
		for (int j = 0; j < args.length; ++j)
			args[j] = Executions.evaluate(page, (String)_args.get(j), Object.class);
		return args;
	}
	/**
	 * 
	 * @param page
	 * @throws Exception
	 */
	public void doAfterCompose(Page page) throws Exception {
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			((Initiator)it.next()).doAfterCompose(page);
		}
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
				log.error(ex);
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
				log.error(ex);
				if (t == null) t = ex;
			}
		}
		if (t != null) throw UiException.Aide.wrap(t);
	}
	
}
