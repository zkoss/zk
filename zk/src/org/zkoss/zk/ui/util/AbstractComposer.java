/* AbstractComposer.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 22 16:37:29 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.metainfo.ComponentInfo;

/** The thinest skeletal implementation of composers.
 * It does nothing but store a reference to the composer's attribute.
 * For the naming of the attribuate, please refer to
 * <a href="http://220.133.44.37:8888/wiki/ZK_Developer%27s_Reference/MVC/Controller/Composer#Default_Names_of_Composer">ZK Developer's Reference</a>
 * for details.
 *
 * <P>Alternatives: you can extend from one of the following skeletons.
 * <dl>
 * <dt>{@link AbstractComposer}</dt>
 * <dd>The thinest composer. It does nothing but stores a reference of the composer
 * to the component's attribute.</dd>
 * <dt>{@link org.zkoss.zk.ui.select.SelectorComposer}</dt>
 * <dd>It supports the autowiring based on Java annoataion and a CSS3-based selector.
 * If you don't know which one to use, use {@link org.zkoss.zk.ui.select.SelectorComposer}.</dd>
 * <dt>{@link GenericForwardComposer}</dt>
 * <dd>It supports the autowiring based on naming convention.
 * You don't need to specify annotations explicitly, but it is error-prone if
 * it is used properly.</dd>
 * </dl>
 * 
 * @author tomyeh
 * @since 6.0.0
 */
abstract public class AbstractComposer<T extends Component>
implements Composer<T>, ComposerExt<T>, java.io.Serializable {
	/** Returns the current page.
	 */
	protected Page getPage() {
		final Execution exec = Executions.getCurrent();
		return exec != null ? ((ExecutionCtrl)exec).getCurrentPage(): null;
	}
	@Override
	public void doAfterCompose(T comp) throws Exception { //do nothing
	}

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) { //do nothing
		return compInfo;
	}
	@Override
	public void doBeforeComposeChildren(T comp) throws Exception {
		//assign this composer as a variable
		//feature #2778508
		ConventionWires.wireController(comp, this);
	}

	@Override
	public boolean doCatch(Throwable ex) throws Exception { //do nothing
		return false;
	}
	@Override
	public void doFinally() throws Exception { //do nothing
	}
}
