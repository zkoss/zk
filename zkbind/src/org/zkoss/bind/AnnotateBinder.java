/* AnnotateBinder.java

	Purpose:
		
	Description:
		
	History:
		Aug 4, 2011 10:56:14 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind;

import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * ZK Annotated Binder, it parse the component annotation and handles the binding of components and view model.
 * @author henrichen
 * @author dennischen
 *
 */
public class AnnotateBinder extends BinderImpl {
	private static final long serialVersionUID = 1463169907348730644L;
	/**
	 * new a annotate binder with default event queue name and scope
	 */
	public AnnotateBinder() {
		this(null,null);
	}
	
	/**
	 * new a binder with event queue name and scope
	 * @param qname event queue name
	 * @param qscope event queue scope, see {@link EventQueues}
	 */
	public AnnotateBinder(String qname, String qscope) {
		super(qname, qscope);
	}
	
	@Override
	public void init(Component comp, Object vm){
		super.init(comp, vm);
		new AnnotateBinderHelper(this).initComponentBindings(comp);
	}
}
