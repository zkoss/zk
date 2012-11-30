/* BindComposer.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 10:09:50 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.AnnotationUtil;
import org.zkoss.bind.impl.BindEvaluatorXUtil;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.ValidationMessagesImpl;
import org.zkoss.bind.impl.AbstractAnnotatedMethodInvoker;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.lang.Strings;
import org.zkoss.util.CacheMap;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.ConventionWires;

/**
 * Base composer to apply ZK Bind.
 * @author henrichen
 * @since 6.0.0
 */
@SuppressWarnings("rawtypes")
public class BindComposer<T extends Component> implements Composer<T>, ComposerExt<T>, Serializable {
	
	private static final long serialVersionUID = 1463169907348730644L;
	
	private static final Log _log = Log.lookup(BindComposer.class);
	
	private static final String VM_ID = "$VM_ID$";
	private static final String BINDER_ID = "$BINDER_ID$";
	
	private Object _viewModel;
	private AnnotateBinder _binder;
	
	private final Map<String, Converter> _converters;
	private final Map<String, Validator> _validators;
	private final BindEvaluatorX evalx;
	
	private static final String ID_ANNO = "id";
	private static final String INIT_ANNO = "init";
	
	private static final String VALUE_ANNO_ATTR = "value";
	
//	private static final String COMPOSER_NAME_ATTR = "composerName";
	private static final String VIEW_MODEL_ATTR = "viewModel";
	private static final String BINDER_ATTR = "binder";
	private static final String VALIDATION_MESSAGES_ATTR = "validationMessages";
	
	private static final String QUEUE_NAME_ANNO_ATTR = "queueName";
	private static final String QUEUE_SCOPE_ANNO_ATTR = "queueScope";
	
	public BindComposer() {
		setViewModel(this);
		_converters = new HashMap<String, Converter>(8);
		_validators = new HashMap<String, Validator>(8);
		evalx = BindEvaluatorXUtil.createEvaluator(null);
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
	
	//--ComposerExt//
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) throws Exception {
		return compInfo;
	}



	public void doBeforeComposeChildren(Component comp) throws Exception {
		//init viewmodel first
		_viewModel = initViewModel(evalx, comp);
		_binder = initBinder(evalx, comp);
		ValidationMessages _vmsgs = initValidationMessages(evalx, comp, _binder);
		
		//wire before call init
		Selectors.wireVariables(comp, _viewModel, Selectors.newVariableResolvers(_viewModel.getClass(), null));
		if(_vmsgs!=null){
			_binder.setValidationMessages(_vmsgs);
		}
		
		BinderKeeper keeper = BinderKeeper.getInstance(comp);
		keeper.book(_binder, comp);
	
		_binder.init(comp, _viewModel, getViewModelInitArgs(evalx,comp));

		//to apply composer-name
		ConventionWires.wireController(comp, this);
	}
	
	//--Composer--//
	public void doAfterCompose(T comp) throws Exception {
		_binder.initAnnotatedBindings();
		
		// trigger ViewModel's @AfterCompose method.
		new AbstractAnnotatedMethodInvoker<AfterCompose>(AfterCompose.class){
			protected boolean shouldLookupSuperclass(AfterCompose annotation) {
				return annotation.superclass();
			}}.invokeMethod(_binder, getViewModelInitArgs(evalx,comp));
			
		// call loadComponent
		BinderKeeper keeper = BinderKeeper.getInstance(comp);
		if(keeper.isRootBinder(_binder)){
			keeper.loadComponentForAllBinders();
		}
	}
	
	private Map<String, Object> getViewModelInitArgs(BindEvaluatorX evalx,Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Collection<Annotation> anncol = compCtrl.getAnnotations(VIEW_MODEL_ATTR, INIT_ANNO);
		if(anncol.size()==0) return null;
		final Annotation ann = anncol.iterator().next();
		
		final Map<String,String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
		Map<String, String[]> args = null;

		for (final Iterator<Entry<String,String[]>> it = attrs.entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				//ignore
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		return args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
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
		
		vmname = BindEvaluatorXUtil.eval(evalx,comp,AnnotationUtil.testString(idanno.getAttributeValues(VALUE_ANNO_ATTR),
				comp,VALUE_ANNO_ATTR,ID_ANNO),String.class);
		vm = BindEvaluatorXUtil.eval(evalx,comp,AnnotationUtil.testString(initanno.getAttributeValues(VALUE_ANNO_ATTR),
				comp,VALUE_ANNO_ATTR,INIT_ANNO),Object.class);
		
		if(Strings.isEmpty(vmname)){
			throw new UiException("name of view model is empty");
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
		comp.setAttribute(VM_ID, vmname);
		
		return vm;
	}
	
	private AnnotateBinder initBinder(BindEvaluatorX evalx, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(BINDER_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(BINDER_ATTR, INIT_ANNO);
		Object binder = null;
		String bname = null;
		
		if(idanno!=null){
			bname = BindEvaluatorXUtil.eval(evalx, 
					comp,
					AnnotationUtil.testString(
						idanno.getAttributeValues(VALUE_ANNO_ATTR), comp, VALUE_ANNO_ATTR, ID_ANNO), 
						String.class);
		}else{
			bname = "binder";
		}
		if(Strings.isEmpty(bname)){
			throw new UiException("name of binder is empty");
		}
		
		if(initanno!=null){
			binder = AnnotationUtil.testString(initanno.getAttributeValues(VALUE_ANNO_ATTR),
					comp,VALUE_ANNO_ATTR,INIT_ANNO);
			String name = AnnotationUtil.testString(initanno.getAttributeValues(QUEUE_NAME_ANNO_ATTR),
					comp,QUEUE_NAME_ANNO_ATTR,INIT_ANNO);
			String scope = AnnotationUtil.testString(initanno.getAttributeValues(QUEUE_SCOPE_ANNO_ATTR),
					comp,QUEUE_SCOPE_ANNO_ATTR,INIT_ANNO);
			if(binder!=null){
				if(name!=null){
					_log.warning(QUEUE_NAME_ANNO_ATTR +" is not available if you use custom binder");
				}
				if(scope!=null){
					_log.warning(QUEUE_SCOPE_ANNO_ATTR +" is not available if you use custom binder");
				}
				
				binder = BindEvaluatorXUtil.eval(evalx,comp,(String)binder,Object.class);
				try {
					if(binder instanceof String){
						binder = comp.getPage().resolveClass((String)binder);
					}
					if(binder instanceof Class<?>){
						binder = ((Class<?>)binder).newInstance();
					}
				} catch (Exception e) {
					throw new UiException(e.getMessage(),e);
				}
				if(!(binder instanceof AnnotateBinder)){
					throw new UiException("evaluated binder is not a binder is "+binder);
				}
				
			}else {
				//no binder, create default binder with custom queue name and scope
				if(name!=null){
					name = BindEvaluatorXUtil.eval(evalx,comp,name,String.class);
					if(name==null){
						_log.warning("evaluated queue name is null, use default name. expression is "+name);
					}
				}
				if(scope!=null){
					scope = BindEvaluatorXUtil.eval(evalx,comp,scope,String.class);
					if(scope==null){
						_log.warning("evaluated queue scope is null, use default scope. expression is "+scope);
					}
				}
				binder = new AnnotateBinder(name,scope);
			}
		}else{
			binder = new AnnotateBinder();
		}
		
		//put to attribute, so binder could be referred by the name
		comp.setAttribute(bname, binder);
		comp.setAttribute(BINDER_ID, bname);
		
		return (AnnotateBinder)binder;
	}
	
	private ValidationMessages initValidationMessages(BindEvaluatorX evalx, Component comp,Binder binder) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(VALIDATION_MESSAGES_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(VALIDATION_MESSAGES_ATTR, INIT_ANNO);
		Object vmessages = null;
		String vname = null;
		
		if(idanno!=null){
			vname = BindEvaluatorXUtil.eval(evalx,comp,AnnotationUtil.testString(idanno.getAttributeValues(VALUE_ANNO_ATTR),
					comp,VALUE_ANNO_ATTR,ID_ANNO),String.class);
		}else{
			return null;//validation messages is default null
		}
		if(Strings.isEmpty(vname)){
			throw new UiException("name of ValidationMessages is empty");
		}
		
		if(initanno!=null){
			vmessages = BindEvaluatorXUtil.eval(evalx,comp,AnnotationUtil.testString(initanno.getAttributeValues(VALUE_ANNO_ATTR),
					comp,VALUE_ANNO_ATTR,INIT_ANNO),Object.class);
			try {
				if(vmessages instanceof String){
					vmessages = comp.getPage().resolveClass((String)vmessages);
				}
				if(vmessages instanceof Class<?>){
					vmessages = ((Class<?>)vmessages).newInstance();
				}
			} catch (Exception e) {
				throw new UiException(e.getMessage(),e);
			}
			if(!(vmessages instanceof ValidationMessages)){
				throw new UiException("evaluated validationMessages is not a ValidationMessages is "+vmessages);
			}
		}else{
			vmessages = new ValidationMessagesImpl();
		}
		
		//put to attribute, so binder could be referred by the name
		comp.setAttribute(vname, vmessages);
		
		return (ValidationMessages)vmessages;
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
	

	/**
	 * 
	 * <p>A parsing scope context for storing Binders, and handle there loadComponent 
	 * invocation properly.</p> 
	 * 
	 * <p>if component trees with bindings are totally separated( none of
	 * each contains another), then for each separated tree, there's only one keeper.</p>
	 * 
	 * @author Ian Y.T Tsai(zanyking)
	 */
	private static class BinderKeeper{
		private static final String KEY_BINDER_KEEPER = "$BinderKeeper$"; 

		/**
		 * get a Binder Keeper or create it by demand.
		 * @param comp
		 * @return
		 */
		static BinderKeeper getInstance(Component comp){
			BinderKeeper keeper = 
				(BinderKeeper) comp.getAttribute(KEY_BINDER_KEEPER, true);
			if(keeper == null){
				comp.setAttribute(KEY_BINDER_KEEPER, 
						keeper = new BinderKeeper(comp));
			}
			return keeper;
		}

		private final LinkedList<Loader> _queue;
		private Component _host;
		
		public BinderKeeper(final Component comp) {
			_host = comp;
			_queue = new LinkedList<Loader>();
			// ensure the keeper will always cleaned up
			Events.postEvent("onRootBinderHostDone", comp, null);
			comp.addEventListener("onRootBinderHostDone", new EventListener<Event>(){
				public void onEvent(Event event) throws Exception {
					//suicide first...
					_host.removeEventListener("onRootBinderHostDone", this);
					BinderKeeper keeper = 
						(BinderKeeper) _host.getAttribute(KEY_BINDER_KEEPER);
					if(keeper==null){
						// suppose to be null...
					}else{
						// The App is in trouble.
						// some error might happened during page processing 
						// which cause loadComponent() never invoked.
						_host.removeAttribute(KEY_BINDER_KEEPER);
					}
				}
			});
		}
		
		public void book(Binder binder, Component comp) {
			_queue.add(new Loader(binder, comp));
		}
		
		public boolean isRootBinder(Binder binder){
			return _queue.getFirst().binder == binder; 
		}
		
		public void loadComponentForAllBinders(){
			_host.removeAttribute(KEY_BINDER_KEEPER);
			for(Loader loader : _queue){
				loader.load();
			}
		}
		
		/**
		 * for Binder to load Component.
		 * @author Ian Y.T Tsai(zanyking)
		 */
		private static class Loader{
			Binder binder;
			Component comp;
			public Loader(Binder _binder, Component comp) {
				super();
				this.binder = _binder;
				this.comp = comp;
			}
			public void load(){
				//load data
				binder.loadComponent(comp, true);//load all bindings
			}
		}//end of class...
	}//end of class...
	
}//end of class...

