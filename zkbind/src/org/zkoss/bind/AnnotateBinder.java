/* AnnotateBinder.java

	Purpose:
		
	Description:
		
	History:
		Aug 4, 2011 10:56:14 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * ZK Annotated Binder, it parse the component annotation and handles the binding of components and view model.
 * @author henrichen
 * @author dennischen
 * @since 6.0.0
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
	
	/**
	 * in AnnotateBinder, {@link #init(Component, Object, Map)} is merely a convenient method for orignal  
	 * @see #initViewModel(Component, Object, Map)
	 * @see #initAnnotatedBindings()
	 * @param comp
	 * @param vm
	 * @param initArgs
	 */
	public void init(Component comp, Object vm, Map<String, Object> initArgs){
		super.init(comp, vm, initArgs);
		new AnnotateBinderHelper(this).initComponentBindings(comp);
		comp.setAttribute(BINDER, this);
	}
	
	/**
	 * 
	 * @param comp
	 * @param vm
	 * @param initArgs
	 * @since 6.0.2
	 */
	public void initViewModel(Component comp, Object vm, Map<String, Object> initArgs){
		super.init(comp, vm, initArgs);
	}
	
	/**
	 * 
	 * @since 6.0.2
	 */
	public void initAnnotatedBindings(){
		new AnnotateBinderHelper(this).initComponentBindings(this.getView());
		//mark this component was handled by binder after init
		this.getView().setAttribute(BINDER, this);
	}
}
