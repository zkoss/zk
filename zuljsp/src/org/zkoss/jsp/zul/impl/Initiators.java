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
package org.zkoss.jsp.zul.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;

/**
 *  A helper class used to handle {@link Initiator} in jsp.
 * 
 * @author Ian Tsai
 *
 */
public class Initiators{
	static final Log log = Log.lookup(Initiators.class);
	private final List _inits = new LinkedList();

	
	/**
	 * Add new initiator and it's args into this handler. 
	 * @param init the initiator
	 * @param args the args
	 */
	public void addInitiator(Initiator init, List args)	{
		_inits.add(new Object[]{init,args});
	}
	
	/** Invokes {@link Initiator#doInit}, if any, and returns
	 * an instance of{@link Initiators}.
	 */
	public void doInit(Page page){
		Object[] objArr;
		Initiator initiator;
		List args;
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			objArr = (Object[]) it .next();
			initiator = (Initiator) objArr[0];
			args = (List) objArr[1];
			try {
				initiator.doInit(page, args.toArray());
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	
	/**
	 * 
	 * @param page
	 * @throws Exception
	 */
	public void doAfterCompose(Page page, List compTags) throws Exception {
		
		Component[] comps = null;
		if(compTags!=null){
			Object[] tags = compTags.toArray();
			comps = new Component[ tags.length];
			ComponentTag tag;
			for(int i=tags.length-1;i>=0;i--){
				tag = (ComponentTag)tags[i];
				comps[i] = tag.getComponent();
			}
		}
		Initiator init;
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			init = (Initiator)((Object[])it.next())[0];
			if (init instanceof InitiatorExt) {
				if (comps == null) comps = new Component[0];
				((InitiatorExt)init).doAfterCompose(page, comps);
			} else 
				init.doAfterCompose(page);
		}
	}
	/** Invokes {@link Initiator#doCatch}.
	 * It eats all exception without throwing one (but logging).
	 * Caller has to re-throw the exception.
	 */
	public void doCatch(Throwable t) {
		for (Iterator it = _inits.iterator(); it.hasNext();) {
			final Initiator init = ((Initiator)((Object[])it.next())[0]);
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
			final Initiator init = ((Initiator)((Object[])it.next())[0]);
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
