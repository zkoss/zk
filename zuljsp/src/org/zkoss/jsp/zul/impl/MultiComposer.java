/**
 * 
 */
package org.zkoss.jsp.zul.impl;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;

/**
 * @author ian
 *
 */
/*package*/class MultiComposer  implements Composer, ComposerExt {
		private final Composer[] _cs;
		/*package*/MultiComposer(Object[] cs) throws Exception {
			if (cs instanceof Composer[]) {
				_cs = (Composer[])cs;
			} else {
				_cs = new Composer[cs.length];
				for (int j = cs.length; --j >=0;) {
					final Object o = cs[j];
					_cs[j] = (Composer)(
						o instanceof String ?
							Classes.newInstanceByThread(((String)o).trim()):
						o instanceof Class ?
							((Class)o).newInstance(): (Composer)o);
				}
			}
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
