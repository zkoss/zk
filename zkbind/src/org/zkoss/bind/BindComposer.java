/* BindComposer.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 10:09:50 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.bind.impl.AnnotateBinderImpl;
import org.zkoss.bind.impl.BindEvaluatorXImpl;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.lang.Strings;
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
	
	private static final String BIND_ANNO = "bind";
	private static final String VIEW_MODEL_ATTR = "viewModel";
	private static final String COMPOSER_NAME_ATTR = "composerName";
	private static final String QUEUE_NAME_ATTR = "queueName";
	private static final String QUEUE_SCOPE_ATTR = "queueScope";
	private static final String BINDER_NAME_ATTR = "binderName";
	
	
	
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
		
		//name this composer
		String cname = (String)comp.getAttribute(COMPOSER_NAME_ATTR);
		if(cname==null){
			cname = getAnnotatedBindString(evalx, comp,COMPOSER_NAME_ATTR);
		}
		
		comp.setAttribute(cname != null ? cname : comp.getId()+"$composer", this);
		
		//init the binder
		final String qname = getAnnotatedBindString(evalx, comp,QUEUE_NAME_ATTR);
		final String qscope = getAnnotatedBindString(evalx, comp,QUEUE_SCOPE_ATTR);
		
		_viewModel = initViewModel(evalx, comp, VIEW_MODEL_ATTR);

		_binder = new AnnotateBinderImpl(comp, _viewModel, qname, qscope);
		//assign binder name
		final String bname = getAnnotatedBindString(evalx, comp,BINDER_NAME_ATTR);
		comp.setAttribute(BinderImpl.BINDER, _binder);
		comp.setAttribute(bname != null ? bname : "binder", _binder);
		
		//load data
		((BinderImpl)_binder).loadComponent(comp); //load all bindings
	}
	
	private Object initViewModel(BindEvaluatorX evalx, Component comp,
			String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation anno = compCtrl.getAnnotation(attr,BIND_ANNO);
		if(anno!=null){
			String vmname = null;
			Object vm = null;
			for (final Iterator<?> it = anno.getAttributes().entrySet().iterator(); it.hasNext();) {
				final Map.Entry<?,?> entry = (Map.Entry<?,?>) it.next();
				final String tag = (String) entry.getKey();
				final Object tagExpr = entry.getValue();
				if(vmname!=null){
					throw new UiException("alreay has a view model["+vmname+","+vm+"] for this component "+comp);
				}
				vmname = tag;
				vm = eval(evalx,comp,(String)tagExpr,Object.class);
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
				if(vm !=null && vm.getClass().isPrimitive()){
					throw new UiException("view model '"+vmname+"' is a primitive type "+vm);
				}
			}
			
			if(vm == null){
				throw new UiException("view model '"+vmname+"' is null");
			}
			comp.setAttribute(vmname, vm);
			return vm;
		}else{
			return _viewModel;
		}
	}

	private static String getAnnotatedBindString(BindEvaluatorX evalx,Component comp,String attr){
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation anno = compCtrl.getAnnotation(attr,BIND_ANNO);
		String value = null;
		if(anno!=null){
			value = anno.getAttribute("value");
			if(!Strings.isBlank(value)){
				return eval(evalx,comp,value,String.class);
			}
			throw new NullPointerException("bind value of "+attr+" return null");
		}
		return value;
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
