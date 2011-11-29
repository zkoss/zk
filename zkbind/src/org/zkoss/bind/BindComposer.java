/* BindComposer.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 10:09:50 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.HashMap;
import java.util.Map;

//import org.zkoss.bind.impl.AnnotateBinderImpl;
import org.zkoss.bind.impl.AnnotateBinderImpl;
import org.zkoss.bind.impl.BindEvaluatorXImpl;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;

/**
 * Base composer to apply ZK Bind.
 * @author henrichen
 *
 */
public class BindComposer<T extends Component> implements Composer<T>, ComposerExt {
	private Object _viewModel;
	private Binder _binder;
	private final Map<String, Converter> _converters;
	private final Map<String, Validator> _validators;
	
	private static final String ID_ANNO = "id";
	private static final String INIT_ANNO = "init";
	
	private static final String COMPOSER_NAME_ATTR = "composerName";
	private static final String VIEW_MODEL_ATTR = "viewModel";
	private static final String BINDER_ATTR = "binder";
	
	
	
	public BindComposer() {
		setViewModel(this);
		_converters = new HashMap<String, Converter>(8);
		_validators = new HashMap<String, Validator>(8);
	}
	
	public Binder getBinder() {
		return _binder;
	}
	
	//can assign a separate view model, default to this
	public void setViewModel(Object viewModel) {
		_viewModel = viewModel;
		if (this._binder != null) {
			this._binder.setViewModel(_viewModel);
		}
	}
	
	public Object getViewModel() {
		return _viewModel;
	}
	
	public Converter getConverter(String name) {
		Converter conv = _converters.get(name);
		return conv;
	}
	
	public Validator getValidator(String name) {
		Validator validator = _validators.get(name);
		return validator;
	}
	
	public void addConverter(String name, Converter converter) {
		_converters.put(name, converter);
	}
	
	public void addValidator(String name, Validator validator) {
		_validators.put(name, validator);
	}

	//--Composer--//
	public void doAfterCompose(T comp) throws Exception {
		BindEvaluatorX evalx = new BindEvaluatorXImpl(null, org.zkoss.bind.xel.BindXelFactory.class);
		
		//name of this composer
		String cname = (String)comp.getAttribute(COMPOSER_NAME_ATTR);
		comp.setAttribute(cname != null ? cname : comp.getId()+"$composer", this);
		
		//init viewmodel first
		_viewModel = initViewModel(evalx, comp);
		_binder = initBinder(evalx, comp);
		//load data
		((BinderImpl)_binder).loadComponent(comp); //load all bindings
	}
	
	private Object initViewModel(BindEvaluatorX evalx, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(VIEW_MODEL_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(VIEW_MODEL_ATTR, INIT_ANNO);
		String vmname = null;
		Object vm = null;
		
		if(idanno==null && initanno==null){
			return _viewModel;
		}else if(idanno==null){
			throw new IllegalSyntaxException("you have to use @id to assign the name of view model for "+comp);
		}else if(initanno==null){
			throw new IllegalSyntaxException("you have to use @init to assign the view model for "+comp);
		}
		
		vmname = eval(evalx,comp,idanno.getAttribute("value"),String.class);
		vm = eval(evalx,comp,initanno.getAttribute("value"),Object.class);
		
		if(vmname==null){
			throw new UiException("name of view model is null");
		}
		
		try {
			if(vm instanceof String){
				vm = comp.getPage().resolveClass((String)vm);
			}
			if(vm instanceof Class<?>){
				vm = ((Class<?>)vm).newInstance();
			}
		} catch (Exception e) {
			throw new UiException(e.getMessage(),e);
		}
		if(vm == null){
			throw new UiException("view model of '"+vmname+"' is null");
		}else if(vm.getClass().isPrimitive()){
			throw new UiException("view model '"+vmname+"' is a primitive type, is "+vm);
		}
		comp.setAttribute(vmname, vm);
		
		return vm;
	}
	
	private Binder initBinder(BindEvaluatorX evalx, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(BINDER_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(BINDER_ATTR, INIT_ANNO);
		Binder binder = null;
		String bname = null;
		
		if(idanno!=null){
			bname = eval(evalx,comp,idanno.getAttribute("value"),String.class);
		}else{
			bname = "binder";
		}
		if(bname==null){
			throw new UiException("name of binder is null");
		}
		
		if(initanno!=null){
			binder = eval(evalx,comp,initanno.getAttribute("value"),Binder.class);
		}else{
			binder = new AnnotateBinderImpl(comp, _viewModel, null, null);
		}
		if(binder == null){
			throw new UiException("binder of '"+bname+"' is null");
		}
		
		comp.setAttribute(BinderImpl.BINDER, binder);
		comp.setAttribute(bname, binder);
		
		return binder;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T eval(BindEvaluatorX evalx, Component comp, String expression, Class<T> expectedType){
		ExpressionX expr = evalx.parseExpressionX(null, expression, expectedType);
		Object obj = evalx.getValue(null, comp, expr);
		return (T)obj;
	}
	
	//--ComposerExt//
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) throws Exception {
		return compInfo;
	}

	public void doBeforeComposeChildren(Component comp) throws Exception {
	}

	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}

	public void doFinally() throws Exception {
		// ignore
	}
	
	//--notifyChange--//
	public void notifyChange(Object bean, String property) {
		getBinder().notifyChange(bean, property);
	}
}
