/* BinderImpl.java

	Purpose:
		
	Description:
		
	History:
		Jul 29, 2011 6:08:51 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormExt;
import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.Property;
import org.zkoss.bind.PropertyChangeEvent;
import org.zkoss.bind.SimpleForm;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DefaultCommand;
import org.zkoss.bind.annotation.DefaultGlobalCommand;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.CommandBinding;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollectorFactory;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.tracker.impl.TrackerImpl;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.CacheMap;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ComponentActivationListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ExecutionInit;

/**
 * Implementation of Binder.
 * @author henrichen
 * @author dennischen
 * @since 6.0.0
 */
public class BinderImpl implements Binder,BinderCtrl,Serializable{

	private static final long serialVersionUID = 1463169907348730644L;
	
	private static final Log _log = Log.lookup(BinderImpl.class);
	
	private static final Map<String, Object> RENDERERS = new HashMap<String, Object>();
	
	//control keys
	public static final String BINDING = "$BINDING$"; //a binding
	public static final String BINDER = "$BINDER$"; //the binder
	public static final String BINDCTX = "$BINDCTX$"; //bind context
	public static final String VAR = "$VAR$"; //variable name in a collection
	public static final String VM = "$VM$"; //the associated view model
	public static final String NOTIFYS = "$NOTIFYS$"; //changed properties to be notified
	public static final String VALIDATES = "$VALIDATES$"; //properties to be validated
	public static final String SRCPATH = "$SRCPATH$"; //source path that trigger @DependsOn tracking
	public static final String DEPENDS_ON_COMP = "$DEPENDS_ON_COMP"; //dependsOn component
	public static final String RENDERER_INSTALLED = "$RENDERER_INSTALLED$";
	
	public static final String LOAD_FORM_EXPRESSION = "$LOAD_FORM_EXPR$";//The attribute name of a loaded bean class, internal use only
	public static final String LOAD_FORM_COMPONENT = "$LOAD_FORM_COMP$";//The attribute name of a loaded bean class, internal use only
	
	public static final String IGNORE_TRACKER = "$IGNORE_TRACKER$"; //ignore adding currently binding to tracker, ex in init
	
	public static final String IGNORE_REF_VALUE = "$IGNORE_REF_VALUE$"; //ignore getting nested value form ref-binding when doing el evaluation.

	public static final String SAVE_BASE = "$SAVE_BASE$"; //bean base of a save operation
	public static final String ON_BIND_INIT = "onBindInit"; //do component binding initialization
	public static final String MODEL = "$MODEL$"; //collection model for index tracking
	
	//events for dummy target
	private static final String ON_POST_COMMAND = "onPostCommand";
	private static final String ON_VMSGS_CHANGED = "onVMsgsChanged";
	
	//private control key
	private static final String FORM_ID = "$FORM_ID$";
	private static final String CHILDREN_ATTR = "$CHILDREN$";
	private static final String ACTIVATOR = "$ACTIVATOR$";//the activator that is stored in root comp
	
	//Command lifecycle result
	private static final int COMMAND_SUCCESS = 0;
	private static final int COMMAND_FAIL_VALIDATE = 1;
	
	
	//TODO make it configurable
	private final static Map<Class<?>, List<Method>> _initMethodCache = 
		new CacheMap<Class<?>, List<Method>>(600,CacheMap.DEFAULT_LIFETIME); //class,list<init method>

	private final static Map<Class<?>, Map<String,CachedItem<Method>>> _commandMethodCache = 
		new CacheMap<Class<?>, Map<String,CachedItem<Method>>>(200,CacheMap.DEFAULT_LIFETIME); //class,map<command, null-able command method>
	
	private final static Map<Class<?>, Map<String,CachedItem<Method>>> _globalCommandMethodCache = 
		new CacheMap<Class<?>, Map<String,CachedItem<Method>>>(200,CacheMap.DEFAULT_LIFETIME); //class,map<command, null-able command method>
	
	//command and default command method parsing and caching 
	private static final CachedItem<Method> NULL_METHOD = new CachedItem<Method>(null);
	private static final String COMMAND_METHOD_MAP_INIT = "$INIT_FLAG$";
	private static final String COMMAND_METHOD_DEFAULT = "$DEFAULT_FLAG$";
	private static final CommandMethodInfoProvider _commandMethodInfoProvider = new CommandMethodInfoProvider() {
		public String getAnnotationName() {
			return Command.class.getSimpleName();
		}
		public String getDefaultAnnotationName() {
			return DefaultCommand.class.getSimpleName();
		}
		public String[] getCommandName(Method method) {
			final Command cmd = method.getAnnotation(Command.class);
			return cmd==null?null:cmd.value();			
		}
		public boolean isDefaultMethod(Method method) {
			return method.getAnnotation(DefaultCommand.class)!=null;
		}
	};
	private static final CommandMethodInfoProvider _globalCommandMethodInfoProvider = new CommandMethodInfoProvider() {
		public String getAnnotationName() {
			return GlobalCommand.class.getSimpleName();
		}
		public String getDefaultAnnotationName() {
			return DefaultGlobalCommand.class.getSimpleName();
		}
		public String[] getCommandName(Method method) {
			final GlobalCommand cmd = method.getAnnotation(GlobalCommand.class);
			return cmd==null?null:cmd.value();			
		}
		public boolean isDefaultMethod(Method method) {
			return method.getAnnotation(DefaultGlobalCommand.class)!=null;
		}
	};
	
	
	private Component _rootComp;
	private BindEvaluatorX _eval;
	private PhaseListener _phaseListener;
	private Tracker _tracker;
	private final Component _dummyTarget = new AbstractComponent();//a dummy target for post command
	
	/* holds all binding in this binder */
	private final Map<Component, Map<String, List<Binding>>> _bindings; //comp -> (evtnm | _fieldExpr | formid) -> bindings

	private final FormBindingHandler _formBindingHandler;
	private final PropertyBindingHandler _propertyBindingHandler;
	private final ChildrenBindingHandler _childrenBindingHandler;
	private final ReferenceBindingHandler _refBindingHandler;
	
	/* the relation of form and inner save-bindings */
	private Map<Component, Set<SaveBinding>> _assocFormSaveBindings;//form comp -> savebindings	
	private Map<Component, Map<SaveBinding,Set<SaveBinding>>> _reversedAssocFormSaveBindings;////associated comp -> binding -> associated save bindings of _formSaveBindingMap
	

	private final Map<BindingKey, CommandEventListener> _listenerMap; //comp+evtnm -> eventlistener
	private final String _quename;
	private final String _quescope;
	private final QueueListener _queueListener;
	
	private ValidationMessages _validationMessages;
	private Set<BindingKey> _hasValidators;//the key to mark they have validator
	
	private final Map<Component, Map<String,TemplateResolver>> _templateResolvers;//comp,<attr,resolver>
	
	//flag to keep info of current vm has converter method or not
	private boolean _hasGetConverterMethod = true;
	
	//flag to keep info of current vm has validator method or not
	private boolean _hasGetValidatorMethod = true;

	//flag to keep info of the binding is initialized or not.
	private boolean _init = false;
	
	//flag to keep info that binder is in activating state
	private boolean _activating = false;
	//to help deferred activation when first execution
	private transient DeferredActivator _deferredActivator;
	
	private final ImplicitObjectContributor _implicitContributor;
	
	private static final String REF_HANDLER_CLASS_PROP = "org.zkoss.bind.ReferenceBindingHandler.class";
	
	public BinderImpl() {
		this(null,null);
	}
	
	public BinderImpl(String qname, String qscope) {
		_bindings = new HashMap<Component, Map<String, List<Binding>>>();
		_formBindingHandler = new FormBindingHandler(); 
		_propertyBindingHandler = new PropertyBindingHandler();
		_childrenBindingHandler = new ChildrenBindingHandler();
		
		_formBindingHandler.setBinder(this);
		_propertyBindingHandler.setBinder(this);
		_childrenBindingHandler.setBinder(this);
		
		_refBindingHandler = MiscUtil.newInstanceFromProperty(REF_HANDLER_CLASS_PROP, null, ReferenceBindingHandler.class);
		if(_refBindingHandler!=null){
			_refBindingHandler.setBinder(this);
		}
		
		//zk-1548
		_implicitContributor = new ImplicitObjectContributorImpl();
		

		_assocFormSaveBindings = new HashMap<Component, Set<SaveBinding>>();
		_reversedAssocFormSaveBindings = new HashMap<Component, Map<SaveBinding,Set<SaveBinding>>>();
		
		_hasValidators = new HashSet<BindingKey>();
		_templateResolvers = new HashMap<Component,Map<String,TemplateResolver>>();
		_listenerMap = new HashMap<BindingKey, CommandEventListener>();
		//use same queue name if user was not specified, 
		//this means, binder in same scope, same queue, they will share the notification by "base"."property" 
		_quename = qname != null && !Strings.isEmpty(qname) ? qname : DEFAULT_QUEUE_NAME;
		_quescope = qscope != null && !Strings.isBlank(qscope) ? qscope : DEFAULT_QUEUE_SCOPE;
		_queueListener = new QueueListener();
	}
	
	private class QueueListener implements EventListener<Event>,Serializable{
		private static final long serialVersionUID = 1L;

		public void onEvent(Event event) throws Exception {
			//only when a event in queue is our event
			if(event instanceof PropertyChangeEvent){
				final PropertyChangeEvent evt = (PropertyChangeEvent) event;
				BinderImpl.this.loadOnPropertyChange(evt.getBase(), evt.getProperty());
			}else if(event instanceof GlobalCommandEvent){
				final GlobalCommandEvent evt = (GlobalCommandEvent) event;
				final Set<Property> notifys = new LinkedHashSet<Property>();
				BinderImpl.this.doGlobalCommand(_rootComp, evt.getCommand(), evt.getArgs(), notifys);
				fireNotifyChanges(notifys);
				notifyVMsgsChanged();
			}
		}
	}
	
	protected void checkInit(){
		if(!_init){
			throw new UiException("binder is not initialized yet");
		}
	}

	/**
	 * @deprecated use {@link #init(Component, Object, Map)} instead
	 */
	public void init(Component comp, Object viewModel){
		init(comp,viewModel,null);
	}
	
	/**
	 * 
	 * 
	 * 
	 * @since 6.0.1
	 */
	public void init(Component comp, Object viewModel, Map<String, Object> initArgs){
		if(_init) throw new UiException("binder is already initialized");
		_init = true;
		
		_rootComp = comp;
		//initial associated view model
		setViewModel(viewModel);
		_dummyTarget.addEventListener(ON_POST_COMMAND, new PostCommandListener());
		_dummyTarget.addEventListener(ON_VMSGS_CHANGED, new VMsgsChangedListener());
		//subscribe queue 
		subscribeQueue(_quename, _quescope, _queueListener);
		
		new AbstractAnnotatedMethodInvoker<Init>(Init.class, _initMethodCache){
			protected boolean shouldLookupSuperclass(Init annotation) {
				return annotation.superclass();
			}}.invokeMethod(this, initArgs);
		
		_rootComp.setAttribute(ACTIVATOR, new Activator());//keep only one instance in root comp
	}
	
	
	//called when onPropertyChange is fired to the subscribed event queue
	private void loadOnPropertyChange(Object base, String prop) {
		if(_log.debugable()){
			_log.debug("loadOnPropertyChange:base=[%s],prop=[%s]",base,prop);
		}
		
		//zk-1468, 
		//ignore a coming ref-binding if the binder is the same since it was loaded already.
		if(base instanceof ReferenceBinding && ((ReferenceBinding)base).getBinder()==this){
			return;
		}
		
		final Tracker tracker = getTracker();
		final Set<LoadBinding> bindings = tracker.getLoadBindings(base, prop);
		BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		try{
			if(collector != null){
				collector.pushStack("NOTIFY_CHANGE");
				collector.addNotifyInfo("notify-change", base, prop,"Size="+bindings.size());
			}
			loadOnPropertyChange0(base, prop, bindings);
		}finally{
			if(collector != null){
				collector.popStack();
			}
		}
	}
	private void loadOnPropertyChange0(Object base, String prop,Set<LoadBinding> bindings) {
		for(LoadBinding binding : bindings) {
			//BUG 828, the sub-sequence binding might be removed after the previous loading.
			final Component comp = binding.getComponent();
			if(comp==null || comp.getPage()==null) continue;
			
			final BindContext ctx = BindContextUtil.newBindContext(this, binding, false, null, comp, null);
			if(binding instanceof PropertyBinding){
				BindContextUtil.setConverterArgs(this, comp, ctx, (PropertyBinding)binding);
			}
			
			try { 
				if(_log.debugable()){
					_log.debug("loadOnPropertyChange:binding.load(),binding=[%s],context=[%s]",binding,ctx);
				}
				doPrePhase(Phase.LOAD_BINDING, ctx);
				binding.load(ctx);
			} finally {
				doPostPhase(Phase.LOAD_BINDING, ctx);
			}
			
			
			//zk-1468, 
			//notify the ref-binding changed since other nested binder might use it
			if(binding instanceof ReferenceBinding && binding!=base){
				notifyChange(binding,".");
			}
			
			if(_validationMessages!=null){
				String attr = null;
				if(binding instanceof PropertyBinding){
					attr = ((PropertyBinding)binding).getFieldName();
				}else if(binding instanceof FormBinding){
					attr = ((FormBinding)binding).getFormId();
				}else{
					//ignore children binding
				}
				if(attr!=null && hasValidator(comp, attr)){
					_validationMessages.clearMessages(comp,attr);
				}
			}
		}
	}
	
	public void setViewModel(Object vm) {
		checkInit();
		_rootComp.setAttribute(BinderImpl.VM, vm);
		_hasGetConverterMethod = true;//reset to true
		_hasGetValidatorMethod = true;//reset to true
	}
	
	public Object getViewModel() {
		checkInit();
		return _rootComp.getAttribute(BinderImpl.VM);
	}
	
	//Note: assume system converter is state-less
	public Converter getConverter(String name) {
		checkInit();
		Converter converter = null;
		if(_hasGetConverterMethod){
			final BindEvaluatorX eval = getEvaluatorX();
			final ExpressionX vmc = eval.parseExpressionX(null, 
				new StringBuilder().append(BinderImpl.VM).append(".getConverter('").append(name).append("')").toString(),
				Converter.class);
			try{
				converter = (Converter)eval.getValue(null, _rootComp, vmc);
			}catch(org.zkoss.zel.MethodNotFoundException x){
				_hasGetConverterMethod = false;
			}
		}
		if(converter == null){
			converter = SystemConverters.get(name);
		}
		if (converter == null) {
			throw new UiException("Cannot find converter:" + name);
		}
		return converter;
	}
	
	//Note: assume system validator is state-less
	public Validator getValidator(String name) {
		checkInit();
		Validator validator = null;
		if(_hasGetValidatorMethod){
			final BindEvaluatorX eval = getEvaluatorX();
			final ExpressionX vmv = eval.parseExpressionX(null, 
				new StringBuilder().append(BinderImpl.VM).append(".getValidator('").append(name).append("')").toString(),
				Validator.class);
			try{
				validator = (Validator)eval.getValue(null, _rootComp, vmv);
			}catch(org.zkoss.zel.MethodNotFoundException x){
				_hasGetValidatorMethod = false;
			}
		}
		if(validator == null){
			validator = SystemValidators.get(name);
		}
		if (validator == null) {
			throw new UiException("Cannot find validator:" + name);
		}
		return validator;
	}
	
	//Note: assume system renderer is state-less 
	protected Object getRenderer(String name) {
		Object renderer = RENDERERS.get(name);
		if (renderer == null && name.indexOf('.') > 0) { //might be a class path
			try {
				renderer = Classes.newInstanceByThread(name);
				RENDERERS.put(name, renderer); //assume renderer is state-less
			} catch (IllegalAccessException e) {
				throw UiException.Aide.wrap(e);
			} catch (Exception e) {
				//ignore
			}
		}
		return renderer;
	}

	public BindEvaluatorX getEvaluatorX() {
		if (_eval == null) {
			_eval = new BindEvaluatorXImpl(null, org.zkoss.bind.xel.BindXelFactory.class);
		}
		return _eval;
	}
	
	public void storeForm(Component comp,String id, Form form){
		final String oldid = (String)comp.getAttribute(FORM_ID, Component.COMPONENT_SCOPE);
		//check if a form exist already, allow to store a form with same id again for replacing the form 
		if(oldid!=null && !oldid.equals(id)){
			throw new IllegalArgumentException("try to store 2 forms in same component id : 1st "+oldid+", 2nd "+id);
		}
		final Form oldForm = (Form)comp.getAttribute(id);
		
		if(oldForm==form) return;
		
		comp.setAttribute(FORM_ID, id);//mark it is a form component with the form id;
		comp.setAttribute(id, form);//after setAttribute, we can access fx in el.
		
		if(form instanceof FormExt){
			final FormExt fex = (FormExt)form;
			comp.setAttribute(id+"Status", fex.getStatus());//by convention fxStatus
			
			if(oldForm instanceof FormExt){//copy the filed information, this is for a form-init that assign a user form
				for(String fn:((FormExt)oldForm).getLoadFieldNames()){
					fex.addLoadFieldName(fn);
				}
				for(String fn:((FormExt)oldForm).getSaveFieldNames()){
					fex.addSaveFieldName(fn);
				}
			}
		}
	}
	
	public Form getForm(Component comp,String id){
		String oldid = (String)comp.getAttribute(FORM_ID, Component.COMPONENT_SCOPE);
		if(oldid==null || !oldid.equals(id)){
			//return null if the id is not correct
			return null;
		}
		return (Form)comp.getAttribute(id);
	}

	private void removeForm(Component comp){
		String id = (String)comp.getAttribute(FORM_ID, Component.COMPONENT_SCOPE);
		if(id!=null){
			comp.removeAttribute(FORM_ID);
			comp.removeAttribute(id);
			comp.removeAttribute(id+"Status");
		}
	}
	
	@Override
	public void addFormInitBinding(Component comp, String id, String initExpr, Map<String, Object> initArgs) {
		checkInit();
		if(Strings.isBlank(id)){
			throw new IllegalArgumentException("form id is blank");
		}
		if(initExpr==null){
			throw new IllegalArgumentException("initExpr is null for component "+comp+", form "+id);
		}
		
		
		Form form = getForm(comp,id);
		if(form==null){
			storeForm(comp,id,new SimpleForm());
		}
		
		addFormInitBinding0(comp,id,initExpr,initArgs);

	}
	
	private void addFormInitBinding0(Component comp, String formId, String initExpr, Map<String, Object> bindingArgs) {
		
		if(_log.debugable()){
			_log.debug("add init-form-binding: comp=[%s],form=[%s],expr=[%s]", comp,formId,initExpr);
		}
		final String attr = formId;
		
		InitFormBindingImpl binding = new InitFormBindingImpl(this, comp, attr, initExpr, bindingArgs);
		
		addBinding(comp, attr, binding);
		final BindingKey bkey = getBindingKey(comp, attr);
		_formBindingHandler.addInitBinding(bkey, binding);
	}
	
	@Override
	public void addFormLoadBindings(Component comp, String id,
			String loadExpr, String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs) {
		checkInit();
		if(Strings.isBlank(id)){
			throw new IllegalArgumentException("form id is blank");
		}
		if(loadExpr==null){
			throw new IllegalArgumentException("loadExpr is null for component "+comp+", form "+id);
		}
		
		Form form = getForm(comp,id);
		if(form==null){
			storeForm(comp,id,new SimpleForm());
		}
		
		addFormLoadBindings0(comp,id,loadExpr,beforeCmds,afterCmds,bindingArgs);
	}

	@Override
	public void addFormSaveBindings(Component comp, String id, String saveExpr,
			String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs, String validatorExpr,
			Map<String, Object> validatorArgs) {
		checkInit();
		if(Strings.isBlank(id)){
			throw new IllegalArgumentException("form id is blank");
		}
		if(saveExpr==null){
			throw new IllegalArgumentException("saveExpr is null for component "+comp+", form "+id);
		}
		
		Form form = getForm(comp,id);
		if(form==null){
			storeForm(comp,id,new SimpleForm());
		}

		addFormSaveBindings0(comp, id, saveExpr, beforeCmds, afterCmds, bindingArgs, validatorExpr, validatorArgs);
	}

	private void addFormLoadBindings0(Component comp, String formId, String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs) {
		final boolean prompt = isPrompt(beforeCmds,afterCmds);
		final String attr = formId;
		
		if(prompt){
			final LoadFormBindingImpl binding = new LoadFormBindingImpl(this, comp, formId, loadExpr,ConditionType.PROMPT,null, bindingArgs);
			addBinding(comp, attr, binding);
			final BindingKey bkey = getBindingKey(comp, attr);
			_formBindingHandler.addLoadPromptBinding(bkey, binding);
		}else{
			if(beforeCmds!=null && beforeCmds.length>0){
				for(String cmd:beforeCmds){
					final LoadFormBindingImpl binding = new LoadFormBindingImpl(this, comp, formId, loadExpr,ConditionType.BEFORE_COMMAND,cmd, bindingArgs);
					addBinding(comp, attr, binding);
					if(_log.debugable()){
						_log.debug("add before command-load-form-binding: comp=[%s],attr=[%s],expr=[%s],command=[%s]", comp,attr,loadExpr,cmd);
					}
					_formBindingHandler.addLoadBeforeBinding(cmd, binding);
				}
			}
			if(afterCmds!=null && afterCmds.length>0){
				for(String cmd:afterCmds){
					final LoadFormBindingImpl binding = new LoadFormBindingImpl(this, comp, formId, loadExpr,ConditionType.AFTER_COMMAND,cmd, bindingArgs);
					addBinding(comp, attr, binding);
					if(_log.debugable()){
						_log.debug("add after command-load-form-binding: comp=[%s],attr=[%s],expr=[%s],command=[%s]", comp,attr,loadExpr,cmd);
					}
					_formBindingHandler.addLoadAfterBinding(cmd, binding);
				}
			}
		}
	}

	private void addFormSaveBindings0(Component comp, String formid, String saveExpr, 
			String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String validatorExpr,Map<String, Object> validatorArgs) {
		final boolean prompt = isPrompt(beforeCmds,afterCmds);
		if(prompt){
			throw new IllegalArgumentException("a save-form-binding have to set with a before|after command condition");
		}
		
		if(beforeCmds!=null && beforeCmds.length>0){
			for(String cmd:beforeCmds){
				final SaveFormBindingImpl binding = new SaveFormBindingImpl(this, comp, formid, saveExpr, ConditionType.BEFORE_COMMAND, cmd, bindingArgs, validatorExpr, validatorArgs);
				addBinding(comp, formid, binding);
				if(_log.debugable()){
					_log.debug("add before command-save-form-binding: comp=[%s],attr=[%s],expr=[%s],command=[%s]", comp,formid,saveExpr,cmd);
				}
				_formBindingHandler.addSaveBeforeBinding(cmd, binding);
			}
		}
		if(afterCmds!=null && afterCmds.length>0){
			for(String cmd:afterCmds){
				final SaveFormBindingImpl binding = new SaveFormBindingImpl(this, comp, formid, saveExpr, ConditionType.AFTER_COMMAND, cmd, bindingArgs, validatorExpr, validatorArgs);
				addBinding(comp, formid, binding);
				if(_log.debugable()){
					_log.debug("add after command-save-form-binding: comp=[%s],attr=[%s],expr=[%s],command=[%s]", comp,formid,saveExpr,cmd);
				}
				_formBindingHandler.addSaveAfterBinding(cmd, binding);
			}
		}
		if(validatorExpr!=null){
			BindingKey bkey = getBindingKey(comp, formid);
			if(!_hasValidators.contains(bkey)){
				_hasValidators.add(bkey);
			}
		}
	}
	
	@Override
	public void addPropertyInitBinding(Component comp, String attr,
			String initExpr,Map<String, Object> initArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if(initExpr==null){
			throw new IllegalArgumentException("initExpr is null for "+attr+" of "+comp);
		}
		if (Strings.isBlank(converterExpr)) {
			converterExpr = getSystemConverter(comp, attr);
			if (converterExpr != null) {
				converterExpr = "'"+converterExpr+"'";
			}
		}
		
		addPropertyInitBinding0(comp,attr,initExpr,initArgs,converterExpr,converterArgs);
		
		initRendererIfAny(comp,attr);
	}

	@Override
	public void addPropertyLoadBindings(Component comp, String attr,
			String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if(loadExpr==null){
			throw new IllegalArgumentException("loadExpr is null for component "+comp+", attr "+attr);
		}
		if (Strings.isBlank(converterExpr)) {
			converterExpr = getSystemConverter(comp, attr);
			if (converterExpr != null) {
				converterExpr = "'"+converterExpr+"'";
			}
		}
		
		addPropertyLoadBindings0(comp, attr, loadExpr, beforeCmds, afterCmds, bindingArgs, converterExpr, converterArgs);
		
		initRendererIfAny(comp,attr);
	}

	@Override
	public void addPropertySaveBindings(Component comp, String attr,
			String saveExpr, String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs, String validatorExpr,
			Map<String, Object> validatorArgs) {
		checkInit();
		if(saveExpr==null){
			throw new IllegalArgumentException("saveExpr is null for component "+comp+", attr "+attr);
		}
		if (Strings.isBlank(converterExpr)) {
			converterExpr = getSystemConverter(comp, attr);
			if (converterExpr != null) {
				converterExpr = "'"+converterExpr+"'";
			}
		}
		if (Strings.isBlank(validatorExpr)) {
			validatorExpr = getSystemValidator(comp, attr);
			if (validatorExpr != null) {
				validatorExpr = "'"+validatorExpr+"'";
			}
		}

		addPropertySaveBindings0(comp, attr, saveExpr, beforeCmds, afterCmds, bindingArgs, 
				converterExpr, converterArgs, validatorExpr, validatorArgs);
	}
	
	
	private void addPropertyInitBinding0(Component comp, String attr,
			String initExpr, Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {
		
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getOverrideAnnotation(compCtrl, attr, Binder.ZKBIND);
		String loadrep = null;
		Class<?> attrType = null;//default is any class
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			loadrep = AnnotationUtil.testString(attrs.get(Binder.LOAD_REPLACEMENT),comp,attr,Binder.LOAD_REPLACEMENT); //check replacement of attr when loading
			
			final String type = AnnotationUtil.testString(attrs.get(Binder.LOAD_TYPE),comp,attr,Binder.LOAD_TYPE); //check type of attr when loading
			if (type != null) {
				try {
					attrType = Classes.forNameByThread(type);
				} catch (ClassNotFoundException e) {
					throw new UiException(e.getMessage(),e);
				}
			}
		}
		loadrep = loadrep == null ? attr : loadrep;
		
		if(_log.debugable()){
			_log.debug("add init-binding: comp=[%s],attr=[%s],expr=[%s],converter=[%s]", comp,attr,initExpr,converterArgs);
		}
		
		InitPropertyBindingImpl binding = new InitPropertyBindingImpl(this, comp, attr, loadrep, attrType, initExpr, bindingArgs, converterExpr, converterArgs);
		
		addBinding(comp, attr, binding); 
		final BindingKey bkey = getBindingKey(comp, attr);
		_propertyBindingHandler.addInitBinding(bkey, binding);
	}

	private String getSystemConverter(Component comp, String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getOverrideAnnotation(compCtrl,attr, Binder.ZKBIND);
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			return AnnotationUtil.testString(attrs.get(Binder.CONVERTER),comp,attr,Binder.CONVERTER); //system converter if exists
		}
		return null;
	}
	
	private String getSystemValidator(Component comp, String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getOverrideAnnotation(compCtrl, attr, Binder.ZKBIND);
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			return AnnotationUtil.testString(attrs.get(Binder.VALIDATOR),comp,attr,Binder.VALIDATOR); //system validator if exists
		}
		return null;
	}
	
	private void initRendererIfAny(Component comp,String attr) {
		final Object installed = comp.getAttribute(BinderImpl.RENDERER_INSTALLED);
		if (installed != null) { //renderer was set already init
			return;
		}
		
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getOverrideAnnotation(compCtrl, null, Binder.ZKBIND);
		final Map<String, String[]> attrs = ann != null ? ann.getAttributes() : null; //(tag, tagExpr)
		
		if (attrs != null) {
			final String rendererName = AnnotationUtil.testString(attrs.get(Binder.RENDERER),comp,attr,Binder.RENDERER); //renderer if any
			//setup renderer
			if (rendererName != null) { //there was system renderer
				String[] values = null;
				if(rendererName.indexOf("=")!=-1){
					values = rendererName.split("=", 2);//zk 6.0.0
				}else{
					values = rendererName.split(":", 2);//after zk 6.0.1
				}
				
				if (values != null) {
					final Object renderer = getRenderer(values[1]);
					//check if user has set a renderer
					Object old = null;
					try {
						old = Fields.get(comp, values[0]);
					} catch (NoSuchMethodException e1) {
						//ignore
					}
					if (old == null) {
						try {
							Fields.set(comp, values[0], renderer, false);
						} catch (Exception  e) {
							throw UiException.Aide.wrap(e);
						}
						
						if(renderer instanceof TemplateRendererCtrl){
							((TemplateRendererCtrl)renderer).setAttributeName(attr);
						}
					}
					
					comp.setAttribute(BinderImpl.RENDERER_INSTALLED,"");//mark installed
				}
			}
		}
	}
	
	private void addPropertyLoadBindings0(Component comp, String attr,
			String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		final boolean prompt = isPrompt(beforeCmds,afterCmds);
		
		//check attribute _accessInfo natural characteristics to register Command event listener
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getOverrideAnnotation(compCtrl, attr, Binder.ZKBIND);
		//check which attribute of component should load to component on which event.
		//the event is usually a engine lifecycle event.
		//ex, listbox's 'selectedIndex' should be loaded to component on 'onAfterRender'
		String evtnm = null;
		String loadRep = null;
		Class<?> attrType = null;//default is any class
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			final String rw = (String) AnnotationUtil.testString(attrs.get(Binder.ACCESS),comp,attr,Binder.ACCESS); //_accessInfo right, "both|save|load", default to load
			if (rw != null && !"both".equals(rw) && !"load".equals(rw)) { //save only, skip
				return;
			}
			evtnm = AnnotationUtil.testString(attrs.get(Binder.LOAD_EVENT),comp,attr,Binder.LOAD_EVENT); //check trigger event for loading
			
			loadRep = AnnotationUtil.testString(attrs.get(Binder.LOAD_REPLACEMENT),comp,attr,Binder.LOAD_REPLACEMENT); //check replacement of attr when loading
			
			final String type = AnnotationUtil.testString(attrs.get(Binder.LOAD_TYPE),comp,attr,Binder.LOAD_TYPE); //check type of attr when loading
			if(type!=null){
				try {
					attrType = Classes.forNameByThread(type);
				} catch (ClassNotFoundException e) {
					throw new UiException(e.getMessage(),e);
				}
			}
		}
		loadRep = loadRep == null ? attr : loadRep;
		
		if(prompt){
			if(_log.debugable()){
				_log.debug("add event(prompt)-load-binding: comp=[%s],attr=[%s],expr=[%s],evtnm=[%s],converter=[%s]", comp,attr,loadExpr,evtnm,converterArgs);
			}
			LoadPropertyBindingImpl binding = new LoadPropertyBindingImpl(this, comp, attr, loadRep, attrType, loadExpr, ConditionType.PROMPT, null,  bindingArgs, converterExpr,converterArgs);
			addBinding(comp, attr, binding);
			
			if (evtnm != null) { //special case, load on an event, ex, onAfterRender of listbox on selectedItem
				registerCommandEventListener(comp, evtnm); //prompt
				addBinding(comp, evtnm, binding);//to mark evtnm has a this binding, so we can remove it
				final BindingKey bkey = getBindingKey(comp, evtnm);
				_propertyBindingHandler.addLoadEventBinding(comp, bkey, binding);
			}
			//if no command , always add to prompt binding, a prompt binding will be load when , 
			//1.load a component property binding
			//2.property change (TODO, DENNIS, ISSUE, I think loading of property change is triggered by tracker in loadOnPropertyChange, not by prompt-binding 
			final BindingKey bkey = getBindingKey(comp, attr);
			_propertyBindingHandler.addLoadPromptBinding(comp, bkey, binding);
		}else{
			if(beforeCmds!=null && beforeCmds.length>0){
				for(String cmd:beforeCmds){
					LoadPropertyBindingImpl binding = new LoadPropertyBindingImpl(this, comp, attr, loadRep, attrType, loadExpr, ConditionType.BEFORE_COMMAND, cmd, bindingArgs, converterExpr, converterArgs);
					addBinding(comp, attr, binding);
					if(_log.debugable()){
						_log.debug("add before command-load-binding: comp=[%s],att=r[%s],expr=[%s],converter=[%s]", comp,attr,loadExpr,converterExpr);
					}
					_propertyBindingHandler.addLoadBeforeBinding(cmd, binding);
				}
			}
			if(afterCmds!=null && afterCmds.length>0){
				for(String cmd:afterCmds){
					LoadPropertyBindingImpl binding = new LoadPropertyBindingImpl(this, comp, attr, loadRep, attrType, loadExpr,  ConditionType.AFTER_COMMAND, cmd, bindingArgs, converterExpr,converterArgs);
					addBinding(comp, attr, binding);
					if(_log.debugable()){
						_log.debug("add after command-load-binding: comp=[%s],att=r[%s],expr=[%s],converter=[%s]", comp,attr,loadExpr,converterExpr);
					}
					_propertyBindingHandler.addLoadAfterBinding(cmd, binding);	
				}
			}
		}
	}
	
	private void addPropertySaveBindings0(Component comp, String attr, String saveExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs, String validatorExpr, Map<String, Object> validatorArgs) {
		final boolean prompt = isPrompt(beforeCmds,afterCmds);
		//check attribute _accessInfo natural characteristics to register Command event listener 
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getOverrideAnnotation(compCtrl, attr, Binder.ZKBIND);
		//check which attribute of component should fire save on which event.
		//ex, listbox's 'selectedIndex' should be loaded to component on 'onSelect'
		//ex, checkbox's 'checked' should be saved to bean on 'onCheck'
		String evtnm = null;
		String saveRep = null;
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			final String rw = AnnotationUtil.testString(attrs.get(Binder.ACCESS),comp,attr,Binder.ACCESS); //_accessInfo right, "both|save|load", default to load
			if (!"both".equals(rw) && !"save".equals(rw)) { //load only, skip
				return;
			}
			evtnm = AnnotationUtil.testString(attrs.get(Binder.SAVE_EVENT),comp,attr,Binder.SAVE_EVENT); //check trigger event for saving
			
			saveRep = AnnotationUtil.testString(attrs.get(Binder.SAVE_REPLACEMENT),comp,attr,Binder.SAVE_REPLACEMENT); //check replacement of attr when saving
		}
		if (evtnm == null) { 
			//no trigger event, since the value never change of component, so both prompt and command are useless
			return;
		}
		saveRep = saveRep == null ? attr : saveRep;
		
		if(prompt){
			final SavePropertyBindingImpl binding = new SavePropertyBindingImpl(this, comp, attr, saveRep, saveExpr, ConditionType.PROMPT, null, bindingArgs, converterExpr, converterArgs, validatorExpr, validatorArgs);
			addBinding(comp, attr, binding);
			if(_log.debugable()){
				_log.debug("add event(prompt)-save-binding: comp=[%s],attr=[%s],expr=[%s],evtnm=[%s],converter=[%s],validate=[%s]", comp,attr,saveExpr,evtnm,converterExpr,validatorExpr);
			}
			registerCommandEventListener(comp, evtnm); //prompt
			addBinding(comp, evtnm, binding);//to mark evtnm has a this binding, so we can remove it in removeComponent
			final BindingKey bkey = getBindingKey(comp, evtnm);
			_propertyBindingHandler.addSavePromptBinding(comp, bkey, binding);
		}else{
			if(beforeCmds!=null && beforeCmds.length>0){
				for(String cmd:beforeCmds){
					final SavePropertyBindingImpl binding = new SavePropertyBindingImpl(this, comp, attr, saveRep, saveExpr, ConditionType.BEFORE_COMMAND, cmd, bindingArgs, converterExpr, converterArgs, validatorExpr, validatorArgs);
					addBinding(comp, attr, binding);
					if(_log.debugable()){
						_log.debug("add before command-save-binding: comp=[%s],att=r[%s],expr=[%s],converter=[%s],validator=[%s]", comp,attr,saveExpr,converterExpr,validatorExpr);
					}
					_propertyBindingHandler.addSaveBeforeBinding(cmd, binding);
				}
			}
			if(afterCmds!=null && afterCmds.length>0){
				for(String cmd:afterCmds){
					final SavePropertyBindingImpl binding = new SavePropertyBindingImpl(this, comp, attr, saveRep, saveExpr, ConditionType.AFTER_COMMAND, cmd, bindingArgs, converterExpr, converterArgs, validatorExpr, validatorArgs);
					addBinding(comp, attr, binding);
					if(_log.debugable()){
						_log.debug("add after command-save-binding: comp=[%s],att=r[%s],expr=[%s],converter=[%s],validator=[%s]", comp,attr,saveExpr,converterExpr,validatorExpr);
					}
					_propertyBindingHandler.addSaveAfterBinding(cmd, binding);	
				}
			}
		}
		
		if(validatorExpr!=null){
			BindingKey bkey = getBindingKey(comp, attr);
			if(!_hasValidators.contains(bkey)){
				_hasValidators.add(bkey);
			}
		}
	}
	
	@Override
	@Deprecated
	public void addChildrenInitBinding(Component comp, String initExpr,Map<String, Object> initArgs) {
		this.addChildrenInitBinding(comp, initExpr, initArgs,null,null);
	}
	@Override
	public void addChildrenInitBinding(Component comp, String initExpr,Map<String, Object> initArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if(initExpr==null){
			throw new IllegalArgumentException("initExpr is null for children of "+comp);
		}
		addChildrenInitBinding0(comp,initExpr,initArgs,converterExpr,converterArgs);
	}
	
	@Override
	@Deprecated
	public void addChildrenLoadBindings(Component comp,  String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs) {
		addChildrenLoadBindings(comp, loadExpr, beforeCmds, afterCmds, bindingArgs,null, null);
	}
	@Override
	public void addChildrenLoadBindings(Component comp,  String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if(loadExpr==null){
			throw new IllegalArgumentException("loadExpr is null for children of "+comp);
		}
		addChildrenLoadBindings0(comp, loadExpr, beforeCmds, afterCmds, bindingArgs,converterExpr,converterArgs);
	}
	
	private void addChildrenInitBinding0(Component comp, String initExpr, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		
		if(_log.debugable()){
			_log.debug("add children-init-binding: comp=[%s],expr=[%s]", comp,initExpr);
		}
		
		InitChildrenBindingImpl binding = new InitChildrenBindingImpl(this, comp, initExpr, bindingArgs,converterExpr,converterArgs);
		
		addBinding(comp, CHILDREN_ATTR, binding); 
		final BindingKey bkey = getBindingKey(comp, CHILDREN_ATTR);
		_childrenBindingHandler.addInitBinding(bkey, binding);
	}
	
	private void addChildrenLoadBindings0(Component comp, String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		final boolean prompt = isPrompt(beforeCmds,afterCmds);

		if(prompt){
			if(_log.debugable()){
				_log.debug("add event(prompt)-children-load-binding: comp=[%s],expr=[%s]", comp,loadExpr);
			}
			LoadChildrenBindingImpl binding = new LoadChildrenBindingImpl(this, comp, loadExpr, ConditionType.PROMPT, null,  bindingArgs,converterExpr,converterArgs);
			addBinding(comp, CHILDREN_ATTR, binding);
			
			final BindingKey bkey = getBindingKey(comp, CHILDREN_ATTR);
			_childrenBindingHandler.addLoadPromptBinding(comp, bkey, binding);
		}else{
			if(beforeCmds!=null && beforeCmds.length>0){
				for(String cmd:beforeCmds){
					LoadChildrenBindingImpl binding = new LoadChildrenBindingImpl(this, comp, loadExpr, ConditionType.BEFORE_COMMAND, cmd, bindingArgs,converterExpr,converterArgs);
					addBinding(comp, CHILDREN_ATTR, binding);
					if(_log.debugable()){
						_log.debug("add before command children-load-binding: comp=[%s],expr=[%s],cmd=[%s]", comp,loadExpr, cmd);
					}
					_childrenBindingHandler.addLoadBeforeBinding(cmd, binding);
				}
			}
			if(afterCmds!=null && afterCmds.length>0){
				for(String cmd:afterCmds){
					LoadChildrenBindingImpl binding = new LoadChildrenBindingImpl(this, comp, loadExpr,  ConditionType.AFTER_COMMAND, cmd, bindingArgs,converterExpr,converterArgs);
					addBinding(comp, CHILDREN_ATTR, binding);
					if(_log.debugable()){
						_log.debug("add after command children-load-binding: comp=[%s],expr=[%s],cmd=[%s]", comp,loadExpr, cmd);
					}
					_childrenBindingHandler.addLoadAfterBinding(cmd, binding);	
				}
			}
		}
	}
	
	@Override
	public void addReferenceBinding(Component comp, String attr,  String loadExpr, Map<String, Object> bindingArgs) {
		checkInit();
		if(loadExpr==null){
			throw new IllegalArgumentException("loadExpr is null for reference of "+comp);
		}
		addReferenceBinding0(comp, attr, loadExpr, bindingArgs);
	}
	
	private void addReferenceBinding0(Component comp, String attr, String loadExpr, Map<String, Object> bindingArgs) {
		if(_log.debugable()){
			_log.debug("add reference-binding: comp=[%s],attr=[%s],expr=[%s]", comp,attr,loadExpr);
		}
		ReferenceBindingImpl binding = new ReferenceBindingImpl(this, loadExpr, comp);
		
		if(_refBindingHandler!=null){
			_refBindingHandler.addReferenceBinding(comp, attr, binding);
		}else{
			throw new UiException("ref binding handler is not supported in current runtime.");
		}
		
		addBinding(comp, attr, binding);
	}

	private boolean isPrompt(String[] beforeCmds, String[] afterCmds){
		return (beforeCmds==null || beforeCmds.length==0) && (afterCmds==null || afterCmds.length==0);
	}

	public void addCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> args) {
		checkInit();
		final CommandBindingImpl binding = new CommandBindingImpl(this, comp, evtnm, commandExpr, args);
		addBinding(comp, evtnm, binding);
		registerCommandEventListener(comp, evtnm, binding, false);
	}
	
	public void addGlobalCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> args) {
		checkInit();
		final CommandBindingImpl binding = new CommandBindingImpl(this, comp, evtnm, commandExpr, args);
		addBinding(comp, evtnm, binding);
		registerCommandEventListener(comp, evtnm, binding, true);
	}
	
	//associate event to CommandBinding
	private void registerCommandEventListener(Component comp, String evtnm, CommandBinding command,boolean global) {
		final CommandEventListener listener = getCommandEventListener(comp,evtnm);
		if(global){
			listener.setGlobalCommand(command);
		}else{
			listener.setCommand(command);
		}
	}
	
	//associate event to prompt
	private void registerCommandEventListener(Component comp, String evtnm) {
		final CommandEventListener listener = getCommandEventListener(comp,evtnm);
		listener.setPrompt(true);
	}
	
	private CommandEventListener getCommandEventListener(Component comp, String evtnm){
		final BindingKey bkey = getBindingKey(comp, evtnm);
		CommandEventListener listener = _listenerMap.get(bkey);
		if (listener == null) {
			listener = new CommandEventListener(comp);
			comp.addEventListener(evtnm, listener);
			_listenerMap.put(bkey, listener);
		}
		return listener;
	}
	
	private void removeEventCommandListenerIfExists(Component comp, String evtnm) {
		final BindingKey bkey = getBindingKey(comp, evtnm);
		final CommandEventListener listener = _listenerMap.remove(bkey);
		if (listener != null) {
			comp.removeEventListener(evtnm, listener);
		}
	}

	//utility class, remove ${ and }
	private String getPureExpressionString(ExpressionX expr) {
		if (expr == null) {
			return null;
		}
		final String evalstr = expr.getExpressionString(); 
		return evalstr.substring(2, evalstr.length() - 1);
	}
	
	private class CommandEventListener implements EventListener<Event>, Serializable{
		private static final long serialVersionUID = 1L;
	//event used to trigger command
		private boolean _prompt = false;
		private CommandBinding _commandBinding;
		private CommandBinding _globalCommandBinding;
		final private Component _target;
		
		CommandEventListener(Component target){
			_target = target;
		}
		
		private void setCommand(CommandBinding command) {
			_commandBinding = command;
		}
		
		private void setGlobalCommand(CommandBinding command) {
			_globalCommandBinding = command;
		}
		
		private void setPrompt(boolean prompt) {
			_prompt = prompt;
		}
		
		public void onEvent(Event event) throws Exception {
			BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
			try{
				if(collector!=null){
					collector.pushStack("ON_EVENT");
					collector.addEnterInfo(event.getTarget(),"on-event",event.getName(),"");
				}
				onEvent0(event);
			}catch(Exception x){
				_log.error(x.getMessage(),x);
				throw x;
			}finally{
				if(collector!=null){
					collector.popStack();
				}
			}
		}
		
		private void onEvent0(Event event) throws Exception {
			//command need to be confirmed shall be execute first!
			//must sort the command sequence?
			
			//BUG 619, event may come from children of some component, 
			//ex tabbox.onSelect is form tab, so we cannot depend on event's target
			final Component comp = _target;//_target is always equals _commandBinding.getComponent();
			final String evtnm = event.getName();
			final Set<Property> notifys = new LinkedHashSet<Property>();
			
			int cmdResult = COMMAND_SUCCESS; //command execution result, default to success
			boolean promptResult = true;
			String command = null;
			if(_log.debugable()){
				_log.debug("====Start command event [%s]",event);
			}
			//BUG ZK-757, The timing of saving textbox's value attribute to ViewModel is later than command execution on onChange event
			//We should save the prompt with validation first. 
			//For a prompt binding that also binds with a command, that should not be mixed with command
			//If user concern the timing of prompt save and validation with command, they should use condition not prompt  
			if(_prompt){
				promptResult = BinderImpl.this.doSaveEvent(comp, event, notifys); //save on event
			}
			
			if (_commandBinding != null) {
				final BindEvaluatorX eval = getEvaluatorX();
				command = (String) eval.getValue(null, comp, ((CommandBindingImpl)_commandBinding).getCommand());
				if(!Strings.isEmpty(command)){//avoid the execution of a empty command.
					
					//ZK-1032 Able to wire Event to command method
					Map<String,Object> implicit = null;
					if(_implicitContributor!=null){
						implicit = _implicitContributor.contirbuteCommandObject(BinderImpl.this,_commandBinding,event);
					}
					
					final Map<String, Object> args = BindEvaluatorXUtil.evalArgs(eval, comp, _commandBinding.getArgs(),implicit);
					
					cmdResult = BinderImpl.this.doCommand(comp, _commandBinding, command, event, args, notifys);
				}
			}

			//load prompt only when prompt result is success
			if (_prompt && promptResult) {
				if(_log.debugable()){
					_log.debug("This is a prompt command");
				}
				BinderImpl.this.doLoadEvent(comp, evtnm); //load on event
			}

			notifyVMsgsChanged();//always, no better way to know which properties of validation are changed
			
			if(_log.debugable()){
				_log.debug("There are [%s] property need to be notify after event = [%s], command = [%s]",notifys.size(),evtnm, command);
			}
			fireNotifyChanges(notifys);
			
			//post global command only when command success
			if (cmdResult==COMMAND_SUCCESS && _globalCommandBinding != null) {
				final BindEvaluatorX eval = getEvaluatorX();
				command = (String) eval.getValue(null, comp, ((CommandBindingImpl)_globalCommandBinding).getCommand());
				if(!Strings.isEmpty(command)){//avoid the execution of a empty command.
					final Map<String, Object> args = BindEvaluatorXUtil.evalArgs(eval, comp, _globalCommandBinding.getArgs());
					//post global command
					postGlobalCommand(command,args);
				}
			}
			
			if(_log.debugable()){
				_log.debug("====End command event [%s]",event);
			}
		}
	}
	
	private class VMsgsChangedListener implements EventListener<Event>,Serializable{
		private static final long serialVersionUID = 1L;
		public void onEvent(Event event) throws Exception {
			if(_validationMessages!=null){
				Set<Property> notify = new HashSet<Property>();
				notify.add(new PropertyImpl(_validationMessages,".",null));
				fireNotifyChanges(notify);
			}
		}
	}
	
	/**
	 * @since 6.0.1
	 */
	@Override
	public boolean isActivating(){
		return _activating;
	}
	
	private void notifyVMsgsChanged(){
		if(_validationMessages!=null){
			//ZK-722 Validation message is not clear after form binding loaded
			//defer the validation notify as possible
			Events.postEvent(-1, _dummyTarget, new Event(ON_VMSGS_CHANGED));
		}
	}
	
	public void sendCommand(String command, Map<String, Object> args) {
		checkInit();
		final Set<Property> notifys = new HashSet<Property>();
		//args come from user, we don't eval it. 
		doCommand(_rootComp, null, command, null, args, notifys);
		fireNotifyChanges(notifys);
	}

	private void fireNotifyChanges(Set<Property> notifys) {
		for(Property prop : notifys) {
			notifyChange(prop.getBase(), prop.getProperty());
		}
	}
	
	public void postCommand(String command, Map<String, Object> args) {
		checkInit();
		final Event evt = new Event(ON_POST_COMMAND,_dummyTarget,new Object[]{command,args});
		Events.postEvent(evt);
	}
	
	
	/**
	 * @param comp the component that trigger the command, major life cycle of binding (on event trigger)
	 * @param commandBinding the command binding, nullable
	 * @param command command is the command name after evaluation
	 * @param evt event that fire this command, nullable
	 * @param commandArgs the passed in argument for executing command
	 * @param notifys container for properties that is to be notifyChange
	 * @return the result of the doCommand, COMMAND_SUCCESS or COMMAND_FAIL_VALIDATE 
	 */
	private int doCommand(Component comp, CommandBinding commandBinding, String command, Event evt, Map<String, Object> commandArgs, Set<Property> notifys) {
		final String evtnm = evt == null ? null : evt.getName();
		if(_log.debugable()){
			_log.debug("Start doCommand comp=[%s],command=[%s],evtnm=[%s]",comp,command,evtnm);
		}
		BindContext ctx = BindContextUtil.newBindContext(this, commandBinding, false, command, comp, evt);
		BindContextUtil.setCommandArgs(this, comp, ctx, commandArgs);
		try {
			doPrePhase(Phase.COMMAND, ctx); //begin of Command
			boolean success = true;
			
			final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
			if(collector!=null){
				collector.addCommandInfo(commandBinding,"on-command", evt.getName(),
						getPureExpressionString(((CommandBindingImpl)commandBinding).getCommand()), command, commandArgs,"");
			}
			
			//validate
			success = doValidate(comp, command, evt, ctx, notifys);
			if (!success) {
				return COMMAND_FAIL_VALIDATE;
			}
			
			//save before command bindings
			doSaveBefore(comp, command, evt, ctx, notifys);
			
			//load before command bindings
			doLoadBefore(comp, command, ctx);
			
			//execute command
			doExecute(comp, command, commandArgs, ctx, notifys);
			
			//save after command bindings
			doSaveAfter(comp, command, evt, ctx, notifys);
			
			//load after command bindings
			doLoadAfter(comp, command, ctx);
			if(_log.debugable()){
				_log.debug("End doCommand");
			}
			return COMMAND_SUCCESS;
		} finally {
			doPostPhase(Phase.COMMAND, ctx); //end of Command
		}
		
	}
	
	private void doGlobalCommand(Component comp, String command, Map<String, Object> commandArgs, Set<Property> notifys) {
		if(_log.debugable()){
			_log.debug("Start doGlobalCommand comp=[%s],command=[%s]",comp,command);
		}
		
		BindContext ctx = BindContextUtil.newBindContext(this, null, false, command, comp, null);
		BindContextUtil.setCommandArgs(this, comp, ctx, commandArgs);
		try {
			doPrePhase(Phase.GLOBAL_COMMAND, ctx); //begin of Command
			//execute command
			doGlobalCommandExecute(comp, command, commandArgs, ctx, notifys);
		} finally {
			doPostPhase(Phase.GLOBAL_COMMAND, ctx); //end of Command
		}
	}
	
	private void doGlobalCommandExecute(Component comp, String command, Map<String, Object> commandArgs, BindContext ctx,Set<Property> notifys) {
		try {
			if(_log.debugable()){
				_log.debug("before doGlobalCommandExecute comp=[%s],command=[%s]",comp,command);
			}
			doPrePhase(Phase.EXECUTE, ctx);
			
			final Object viewModel = getViewModel();
			
			Method method = getCommandMethod(viewModel.getClass(), command, _globalCommandMethodInfoProvider,_globalCommandMethodCache);
			
			if (method != null) {
				
				ParamCall parCall = createParamCall(ctx);
				if(commandArgs != null){
					parCall.setBindingArgs(commandArgs);
				}
				
				parCall.call(viewModel, method);
				
				notifys.addAll(BindELContext.getNotifys(method, viewModel,
						(String) null, (Object) null, ctx)); // collect notifyChange
			}else{
				//do nothing
				if(_log.debugable()){
					_log.debug("no global command method in [%s]", viewModel);
				}
			}
			if(_log.debugable()){
				_log.debug("after doGlobalCommandExecute notifys=[%s]", notifys);
			}
		} finally {
			doPostPhase(Phase.EXECUTE, ctx);
		}
	}
	
	/*package*/ void doPrePhase(Phase phase, BindContext ctx) {
		if (_phaseListener != null) {
			_phaseListener.prePhase(phase, ctx);
		}
		BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if(collector!=null){
			collector.pushStack(phase.name());
		}
		
	}
	
	/*package*/ void doPostPhase(Phase phase, BindContext ctx) {
		if (_phaseListener != null) {
			_phaseListener.postPhase(phase, ctx);
		}
		BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if(collector!=null){
			collector.popStack();
		}
	}

	//for event -> prompt only, no command 
	private boolean doSaveEvent(Component comp, Event evt, Set<Property> notifys) {
		final String evtnm = evt == null ? null : evt.getName();
		if(_log.debugable()){
			_log.debug("doSaveEvent comp=[%s],evtnm=[%s],notifys=[%s]",comp,evtnm,notifys);
		}
		final BindingKey bkey = getBindingKey(comp, evtnm);
		return _propertyBindingHandler.doSaveEvent(bkey, comp, evt, notifys);
	}
	
	//for event -> prompt only, no command
	private void doLoadEvent(Component comp, String evtnm) {
		if(_log.debugable()){
			_log.debug("doLoadEvent comp=[%s],evtnm=[%s]",comp,evtnm);
		}
		final BindingKey bkey = getBindingKey(comp, evtnm); 
		_propertyBindingHandler.doLoadEvent(bkey, comp, evtnm);
	}
	
	//doCommand -> doValidate
	private boolean doValidate(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		final Set<Property> validates = new HashSet<Property>();
		try {
			if(_log.debugable()){
				_log.debug("doValidate comp=[%s],command=[%s],evt=[%s],context=[%s]",comp,command,evt,ctx);
			}
			doPrePhase(Phase.VALIDATE, ctx);
			
			//we collect properties that need to be validated, than validate one-by-one
			ValidationHelper vHelper = new ValidationHelper(this,new ValidationHelper.InfoProvider() {
				public Map<String, List<SaveFormBinding>> getSaveFormBeforeBindings() {
					return _formBindingHandler.getSaveFormBeforeBindings();
				}		
				public Map<String, List<SaveFormBinding>> getSaveFormAfterBindings() {
					return _formBindingHandler.getSaveFormAfterBindings();
				}
				public Map<String, List<SavePropertyBinding>> getSaveBeforeBindings() {
					return _propertyBindingHandler.getSaveBeforeBindings();
				}
				public Map<String, List<SavePropertyBinding>> getSaveAfterBindings() {
					return _propertyBindingHandler.getSaveAfterBindings();
				}
				public BindingKey getBindingKey(Component comp, String attr) {
					return BinderImpl.this.getBindingKey(comp,attr);
				}
			});
			
			//collect Property of special command for validation in validates
			vHelper.collectSaveBefore(comp, command, evt, validates);
			vHelper.collectSaveAfter(comp, command, evt, validates);
			
			//do validation (defined by application)
			if (validates.isEmpty()) {
				return true;
			} else {
				if(_log.debugable()){
					_log.debug("doValidate validates=[%s]",validates);
				}
				boolean valid = true;
				Map<String,Property[]> properties = _propertyBindingHandler.toCollectedProperties(validates);
				valid &= vHelper.validateSaveBefore(comp, command, properties,valid,notifys);
				valid &= vHelper.validateSaveAfter(comp, command, properties,valid,notifys);
				
				 //ZK-878 Exception if binding a form with errorMessage
				 //To handle wrong value exception when getting a component value.
				for(Property p :validates){
					if(p instanceof WrongValuePropertyImpl){
						for(WrongValueException wve:((WrongValuePropertyImpl)p).getWrongValueExceptions()){
							//refer to UiEngineImpl#handleError()
							Component wvc = wve.getComponent();
							if(wvc!=null){
								wve = ((ComponentCtrl)wvc).onWrongValue(wve);
								if (wve != null) {
									Component c = wve.getComponent();
									if (c == null) c = wvc;
									Clients.wrongValue(c, wve.getMessage());
								}
							}
						}
						valid = false;
					}
				}
				return valid;
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		} finally {
			doPostPhase(Phase.VALIDATE, ctx);
		}
	}
	
	private ParamCall createParamCall(BindContext ctx){
		final ParamCall call = new ParamCall();
		call.setBinder(this);
		call.setBindContext(ctx);
		final Component comp = ctx.getComponent();
		if(comp!=null){
			call.setComponent(comp);
		}
		final Execution exec = Executions.getCurrent();
		if(exec!=null){
			call.setExecution(exec);
		}
		
		return call;
	}
	
	
	private void doExecute(Component comp, String command, Map<String, Object> commandArgs, BindContext ctx, Set<Property> notifys) {
		try {
			if(_log.debugable()){
				_log.debug("before doExecute comp=[%s],command=[%s],notifys=[%s]",comp,command,notifys);
			}
			doPrePhase(Phase.EXECUTE, ctx);
			
			final Object viewModel = getViewModel();
			
			Method method = getCommandMethod(viewModel.getClass(), command, _commandMethodInfoProvider, _commandMethodCache);
			
			if (method != null) {
				
				BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
				if(collector!=null){
					collector.addCommandInfo(ctx.getBinding(),"execute-command","",
							"'"+command+"'",method, commandArgs,"");
				}
				
				ParamCall parCall = createParamCall(ctx);
				if(commandArgs != null){
					parCall.setBindingArgs(commandArgs);
				}
				
				parCall.call(viewModel, method);
				
				notifys.addAll(BindELContext.getNotifys(method, viewModel,
						(String) null, (Object) null, ctx)); // collect notifyChange
			}else{
				throw new UiException("cannot find any method that is annotated for the command "+command+" with @Command in "+viewModel);
			}
			if(_log.debugable()){
				_log.debug("after doExecute notifys=[%s]", notifys);
			}
		} finally {
			doPostPhase(Phase.EXECUTE, ctx);
		}
	}

	
	private static interface CommandMethodInfoProvider {
		String getAnnotationName();
		String getDefaultAnnotationName();
		
		String[] getCommandName(Method method);
		boolean isDefaultMethod(Method m);
		
	}
	
	private Method getCommandMethod(Class<?> clz, String command, CommandMethodInfoProvider cmdInfo,Map<Class<?>, Map<String,CachedItem<Method>>> cache) {
		Map<String,CachedItem<Method>> methods;
		synchronized(cache){
			methods = cache.get(clz);
			if(methods==null){
				methods = new HashMap<String,CachedItem<Method>>();
				cache.put(clz, methods);
			}
		}
		CachedItem<Method> method = null;
		synchronized(methods){
			method = methods.get(command);
			if(method!=null){//quick check and return
				return method.value;
			}else if(methods.get(COMMAND_METHOD_MAP_INIT)!=null){
				//map is already initialized, check default method.
				method = methods.get(COMMAND_METHOD_DEFAULT);//get default
				if(method!=null){
					return method.value;
				}
				return null;
			}
			methods.clear();
			//scan
			for(Method m : clz.getMethods()){
				if(cmdInfo.isDefaultMethod(m)){
					if(methods.get(COMMAND_METHOD_DEFAULT)!=null){
						throw new UiException("there are more than one "+cmdInfo.getDefaultAnnotationName()+" method in "+clz+", "+methods.get(COMMAND_METHOD_DEFAULT).value+" and "+m);
					}
					methods.put(COMMAND_METHOD_DEFAULT, new CachedItem<Method>(m));
				}

				String[] vals = cmdInfo.getCommandName(m);
				if(vals==null) continue;
				if(vals.length==0){
					vals = new String[]{m.getName()};//command name from method.
				}
				for(String val:vals){
					val = val.trim();
					if(methods.get(val)!=null){
						throw new UiException("there are more than one "+cmdInfo.getAnnotationName()+" method "+val+" in "+clz+", "+methods.get(val).value+" and "+m);
					}
					methods.put(val, new CachedItem<Method>(m));
				}
			}
			
			methods.put(COMMAND_METHOD_MAP_INIT, NULL_METHOD);//mark this map has been initialized.
		}
		
		method = methods.get(command);
		if(method!=null){
			return method.value;
		}
		method = methods.get(COMMAND_METHOD_DEFAULT);//get default
		return method==null?null:method.value;
	}

	//doCommand -> doSaveBefore
	private void doSaveBefore(Component comp, String command, Event evt,  BindContext ctx, Set<Property> notifys) {
		if(_log.debugable()){
			_log.debug("doSaveBefore, comp=[%s],command=[%s],evt=[%s],notifys=[%s]",comp,command,evt,notifys);
		}
		try {
			doPrePhase(Phase.SAVE_BEFORE, ctx);		
			_propertyBindingHandler.doSaveBefore(comp, command, evt, notifys);
			_formBindingHandler.doSaveBefore(comp, command, evt, notifys);
		} finally {
			doPostPhase(Phase.SAVE_BEFORE, ctx);
		}
	}

	
	private void doSaveAfter(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		if(_log.debugable()){
			_log.debug("doSaveAfter, comp=[%s],command=[%s],evt=[%s],notifys=[%s]",comp,command,evt,notifys);
		}
		try {
			doPrePhase(Phase.SAVE_AFTER, ctx);
			_propertyBindingHandler.doSaveAfter(comp, command, evt, notifys);
			_formBindingHandler.doSaveAfter(comp, command, evt, notifys);
		} finally {
			doPostPhase(Phase.SAVE_AFTER, ctx);
		}		
		
	}

	
	private void doLoadBefore(Component comp, String command, BindContext ctx) {
		if(_log.debugable()){
			_log.debug("doLoadBefore, comp=[%s],command=[%s]",comp,command);
		}
		try {
			doPrePhase(Phase.LOAD_BEFORE, ctx);		
			_propertyBindingHandler.doLoadBefore(comp, command);
			_formBindingHandler.doLoadBefore(comp, command);
			_childrenBindingHandler.doLoadBefore(comp,command);
		} finally {
			doPostPhase(Phase.LOAD_BEFORE, ctx);
		}
	}
	
	private void doLoadAfter(Component comp, String command, BindContext ctx) {
		if(_log.debugable()){
			_log.debug("doLoadAfter, comp=[%s],command=[%s]",comp,command);
		}
		try {
			doPrePhase(Phase.LOAD_AFTER, ctx);
			_propertyBindingHandler.doLoadAfter(comp, command);
			_formBindingHandler.doLoadAfter(comp, command);
			_childrenBindingHandler.doLoadAfter(comp,command);
		} finally {
			doPostPhase(Phase.LOAD_AFTER, ctx);
		}		

	}
	
	/**
	 * Remove all bindings that associated with the specified component.
	 * @param comp the component
	 */
	public void removeBindings(Component comp) {
		checkInit();
		if(_rootComp==comp){
			//the binder component was detached, unregister queue
			unsubscribeQueue(_quename, _quescope, _queueListener);
			_rootComp.removeAttribute(ACTIVATOR);
		}
		if(_validationMessages!=null){
			_validationMessages.clearMessages(comp);
		}
		
		final Map<String, List<Binding>> attrMap = _bindings.remove(comp);
		if (attrMap != null) {
			final Set<Binding> removed = new HashSet<Binding>();
			for(Entry<String, List<Binding>> entry : attrMap.entrySet()) {
				final String key = entry.getKey(); 
				removeBindings(comp, key);
				removed.addAll(entry.getValue());
			}
			if (!removed.isEmpty()) {
				removeBindings(removed);
			}
		}
		
		removeFormAssociatedSaveBinding(comp);
		removeForm(comp);
		
		removeTemplateResolver(comp);
		if(_refBindingHandler!=null){
			_refBindingHandler.removeReferenceBinding(comp);
		}
		
		//remove trackings
		TrackerImpl tracker = (TrackerImpl) getTracker();
		tracker.removeTrackings(comp);

		BinderUtil.unmarkHandling(comp);
	}

	/**
	 * Remove all bindings that associated with the specified component and key (_fieldExpr|evtnm|formid).
	 * @param comp the component
	 * @param key can be component attribute, event name, or form id
	 */
	public void removeBindings(Component comp, String key) {
		checkInit();
		removeEventCommandListenerIfExists(comp, key); //_listenerMap; //comp+evtnm -> eventlistener
		
		final BindingKey bkey = getBindingKey(comp, key);
		final Set<Binding> removed = new HashSet<Binding>();
		
		_formBindingHandler.removeBindings(bkey,removed);
		_propertyBindingHandler.removeBindings(bkey, removed);
		_childrenBindingHandler.removeBindings(bkey, removed);
		if(_validationMessages!=null){
			_validationMessages.clearMessages(comp,key);
		}
		_hasValidators.remove(bkey);
		
		removeTemplateResolver(comp,key);
		
		if(_refBindingHandler!=null){
			_refBindingHandler.removeReferenceBinding(comp,key);
		}
		
		removeBindings(removed);
	}
	
	@Override
	public List<Binding> getLoadPromptBindings(Component comp, String attr) {
		checkInit();
		final List<Binding> bindings = new ArrayList<Binding>();
		final BindingKey bkey = getBindingKey(comp, attr);
		final List<LoadPropertyBinding> loadBindings = _propertyBindingHandler.getLoadPromptBindings(bkey);
		if(loadBindings!=null && loadBindings.size()>0){
			bindings.addAll(loadBindings);
		}
		if(bindings.size()==0){//optimize, they are exclusive
			List<LoadChildrenBinding> childrenLoadBindings = _childrenBindingHandler.getLoadPromptBindings(bkey);
			if(childrenLoadBindings!=null && childrenLoadBindings.size()>0){
				bindings.addAll(childrenLoadBindings);
			}
		}
		return bindings;
	}


	private void removeBindings(Collection<Binding> removed) {
		_formBindingHandler.removeBindings(removed);
		_propertyBindingHandler.removeBindings(removed);
		_childrenBindingHandler.removeBindings(removed);
	}
	
	private void addBinding(Component comp, String attr, Binding binding) {
		Map<String, List<Binding>> attrMap = _bindings.get(comp);
		if (attrMap == null) {
			//bug 657, we have to keep the attribute assignment order.
			attrMap = new LinkedHashMap<String, List<Binding>>(); 
			_bindings.put(comp, attrMap);
		}
		List<Binding> bindings = attrMap.get(attr);
		if (bindings == null) {
			bindings = new ArrayList<Binding>();
			attrMap.put(attr, bindings);
		}
		bindings.add(binding);
		
		//associate component with this binder, which means, one component can only bind by one binder
		BinderUtil.markHandling(comp,this);
	}
	
	
	
	@Override
	public void setTemplate(Component comp, String attr, String templateExpr, Map<String,Object> templateArgs){
		Map<String,TemplateResolver> resolvers = _templateResolvers.get(comp);
		if(resolvers==null){
			resolvers = new HashMap<String,TemplateResolver>();
			_templateResolvers.put(comp, resolvers);
		}	
		resolvers.put(attr, new TemplateResolverImpl(this, comp,attr,templateExpr,templateArgs));
	}
	
	@Override
	public TemplateResolver getTemplateResolver(Component comp, String attr){
		Map<String,TemplateResolver> resolvers = _templateResolvers.get(comp);
		return resolvers==null?null:resolvers.get(attr);
	}
	
	private void removeTemplateResolver(Component comp,String attr){
		Map<String,TemplateResolver> resolvers = _templateResolvers.get(comp);
		if(resolvers!=null){
			resolvers.remove(attr);
		}
	}
	private void removeTemplateResolver(Component comp){
		_templateResolvers.remove(comp);
	}
	

	public Tracker getTracker() {
		if (_tracker == null) {
			_tracker = new TrackerImpl();
		}
		return _tracker;
	}
	
	/**
	 * Internal Use only. init and load the component
	 */
	public void loadComponent(Component comp,boolean loadinit) {
		BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		try{
			if(collector!=null){
				collector.pushStack("LOAD_COMPONENT");
				collector.addEnterInfo(comp,"binder-api","loadComponent","");
			}
			loadComponent0(comp,loadinit);
		}finally{
			if(collector!=null){
				collector.popStack();
			}
		}
	}
	
	private void loadComponent0(Component comp,boolean loadinit) {
		loadComponentProperties0(comp,loadinit);
		
		for(Component kid = comp.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
			loadComponent0(kid,loadinit); //recursive
		}
	}
	
	private void loadComponentProperties0(Component comp,boolean loadinit) {
		
		final Map<String, List<Binding>> compBindings = _bindings.get(comp);
		if (compBindings != null) {// if component is not registered in this binder, do nothing.
			for(String key : compBindings.keySet()) {
				final BindingKey bkey = getBindingKey(comp, key);
				if(loadinit){
					_formBindingHandler.doInit(comp,bkey);
				}
				_formBindingHandler.doLoad(comp,bkey);
			}
			for(String key : compBindings.keySet()) {
				final BindingKey bkey = getBindingKey(comp, key);
				if(loadinit){
					_propertyBindingHandler.doInit(comp,bkey);
				}
				_propertyBindingHandler.doLoad(comp,bkey);
			}
			for(String key : compBindings.keySet()) {
				final BindingKey bkey = getBindingKey(comp, key);
				if(loadinit){
					_childrenBindingHandler.doInit(comp,bkey);
				}
					
				_childrenBindingHandler.doLoad(comp,bkey);
			}
		}
	}
	
	public void notifyChange(Object base, String attr) {
		checkInit();
		if(_log.debugable()){
			_log.debug("notifyChange base=[%s],attr=[%s]",base,attr);
		}
		getEventQueue().publish(new PropertyChangeEvent(_rootComp, base, attr));
	}
	
	private void postGlobalCommand(String command, Map<String, Object> args) {
		if(_log.debugable()){
			_log.debug("postGlobalCommand command=[%s], args=[%s]",command,args);
		}
		getEventQueue().publish(new GlobalCommandEvent(_rootComp, command, args));
	}
	
	public void setPhaseListener(PhaseListener listener) {
		_phaseListener = listener;
	}
	
	public PhaseListener getPhaseListener(){
		return _phaseListener;
	}

	private void subscribeQueue(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, true);
		que.subscribe(listener);
	}
	
	private void unsubscribeQueue(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, false);
		if(que!=null){
			que.unsubscribe(listener);
		}
	}
	
	private boolean isSubscribed(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, false);
		return que==null?false:que.isSubscribed(listener);
	}
	
	protected EventQueue<Event> getEventQueue() {
		return EventQueues.lookup(_quename, _quescope, true);
	}

	// create a unique id base on component's uuid and attr
	private BindingKey getBindingKey(Component comp, String attr) {
		return new BindingKey(comp,attr);
	}

	private class PostCommandListener implements EventListener<Event>,Serializable{
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public void onEvent(Event event) throws Exception {
			Object[] data = (Object[])event.getData();
			String command = (String)data[0];
			Map<String,Object> args = (Map<String,Object>)data[1]; 
			sendCommand(command, args);
		}
	}
	

	private void removeFormAssociatedSaveBinding(Component comp) {
		_assocFormSaveBindings.remove(comp);
		Map<SaveBinding,Set<SaveBinding>> associated = _reversedAssocFormSaveBindings.remove(comp);
		if(associated!=null){
			Set<Entry<SaveBinding,Set<SaveBinding>>> entries = associated.entrySet();
			for(Entry<SaveBinding,Set<SaveBinding>> entry:entries){
				entry.getValue().remove(entry.getKey());
			}
		}
	}
	
	@Override
	public void addFormAssociatedSaveBinding(Component associatedComp, String formId, SaveBinding saveBinding, String fieldName){
		checkInit();
		//find the form component by form id and a associated/nested component
		Component formComp = lookupAossicatedFormComponent(formId,associatedComp);
		if(formComp==null){
			throw new UiException("cannot find any form "+formId+" with "+associatedComp);
		}
		Set<SaveBinding> bindings = _assocFormSaveBindings.get(formComp);
		if(bindings==null){
			bindings = new LinkedHashSet<SaveBinding>();//keep the order
			_assocFormSaveBindings.put(formComp, bindings);
		}
		bindings.add(saveBinding);
		
		//keep the reverse association , so we can remove it if the associated component is detached (and the form component is not).
		Map<SaveBinding,Set<SaveBinding>> reverseMap = _reversedAssocFormSaveBindings.get(associatedComp);
		if(reverseMap==null){
			reverseMap = new HashMap<SaveBinding, Set<SaveBinding>>();
			_reversedAssocFormSaveBindings.put(associatedComp, reverseMap);
		}
		reverseMap.put(saveBinding,bindings);
		
		//ZK-1017 Property of a form is not correct when validation
		//ZK-1005 ZK 6.0.1 validation fails on nested bean
		((SavePropertyBindingImpl)saveBinding).setFormFieldInfo(formComp, formId, fieldName);
	}
	
	private Component lookupAossicatedFormComponent(String formId,Component associatedComp) {
		String fid = null;
		Component p = associatedComp;
		while(p!=null){
			fid = (String)p.getAttribute(FORM_ID);//check in default component scope
			if(fid!=null && fid.equals(formId)){
				break;
			}
			p = p.getParent();
		}
		
		return p;
	}

	@Override
	public Set<SaveBinding> getFormAssociatedSaveBindings(Component comp){
		checkInit();
		Set<SaveBinding> bindings = _assocFormSaveBindings.get(comp);
		if(bindings==null){
			return Collections.emptySet();
		}
		return new LinkedHashSet<SaveBinding>(bindings);//keep the order
	}

	//utility to simply hold a value which might be null
	private static class CachedItem<T> {
		final T value;
		public CachedItem(T value){
			this.value = value;
		}
	}
	
	public boolean hasValidator(Component comp, String attr){
		BindingKey bkey = getBindingKey(comp, attr);
		return _hasValidators.contains(bkey);
	}

	@Override
	public Component getView() {
		checkInit();
		return _rootComp;
	}

	@Override
	public ValidationMessages getValidationMessages() {
		return _validationMessages;
	}

	@Override
	public void setValidationMessages(ValidationMessages messages) {
		_validationMessages = messages;
	}

	/**
	 * did activate when the session is activating
	 */
	private void didActivate() {
		_activating = true;
		try{
			_log.debug("didActivate : [%s]",BinderImpl.this);
			//re-tie value to tracker.
			loadComponent(_rootComp, false);
		}finally{
			_activating = false;
		}
	}
 
	/**
	 * object that store in root component to help activating.
	 */
	private class Activator implements ComponentActivationListener,Serializable{
		private static final long serialVersionUID = 1L;
		@Override
		public void didActivate(Component comp) {
			if(_rootComp.equals(comp)){
				//zk 1442, don't do multiple subscribed if didActivate is called every request (e.x. jboss5)
				if(!isSubscribed(_quename, _quescope, _queueListener))
					subscribeQueue(_quename, _quescope, _queueListener);
				if(_deferredActivator==null){
					//defer activation to execution only for the first didActivate when failover
					comp.getDesktop().addListener(_deferredActivator = new DeferredActivator());
				}
			}
		}
		@Override
		public void willPassivate(Component comp) {
			//zk 1442, do nothing
		}
	}
	
	/**
	 * object that store in desktop listener to help activating.
	 * it do the activation when first execution come into
	 */
	private class DeferredActivator implements ExecutionInit, Serializable{
		private static final long serialVersionUID = 1L;
		@Override
		public void init(Execution exec, Execution parent) throws Exception {
			Desktop desktop = exec.getDesktop();
			desktop.removeListener(_deferredActivator);
			BinderImpl.this.didActivate();
		}	
	}

	
	public BindingExecutionInfoCollector getBindingExecutionInfoCollector(){
		BindingExecutionInfoCollectorFactory factory = BindingExecutionInfoCollectorFactory.getInstance();
		return factory==null?null:factory.getCollector(this);
	}
	
}
