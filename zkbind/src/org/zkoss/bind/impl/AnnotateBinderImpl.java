/* AnnotateBinderImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 4, 2011 10:56:14 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.zk.ui.Component;

/**
 * ZK Annotated Binder.
 * @author henrichen
 *
 */
public class AnnotateBinderImpl extends BinderImpl {
	public AnnotateBinderImpl(Component comp, Object vm, String qname, String qscope) {
		super(comp, vm, qname, qscope);
		new AnnotateBinderHelper(this).initComponentBindings(comp);
	}
}
