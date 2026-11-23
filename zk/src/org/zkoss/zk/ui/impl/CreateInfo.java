/* CreateInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 12:05:27 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.impl;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.FullComposer;

/** Info used with execCreate
 */
/*package*/ class CreateInfo {
	/*package*/ final Execution exec;
	/*package*/ final Page page;
	/*package*/ final UiFactory uf;
	private List<Composer> _composers, _composerExts;
	private Composer _syscomposer;

	/*package*/ CreateInfo(UiFactory uf, Execution exec, Page page, Composer composer) {
		this.exec = exec;
		this.page = page;
		this.uf = uf;
		if (composer instanceof FullComposer)
			pushFullComposer(composer);
		else
			_syscomposer = composer;
	}

	/*package*/ void pushFullComposer(Composer composer) {
		assert composer instanceof FullComposer; //illegal state
		if (_composers == null)
			_composers = new LinkedList<Composer>();
		_composers.add(0, composer);
		if (composer instanceof ComposerExt) {
			if (_composerExts == null)
				_composerExts = new LinkedList<Composer>();
			_composerExts.add(0, composer);
		}
	}

	/*package*/ void popFullComposer() {
		Composer o = _composers.remove(0);
		if (_composers.isEmpty())
			_composers = null;
		if (o instanceof ComposerExt) {
			_composerExts.remove(0);
			if (_composerExts.isEmpty())
				_composerExts = null;
		}
	}

	/** Invoke setFullComposerOnly to ensure only composers that implement
	 * FullComposer are called.
	 */
	private static boolean beforeInvoke(Composer composer, boolean bRoot) {
		//If bRoot (implies system-level composer), always invoke (no setFullxxx)
		return !bRoot && composer instanceof MultiComposer && ((MultiComposer) composer).setFullComposerOnly(true);
	}

	private static void afterInvoke(Composer composer, boolean bRoot, boolean old) {
		if (!bRoot && composer instanceof MultiComposer)
			((MultiComposer) composer).setFullComposerOnly(old);
	}

	@SuppressWarnings("unchecked")
	/*package*/ void doAfterCompose(Component comp, boolean bRoot) throws Exception {
		if (_composers != null)
			for (Composer composer : _composers) {
				final boolean old = beforeInvoke(composer, bRoot);

				composer.doAfterCompose(comp);

				afterInvoke(composer, bRoot, old);
			}

		if (bRoot && _syscomposer != null)
			_syscomposer.doAfterCompose(comp);
	}

	/*package*/ ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo, boolean bRoot)
			throws Exception {
		if (_composerExts != null)
			for (Composer composer : _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);

				compInfo = ((ComposerExt) composer).doBeforeCompose(page, parent, compInfo);

				afterInvoke(composer, bRoot, old);
				if (compInfo == null)
					return null;
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			compInfo = ((ComposerExt) _syscomposer).doBeforeCompose(page, parent, compInfo);
		return compInfo;
	}

	/*package*/ void doBeforeComposeChildren(Component comp, boolean bRoot) throws Exception {
		if (_composerExts != null)
			for (Composer composer : _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);
				UiEngineImpl.doBeforeComposeChildren((ComposerExt) composer, comp);
				afterInvoke(composer, bRoot, old);
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			UiEngineImpl.doBeforeComposeChildren((ComposerExt) _syscomposer, comp);
	}

	/*package*/ boolean doCatch(Throwable ex, boolean bRoot) {
		if (_composerExts != null)
			for (Composer composer : _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);
				try {
					final boolean ret = ((ComposerExt) composer).doCatch(ex);
					afterInvoke(composer, bRoot, old);
					if (ret)
						return true; //ignore
				} catch (Throwable t) {
					UiEngineImpl.log.error("Failed to invoke doCatch for " + composer, t);
				}
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			try {
				return ((ComposerExt) _syscomposer).doCatch(ex);
			} catch (Throwable t) {
				UiEngineImpl.log.error("Failed to invoke doCatch for " + _syscomposer, t);
			}
		return false;
	}

	/*package*/ void doFinally(boolean bRoot) throws Exception {
		if (_composerExts != null)
			for (Composer composer : _composerExts) {
				final boolean old = beforeInvoke(composer, bRoot);

				((ComposerExt) composer).doFinally();

				afterInvoke(composer, bRoot, old);
			}

		if (bRoot && _syscomposer instanceof ComposerExt)
			((ComposerExt) _syscomposer).doFinally();
	}
}
