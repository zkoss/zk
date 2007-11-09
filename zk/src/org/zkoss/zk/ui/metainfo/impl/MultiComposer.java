/* MultiComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Nov  9 13:02:22     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import org.zkoss.lang.Classes;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;

/**
 * To proxy a collection of composer
 * @author tomyeh
 * @since 3.1.0
 */
public class MultiComposer implements Composer {
	private final Composer[] _cs;

	/** Returns an instance of composer to represents the specified
	 * array of composers, or null if no composer is specified.
	 *
	 * @param cs an array of Composer instances, or the name of the class,
	 * or the class that implements {@link Composer}.
	 * @return a composer to represent cs, or null if cs is null or empty.
	 */
	public static Composer getComposer(Object[] ary) throws Exception{
		if (ary == null || ary.length == 0)
			return null;

		if (ary.length == 1) {
			Object o = ary[0];
			if (o instanceof String)
				o = Classes.newInstanceByThread(((String)o).trim());
			else if (o instanceof Class)
				o = ((Class)o).newInstance();
			return (Composer)o;
		}

		final Composer[] cs;
		boolean ext = false;
		if (ary instanceof Composer[]) {
			cs = (Composer[])ary;
			for (int j = cs.length; --j >= 0;)
				if (cs[j] instanceof ComposerExt) {
					ext = true;
					break;
				}
		} else {
			cs = new Composer[ary.length];
			for (int j = ary.length; --j >=0;) {
				final Object o = ary[j];
				cs[j] = (Composer)(
					o instanceof String ?
						Classes.newInstanceByThread(((String)o).trim()):
					o instanceof Class ?
						((Class)o).newInstance(): (Composer)o);
				ext = ext || (cs[j] instanceof ComposerExt);
			}
		}

		if (ext) return new MultiComposerExt(cs);
		return new MultiComposer(cs);
	}

	/** The constructor.
	 * This method is designed to be called by {@link #getComposer}.
	 * Use {@link #getComposer} instead.
	 *
	 * @param cs the array of composer instances (cannot be null).
	 */
	protected MultiComposer(Composer[] cs) throws Exception {
		_cs = cs;
	}
	public void doAfterCompose(Component comp) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			_cs[j].doAfterCompose(comp);
	}
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo) {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt) {
				compInfo = ((ComposerExt)_cs[j])
					.doBeforeCompose(page, parent, compInfo);
				if (compInfo == null)
					return null;
			}
		return compInfo;
	}
	public void doBeforeComposeChildren(Component comp) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt)
				((ComposerExt)_cs[j]).doBeforeComposeChildren(comp);
	}
	public boolean doCatch(Throwable ex) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt)
				if (((ComposerExt)_cs[j]).doCatch(ex))
					return true; //caught (eat it)
		return false;
	}
	public void doFinally() throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt)
				((ComposerExt)_cs[j]).doFinally();
	}
}
/*package*/ class MultiComposerExt extends MultiComposer implements ComposerExt {
	/*package*/ MultiComposerExt(Composer[] cs) throws Exception {
		super(cs);
	}
}
