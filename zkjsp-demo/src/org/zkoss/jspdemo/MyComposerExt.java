/**
 * 
 */
package org.zkoss.jspdemo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;

/**
 * @author ian
 *
 */
public class MyComposerExt implements Composer, ComposerExt {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Composer#doAfterCompose(org.zkoss.zk.ui.Component)
	 */
	public void doAfterCompose(Component comp) throws Exception {
		printf(MyComposerExt.class+":: void doAfterCompose(Component) comp="+comp);
	}
	
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doBeforeComposeChildren(org.zkoss.zk.ui.Component)
	 */
	public void doBeforeComposeChildren(Component comp) throws Exception {
		printf(MyComposerExt.class+":: void doBeforeComposeChildren(Component) comp="+comp);
	}
	
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doBeforeCompose(org.zkoss.zk.ui.Page, org.zkoss.zk.ui.Component, org.zkoss.zk.ui.metainfo.ComponentInfo)
	 */
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		printf(MyComposerExt.class+":: void doBeforeCompose(Page, Component, ComponentInfo) is invoked!!!");
		return null;
	}
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doCatch(java.lang.Throwable)
	 */
	public boolean doCatch(Throwable ex) throws Exception {
		printf(MyComposerExt.class+":: void doCatch(Throwable) Throwable="+ex);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.ComposerExt#doFinally()
	 */
	public void doFinally() throws Exception {
		printf(MyComposerExt.class+":: void doFinally is invoked!!!");
	}
	
	
	public static void printf(String strs)
	{
		System.out.println(strs);
	}

}
