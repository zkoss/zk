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
	 *  in {@link AnnotateBinder}, this method will do component annotation bindings parsing also.<br> 
	 *  it's equivalent to call {@link #initViewModel(Component, Object, Map)} plus {@link #initAnnotatedBindings()}.
	 *  @see #initViewModel(Component, Object, Map)
	 *  @see #initAnnotatedBindings()  
	 */
	public void init(Component comp, Object vm, Map<String, Object> initArgs){
		initViewModel(comp, vm, initArgs);
		initAnnotatedBindings();
	}
	
	/**
	 * this method will call super's {@link Binder#init(Component, Object)} only, this is a compromise 
	 * to keep the original specification of {@link AnnotateBinder#init(Component, Object, Map)}   
	 * @param comp host component of this binder
	 * @param vm View model instance
	 * @param initArgs init arguments
	 * @since 6.0.2
	 */
	public void initViewModel(Component comp, Object vm, Map<String, Object> initArgs){
		super.init(comp, vm, initArgs);
	}
	
	/**
	 * Do component zul annotation parsing and build binding relationship tree for further data bind evaluating works.  
	 * @since 6.0.2
	 */
	public void initAnnotatedBindings(){
		if(!_init ) throw new UiException("initViewModel need to be called before invoking this method");
		if(_initBindings ) throw new UiException("this method can be called only once.");
		_initBindings = true;
		
		new AnnotateBinderHelper(this).initComponentBindings(this.getView());
		//mark this component was handled by binder after init
		this.getView().setAttribute(BINDER, this);
	}
}
