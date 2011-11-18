/* BinderImpl.java

	Purpose:
		
	Description:
		
	History:
		Jul 29, 2011 6:08:51 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Form;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.Property;
import org.zkoss.bind.SimpleForm;
import org.zkoss.bind.Validator;
import org.zkoss.bind.converter.FormatedDateConverter;
import org.zkoss.bind.converter.FormatedNumberConverter;
import org.zkoss.bind.converter.ObjectBooleanConverter;
import org.zkoss.bind.converter.UriConverter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.CommandBinding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.tracker.impl.TrackerImpl;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Template;

/**
 * Implementation of Binder.
 * @author henrichen
 *
 */
public class BinderImpl implements Binder,BinderCtrl {
	
	
	private static final LogUtil log = new LogUtil(BinderImpl.class.getName());
	
	private static final Map<String, Converter> CONVERTERS = new HashMap<String, Converter>();
	private static final Map<String, Validator> VALIDATORS = new HashMap<String, Validator>();
	private static final Map<String, Object> RENDERERS = new HashMap<String, Object>();
	static {
		initConverter();
		initValidator();
	}

	//TODO can be defined in property-library
	private static void initConverter() {
		//TODO use library-property to initialize default user converters
		CONVERTERS.put("objectBoolean", new ObjectBooleanConverter());
		CONVERTERS.put("formatedDate", new FormatedDateConverter());
		CONVERTERS.put("formatedNumber", new FormatedNumberConverter());
		
		CONVERTERS.put("uri", new UriConverter());
	}
	
	//TODO can be defined in property-library
	private static void initValidator() {
		//TODO initial the system validator
		
	}
	
	//control keys
	public static final String BINDING = "$BINDING$"; //a binding
	public static final String BINDER = "$BINDER$"; //the binder
	public static final String BINDCTX = "$BINDCTX$"; //bind context
	public static final String VAR = "$VAR$"; //variable name in a collection
	public static final String ITERATION_VAR = "$INTERATION_VAR$"; //iteration status variable name in a collection
	public static final String VM = "$VM$"; //the associated view model
	public static final String QUE = "$QUE$"; //the associated event queue name
	public static final String NOTIFYS = "$NOTIFYS$"; //changed properties to be notified
	public static final String VALIDATES = "$VALIDATES$"; //properties to be validated
	public static final String SRCPATH = "$SRCPATH$"; //source path that trigger @DependsOn tracking
	
	public static final String IGNORE_TRACKER = "$IGNORE_TRACKER$"; //ignore adding currently binding to tracker, ex in init 
	
	//System Annotation, see lang-addon.xml
	private static final String SYSBIND = "$SYSBIND$"; //system binding annotation name
	private static final String RENDERER = "$R$"; //system renderer for binding
	private static final String LOADEVENT = "$LE$"; //load trigger event
	private static final String SAVEEVENT = "$SE$"; //save trigger event
	private static final String ACCESS = "$A$"; //access type (load|save|both), load is default
	private static final String CONVERTER = "$C$"; //system converter for binding
	private static final String VALIDATOR = "$V$"; //system validator for binding
	
	private static final String ON_POST_COMMAND = "onPostCommand";
	
	//private control key
	private static final String ZBIND_COMP_UUID = "$COMPUUID$";
	private static final String FORM_ID = "$FORM_ID$";
	
	//Command lifecycle result
	private static final int SUCCESS = 0;
	private static final int FAIL_VALIDATE = 1;
	
	private Component _rootComp;
	private BindEvaluatorX _eval;
	private PhaseListener _phaseListener;
	private Tracker _tracker;
	private final Component _dummyTarget = new AbstractComponent();//a dummy target for post command
	
	/* holds all binding in this binder */
	private final Map<Component, Map<String, List<Binding>>> _bindings; //comp -> (evtnm | _fieldExpr | formid) -> bindings

	private final FormBindingHelper _formBindingHelper;
	private final PropertyBindingHelper _propertyBindingHelper;
	
	/* the relation of form and inner save-bindings */
	private Map<Component, Set<SaveBinding>> _assocFormSaveBindings;//form comp -> savebindings	
	private Map<Component, Map<SaveBinding,Set<SaveBinding>>> _reversedAssocFormSaveBindings;////associated comp -> binding -> associated save bindings of _formSaveBindingMap
	

	private final Map<String, CommandEventListener> _listenerMap; //comp+evtnm -> eventlistener
	private final String _quename;
	private final String _quescope;
	private final EventListener<Event> _queueListener;
	
	//flag to keep info of current vm has converter method or not
	private boolean _hasGetConverterMethod = true;
	
	//flag to keep info of current vm has validator method or not
	private boolean _hasGetValidatorMethod = true;
	
	public BinderImpl(Component comp, Object vm, String qname, String qscope) {
		_rootComp = comp;
		_bindings = new HashMap<Component, Map<String, List<Binding>>>();
		_formBindingHelper = new FormBindingHelper(this); 
		_propertyBindingHelper = new PropertyBindingHelper(this);
		
		_assocFormSaveBindings = new HashMap<Component, Set<SaveBinding>>();
		_reversedAssocFormSaveBindings = new HashMap<Component, Map<SaveBinding,Set<SaveBinding>>>();
		
		_listenerMap = new HashMap<String, CommandEventListener>();
		
		//use same queue name if user was not specified, 
		//this means, binder in same scope, same queue, they will share the notification by "base"."property" 
		_quename = qname != null && !Strings.isEmpty(qname) ? qname : BinderImpl.QUE;
		_quescope = qscope != null && !Strings.isBlank(qscope) ? qscope : EventQueues.DESKTOP;
		
		//initial associated view model
		setViewModel(vm);
		
		_dummyTarget.addEventListener(ON_POST_COMMAND, new PostCommandListener());
		
		//subscribe change listener
		subscribeChangeListener(_quename, _quescope, _queueListener = new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				//only when a event in queue is our event
				if(event instanceof PropertyChangeEvent){
					final PropertyChangeEvent evt = (PropertyChangeEvent) event;
					BinderImpl.this.loadOnPropertyChange(evt.getBase(), evt.getPropertyName());
				}
			}
		});
	}
	
	//called when onPropertyChange is fired to the subscribed event queue
	private void loadOnPropertyChange(Object base, String prop) {
		log.debug("loadOnPropertyChange:base=[%s],prop=[%s]",base,prop);
		final Tracker tracker = getTracker();
		final Set<LoadBinding> bindings = tracker.getLoadBindings(base, prop);
		for(LoadBinding binding : bindings) {
			final BindContext ctx = BindContextUtil.newBindContext(this, binding, false, null, binding.getComponent(), null);
			if(binding instanceof PropertyBinding){
				BindContextUtil.setConverterArgs(this, binding.getComponent(), ctx, (PropertyBinding)binding);
			}
			
			log.debug("loadOnPropertyChange:binding.load(),binding=[%s],context=[%s]",binding,ctx);
			binding.load(ctx);
		}
	}
	
	public void setViewModel(Object vm) {
		_rootComp.setAttribute(BinderImpl.VM, vm);
		_hasGetConverterMethod = true;//reset to true
		_hasGetValidatorMethod = true;//reset to true
	}
	
	public Object getViewModel() {
		return _rootComp.getAttribute(BinderImpl.VM);
	}
	
	//Note: assume system converter is state-less
	public Converter getConverter(String name) {
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
			converter = CONVERTERS.get(name);
		}
		if (converter == null && name.indexOf('.') > 0) { //might be a class path
			try {
				converter = (Converter) Classes.newInstanceByThread(name);
				CONVERTERS.put(name, converter); //assume converter is state-less
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
		}
		if (converter == null) {
			throw new UiException("Cannot find the named converter:" + name);
		}
		return converter;
	}
	
	//Note: assume system validator is state-less
	public Validator getValidator(String name) {
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
			validator = VALIDATORS.get(name);
		}
		if (validator == null && name.indexOf('.') > 0) { //might be a class path
			try {
				validator = (Validator) Classes.newInstanceByThread(name);
				VALIDATORS.put(name, validator); //assume converter is state-less
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
		}
		if (validator == null) {
			throw new UiException("Cannot find the named validator:" + name);
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

	public void addFormBindings(Component comp, String idScript, String initExpr,
		String[] loadExprs, String[] saveExprs, String validator, Map<String, Object> args, Map<String, Object> validatorArgs) {
		final BindEvaluatorX eval = getEvaluatorX();
		final ExpressionX idExpr = eval.parseExpressionX(null, idScript, String.class);
		final String id = (String) eval.getValue(null, comp, idExpr);
		final Form form = doInitFormBinding(comp,initExpr,args);
		
		comp.setAttribute(FORM_ID, id);//mark it is a form component with the form id;
		comp.setAttribute(id, form);//after setAttribute, we can access fx in el.
		for(String loadExpr : loadExprs) {
			addLoadFormBinding(comp, id, form, loadExpr, args);
		}
		for(String saveExpr : saveExprs) {
			addSaveFormBinding(comp, id, form, saveExpr, validator, args, validatorArgs);
		}
	}
	
	private Form doInitFormBinding(Component comp, String initExpr, Map<String, Object> args) {
		if(initExpr==null) return new SimpleForm();
		final BindEvaluatorX eval = getEvaluatorX();
		final BindContext ctx = BindContextUtil.newBindContext(this, null, false, null, comp, null);
		ctx.setAttribute(IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el
		
		final ExpressionX expr = eval.parseExpressionX(ctx, initExpr, Object.class);
		final Object obj = eval.getValue(null, comp, expr);
		if(obj instanceof Form){
			return (Form)obj;
		}
		throw new UiException("the return value of init expression is not a From is :" + obj); 
	}
	
	//find and cache the uuid of component to a attribute, 
	//because if a component was detached (ex a listitem of listbox) the uuid will also be changed 
	//then binder is not able to remove its reference by last uuid 
	private String getBindUuid(Component comp){
		String uuid = (String)comp.getAttribute(ZBIND_COMP_UUID,Component.COMPONENT_SCOPE);
		if(uuid==null){
			uuid = comp.getUuid();
			comp.setAttribute(ZBIND_COMP_UUID, uuid,Component.COMPONENT_SCOPE);
		}
		return uuid;
	}
	
	private void removeBindUuid(Component comp){
		comp.removeAttribute(ZBIND_COMP_UUID,Component.COMPONENT_SCOPE);
	}

	private void addLoadFormBinding(Component comp, String formid, Form form, String loadExpr, Map<String, Object> args) {
		final LoadFormBindingImpl binding = new LoadFormBindingImpl(this, comp, formid, form, loadExpr, args); 
		final String attr = formid;
		addBinding(comp, attr, binding);
		
		final String command = binding.getCommandName();
		if (command == null) {
			final String bindDualId = getBindDualId(comp, attr);
			_formBindingHelper.addLoadFormPromptBinding(bindDualId, binding);
		} else {
			final boolean after = binding.isAfter();
			if (after) {
				_formBindingHelper.addLoadFormAfterBinding(command, binding);
			} else {
				_formBindingHelper.addLoadFormBeforeBinding(command, binding);
			}
		}
	}

	private void addSaveFormBinding(Component comp, String formid, Form form, String saveExpr, String validator, Map<String, Object> args, Map<String, Object> validatorArgs) {
		//register event Command listener 
		final SaveFormBindingImpl binding = new SaveFormBindingImpl(this, comp, formid, form, saveExpr, validator, args, validatorArgs);
//		final String formid = form.getId();
		final String command = binding.getCommandName();
		if (command == null) {
			throw new UiException("Form "+formid+" must be saved by a Command: "+ binding.getPropertyString());
		}
		addBinding(comp, formid, binding);
		
		final boolean after = binding.isAfter();
		if (after) {
			_formBindingHelper.addSaveFormAfterBinding(command, binding);
		} else {
			_formBindingHelper.addSaveFormBeforeBinding(command, binding);
		}
	}

	public void addPropertyBinding(Component comp, String attr, String initExpr, 
			String[] loadExprs, String[] saveExprs, String converter, String validator, 
			Map<String, Object> args, Map<String, Object> converterArgs, Map<String, Object> validatorArgs) {
		if (Strings.isBlank(converter)) {
			converter = getSystemConverter(comp, attr);
			if (converter != null) {
				converter = "'"+converter+"'";
			}
		}
		if (Strings.isBlank(validator)) {
			validator = getSystemValidator(comp, attr);
			if (validator != null) {
				validator = "'"+validator+"'";
			}
		}
		
		doInitPropertyBinding(comp,attr,initExpr,converter,args,converterArgs);
		
		for(String loadExpr : loadExprs) {
			addLoadBinding(comp, attr, loadExpr, converter, args, converterArgs);
		}
		for(String saveExpr : saveExprs) {
			addSaveBinding(comp, attr, saveExpr, converter, validator, args,converterArgs, validatorArgs);
		}
		initRendererIfAny(comp);
	}

	private void doInitPropertyBinding(Component comp, String attr,
			String initExpr, String converter, Map<String, Object> args,Map<String, Object> converterArgs) {
		if(initExpr==null) return;
		
		InitPropertyBindingImpl binding = new InitPropertyBindingImpl(this,comp,attr,initExpr,converter,args,converterArgs);
		final BindContext ctx = BindContextUtil.newBindContext(this, binding, false, null, comp, null);
		ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el
		
		//needs converter args
		if(binding instanceof PropertyBinding){
			BindContextUtil.setConverterArgs(this, binding.getComponent(), ctx, (PropertyBinding)binding);
		}
		
		binding.load(ctx);
	}

	private String getSystemConverter(Component comp, String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(attr, BinderImpl.SYSBIND);
		if (ann != null) {
			final Map<?, ?> attrs = ann.getAttributes(); //(tag, tagExpr)
			return (String) attrs.get(BinderImpl.CONVERTER); //system converter if exists
		}
		return null;
	}
	
	private String getSystemValidator(Component comp, String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(attr, BinderImpl.SYSBIND);
		if (ann != null) {
			final Map<?, ?> attrs = ann.getAttributes(); //(tag, tagExpr)
			return (String) attrs.get(BinderImpl.VALIDATOR); //system validator if exists
		}
		return null;
	}
	
	private void initRendererIfAny(Component comp) {
		//check if exists template
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(BinderImpl.SYSBIND);
		final Map<?, ?> attrs = ann != null ? ann.getAttributes() : null; //(tag, tagExpr)
		final Template tm = comp.getTemplate("model");
		if (tm == null) { //no template
			return;
		}
		
		final Object installed = comp.getAttribute(BinderImpl.VAR);
		if (installed != null) { //renderer was set already init
			return;
		}
		
		final String var = (String) tm.getParameters().get("var");
		final String varnm = var == null ? "each" : var; //var is not specified, default to "each"
		comp.setAttribute(BinderImpl.VAR, varnm);
		
		final String itervar = (String) tm.getParameters().get("status");
		final String itervarnm = itervar == null ? "iterationStatus" : itervar; //provide default value if not specified
		comp.setAttribute(BinderImpl.ITERATION_VAR, itervarnm);

		if (attrs != null) {
			final String rendererName = (String) attrs.get(BinderImpl.RENDERER); //renderer if any
			//setup renderer
			if (rendererName != null) { //there was system renderer
				final String[] values = rendererName.split("=", 2);
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
					}
				}
			}
		}
	}
	
	private void addLoadBinding(Component comp, String attr, String loadExpr, String converter, 
			Map<String, Object> args, Map<String, Object> converterArgs) {
		//check attribute _accessInfo natural characteristics to register Command event listener 
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(attr, BinderImpl.SYSBIND);
		//check which attribute of component should load to component on which event.
		//the event is usually a engine lifecycle event.
		//ex, listbox's 'selectedIndex' should be loaded to component on 'onAfterRender'
		String evtnm = null;
		if (ann != null) {
			final Map<?, ?> attrs = ann.getAttributes(); //(tag, tagExpr)
			final String rw = (String) attrs.get(BinderImpl.ACCESS); //_accessInfo right, "both|save|load", default to load
			if (rw != null && !"both".equals(rw) && !"load".equals(rw)) { //save only, skip
				return;
			}
			evtnm = (String) attrs.get(BinderImpl.LOADEVENT); //check trigger event for loading
		}
		
		LoadPropertyBindingImpl binding = new LoadPropertyBindingImpl(this, comp, attr, loadExpr, converter, args, converterArgs);
		addBinding(comp, attr, binding);
		
		final String command = binding.getCommandName();
		if (command == null) {
			log.debug("add event(prompt)-load-binding: comp=[%s],attr=[%s],expr=[%s],evtnm=[%s],converter=[%s]", comp,attr,loadExpr,evtnm,converter);
			if (evtnm != null) { //special case, load on an event, ex, onAfterRender of listbox on selectedItem
				addEventCommandListenerIfNotExists(comp, evtnm, null); //local command
				final String bindDualId = getBindDualId(comp, evtnm);
				_propertyBindingHelper.addLoadEventBinding(comp, bindDualId, binding);
			}
			//if no command , always add to prompt binding, a prompt binding will be load when , 
			//1.load a component property binding
			//2.property change (TODO, DENNIS, ISSUE, I think loading of property change is triggered by tracker in loadOnPropertyChange, not by prompt-binding 
			final String bindDualId = getBindDualId(comp, attr);
			_propertyBindingHelper.addLoadPromptBinding(comp, bindDualId, binding);
		} else {
			final boolean after = binding.isAfter();
			log.debug("add command-load-binding: comp=[%s],attr=[%s],expr=[%s],after=[%s],converter=[%s]", comp,attr,loadExpr,after,converter);
			if (after) {
				_propertyBindingHelper.addLoadAfterBinding(command, binding);
			} else {
				_propertyBindingHelper.addLoadBeforeBinding(command, binding);
			}
		}
	}
	
	private void addSaveBinding(Component comp, String attr, String saveExpr, String converter, String validator, 
			Map<String, Object> args, Map<String, Object> converterArgs, Map<String, Object> validatorArgs) {
		//check attribute _accessInfo natural characteristics to register Command event listener 
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(attr, BinderImpl.SYSBIND);
		//check which attribute of component should fire save on which event.
		//ex, listbox's 'selectedIndex' should be loaded to component on 'onSelect'
		//ex, checkbox's 'checked' should be saved to bean on 'onCheck'
		String evtnm = null;
		if (ann != null) {
			final Map<?, ?> attrs = ann.getAttributes(); //(tag, tagExpr)
			final String rw = (String) attrs.get(BinderImpl.ACCESS); //_accessInfo right, "both|save|load", default to load
			if (!"both".equals(rw) && !"save".equals(rw)) { //load only, skip
				return;
			}
			evtnm = (String) attrs.get(BinderImpl.SAVEEVENT); //check trigger event for saving
		}
		if (evtnm == null) { //no trigger event...
			return;
		}

		final SavePropertyBindingImpl binding = new SavePropertyBindingImpl(this, comp, attr, saveExpr, converter, validator, args, converterArgs, validatorArgs);
		addBinding(comp, attr, binding);
		
		final String command = binding.getCommandName();
		if (command == null) { //save on event
			log.debug("add event(prompt)-save-binding: comp=[%s],attr=[%s],expr=[%s],evtnm=[%s],converter=[%s],validate=[%s]", comp,attr,saveExpr,evtnm,converter,validator);
			addEventCommandListenerIfNotExists(comp, evtnm, null); //local command
			final String bindDualId = getBindDualId(comp, evtnm);
			_propertyBindingHelper.addSavePromptBinding(comp, bindDualId, binding);
		} else {
			final boolean after = binding.isAfter();
			log.debug("add command-save-binding: comp=[%s],att=r[%s],expr=[%s],after=[%s],converter=[%s],validate=[%s]", comp,attr,saveExpr,after,converter,validator);
			if (after) {
				_propertyBindingHelper.addSaveAfterBinding(command, binding);
			} else {
				_propertyBindingHelper.addSaveBeforeBinding(command, binding);
			}
		}
	}

	public void addCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> args) {
		final CommandBindingImpl binding = new CommandBindingImpl(this, comp, evtnm, commandExpr, args);
		addBinding(comp, evtnm, binding);
		addEventCommandListenerIfNotExists(comp, evtnm, binding);
	}
	
	//associate event to CommandBinding
	private void addEventCommandListenerIfNotExists(Component comp, String evtnm, CommandBinding command) {
		final String bindDualId = getBindDualId(comp, evtnm);
		CommandEventListener listener = _listenerMap.get(bindDualId);
		if (listener == null) {
			listener = new CommandEventListener();
			comp.addEventListener(evtnm, listener);
			_listenerMap.put(bindDualId, listener);
		}
		//DENNIS, this method will call by
		//1.addPropertyBinding -> command is null -> means prompt when evtnm is fired.
		//2.addCommandBinding -> command is not null -> means trigger command when evtnm is fired.
		//ex, <textbox value="@bind(vm.firstname)" onChange="@bind('save')"/>
		//and in current spec, we only allow one command to be executed in one event. 
		listener.setCommand(command);
	}
	
	private void removeEventCommandListenerIfExists(Component comp, String evtnm) {
		final String bindDualId = getBindDualId(comp, evtnm);
		final CommandEventListener listener = _listenerMap.remove(bindDualId);
		if (listener != null) {
			comp.removeEventListener(evtnm, listener);
		}
	}

	private class CommandEventListener implements EventListener<Event> { //event used to trigger command
		private boolean _prompt = false;
		private CommandBinding _commandBinding;
		private void setCommand(CommandBinding command) {
			//if 1.add a non-null command then 2.add a null command, the prompt will be true and commandBinding is not null 
			//ex, <textbox value="@bind(vm.firstname)" onChange="@bind('save')"/>
			if (!_prompt && command == null) {
				_prompt = true;
			} else {
				_commandBinding = command;
			}
		}
		
		public void onEvent(Event event) throws Exception {
			//command need to be confirmed shall be execute first!
			//must sort the command sequence?
			final Component comp = event.getTarget();
			final String evtnm = event.getName();
			final Set<Property> notifys = new LinkedHashSet<Property>();
			int result = SUCCESS; //command execution result, default to success
			String command = null;
			log.debug("====Start command event [%s]",event);
			if (_commandBinding != null) {
				final BindEvaluatorX eval = getEvaluatorX();
				command = (String) eval.getValue(null, comp, ((CommandBindingImpl)_commandBinding).getCommand());
				final Map<String, Object> args = BindEvaluatorXUtil.evalArgs(eval, comp, _commandBinding.getArgs());
				result = BinderImpl.this.doCommand(comp, command, event, args, notifys/*, false*/);
			}
			//check confirm
			switch(result) {
//				case BinderImpl.FAIL_CONFIRM:
//					BinderImpl.this.doFailConfirm();
//					break;
				case BinderImpl.FAIL_VALIDATE:
					log.debug("There are [%s] property need to be notify after fail validate",notifys.size());
					fireNotifyChanges(notifys); //still has to go through notifyChange to show error message
					return;
			}
			//confirm might cancel the operation, on event binding must be the last one to be done 
			if (_prompt) {
				log.debug("This is a prompt command");
				if (command != null) { //command has own VALIDATE phase, don't do validate again
					BinderImpl.this.doSaveEventNoValidate(comp, event, notifys); //save on event without validation
				} else {
					//TODO, DENNIS, ISSUE, What is the Spec of validation of prompt save?
					//1.if a validation failed, should we break it?
					//2.if a validation passed, should we save it?
					//3.if any validation failed, should we do load-binding 
					BinderImpl.this.doSaveEvent(comp, event, notifys); //save on event
				}
				BinderImpl.this.doLoadEvent(comp, evtnm); //load on event
			}
			log.debug("There are [%s] property need to be notify after command",notifys.size());
			fireNotifyChanges(notifys);
			log.debug("====End command event [%s]",event);
		}
	}
	
	public void sendCommand(String command, Map<String, Object> args) {
		final Set<Property> notifys = new HashSet<Property>();
		//args come from user, we don't eval it. 
		doCommand(_rootComp, command, null, args, notifys);
		fireNotifyChanges(notifys);
	}

	private void fireNotifyChanges(Set<Property> notifys) {
		for(Property prop : notifys) {
			notifyChange(prop.getBase(), prop.getProperty());
		}
	}
	
	public void postCommand(String command, Map<String, Object> args) {
		final Event evt = new Event(ON_POST_COMMAND,_dummyTarget,new Object[]{command,args});
		Events.postEvent(evt);
	}
	
	//comp the component that trigger the command
	//major life cycle of binding (on event trigger)
	//command is the command name after evaluation
	//evt event that fire this command
	//args the passed in argument for executing command
	//notifies container for properties that is to be notifyChange
	//skipConfirm whether skip checking confirm 
	//return properties to be notified change
	private int doCommand(Component comp, String command, Event evt, Map<String, Object> commandArgs, Set<Property> notifys/*, boolean skipConfirm*/) {
		final String evtnm = evt == null ? null : evt.getName();
		log.debug("Start doCommand comp=[%s],command=[%s],evtnm=[%s]",comp,command,evtnm);
		BindContext ctx = BindContextUtil.newBindContext(this, null, false, command, comp, evt);
		BindContextUtil.setCommandArgs(this, comp, ctx, commandArgs);
		try {
			doPrePhase(Phase.COMMAND, ctx); //begin of Command
			boolean success = true;
			
			//validate
			success = doValidate(comp, command, evt, ctx, notifys);
			if (!success) {
				return FAIL_VALIDATE;
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
		
			log.debug("End doCommand");
			return SUCCESS;
		} finally {
			doPostPhase(Phase.COMMAND, ctx); //end of Command
		}
		
	}
	
	/*package*/ void doPrePhase(Phase phase, BindContext ctx) {
		if (_phaseListener != null) {
			_phaseListener.prePhase(phase, ctx);
		}
	}
	
	/*package*/ void doPostPhase(Phase phase, BindContext ctx) {
		if (_phaseListener != null) {
			_phaseListener.postPhase(phase, ctx);
		}
	}
	//for event -> prompt only, no command 
	private void doSaveEventNoValidate(Component comp, Event evt, Set<Property> notifys) {
		final String evtnm = evt == null ? null : evt.getName();
		log.debug("doSaveEventNoValidate comp=[%s],evtnm=[%s],notifys=[%s]",comp,evtnm,notifys);
		final String bindDualId = getBindDualId(comp, evtnm);
		_propertyBindingHelper.doSaveEventNoValidate(bindDualId, comp, evt, notifys);
	}
	//for event -> prompt only, no command 
	private boolean doSaveEvent(Component comp, Event evt, Set<Property> notifys) {
		final String evtnm = evt == null ? null : evt.getName(); 
		log.debug("doSaveEvent comp=[%s],evtnm=[%s],notifys=[%s]",comp,evtnm,notifys);
		final String bindDualId = getBindDualId(comp, evtnm);
		return _propertyBindingHelper.doSaveEvent(bindDualId, comp, evt, notifys);
	}
	
	//for event -> prompt only, no command
	private void doLoadEvent(Component comp, String evtnm) {
		log.debug("doLoadEvent comp=[%s],evtnm=[%s]",comp,evtnm);
		final String bindDualId = getBindDualId(comp, evtnm); 
		_propertyBindingHelper.doLoadEvent(bindDualId, comp, evtnm);
	}
	
	//doCommand -> doValidate
	private boolean doValidate(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		final Set<Property> validates = new HashSet<Property>();
		try {
			log.debug("doValidate comp=[%s],command=[%s],evt=[%s],context=[%s]",comp,command,evt,ctx);
			doPrePhase(Phase.VALIDATE, ctx);
			
			//we collect properties that need to be validated, than validate one-by-one
			ValidationHelper vHelper = new ValidationHelper(this,new ValidationHelper.InfoProvider() {
				public Map<String, List<SaveFormBinding>> getSaveFormBeforeBindings() {
					return _formBindingHelper.getSaveFormBeforeBindings();
				}		
				public Map<String, List<SaveFormBinding>> getSaveFormAfterBindings() {
					return _formBindingHelper.getSaveFormAfterBindings();
				}
				public Map<String, List<SavePropertyBinding>> getSaveEventBindings() {
					return _propertyBindingHelper.getSaveEventBindings();
				}
				public Map<String, List<SavePropertyBinding>> getSaveBeforeBindings() {
					return _propertyBindingHelper.getSaveBeforeBindings();
				}
				public Map<String, List<SavePropertyBinding>> getSaveAfterBindings() {
					return _propertyBindingHelper.getSaveAfterBindings();
				}
				public String getBindDualId(Component comp, String attr) {
					return BinderImpl.this.getBindDualId(comp,attr);
				}
			});
			
			//collect Property of special command for validation in validates
			vHelper.collectSaveBefore(comp, command, evt, validates);
			vHelper.collectSaveAfter(comp, command, evt, validates);
			if (evt != null) {
				//also collect the validate on the prompt save-bind that is related to evt 
				vHelper.collectSaveEvent(comp, command, evt, validates);
			}
			
			//do validation (defined by application)
			if (validates.isEmpty()) {
				return true;
			} else {
				log.debug("doValidate validates=[%s]",validates);
				boolean valid = true;
				Map<String,Property[]> properties = _propertyBindingHelper.toCollectedProperties(validates);
				valid &= vHelper.validateSaveBefore(comp, command, properties,valid,notifys);
				valid &= vHelper.validateSaveAfter(comp, command, properties,valid,notifys);
				if (evt != null) {
					//also collect the validate on the prompt save-bind that is related to evt 
					valid &= vHelper.validateSaveEvent(comp, command, evt, properties,valid,notifys);
				}
				return valid;
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		} finally {
			doPostPhase(Phase.VALIDATE, ctx);
		}
	}
	
	private void doExecute(Component comp, String command, Map<String, Object> commandArgs, BindContext ctx, Set<Property> notifys) {
		try {
			log.debug("before doExecute comp=[%s],command=[%s],notifys=[%s]",comp,command,notifys);
			doPrePhase(Phase.EXECUTE, ctx);
			
			final Object base = getViewModel();
			
			//TODO, DENNIS, FEATURE,
			//search if there is any method with command annotation. @Command(command), if yes, call this method
			//command = searchMethodByAnnotation(base,command);
			
			
			Method method = null;
			Object[] param = null;
			try { //try one without arguments
				method = Classes.getMethodInPublic(base.getClass(), command, null);
				param = new Object[0];
			} catch (NoSuchMethodException e) { //try one with Map arguments 
				try {
					method = Classes.getMethodInPublic(base.getClass(), command, new Class[] {Map.class});
					//check if method has a Map argument, then will call it with args
					param = new Object[] {commandArgs == null ? Collections.emptyMap() : commandArgs};
				} catch (NoSuchMethodException e1) { //try one with BindContext argument
					try {
						method = Classes.getMethodInPublic(base.getClass(), command, new Class[] {BindContext.class});
						param = new Object[] {ctx};
					} catch (NoSuchMethodException e2) {
						//TODO, DENNIS , if not method to execute, should we just ignore it?
						throw UiException.Aide.wrap(e);
					}
				}
			}
			if (method != null) {
				try {
					method.invoke(base, param);
				} catch (Exception e) {
					throw UiException.Aide.wrap(e);
				}
				notifys.addAll(BindELContext.getNotifys(method, base, (String)null, (Object) null)); //collect notifyChange
			}
			log.debug("after doExecute notifys=[%s]",notifys);
		} finally {
			doPostPhase(Phase.EXECUTE, ctx);
		}
	}
	
	//doCommand -> doSaveBefore
	private void doSaveBefore(Component comp, String command, Event evt,  BindContext ctx, Set<Property> notifys) {
		log.debug("doSaveBefore, comp=[%s],command=[%s],evt=[%s],notifys=[%s]",comp,command,evt,notifys);
		try {
			doPrePhase(Phase.SAVE_BEFORE, ctx);		
			_propertyBindingHelper.doSavePropertyBefore(comp, command, evt, notifys);
			_formBindingHelper.doSaveFormBefore(comp, command, evt, notifys);
		} finally {
			doPostPhase(Phase.SAVE_BEFORE, ctx);
		}
	}

	
	private void doSaveAfter(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		log.debug("doSaveAfter, comp=[%s],command=[%s],evt=[%s],notifys=[%s]",comp,command,evt,notifys);
		try {
			doPrePhase(Phase.SAVE_AFTER, ctx);
			_propertyBindingHelper.doSavePropertyAfter(comp, command, evt, notifys);
			_formBindingHelper.doSaveFormAfter(comp, command, evt, notifys);
		} finally {
			doPostPhase(Phase.SAVE_AFTER, ctx);
		}		
		
	}

	
	private void doLoadBefore(Component comp, String command, BindContext ctx) {
		log.debug("doLoadBefore, comp=[%s],command=[%s]",comp,command);
		try {
			doPrePhase(Phase.LOAD_BEFORE, ctx);		
			_propertyBindingHelper.doLoadPropertyBefore(comp, command);
			_formBindingHelper.doLoadFormBefore(comp, command);
		} finally {
			doPostPhase(Phase.LOAD_BEFORE, ctx);
		}
	}
	
	private void doLoadAfter(Component comp, String command, BindContext ctx) {
		log.debug("doLoadAfter, comp=[%s],command=[%s]",comp,command);
		try {
			doPrePhase(Phase.LOAD_AFTER, ctx);
			_propertyBindingHelper.doLoadPropertyAfter(comp, command);
			_formBindingHelper.doLoadFormAfter(comp, command);
		} finally {
			doPostPhase(Phase.LOAD_AFTER, ctx);
		}		

	}
	
	/**
	 * Remove all bindings that associated with the specified component.
	 * @param comp the component
	 */
	public void removeBindings(Component comp) {
		if(_rootComp==comp){
			//the binder component was detached, unregister queue
			unsubscribeChangeListener(_quename, _quescope, _queueListener);
		}
		
		final Map<String, List<Binding>> attrMap = _bindings.remove(comp);
		if (attrMap != null) {
			final Set<Binding> removed = new HashSet<Binding>();
			for(Entry<String, List<Binding>> entry : attrMap.entrySet()) {
				final String key = entry.getKey(); 
//				removeEventCommandListenerIfExists(comp, key); //_listenerMap; //comp+evtnm -> eventlistener
				removeBindings(comp, key);
				removed.addAll(entry.getValue());
			}
			if (!removed.isEmpty()) {
				removeBindings(removed);
			}
		}
		
		removeFormAssociatedSaveBinding(comp);
		
		//remove trackings
		TrackerImpl tracker = (TrackerImpl) getTracker();
		tracker.removeTrackings(comp);

		comp.removeAttribute(BINDER);
		removeBindUuid(comp);
	}

	/**
	 * Remove all bindings that associated with the specified component and key (_fieldExpr|evtnm|formid).
	 * @param comp the component
	 * @param key can be component attribute, event name, or form id
	 */
	public void removeBindings(Component comp, String key) {
		removeEventCommandListenerIfExists(comp, key); //_listenerMap; //comp+evtnm -> eventlistener
		
		final String bindDualId = getBindDualId(comp, key);
		final Set<Binding> removed = new HashSet<Binding>();
		
		_formBindingHelper.removeBindings(bindDualId,removed);
		_propertyBindingHelper.removeBindings(bindDualId, removed);

		removeBindings(removed);
	}

	private void removeBindings(Collection<Binding> removed) {
		_formBindingHelper.removeBindings(removed);
		_propertyBindingHelper.removeBindings(removed);
	}
	
	private void addBinding(Component comp, String attr, Binding binding) {
		Map<String, List<Binding>> attrMap = _bindings.get(comp);
		if (attrMap == null) {
			attrMap = new HashMap<String, List<Binding>>(); 
			_bindings.put(comp, attrMap);
		}
		List<Binding> bindings = attrMap.get(attr);
		if (bindings == null) {
			bindings = new ArrayList<Binding>();
			attrMap.put(attr, bindings);
		}
		bindings.add(binding);
		
		//associate component with this binder, which means, one component can only bind by one binder
		comp.setAttribute(BINDER, this);
	}

	public Tracker getTracker() {
		if (_tracker == null) {
			_tracker = new TrackerImpl();
		}
		return _tracker;
	}
	
	/**
	 * Internal Use only.
	 */
	public void loadComponent(Component comp) {
		loadComponentProperties(comp);
		for(Component kid = comp.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
			loadComponent(kid); //recursive
		}
	}
	
	private void loadComponentProperties(Component comp) {
		
		final Map<String, List<Binding>> compBindings = _bindings.get(comp);
		if (compBindings != null) {
			for(String key : compBindings.keySet()) {
				final String bindDualId = getBindDualId(comp, key);
				_formBindingHelper.loadComponentProperties(comp,bindDualId);
			}
			for(String key : compBindings.keySet()) {
				final String bindDualId = getBindDualId(comp, key);
				_propertyBindingHelper.loadComponentProperties(comp,bindDualId);
			}
		}
	}
	
	public void notifyChange(Object base, String attr) {
		log.debug("notifyChange base=[%s],attr=[%s]",base,attr);
		getEventQueue().publish(new PropertyChangeEvent("onPropertyChange", _rootComp, base, attr));
	}
	
	public void setPhaseListener(PhaseListener listener) {
		_phaseListener = listener;
	}
	
	PhaseListener getPhaseListener(){
		return _phaseListener;
	}

	private void subscribeChangeListener(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, true);
		que.subscribe(listener);
	}
	
	private void unsubscribeChangeListener(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, false);
		if(que!=null){
			que.unsubscribe(listener);
		}
	}
	
	private class PropertyChangeEvent extends Event {
		private static final long serialVersionUID = 201109091736L;
		private final Object _base;
		private final String _propName;

		public PropertyChangeEvent(String name, Component comp, Object base, String propName) {
			super(name, comp);
			this._base = base;
			this._propName = propName;
		}

		public Object getBase() {
			return _base;
		}

		public String getPropertyName() {
			return _propName;
		}
	}
	
	protected EventQueue<Event> getEventQueue() {
		return EventQueues.lookup(_quename, _quescope, true);
	}

	// create a unique id base on component's uuid and attr
	private String getBindDualId(Component comp, String attr) {
		final String uuid = getBindUuid(comp);
		return uuid+"#"+attr;
	}

	private class PostCommandListener implements EventListener<Event>{
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
	public void addFormAssociatedSaveBinding(Component associatedComp, String formId, SaveBinding saveBinding){
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
		Set<SaveBinding> bindings = _assocFormSaveBindings.get(comp);
		if(bindings==null){
			return Collections.emptySet();
		}
		return new LinkedHashSet<SaveBinding>(bindings);//keep the order
	}
	
}
