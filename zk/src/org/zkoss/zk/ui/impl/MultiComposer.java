/* MultiComposer.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  9 13:02:22     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.FullComposer;

/**
 * To proxy a collection of composers.
 * @author tomyeh
 * @since 5.0.1
 */
public class MultiComposer<T extends Component> implements Composer<T> {
	private final Composer<T>[] _cs;
	private boolean _fullOnly;

	/** Returns an instance of composer to represent the specified
	 * array of composers, or null if no composer is specified.
	 *
	 * @param page used to resolve the class if ary contains a class name.
	 * Ignored if null.
	 * @param ary an array of Composer instances, or the name of the class,
	 * or the class that implements {@link Composer}.
	 * @return a composer to represent cs, or null if cs is null or empty.
	 */
	@SuppressWarnings("unchecked")
	public static Composer getComposer(Page page, Object[] ary)
	throws Exception {
		if (ary == null || ary.length == 0)
			return null;

		if (ary.length == 1)
			return Utils.newComposer(page, ary[0]);

		final Composer[] cs;
		boolean ext = false, full = false;
		if (ary instanceof Composer[]) {
			cs = (Composer[])ary;
			for (int j = cs.length; --j >= 0;) {
				if (cs[j] instanceof ComposerExt) {
					ext = true;
					if (full) break;
				}
				if (cs[j] instanceof FullComposer) {
					full = true;
					if (ext) break;
				}
			}
		} else {
			cs = new Composer[ary.length];
			for (int j = ary.length; --j >=0;) {
				cs[j] = Utils.newComposer(page, ary[j]);
				ext = ext || (cs[j] instanceof ComposerExt);
				full = full || (cs[j] instanceof FullComposer);
			}
		}

		if (full) {
			if (ext) return new MultiFullComposerExt(cs);
			return new MultiFullComposer(cs);
		} else {
			if (ext) return new MultiComposerExt(cs);
			return new MultiComposer(cs);
		}
	}

	/** Sets whether to invoke only the composer that implements {@link FullComposer}.
	 * <p>Default: false
	 * @return the previous value.
	 * @since 5.0.1
	 */
	public boolean setFullComposerOnly(boolean fullOnly) {
		boolean b = _fullOnly;
		_fullOnly = fullOnly;
		return b;
	}
	/** Returns whether to invoke only the composer that implements {@link FullComposer}.
	 * @since 5.0.1
	 */
	public boolean isFullComposerOnly() {
		return _fullOnly;
	}
	private boolean shallInvoke(Composer composer) {
		return !_fullOnly || composer instanceof FullComposer;
	}

	/** The constructor.
	 * This method is designed to be called by {@link #getComposer}.
	 * Use {@link #getComposer} instead.
	 *
	 * @param cs the array of composer instances.
	 */
	protected MultiComposer(Composer<T>[] cs) throws Exception {
		//ZK8 - prevent duplicate composer classes
		Map<String, Composer<T>> csMap = new HashMap<String, Composer<T>>();
		for (int j = 0; j < cs.length; ++j)
			csMap.put(cs[j].getClass().toString(), cs[j]);
		_cs = csMap.values().toArray(new Composer[csMap.values().size()]);
	}
	public void doAfterCompose(T comp) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (shallInvoke(_cs[j]))
				_cs[j].doAfterCompose(comp);
	}
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt && shallInvoke(_cs[j])) {
				compInfo = ((ComposerExt)_cs[j])
					.doBeforeCompose(page, parent, compInfo);
				if (compInfo == null)
					return null;
			}
		return compInfo;
	}
	@SuppressWarnings("unchecked")
	public void doBeforeComposeChildren(T comp) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt && shallInvoke(_cs[j]))
				((ComposerExt)_cs[j]).doBeforeComposeChildren(comp);
	}
	public boolean doCatch(Throwable ex) throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt && shallInvoke(_cs[j]))
				if (((ComposerExt)_cs[j]).doCatch(ex))
					return true; //caught (eat it)
		return false;
	}
	public void doFinally() throws Exception {
		for (int j = 0; j < _cs.length; ++j)
			if (_cs[j] instanceof ComposerExt && shallInvoke(_cs[j]))
				((ComposerExt)_cs[j]).doFinally();
	}

	private static class MultiComposerExt<T extends Component>
	extends MultiComposer<T> implements ComposerExt<T> {
		private MultiComposerExt(Composer<T>[] cs) throws Exception {
			super(cs);
		}
	}
	private static class MultiFullComposer<T extends Component>
	extends MultiComposer<T> implements FullComposer {
		private MultiFullComposer(Composer<T>[] cs) throws Exception {
			super(cs);
		}
	}
	private static class MultiFullComposerExt<T extends Component>
	extends MultiFullComposer<T> implements ComposerExt<T> {
		private MultiFullComposerExt(Composer<T>[] cs) throws Exception {
			super(cs);
		}
	}
}
