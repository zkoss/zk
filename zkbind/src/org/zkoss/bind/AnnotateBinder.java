/* AnnotateBinder.java

	Purpose:
		
	Description:
		
	History:
		Aug 4, 2011 10:56:14 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * ZK Annotated Binder, it parse the component annotation and handles the binding of components and view model.
 * @author henrichen
 * @author dennischen
 * @since 6.0.0
 */
public class AnnotateBinder extends BinderImpl {
	private static final long serialVersionUID = 1463169907348730644L;
	private boolean _initBindings;
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
	 *  {@inheritDoc}
	 * <b>since 6.0.2</b>, this method will take care of super's {@link Binder#init(Component, Object)} only<br>
	 * If {@link AnnotateBinder} need to be used manually, {@link #initAnnotatedBindings()} need to be invoked for scanning and building binding syntax tree.
	 *  @see #initAnnotatedBindings()  
	 */
	public void init(Component comp, Object vm, Map<String, Object> initArgs){
		super.init(comp, vm, initArgs);
	}
	
	
	/**
	 * This method will parse Zul component's annotation that user declared and call {@link Binder}'s addBindings series methods 
	 * to initiate binder's internal binding syntax trees, which then will cooperate with context(for look up variables) and be used by Binder's EL engine 
	 * while {@link #loadComponent(Component, boolean)} or {@link Command} been triggered.<br>
	 *   
	 * @since 6.0.2
	 */
	public void initAnnotatedBindings(){
		checkInit();
		if(_initBindings ) throw new UiException("this method can be called only once.");
		_initBindings = true;
		
		new AnnotateBinderHelper(this).initComponentBindings(this.getView());
		//mark this component was handled by binder after init
		BinderUtil.markHandling(this.getView(), this);
	}
}
