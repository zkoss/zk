/* BinderImpl.java

	Purpose:
		
	Description:
		
	History:
		Jul 29, 2011 6:08:51 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormLegacy;
import org.zkoss.bind.FormLegacyExt;
import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.Property;
import org.zkoss.bind.PropertyChangeEvent;
import org.zkoss.bind.SimpleForm;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DefaultCommand;
import org.zkoss.bind.annotation.DefaultGlobalCommand;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.NotifyCommands;
import org.zkoss.bind.annotation.SmartNotifyChange;
import org.zkoss.bind.init.ViewModelAnnotationResolvers;
import org.zkoss.bind.init.ZKBinderPhaseListeners;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ViewModelProxyObject;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.CommandBinding;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.InitChildrenBinding;
import org.zkoss.bind.sys.InitFormBinding;
import org.zkoss.bind.sys.InitPropertyBinding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.bind.sys.LoadFormBinding;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.DebuggerFactory;
import org.zkoss.bind.sys.debugger.impl.DefaultAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.impl.DefaultExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.AddBindingInfo;
import org.zkoss.bind.sys.debugger.impl.info.AddCommandBindingInfo;
import org.zkoss.bind.sys.debugger.impl.info.CommandInfo;
import org.zkoss.bind.sys.debugger.impl.info.EventInfo;
import org.zkoss.bind.sys.debugger.impl.info.NotifyChangeInfo;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.tracker.impl.TrackerImpl;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.bind.xel.zel.ImplicitObjectELResolver;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.CacheMap;
import org.zkoss.util.EmptyCacheMap;
import org.zkoss.util.Pair;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Deferrable;
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
public class BinderImpl implements Binder, BinderCtrl, Serializable {
	private static final long serialVersionUID = 1463169907348730644L;

	private static final Logger _log = LoggerFactory.getLogger(BinderImpl.class);

	private static final Map<String, Object> RENDERERS = new HashMap<String, Object>();

	//events for dummy target
	private static final String ON_POST_COMMAND = "onPostCommand";
	private static final String ON_VMSGS_CHANGED = "onVMsgsChanged";

	//ZK-4761: for debug mode only
	public static final boolean DISABLE_METHOD_CACHE;
	private static final String DISABLE_METHOD_CACHE_PROP = "org.zkoss.bind.disableMethodCache";

	static {
		DISABLE_METHOD_CACHE = Boolean.parseBoolean(Library.getProperty(DISABLE_METHOD_CACHE_PROP, "false"));
	}

	private static final Map<Class<?>, List<Method>> _initMethodCache = DISABLE_METHOD_CACHE ? new EmptyCacheMap() : new CacheMap<Class<?>, List<Method>>(600,
			CacheMap.DEFAULT_LIFETIME); //class,list<init method>
	private static final Map<Class<?>, List<Method>> _destroyMethodCache = DISABLE_METHOD_CACHE ? new EmptyCacheMap() : new CacheMap<Class<?>, List<Method>>(600,
			CacheMap.DEFAULT_LIFETIME); //class,list<destroy method>

	private static final Map<Class<?>, Map<String, CachedItem<Method>>> _commandMethodCache = DISABLE_METHOD_CACHE ? new EmptyCacheMap() : new CacheMap<Class<?>, Map<String, CachedItem<Method>>>(
			200, CacheMap.DEFAULT_LIFETIME); //class,map<command, null-able command method>

	private static final Map<Class<?>, Map<String, CachedItem<Method>>> _globalCommandMethodCache = DISABLE_METHOD_CACHE ? new EmptyCacheMap() : new CacheMap<Class<?>, Map<String, CachedItem<Method>>>(
			200, CacheMap.DEFAULT_LIFETIME); //class,map<command, null-able command method>

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
			final Command cmd = ViewModelAnnotationResolvers.getAnnotation(method, Command.class);
			return cmd == null ? null : cmd.value();
		}

		public boolean isDefaultMethod(Method method) {
			return ViewModelAnnotationResolvers.getAnnotation(method, DefaultCommand.class) != null;
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
			final GlobalCommand cmd = ViewModelAnnotationResolvers.getAnnotation(method, GlobalCommand.class);
			return cmd == null ? null : cmd.value();
		}

		public boolean isDefaultMethod(Method method) {
			return ViewModelAnnotationResolvers.getAnnotation(method, DefaultGlobalCommand.class) != null;
		}
	};

	private Component _rootComp;
	private BindEvaluatorX _eval;
	private transient List<PhaseListener> _phaseListeners;
	private Tracker _tracker;
	private final Component _dummyTarget = new AbstractComponent(); //a dummy target for post command

	/* holds all binding in this binder */
	private final Map<Component, Map<String, List<Binding>>> _bindings; //comp -> (evtnm | _fieldExpr | formid) -> bindings

	private final FormBindingHandler _formBindingHandler;
	private final PropertyBindingHandler _propertyBindingHandler;
	private final ChildrenBindingHandler _childrenBindingHandler;
	private final ReferenceBindingHandler _refBindingHandler;

	/* the relation of form and inner save-bindings */
	private Map<Component, Set<SaveBinding>> _assocFormSaveBindings; //form comp -> savebindings	
	private Map<Component, Map<SaveBinding, Set<SaveBinding>>> _reversedAssocFormSaveBindings; //associated comp -> binding -> associated save bindings of _formSaveBindingMap

	private final Map<BindingKey, CommandEventListener> _listenerMap; //comp+evtnm -> eventlistener
	private final String _quename;
	private final String _quescope;
	private final QueueListener _queueListener;

	private ValidationMessages _validationMessages;
	private Set<BindingKey> _hasValidators; //the key to mark they have validator

	private final Map<Component, Map<String, TemplateResolver>> _templateResolvers; //comp,<attr,resolver>

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

	private transient Map<Object, Set<String>> _saveFormFields;

	private final ImplicitObjectContributor _implicitContributor;

	private static final String REF_HANDLER_CLASS_PROP = "org.zkoss.bind.ReferenceBindingHandler.class";

	//ZK-3133 to cache MatchMedia annotation values
	private transient Map<String, Method> _matchMediaValues;

	//ZK-4791
	private static final Pattern CALL_OTHER_VM_COMMAND_PATTERN = Pattern.compile("\\$([^.]*)\\..*$");

	// ZK-4855, internal used only
	public static final String ZKFORMPROXYNOTIFIEDKEY = "$$zkFormProxyNotified$$";

	public BinderImpl() {
		this(null, null);
	}

	public BinderImpl(String qname, String qscope) {
		_bindings = new HashMap<Component, Map<String, List<Binding>>>();
		_formBindingHandler = new FormBindingHandler();
		_propertyBindingHandler = new PropertyBindingHandler();
		_childrenBindingHandler = new ChildrenBindingHandler();

		_formBindingHandler.setBinder(this);
		_propertyBindingHandler.setBinder(this);
		_childrenBindingHandler.setBinder(this);

		_refBindingHandler = MiscUtil.newInstanceFromProperty(REF_HANDLER_CLASS_PROP, null,
				ReferenceBindingHandler.class);
		if (_refBindingHandler != null) {
			_refBindingHandler.setBinder(this);
		}

		//zk-1548
		_implicitContributor = new ImplicitObjectContributorImpl();

		_assocFormSaveBindings = new HashMap<Component, Set<SaveBinding>>();
		_reversedAssocFormSaveBindings = new HashMap<Component, Map<SaveBinding, Set<SaveBinding>>>();

		_hasValidators = new HashSet<BindingKey>();
		_templateResolvers = new HashMap<Component, Map<String, TemplateResolver>>();
		_listenerMap = new HashMap<BindingKey, CommandEventListener>();
		//use same queue name if user was not specified, 
		//this means, binder in same scope, same queue, they will share the notification by "base"."property" 
		_quename = qname != null && !Strings.isEmpty(qname) ? qname : DEFAULT_QUEUE_NAME;
		_quescope = qscope != null && !Strings.isBlank(qscope) ? qscope : DEFAULT_QUEUE_SCOPE;
		_queueListener = new QueueListener();
		init();
	}

	private void init() {
		_phaseListeners = new LinkedList<PhaseListener>(ZKBinderPhaseListeners.getSystemPhaseListeners());

		String clz = Library.getProperty(PHASE_LISTENER_CLASS_KEY);
		if (!Strings.isEmpty(clz)) {
			try {
				addPhaseListener((PhaseListener) Classes.forNameByThread(clz).newInstance());
			} catch (Exception e) {
				_log.error("Error when initial phase listener:" + clz, e);
			}
		}
	}
	
	/**
	 * @since 6.0.1
	 */
	public void init(Component comp, Object viewModel, Map<String, Object> initArgs) {
		if (_init)
			throw new UiException("binder is already initialized");
		_init = true;
		
		_rootComp = comp;
		//initial associated view model
		setViewModel(viewModel);
		_dummyTarget.addEventListener(ON_POST_COMMAND, new PostCommandListener());
		_dummyTarget.addEventListener(ON_VMSGS_CHANGED, new VMsgsChangedListener());
		
		initQueue();

		if (viewModel instanceof Composer<?> && !(viewModel instanceof BindComposer<?>)) { //do we need to warn this?
			//show a warn only
			_log.warn("you are using a composer [{}] as a view model", viewModel);
		}
		new AbstractAnnotatedMethodInvoker<Init>(Init.class, _initMethodCache) {
			protected boolean shouldLookupSuperclass(Init annotation) {
				return annotation.superclass();
			}
		}.invokeMethod(this, initArgs);

		initActivator();
		//F80 - store subtree's binder annotation count
		if (comp instanceof ComponentCtrl)
			((ComponentCtrl) comp).enableBindingAnnotation();
		//ZK-3133
		_matchMediaValues = initMatchMediaValues(viewModel);
		if (!_matchMediaValues.isEmpty()) {
			Clients.response(new AuInvoke(_rootComp, "$binder"));
			final Execution exec = Executions.getCurrent();
			if (exec != null) {
				Cookie[] cookies = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getCookies();
				String[] matchMedias = null;
				JSONObject args = new JSONObject();
				if (cookies != null) {
					for (Cookie c : cookies) {
						// ZKMatchMeida and ZKClientInfo are both refer to the cookie names in Binder.js
						String name = c.getName();
						String value = c.getValue();
						try {
							value = URLDecoder.decode(value, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							_log.error("Failed to decode cookie " + name, e);
							continue;
						}
						if ("ZKMatchMedia".equals(name)) {
							matchMedias = value.trim().split(",");
						} else if ("ZKClientInfo".equals(name)) {
							args.put(BinderCtrl.CLIENT_INFO, JSONValue.parse(value));
						}
						if (matchMedias != null && args.size() != 0) {
							if (!matchMedias[0].isEmpty()) {
								for (String s : matchMedias) {
									if (!_matchMediaValues.containsKey(s))
										continue;
									final Event evt = new Event(ON_POST_COMMAND, _dummyTarget, new Object[] { s, args });
									Events.postEvent(-1, evt);
								}
							}
							break;
						}
					}
				}
			}
		}
	}

	private Map<String, Method> initMatchMediaValues(Object viewModel) {
		Map<String, Method> values = new HashMap<>(6);
		for (Method m : BindUtils.getViewModelClass(viewModel).getMethods()) {
			MatchMedia annomm = ViewModelAnnotationResolvers.getAnnotation(m, MatchMedia.class);
			if (annomm != null) {
				for (String s : annomm.value()) {
					s = BinderCtrl.MATCHMEDIAVALUE_PREFIX + s.trim();
					if (values.containsKey(s))
						throw new UiException("there are more then one MatchMedia method \"" + s.substring(16)
								+ "\" in class " + viewModel);
					values.put(s, m);
				}
			}
		}
		return values.isEmpty() ? Collections.emptyMap() : values;
	}

	public void destroy(Component comp, Object viewModel) {
		new AbstractAnnotatedMethodInvoker<Destroy>(Destroy.class, _destroyMethodCache) {
			protected boolean shouldLookupSuperclass(Destroy annotation) {
				return annotation.superclass();
			}
		}.invokeMethod(this, null);
	}

	private class QueueListener implements EventListener<Event>, Serializable {
		private static final long serialVersionUID = 1L;

		public void onEvent(Event event) throws Exception {
			//only when a event in queue is our event
			if (event instanceof PropertyChangeEvent) {
				final PropertyChangeEvent evt = (PropertyChangeEvent) event;
				BinderImpl.this.doPropertyChange(evt.getBase(), evt.getProperty());
			} else if (event instanceof GlobalCommandEvent) {
				final GlobalCommandEvent evt = (GlobalCommandEvent) event;
				final Set<Property> notifys = new LinkedHashSet<Property>();
				BinderImpl.this.doGlobalCommand(_rootComp, evt.getCommand(), evt.getTriggerEvent(), evt.getArgs(), notifys);
				fireNotifyChanges(notifys);
				notifyVMsgsChanged();
			}
		}
	}

	protected void checkInit() {
		if (!_init) {
			throw new UiException("binder is not initialized yet");
		}
	}

	public Map<String, List<Binding>> getBindings(Component comp) {
		return _bindings.get(comp);
	}

	//called when onPropertyChange is fired to the subscribed event queue
	private void doPropertyChange(Object base, String prop) {
		String debugInfo = MessageFormat.format("doPropertyChange: base=[{0}],prop=[{1}]", base, prop);
		if (_log.isDebugEnabled()) {
			_log.debug(debugInfo);
		}

		//zk-1468, 
		//ignore a coming ref-binding if the binder is the same since it was loaded already.
		if (base instanceof ReferenceBinding && ((ReferenceBinding) base).getBinder() == this) {
			return;
		}

		//ZK-4855
		if (base instanceof FormProxyObject) {
			Execution execution = Executions.getCurrent();
			Set<Pair<Object, String>> zkProxyNotified = execution != null ? (Set<Pair<Object, String>>) execution.getAttribute(ZKFORMPROXYNOTIFIEDKEY) : null;
			if (zkProxyNotified != null)
				zkProxyNotified.remove(new Pair<>(base, prop)); //try to remove flag
		}

		final Tracker tracker = getTracker();
		final Set<LoadBinding> bindings = tracker.getLoadBindings(base, prop);
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		try {
			if (collector != null) {
				collector.pushStack("NOTIFY_CHANGE");
				collector.addInfo(new NotifyChangeInfo(_rootComp, base, prop, "Size=" + bindings.size()));
			}
			doPropertyChange0(base, prop, bindings);
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			if (collector != null) {
				collector.popStack();
			}
		}
	}

	private void doPropertyChange0(Object base, String prop, Set<LoadBinding> bindings) {
		Execution exec = Executions.getCurrent();
		Set<Component> skipCheckChildren = (Set<Component>) exec.getAttribute(HtmlShadowElement.SKIP_DISTRIBUTED_CHILDREN_PROPERTY_CHANGE);
		for (LoadBinding binding : bindings) {
			//BUG 828, the sub-sequence binding might be removed after the previous loading.
			final Component comp = binding.getComponent();
			if (!(comp instanceof ShadowElement) && (comp == null || comp.getPage() == null))
				continue;

			boolean skip = false;
			if (skipCheckChildren != null)
				for (Component skipComp : skipCheckChildren)
					if (Components.isAncestor(skipComp, comp)) {
						skip = true;
						break;
					}
			if (skip)
				continue;

			final BindContext ctx = BindContextUtil.newBindContext(this, binding, false, null, comp, null);
			if (binding instanceof PropertyBinding) {
				BindContextUtil.setConverterArgs(this, comp, ctx, (PropertyBinding) binding);
			}

			String debugInfo = MessageFormat.format("doPropertyChange:binding.load() "
					+ "binding=[{0}],context=[{1}]", binding, ctx);
			try {
				if (_log.isDebugEnabled()) {
					_log.debug(debugInfo);
				}
				doPrePhase(Phase.LOAD_BINDING, ctx);
				binding.load(ctx);
			} catch (Exception ex) {
				throw new RuntimeException(debugInfo, ex);
			} finally {
				doPostPhase(Phase.LOAD_BINDING, ctx);
			}

			//zk-1468, 
			//notify the ref-binding changed since other nested binder might use it
			if (binding instanceof ReferenceBinding && binding != base) {
				notifyChange(binding, ".");
			}

			if (_validationMessages != null) {
				String attr = null;
				if (binding instanceof PropertyBinding) {
					attr = ((PropertyBinding) binding).getFieldName();
				} else if (binding instanceof FormBinding) {
					attr = ((FormBinding) binding).getFormId();
				} else {
					//ignore children binding
				}
				if (attr != null && hasValidator(comp, attr)) {
					_validationMessages.clearMessages(comp, attr);
				}
			}
		}
	}

	public void setViewModel(Object vm) {
		checkInit();
		_rootComp.setAttribute(BinderCtrl.VM, vm);
		_hasGetConverterMethod = true; //reset to true
		_hasGetValidatorMethod = true; //reset to true
		collectNotifyCommands(vm);
	}

	private transient Map<String, NotifyCommand> _notifyCommands;

	private void collectNotifyCommands(Object vm) {
		Class<?> viewModelClz = BindUtils.getViewModelClass(vm);
		NotifyCommands commands = ViewModelAnnotationResolvers.getAnnotation(viewModelClz, NotifyCommands.class);
		NotifyCommand command = ViewModelAnnotationResolvers.getAnnotation(viewModelClz, NotifyCommand.class);
		if (_notifyCommands != null)
			_notifyCommands.clear();

		if (command != null) {
			for (String cmd : command.value()) {
				_notifyCommands = AllocUtil.inst.putMap(_notifyCommands, cmd, command);
			}
		}
		if (commands != null) {
			for (NotifyCommand nc : commands.value()) {
				for (String cmd : nc.value()) {
					_notifyCommands = AllocUtil.inst.putMap(_notifyCommands, cmd, nc);
				}
			}
		}
	}

	public Object getViewModel() {
		checkInit();
		return getOriginViewModel(_rootComp.getAttribute(BinderCtrl.VM));
	}

	private Object getViewModelInView() {
		checkInit();
		return _rootComp.getAttribute(BinderCtrl.VM);
	}

	private static Object getOriginViewModel(Object vm) {
		if (vm instanceof ViewModelProxyObject)
			vm = ((ViewModelProxyObject) vm).getOriginObject();
		return vm;
	}

	//Note: assume system converter is state-less
	public Converter getConverter(String name) {
		checkInit();
		Converter converter = null;
		if (_hasGetConverterMethod) {
			Object vm = getViewModel();
			Class<? extends Object> clz = BindUtils.getViewModelClass(vm);
			Method m = null;
			Object result = null;
			try {
				m = clz.getMethod("getConverter", String.class);
			} catch (SecurityException x) {
				_hasGetConverterMethod = false;
			} catch (NoSuchMethodException e) {
				_hasGetConverterMethod = false;
			}
			if (m != null) {
				try {
					result = m.invoke(vm, name);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					_hasGetConverterMethod = false;
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
				if (result != null && !(result instanceof Converter)) {
					_hasGetConverterMethod = false;
				} else {
					converter = (Converter) result;
				}
			}
		}
		if (converter == null) {
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
		if (_hasGetValidatorMethod) {
			Object vm = getViewModel();
			Class<? extends Object> clz = BindUtils.getViewModelClass(vm);
			Method m = null;
			Object result = null;
			try {
				m = clz.getMethod("getValidator", String.class);
			} catch (SecurityException x) {
				_hasGetValidatorMethod = false;
			} catch (NoSuchMethodException e) {
				_hasGetValidatorMethod = false;
			}
			if (m != null) {
				try {
					result = m.invoke(vm, name);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					_hasGetValidatorMethod = false;
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
				if (result != null && !(result instanceof Validator)) {
					_hasGetValidatorMethod = false;
				} else {
					validator = (Validator) result;
				}
			}
		}
		if (validator == null) {
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
				throw UiException.Aide.wrap(e, e.getMessage());
			} catch (Exception e) {
				//ignore
			}
		}
		return renderer;
	}

	public BindEvaluatorX getEvaluatorX() {
		if (_eval == null) {
			_eval = BindEvaluatorXUtil.createEvaluator(null);
		}
		return _eval;
	}

	public void storeForm(Component comp, String id, Form form) {
		final String oldid = (String) comp.getAttribute(FORM_ID, Component.COMPONENT_SCOPE);
		//check if a form exist already, allow to store a form with same id again for replacing the form 
		if (oldid != null && !oldid.equals(id)) {
			throw new IllegalArgumentException(
					"try to store 2 forms in same component id : 1st " + oldid + ", 2nd " + id);
		}
		final Form oldForm = (Form) comp.getAttribute(id);

		if (oldForm == form)
			return;

		comp.setAttribute(FORM_ID, id); //mark it is a form component with the form id;
		comp.setAttribute(id, form); //after setAttribute, we can access fx in el.

		if (form instanceof FormLegacyExt) {
			final FormLegacyExt fex = (FormLegacyExt) form;
			comp.setAttribute(id + "Status", fex.getStatus()); //by convention fxStatus
			
			if (oldForm instanceof FormLegacyExt) { //copy the filed information, this is for a form-init that assign a user form
				for (String fn : ((FormLegacyExt) oldForm).getLoadFieldNames()) {
					fex.addLoadFieldName(fn);
				}
				for (String fn : ((FormLegacyExt) oldForm).getSaveFieldNames()) {
					fex.addSaveFieldName(fn);
				}
			}
			return;
		}
		if (form instanceof Form) {
			final Form fex = form;
			comp.setAttribute(id + "Status", fex.getFormStatus()); //by convention fxStatus
		}
		Map<Object, Set<String>> initSaveFormMap = initSaveFormMap();
		Set<String> remove = initSaveFormMap.remove(oldForm);
		if (remove != null) {
			Set<String> set = initSaveFormMap.get(form);
			if (set == null) {
				set = new HashSet<String>(16);
				initSaveFormMap.put(form, set);
			}
			set.addAll(remove);
		}
	}

	public Form getForm(Component comp, String id) {
		String oldid = (String) comp.getAttribute(FORM_ID, Component.COMPONENT_SCOPE);
		if (oldid == null || !oldid.equals(id)) {
			//return null if the id is not correct
			return null;
		}
		return (Form) comp.getAttribute(id);
	}

	private void removeForm(Component comp) {
		String id = (String) comp.getAttribute(FORM_ID, Component.COMPONENT_SCOPE);
		if (id != null) {
			comp.removeAttribute(FORM_ID);
			Object form = comp.removeAttribute(id);
			if (form != null)
				initSaveFormMap().remove(form);
			comp.removeAttribute(id + "Status");
		}
	}

	private void initFormLegacyBean(Component comp, String id, Object bean) {
		Form form = getForm(comp, id);
		if (form == null && bean instanceof FormLegacy)
			storeForm(comp, id, new SimpleForm());
	}

	public void addFormInitBinding(Component comp, String id, String initExpr, Map<String, Object> initArgs) {
		checkInit();
		if (Strings.isBlank(id)) {
			throw new IllegalArgumentException(MiscUtil.formatLocationMessage("form id is blank", comp));
		}
		if (initExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("initExpr is null for component " + comp + ", form " + id, comp));
		}

		addFormInitBinding0(comp, id, initExpr, initArgs);

	}

	private void addFormInitBinding0(Component comp, String formId, String initExpr, Map<String, Object> bindingArgs) {

		if (_log.isDebugEnabled()) {
			_log.debug("add init-form-binding: comp=[{}],form=[{}],expr=[{}]", comp, formId, initExpr);
		}
		final String attr = formId;

		InitFormBinding binding = newInitFormBinding(comp, attr, initExpr, bindingArgs);

		addBinding(comp, attr, binding);
		final BindingKey bkey = getBindingKey(comp, attr);
		_formBindingHandler.addInitBinding(bkey, binding);

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.addInfo(new AddBindingInfo(AddBindingInfo.FORM_INIT, comp, null, binding.getPropertyString(),
					formId, bindingArgs, null));
		}
		BindContext ctx = BindContextUtil.newBindContext(this, binding, false, null, comp, null);
		final Object bean = getEvaluatorX().getValue(ctx, comp, ((InitFormBindingImpl) binding)._accessInfo.getProperty());
		initFormLegacyBean(comp, formId, bean);
	}

	public void addFormLoadBindings(Component comp, String id, String loadExpr, String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs) {
		checkInit();
		if (Strings.isBlank(id)) {
			throw new IllegalArgumentException(MiscUtil.formatLocationMessage("form id is blank", comp));
		}
		if (loadExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("loadExpr is null for component " + comp + ", form " + id, comp));
		}

		addFormLoadBindings0(comp, id, loadExpr, beforeCmds, afterCmds, bindingArgs);
	}

	public void addFormSaveBindings(Component comp, String id, String saveExpr, String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs, String validatorExpr, Map<String, Object> validatorArgs) {
		checkInit();
		if (Strings.isBlank(id)) {
			throw new IllegalArgumentException(MiscUtil.formatLocationMessage("form id is blank", comp));
		}
		if (saveExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("saveExpr is null for component " + comp + ", form " + id, comp));
		}

		addFormSaveBindings0(comp, id, saveExpr, beforeCmds, afterCmds, bindingArgs, validatorExpr, validatorArgs);
	}

	private void addFormLoadBindings0(Component comp, String formId, String loadExpr, String[] beforeCmds,
			String[] afterCmds, Map<String, Object> bindingArgs) {
		final boolean prompt = isPrompt(beforeCmds, afterCmds);
		final String attr = formId;
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (prompt) {
			final LoadFormBinding binding = newLoadFormBinding(comp, formId, loadExpr, ConditionType.PROMPT, null,
					bindingArgs);
			addBinding(comp, attr, binding);
			final BindingKey bkey = getBindingKey(comp, attr);
			_formBindingHandler.addLoadPromptBinding(bkey, binding);

			if (collector != null) {
				collector.addInfo(new AddBindingInfo(AddBindingInfo.FORM_LOAD, comp, null, binding.getPropertyString(),
						formId, bindingArgs, null));
			}
		} else {
			if (beforeCmds != null && beforeCmds.length > 0) {
				for (String cmd : beforeCmds) {
					final LoadFormBinding binding = newLoadFormBinding(comp, formId, loadExpr,
							ConditionType.BEFORE_COMMAND, cmd, bindingArgs);
					addBinding(comp, attr, binding);
					if (_log.isDebugEnabled()) {
						_log.debug("add before command-load-form-binding: comp=[{}],attr=[{}],expr=[{}],command=[{}]",
								comp, attr, loadExpr, cmd);
					}
					_formBindingHandler.addLoadBeforeBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.FORM_LOAD, comp, "before = '" + cmd + "'",
								binding.getPropertyString(), formId, bindingArgs, null));
					}
				}
			}
			if (afterCmds != null && afterCmds.length > 0) {
				for (String cmd : afterCmds) {
					final LoadFormBinding binding = newLoadFormBinding(comp, formId, loadExpr,
							ConditionType.AFTER_COMMAND, cmd, bindingArgs);
					addBinding(comp, attr, binding);
					if (_log.isDebugEnabled()) {
						_log.debug("add after command-load-form-binding: comp=[{}],attr=[{}],expr=[{}],command=[{}]",
								comp, attr, loadExpr, cmd);
					}
					_formBindingHandler.addLoadAfterBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.FORM_LOAD, comp, "after = '" + cmd + "'",
								binding.getPropertyString(), formId, bindingArgs, null));
					}
				}
			}
		}
	}

	private void addFormSaveBindings0(Component comp, String formId, String saveExpr, String[] beforeCmds,
			String[] afterCmds, Map<String, Object> bindingArgs, String validatorExpr,
			Map<String, Object> validatorArgs) {
		final boolean prompt = isPrompt(beforeCmds, afterCmds);
		if (prompt) {
			throw new IllegalArgumentException(MiscUtil.formatLocationMessage(
					"a save-form-binding have to set with a before|after command condition", comp));
		}
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (beforeCmds != null && beforeCmds.length > 0) {
			for (String cmd : beforeCmds) {
				final SaveFormBinding binding = newSaveFormBinding(comp, formId, saveExpr, ConditionType.BEFORE_COMMAND,
						cmd, bindingArgs, validatorExpr, validatorArgs);
				addBinding(comp, formId, binding);
				if (_log.isDebugEnabled()) {
					_log.debug("add before command-save-form-binding: comp=[{}],attr=[{}],expr=[{}],command=[{}]", comp,
							formId, saveExpr, cmd);
				}
				_formBindingHandler.addSaveBeforeBinding(cmd, binding);

				if (collector != null) {
					collector.addInfo(new AddBindingInfo(AddBindingInfo.FORM_SAVE, comp, "before = '" + cmd + "'",
							formId, binding.getPropertyString(), bindingArgs, null));
				}
			}
		}
		if (afterCmds != null && afterCmds.length > 0) {
			for (String cmd : afterCmds) {
				final SaveFormBinding binding = newSaveFormBinding(comp, formId, saveExpr, ConditionType.AFTER_COMMAND,
						cmd, bindingArgs, validatorExpr, validatorArgs);
				addBinding(comp, formId, binding);
				if (_log.isDebugEnabled()) {
					_log.debug("add after command-save-form-binding: comp=[{}],attr=[{}],expr=[{}],command=[{}]", comp,
							formId, saveExpr, cmd);
				}
				_formBindingHandler.addSaveAfterBinding(cmd, binding);

				if (collector != null) {
					collector.addInfo(new AddBindingInfo(AddBindingInfo.FORM_SAVE, comp, "after = '" + cmd + "'",
							formId, binding.getPropertyString(), bindingArgs, null));
				}
			}
		}
		if (validatorExpr != null) {
			BindingKey bkey = getBindingKey(comp, formId);
			if (!_hasValidators.contains(bkey)) {
				_hasValidators.add(bkey);
			}
		}
	}

	public void addPropertyInitBinding(Component comp, String attr, String initExpr, Map<String, Object> initArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if (initExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("initExpr is null for " + attr + " of " + comp, comp));
		}
		if (Strings.isBlank(converterExpr)) {
			converterExpr = getSystemConverter(comp, attr);
			if (converterExpr != null) {
				converterExpr = "'" + converterExpr + "'";
			}
		}

		addPropertyInitBinding0(comp, attr, initExpr, initArgs, converterExpr, converterArgs);

		initRendererIfAny(comp, attr);
	}

	public void addPropertyLoadBindings(Component comp, String attr, String loadExpr, String[] beforeCmds,
			String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs) {
		checkInit();
		if (loadExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("loadExpr is null for component " + comp + ", attr " + attr, comp));
		}
		if (Strings.isBlank(converterExpr)) {
			converterExpr = getSystemConverter(comp, attr);
			if (converterExpr != null) {
				converterExpr = "'" + converterExpr + "'";
			}
		}

		addPropertyLoadBindings0(comp, attr, loadExpr, beforeCmds, afterCmds, bindingArgs, converterExpr,
				converterArgs);

		initRendererIfAny(comp, attr);
	}

	public void addPropertySaveBindings(Component comp, String attr, String saveExpr, String[] beforeCmds,
			String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs, String validatorExpr, Map<String, Object> validatorArgs) {
		checkInit();
		if (saveExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("saveExpr is null for component " + comp + ", attr " + attr, comp));
		}
		if (Strings.isBlank(converterExpr)) {
			converterExpr = getSystemConverter(comp, attr);
			if (converterExpr != null) {
				converterExpr = "'" + converterExpr + "'";
			}
		}
		if (Strings.isBlank(validatorExpr)) {
			validatorExpr = getSystemValidator(comp, attr);
			if (validatorExpr != null) {
				validatorExpr = "'" + validatorExpr + "'";
			}
		}

		addPropertySaveBindings0(comp, attr, saveExpr, beforeCmds, afterCmds, bindingArgs, converterExpr, converterArgs,
				validatorExpr, validatorArgs);
	}

	private void addPropertyInitBinding0(Component comp, String attr, String initExpr, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {

		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getSystemAnnotation(compCtrl, attr);
		String loadrep = null;
		Class<?> attrType = null; //default is any class
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			loadrep = AnnotationUtil.testString(attrs.get(Binder.LOAD_REPLACEMENT), ann); //check replacement of attr when loading

			final String type = AnnotationUtil.testString(attrs.get(Binder.LOAD_TYPE), ann); //check type of attr when loading
			if (type != null) {
				try {
					attrType = Classes.forNameByThread(type);
				} catch (ClassNotFoundException e) {
					throw UiException.Aide.wrap(e, e.getMessage());
				}
			}
		}
		loadrep = loadrep == null ? attr : loadrep;

		if (_log.isDebugEnabled()) {
			_log.debug("add init-binding: comp=[{}],attr=[{}],expr=[{}],converter=[{}]", comp, attr, initExpr,
					converterArgs);
		}

		InitPropertyBinding binding = newInitPropertyBinding(comp, attr, loadrep, attrType, initExpr, bindingArgs,
				converterExpr, converterArgs);

		addBinding(comp, attr, binding);
		final BindingKey bkey = getBindingKey(comp, attr);
		_propertyBindingHandler.addInitBinding(bkey, binding);

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_INIT, comp, null, binding.getPropertyString(),
					binding.getFieldName(), bindingArgs, null));
		}
	}

	private String getSystemConverter(Component comp, String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getSystemAnnotation(compCtrl, attr);
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			return AnnotationUtil.testString(attrs.get(Binder.CONVERTER), ann); //system converter if exists
		}
		return null;
	}

	private String getSystemValidator(Component comp, String attr) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getSystemAnnotation(compCtrl, attr);
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			return AnnotationUtil.testString(attrs.get(Binder.VALIDATOR), ann); //system validator if exists
		}
		return null;
	}

	private void initRendererIfAny(Component comp, String attr) {
		final Object installed = comp.getAttribute(BinderCtrl.RENDERER_INSTALLED);
		if (installed != null) { //renderer was set already init
			return;
		}

		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getSystemAnnotation(compCtrl, null);
		final Map<String, String[]> attrs = ann != null ? ann.getAttributes() : null; //(tag, tagExpr)

		//only set up renderer when has model binding. (or will get error in no-model + selectedTab case
		final String installAttr = "model"; //TODO make it configurable in lang-addon.xml
		if (attrs != null && installAttr.equals(attr)) {
			final String rendererName = AnnotationUtil.testString(attrs.get(Binder.RENDERER), ann); //renderer if any
			//setup renderer
			if (rendererName != null) { //there was system renderer
				String[] values = null;
				if (rendererName.indexOf("=") != -1) {
					values = rendererName.split("=", 2); //zk 6.0.0
				} else {
					values = rendererName.split(":", 2); //after zk 6.0.1
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
						} catch (Exception e) {
							throw UiException.Aide.wrap(e, e.getMessage());
						}

						if (renderer instanceof TemplateRendererCtrl) {
							((TemplateRendererCtrl) renderer).setAttributeName(attr);
						}
					}

					comp.setAttribute(BinderCtrl.RENDERER_INSTALLED, ""); //mark installed
				}
			}
		}
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected LoadPropertyBinding newLoadPropertyBinding(Component comp, String attr, String loadAttr,
			Class<?> attrType, String loadExpr, ConditionType conditionType, String command,
			Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {
		return new LoadPropertyBindingImpl(this, comp, attr, loadAttr, attrType, loadExpr, conditionType, command,
				bindingArgs, converterExpr, converterArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected SavePropertyBinding newSavePropertyBinding(Component comp, String attr, String saveAttr, String saveExpr,
			ConditionType conditionType, String command, Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs, String validatorExpr, Map<String, Object> validatorArgs) {
		return new SavePropertyBindingImpl(this, comp, attr, saveAttr, saveExpr, conditionType, command, bindingArgs,
				converterExpr, converterArgs, validatorExpr, validatorArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected InitPropertyBinding newInitPropertyBinding(Component comp, String attr, String loadAttr,
			Class<?> attrType, String initExpr, Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs) {
		return new InitPropertyBindingImpl(this, comp, attr, loadAttr, attrType, initExpr, bindingArgs, converterExpr,
				converterArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected InitChildrenBinding newInitChildrenBinding(Component comp, String initExpr,
			Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {
		return new InitChildrenBindingImpl(this, comp, initExpr, bindingArgs, converterExpr, converterArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected LoadChildrenBinding newLoadChildrenBinding(Component comp, String loadExpr, ConditionType conditionType,
			String command, Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {
		return new LoadChildrenBindingImpl(this, comp, loadExpr, conditionType, command, bindingArgs, converterExpr,
				converterArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected CommandBinding newCommandBinding(Component comp, String evtnm, String cmdScript,
			Map<String, Object> args) {
		return new CommandBindingImpl(this, comp, evtnm, cmdScript, args);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected InitFormBinding newInitFormBinding(Component comp, String formId, String initExpr,
			Map<String, Object> bindingArgs) {
		return new InitFormBindingImpl(this, comp, formId, initExpr, bindingArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected LoadFormBinding newLoadFormBinding(Component comp, String formId, String loadExpr,
			ConditionType conditionType, String command, Map<String, Object> bindingArgs) {
		return new LoadFormBindingImpl(this, comp, formId, loadExpr, conditionType, command, bindingArgs);
	}

	/** Make this extenable.
	 * @since 7.0.3
	 */
	protected SaveFormBinding newSaveFormBinding(Component comp, String formId, String saveExpr,
			ConditionType conditionType, String command, Map<String, Object> bindingArgs, String validatorExpr,
			Map<String, Object> validatorArgs) {
		return new SaveFormBindingImpl(this, comp, formId, saveExpr, conditionType, command, bindingArgs, validatorExpr,
				validatorArgs);
	}

	private void addPropertyLoadBindings0(Component comp, String attr, String loadExpr, String[] beforeCmds,
			String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs) {
		final boolean prompt = isPrompt(beforeCmds, afterCmds);
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		//check attribute _accessInfo natural characteristics to register Command event listener
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getSystemAnnotation(compCtrl, attr);
		//check which attribute of component should load to component on which event.
		//the event is usually a engine lifecycle event.
		//ex, listbox's 'selectedIndex' should be loaded to component on 'onAfterRender'
		String evtnm = null;
		String loadRep = null;
		Class<?> attrType = null; //default is any class
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			final String rw = AnnotationUtil.testString(attrs.get(Binder.ACCESS), ann); //_accessInfo right, "both|save|load", default to load
			if (rw != null && !"both".equals(rw) && !"load".equals(rw)) { //save only, skip
				return;
			}
			evtnm = AnnotationUtil.testString(attrs.get(Binder.LOAD_EVENT), ann); //check trigger event for loading

			loadRep = AnnotationUtil.testString(attrs.get(Binder.LOAD_REPLACEMENT), ann); //check replacement of attr when loading

			final String type = AnnotationUtil.testString(attrs.get(Binder.LOAD_TYPE), ann); //check type of attr when loading
			if (type != null) {
				try {
					attrType = Classes.forNameByThread(type);
				} catch (ClassNotFoundException e) {
					throw UiException.Aide.wrap(e, e.getMessage());
				}
			}
		}
		loadRep = loadRep == null ? attr : loadRep;

		if (prompt) {
			if (_log.isDebugEnabled()) {
				_log.debug("add event(prompt)-load-binding: comp=[{}],attr=[{}],expr=[{}],evtnm=[{}],converter=[{}]",
						comp, attr, loadExpr, evtnm, converterArgs);
			}
			LoadPropertyBinding binding = newLoadPropertyBinding(comp, attr, loadRep, attrType, loadExpr,
					ConditionType.PROMPT, null, bindingArgs, converterExpr, converterArgs);
			addBinding(comp, attr, binding);

			if (evtnm != null) { //special case, load on an event, ex, onAfterRender of listbox on selectedItem
				registerCommandEventListener(comp, evtnm); //prompt
				addBinding(comp, evtnm, binding); //to mark evtnm has a this binding, so we can remove it
				final BindingKey bkey = getBindingKey(comp, evtnm);
				_propertyBindingHandler.addLoadEventBinding(comp, bkey, binding);
			}
			//if no command , always add to prompt binding, a prompt binding will be load when , 
			//1.load a component property binding
			//2.property change (TODO, DENNIS, ISSUE, I think loading of property change is triggered by tracker in doPropertyChange, not by prompt-binding 
			final BindingKey bkey = getBindingKey(comp, attr);
			_propertyBindingHandler.addLoadPromptBinding(comp, bkey, binding);

			if (collector != null) {
				collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_LOAD, comp, evtnm, binding.getPropertyString(),
						binding.getFieldName(), bindingArgs, null));
			}
		} else {
			if (beforeCmds != null && beforeCmds.length > 0) {
				for (String cmd : beforeCmds) {
					LoadPropertyBinding binding = newLoadPropertyBinding(comp, attr, loadRep, attrType, loadExpr,
							ConditionType.BEFORE_COMMAND, cmd, bindingArgs, converterExpr, converterArgs);
					addBinding(comp, attr, binding);
					if (_log.isDebugEnabled()) {
						_log.debug("add before command-load-binding: comp=[{}],att=r[{}],expr=[{}],converter=[{}]",
								comp, attr, loadExpr, converterExpr);
					}
					_propertyBindingHandler.addLoadBeforeBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_LOAD, comp, "before = '" + cmd + "'",
								binding.getPropertyString(), binding.getFieldName(), bindingArgs, null));
					}
				}
			}
			if (afterCmds != null && afterCmds.length > 0) {
				for (String cmd : afterCmds) {
					LoadPropertyBinding binding = newLoadPropertyBinding(comp, attr, loadRep, attrType, loadExpr,
							ConditionType.AFTER_COMMAND, cmd, bindingArgs, converterExpr, converterArgs);
					addBinding(comp, attr, binding);
					if (_log.isDebugEnabled()) {
						_log.debug("add after command-load-binding: comp=[{}],att=r[{}],expr=[{}],converter=[{}]", comp,
								attr, loadExpr, converterExpr);
					}
					_propertyBindingHandler.addLoadAfterBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_LOAD, comp, "after = '" + cmd + "'",
								binding.getPropertyString(), binding.getFieldName(), bindingArgs, null));
					}
				}
			}
		}
	}

	private void addPropertySaveBindings0(Component comp, String attr, String saveExpr, String[] beforeCmds,
			String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr,
			Map<String, Object> converterArgs, String validatorExpr, Map<String, Object> validatorArgs) {
		final boolean prompt = isPrompt(beforeCmds, afterCmds);
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		//check attribute _accessInfo natural characteristics to register Command event listener 
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = AnnotationUtil.getSystemAnnotation(compCtrl, attr);
		//check which attribute of component should fire save on which event.
		//ex, listbox's 'selectedIndex' should be loaded to component on 'onSelect'
		//ex, checkbox's 'checked' should be saved to bean on 'onCheck'
		String evtnm = null;
		String saveRep = null;
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			final String rw = AnnotationUtil.testString(attrs.get(Binder.ACCESS), ann); //_accessInfo right, "both|save|load", default to load
			if (!"both".equals(rw) && !"save".equals(rw)) { //load only, skip
				if (BinderUtil.hasContext() && BinderUtil.getContext().isIgnoreAccessCreationWarn()) {
					return;
				}
				_log.warn(MiscUtil.formatLocationMessage(
						"component " + comp + " doesn't support to save attribute " + attr, comp));
				return;
			}
			evtnm = AnnotationUtil.testString(attrs.get(Binder.SAVE_EVENT), ann); //check trigger event for saving

			saveRep = AnnotationUtil.testString(attrs.get(Binder.SAVE_REPLACEMENT), ann); //check replacement of attr when saving
		}
		if (evtnm == null) {
			//no trigger event, since the value never change of component, so both prompt and command are useless
			if (BinderUtil.hasContext() && BinderUtil.getContext().isIgnoreAccessCreationWarn()) {
				return;
			}
			_log.warn(MiscUtil
					.formatLocationMessage("component " + comp + " doesn't has event to save attribute " + attr, comp));
			return;
		}
		saveRep = saveRep == null ? attr : saveRep;

		if (prompt) {
			final SavePropertyBinding binding = newSavePropertyBinding(comp, attr, saveRep, saveExpr,
					ConditionType.PROMPT, null, bindingArgs, converterExpr, converterArgs, validatorExpr,
					validatorArgs);
			addBinding(comp, attr, binding);
			if (_log.isDebugEnabled()) {
				_log.debug(
						"add event(prompt)-save-binding: comp=[{}],attr=[{}],expr=[{}],evtnm=[{}],converter=[{}],validate=[{}]",
						comp, attr, saveExpr, evtnm, converterExpr, validatorExpr);
			}
			registerCommandEventListener(comp, evtnm); //prompt
			addBinding(comp, evtnm, binding); //to mark evtnm has a this binding, so we can remove it in removeComponent
			final BindingKey bkey = getBindingKey(comp, evtnm);
			_propertyBindingHandler.addSavePromptBinding(comp, bkey, binding);

			if (collector != null) {
				collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_SAVE, comp, null, binding.getFieldName(),
						binding.getPropertyString(), bindingArgs, null));
			}
		} else {
			if (beforeCmds != null && beforeCmds.length > 0) {
				for (String cmd : beforeCmds) {
					final SavePropertyBinding binding = newSavePropertyBinding(comp, attr, saveRep, saveExpr,
							ConditionType.BEFORE_COMMAND, cmd, bindingArgs, converterExpr, converterArgs, validatorExpr,
							validatorArgs);
					addBinding(comp, attr, binding);
					if (_log.isDebugEnabled()) {
						_log.debug(
								"add before command-save-binding: comp=[{}],att=r[{}],expr=[{}],converter=[{}],validator=[{}]",
								comp, attr, saveExpr, converterExpr, validatorExpr);
					}
					_propertyBindingHandler.addSaveBeforeBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_SAVE, comp, "before = '" + cmd + "'",
								binding.getFieldName(), binding.getPropertyString(), bindingArgs, null));
					}
				}
			}
			if (afterCmds != null && afterCmds.length > 0) {
				for (String cmd : afterCmds) {
					final SavePropertyBinding binding = newSavePropertyBinding(comp, attr, saveRep, saveExpr,
							ConditionType.AFTER_COMMAND, cmd, bindingArgs, converterExpr, converterArgs, validatorExpr,
							validatorArgs);
					addBinding(comp, attr, binding);
					if (_log.isDebugEnabled()) {
						_log.debug(
								"add after command-save-binding: comp=[{}],att=r[{}],expr=[{}],converter=[{}],validator=[{}]",
								comp, attr, saveExpr, converterExpr, validatorExpr);
					}
					_propertyBindingHandler.addSaveAfterBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_SAVE, comp, "after = '" + cmd + "'",
								binding.getFieldName(), binding.getPropertyString(), bindingArgs, null));
					}
				}
			}
		}

		if (validatorExpr != null) {
			BindingKey bkey = getBindingKey(comp, attr);
			if (!_hasValidators.contains(bkey)) {
				_hasValidators.add(bkey);
			}
		}
	}

	public void addChildrenInitBinding(Component comp, String initExpr, Map<String, Object> initArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if (initExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("initExpr is null for children of " + comp, comp));
		}
		addChildrenInitBinding0(comp, initExpr, initArgs, converterExpr, converterArgs);
	}

	public void addChildrenLoadBindings(Component comp, String loadExpr, String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {
		checkInit();
		if (loadExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("loadExpr is null for children of " + comp, comp));
		}
		addChildrenLoadBindings0(comp, loadExpr, beforeCmds, afterCmds, bindingArgs, converterExpr, converterArgs);
	}

	private void addChildrenInitBinding0(Component comp, String initExpr, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {

		if (_log.isDebugEnabled()) {
			_log.debug("add children-init-binding: comp=[{}],expr=[{}]", comp, initExpr);
		}

		InitChildrenBinding binding = newInitChildrenBinding(comp, initExpr, bindingArgs, converterExpr, converterArgs);

		addBinding(comp, CHILDREN_ATTR, binding);
		final BindingKey bkey = getBindingKey(comp, CHILDREN_ATTR);
		_childrenBindingHandler.addInitBinding(bkey, binding);

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.addInfo(new AddBindingInfo(AddBindingInfo.CHILDREN_INIT, comp, null, binding.getPropertyString(),
					null, bindingArgs, null));
		}

	}

	private void addChildrenLoadBindings0(Component comp, String loadExpr, String[] beforeCmds, String[] afterCmds,
			Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {
		final boolean prompt = isPrompt(beforeCmds, afterCmds);
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (prompt) {
			if (_log.isDebugEnabled()) {
				_log.debug("add event(prompt)-children-load-binding: comp=[{}],expr=[{}]", comp, loadExpr);
			}
			LoadChildrenBindingImpl binding = new LoadChildrenBindingImpl(this, comp, loadExpr, ConditionType.PROMPT,
					null, bindingArgs, converterExpr, converterArgs);
			addBinding(comp, CHILDREN_ATTR, binding);

			final BindingKey bkey = getBindingKey(comp, CHILDREN_ATTR);
			_childrenBindingHandler.addLoadPromptBinding(comp, bkey, binding);

			if (collector != null) {
				collector.addInfo(new AddBindingInfo(AddBindingInfo.CHILDREN_LOAD, comp, null,
						binding.getPropertyString(), null, bindingArgs, null));
			}

		} else {
			if (beforeCmds != null && beforeCmds.length > 0) {
				for (String cmd : beforeCmds) {
					LoadChildrenBindingImpl binding = new LoadChildrenBindingImpl(this, comp, loadExpr,
							ConditionType.BEFORE_COMMAND, cmd, bindingArgs, converterExpr, converterArgs);
					addBinding(comp, CHILDREN_ATTR, binding);
					if (_log.isDebugEnabled()) {
						_log.debug("add before command children-load-binding: comp=[{}],expr=[{}],cmd=[{}]", comp,
								loadExpr, cmd);
					}
					_childrenBindingHandler.addLoadBeforeBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.CHILDREN_LOAD, comp,
								"before = '" + cmd + "'", binding.getPropertyString(), null, bindingArgs, null));
					}
				}
			}
			if (afterCmds != null && afterCmds.length > 0) {
				for (String cmd : afterCmds) {
					LoadChildrenBindingImpl binding = new LoadChildrenBindingImpl(this, comp, loadExpr,
							ConditionType.AFTER_COMMAND, cmd, bindingArgs, converterExpr, converterArgs);
					addBinding(comp, CHILDREN_ATTR, binding);
					if (_log.isDebugEnabled()) {
						_log.debug("add after command children-load-binding: comp=[{}],expr=[{}],cmd=[{}]", comp,
								loadExpr, cmd);
					}
					_childrenBindingHandler.addLoadAfterBinding(cmd, binding);

					if (collector != null) {
						collector.addInfo(new AddBindingInfo(AddBindingInfo.CHILDREN_LOAD, comp,
								"after = '" + cmd + "'", binding.getPropertyString(), null, bindingArgs, null));
					}
				}
			}
		}
	}

	public void addReferenceBinding(Component comp, String attr, String loadExpr, Map<String, Object> bindingArgs) {
		checkInit();
		if (loadExpr == null) {
			throw new IllegalArgumentException(
					MiscUtil.formatLocationMessage("loadExpr is null for reference of " + comp, comp));
		}
		addReferenceBinding0(comp, attr, loadExpr, bindingArgs);
	}

	private void addReferenceBinding0(Component comp, String attr, String loadExpr, Map<String, Object> bindingArgs) {
		if (_log.isDebugEnabled()) {
			_log.debug("add reference-binding: comp=[{}],attr=[{}],expr=[{}]", comp, attr, loadExpr);
		}
		ReferenceBindingImpl binding = new ReferenceBindingImpl(this, comp, attr, loadExpr);

		if (_refBindingHandler != null) {
			_refBindingHandler.addReferenceBinding(comp, attr, binding);
		} else {
			throw new UiException(
					MiscUtil.formatLocationMessage("ref binding handler is not supported in current runtime.", comp));
		}

		addBinding(comp, attr, binding);

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.addInfo(new AddBindingInfo(AddBindingInfo.REFERENCE, comp, null, binding.getPropertyString(),
					"self." + attr, bindingArgs, null));
		}
	}

	private boolean isPrompt(String[] beforeCmds, String[] afterCmds) {
		return (beforeCmds == null || beforeCmds.length == 0) && (afterCmds == null || afterCmds.length == 0);
	}

	public void addCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> args) {
		checkInit();
		final CommandBinding binding = newCommandBinding(comp, evtnm, commandExpr, args);
		addBinding(comp, evtnm, binding);
		registerCommandEventListener(comp, evtnm, binding, false);

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.addInfo(new AddCommandBindingInfo(AddCommandBindingInfo.VIEWMODEL, comp, evtnm,
					binding.getCommandString(), args, null));
		}
	}

	public void addGlobalCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> args) {
		checkInit();
		final CommandBinding binding = newCommandBinding(comp, evtnm, commandExpr, args);
		addBinding(comp, evtnm, binding);
		registerCommandEventListener(comp, evtnm, binding, true);

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.addInfo(new AddCommandBindingInfo(AddCommandBindingInfo.GLOBAL, comp, evtnm,
					binding.getCommandString(), args, null));
		}
	}

	//associate event to CommandBinding
	private void registerCommandEventListener(Component comp, String evtnm, CommandBinding command, boolean global) {
		final CommandEventListener listener = getCommandEventListener(comp, evtnm);
		if (global) {
			listener.setGlobalCommand(command);
		} else {
			listener.setCommand(command);
		}
	}

	//associate event to prompt
	private void registerCommandEventListener(Component comp, String evtnm) {
		final CommandEventListener listener = getCommandEventListener(comp, evtnm);
		listener.setPrompt(true);
	}

	private CommandEventListener getCommandEventListener(Component comp, String evtnm) {
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

	private class CommandEventListener implements EventListener<Event>, Serializable, Deferrable {
		private static final long serialVersionUID = 1L;
		//event used to trigger command
		private boolean _prompt = false;
		private CommandBinding _commandBinding;
		private CommandBinding _globalCommandBinding;
		private final Component _target;

		CommandEventListener(Component target) {
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
			try {
				if (collector != null) {
					collector.pushStack("ON_EVENT");
					collector.addInfo(new EventInfo(event.getTarget(), event.getName(), null));
				}
				onEvent0(event);
			} catch (Exception x) {
				_log.error(x.getMessage(), x);
				throw x;
			} finally {
				if (collector != null) {
					collector.popStack();
				}
			}
		}

		private void onEvent0(Event event) throws Exception {
			//command need to be confirmed shall be execute first!
			//must sort the command sequence?

			//BUG 619, event may come from children of some component, 
			//ex tabbox.onSelect is form tab, so we cannot depend on event's target
			final Component comp = _target; //_target is always equals _commandBinding.getComponent();
			final String evtnm = event.getName();
			final Set<Property> notifys = new LinkedHashSet<Property>();

			int cmdResult = COMMAND_SUCCESS; //command execution result, default to success
			boolean promptResult = true;
			String command = null;
			if (_log.isDebugEnabled()) {
				_log.debug("====Start command event [{}]", event);
			}
			//BUG ZK-757, The timing of saving textbox's value attribute to ViewModel is later than command execution on onChange event
			//We should save the prompt with validation first. 
			//For a prompt binding that also binds with a command, that should not be mixed with command
			//If user concern the timing of prompt save and validation with command, they should use condition not prompt  
			if (_prompt) {
				promptResult = BinderImpl.this.doSaveEvent(comp, event, notifys); //save on event
			}

			if (_commandBinding != null) {
				final BindEvaluatorX eval = getEvaluatorX();
				//ZK-3084: An EL in a command binding cannot access "event" object
				Map<String, Object> implicit = _implicitContributor.contirbuteCommandObject(BinderImpl.this,
						_commandBinding, event);
				BindContext ctx = new BindContextImpl(null, null, false, null, comp, null);
				ctx.setAttribute(ImplicitObjectELResolver.IMPLICIT_OBJECTS, implicit);
				command = (String) eval.getValue(ctx, comp, ((CommandBindingImpl) _commandBinding).getCommand());
				if (!Strings.isEmpty(command)) { //avoid the execution of a empty command.
					//ZK-1032 Able to wire Event to command method
					final Map<String, Object> args = BindEvaluatorXUtil.evalArgs(eval, comp, _commandBinding.getArgs(),
							implicit);
					cmdResult = BinderImpl.this.doCommand(comp, _commandBinding, command, event, args, notifys);
				}
			}

			//load prompt only when prompt result is success
			if (_prompt && promptResult) {
				if (_log.isDebugEnabled()) {
					_log.debug("This is a prompt command");
				}
				BinderImpl.this.doLoadEvent(comp, event); //load on event
			}

			notifyVMsgsChanged(); //always, no better way to know which properties of validation are changed

			if (_log.isDebugEnabled()) {
				_log.debug("There are [{}] property need to be notify after event = [{}], command = [{}]",
						notifys.size(), evtnm, command);
			}
			fireNotifyChanges(notifys);

			//post global command only when command success
			if (cmdResult == COMMAND_SUCCESS && _globalCommandBinding != null) {
				final BindEvaluatorX eval = getEvaluatorX();
				command = (String) eval.getValue(null, comp, ((CommandBindingImpl) _globalCommandBinding).getCommand());
				if (!Strings.isEmpty(command)) { //avoid the execution of a empty command.

					//ZK-1791 @global-command does not provide predefined "event" variable
					Map<String, Object> implicit = null;
					if (_implicitContributor != null) {
						implicit = _implicitContributor.contirbuteCommandObject(BinderImpl.this, _commandBinding,
								event);
					}

					final Map<String, Object> args = BindEvaluatorXUtil.evalArgs(eval, comp,
							_globalCommandBinding.getArgs(), implicit);
					//post global command
					postGlobalCommand(comp, _globalCommandBinding, command, event, args);
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug("====End command event [{}]", event);
			}
		}

		// ZK-2993: Provides a custom attribute to defer the event post for the specified component
		public boolean isDeferrable() {
			return "true".equals(_target.getAttribute("org.zkoss.bind.event.deferPost"));
		}
	}

	private class VMsgsChangedListener implements EventListener<Event>, Serializable {
		private static final long serialVersionUID = 1L;

		public void onEvent(Event event) throws Exception {
			if (_validationMessages != null) {
				Set<Property> notify = new HashSet<Property>();
				notify.add(new PropertyImpl(_validationMessages, ".", null));
				fireNotifyChanges(notify);
			}
		}
	}

	/**
	 * @since 6.0.1
	 */

	public boolean isActivating() {
		return _activating;
	}

	private void notifyVMsgsChanged() {
		if (_validationMessages != null) {
			//ZK-722 Validation message is not clear after form binding loaded
			//defer the validation notify as possible
			Events.postEvent(-1, _dummyTarget, new Event(ON_VMSGS_CHANGED));
		}
	}

	public int sendCommand(String command, Map<String, Object> args) {
		checkInit();
		final Set<Property> notifys = new HashSet<Property>();
		Event evt = null;
		//ZK-3133
		if (args != null) {
			if (args.containsKey(BinderCtrl.CLIENT_INFO)) {
				Map<String, Object> inf = new HashMap<String, Object>();
				inf.put("", args.get(BinderCtrl.CLIENT_INFO));
				evt = ClientInfoEvent.getClientInfoEvent(new AuRequest(_rootComp.getDesktop(), command, inf));
			} else {
				Event uploadInfoEvt = (Event) args.get(BinderCtrl.CLIENT_UPLOAD_INFO); // ZK-4472
				if (uploadInfoEvt != null)
					evt = uploadInfoEvt;
			}
		}
		//args come from user, we don't eval it.
		int result = doCommand(_rootComp, null, command, evt, args, notifys);
		if (result == COMMAND_FAIL_VALIDATE && _validationMessages != null) {
			notifys.add(new PropertyImpl(_validationMessages, ".", null));
		}
		fireNotifyChanges(notifys);
		return result;
	}

	protected void fireNotifyChanges(Set<Property> notifys) {
		for (Property prop : notifys) {
			notifyChange(prop.getBase(), prop.getProperty());
		}
	}

	public void postCommand(String command, Map<String, Object> args) {
		checkInit();
		final Event evt = new Event(ON_POST_COMMAND, _dummyTarget, new Object[] { command, args });
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
	private int doCommand(Component comp, CommandBinding commandBinding, String command, Event evt,
			Map<String, Object> commandArgs, Set<Property> notifys) {
		final String evtnm = evt == null ? null : evt.getName();
		String debugInfo = MessageFormat.format("doCommand "
				+ "comp=[{0}],command=[{1}],evtnm=[{2}]", comp, command, evtnm);
		if (_log.isDebugEnabled()) {
			_log.debug("Start " + debugInfo);
		}
		BindContext ctx = BindContextUtil.newBindContext(this, commandBinding, false, command, comp, evt);
		BindContextUtil.setCommandArgs(this, comp, ctx, commandArgs);
		try {
			doPrePhase(Phase.COMMAND, ctx); //begin of Command
			boolean success = true;

			final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
			if (collector != null) {
				collector.addInfo(new CommandInfo(CommandInfo.ON_COMMAND, comp, evtnm,
						commandBinding == null ? null
								: BindEvaluatorXUtil
										.getExpressionString(((CommandBindingImpl) commandBinding).getCommand()),
						command, commandArgs, null));
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
			if (_log.isDebugEnabled()) {
				_log.debug("End doCommand");
			}
			return COMMAND_SUCCESS;
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.COMMAND, ctx); //end of Command
		}

	}

	private void doGlobalCommand(Component comp, String command, Event evt, Map<String, Object> commandArgs,
			Set<Property> notifys) {
		String debugInfo = MessageFormat.format("doGlobalCommand comp=[{0}],command=[{1}]", comp, command);
		if (_log.isDebugEnabled()) {
			_log.debug("Start " + debugInfo);
		}

		BindContext ctx = BindContextUtil.newBindContext(this, null, false, command, comp, evt);
		BindContextUtil.setCommandArgs(this, comp, ctx, commandArgs);
		try {
			doPrePhase(Phase.GLOBAL_COMMAND, ctx); //begin of Command

			final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
			if (collector != null) {
				collector.addInfo(
						new CommandInfo(CommandInfo.ON_GLOBAL_COMMAND, comp, null, null, command, commandArgs, null));
			}

			//execute command
			doGlobalCommandExecute(comp, command, commandArgs, ctx, notifys);
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.GLOBAL_COMMAND, ctx); //end of Command
		}
	}

	private void doGlobalCommandExecute(Component comp, String command, Map<String, Object> commandArgs,
			BindContext ctx, Set<Property> notifys) {
		String debugInfo = MessageFormat.format("doGlobalCommandExecute comp=[{0}],command=[{1}]", comp, command); 
		try {
			if (_log.isDebugEnabled()) {
				_log.debug("before " + debugInfo);
			}
			doPrePhase(Phase.EXECUTE, ctx);

			final Object viewModel = getViewModelInView();

			Method method = getCommandMethod(BindUtils.getViewModelClass(viewModel), command, _globalCommandMethodInfoProvider,
					_globalCommandMethodCache, commandArgs != null ? commandArgs.size() : 0, true);

			if (method != null) {

				BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
				if (collector != null) {
					collector.addInfo(new CommandInfo(CommandInfo.EXECUTE_GLOBAL, comp, null, null, command,
							commandArgs, method.toString()));
				}

				ParamCall parCall = createParamCall(ctx);
				if (commandArgs != null) {
					parCall.setBindingArgs(commandArgs);
				}
				handleNotifyChange(ctx, viewModel, method, parCall, notifys);
			} else {
				//do nothing
				if (_log.isDebugEnabled()) {
					_log.debug("no global command method in [{}]", viewModel);
				}
				debugInfo += MessageFormat.format(",no global command method in viewModel=[{0}]", viewModel);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("after doGlobalCommandExecute notifys=[{}]", notifys);
			}
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.EXECUTE, ctx);
		}
	}

	static void handleNotifyChange(BindContext ctx, Object viewModel,
	                                         Method method, ParamCall parCall,
	                                         Set<Property> notifys) {
		final SmartNotifyChange sannt = ViewModelAnnotationResolvers.getAnnotation(method, SmartNotifyChange.class);
		Object originViewModel = getOriginViewModel(viewModel);
		if (sannt != null) {
			Set<Property> properties = new LinkedHashSet<Property>(5);
			properties.addAll(BindELContext.getNotifys(method, originViewModel, (String) null, (Object) null, ctx)); // collect notifyChange

			parCall.call(viewModel, method);

			for (Iterator<Property> it = properties.iterator(); it.hasNext();) {
				Property prop = it.next();
				Object result = null;
				try {
					result = Fields.get(prop.getBase(), prop.getProperty());
					if (Objects.equals(result, prop.getValue()))
						it.remove();

				} catch (NoSuchMethodException ignored) {
				}
			}
			notifys.addAll(properties);
		} else {
			parCall.call(viewModel, method);
			notifys.addAll(BindELContext.getNotifys(method, originViewModel, (String) null, (Object) null, ctx)); // collect notifyChange
		}
	}

	/*package*/ void doPrePhase(Phase phase, BindContext ctx) {
		BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.pushStack(phase.name());
		}
		for (PhaseListener listener : getPhaseListeners()) {
			if (listener != null) {
				listener.prePhase(phase, ctx);
			}
		}
	}

	/*package*/ void doPostPhase(Phase phase, BindContext ctx) {
		for (PhaseListener listener : getPhaseListeners()) {
			if (listener != null) {
				listener.postPhase(phase, ctx);
			}
		}
		BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		if (collector != null) {
			collector.popStack();
		}
	}

	//for event -> prompt only, no command 
	private boolean doSaveEvent(Component comp, Event evt, Set<Property> notifys) {
		final String evtnm = evt == null ? null : evt.getName();
		if (_log.isDebugEnabled()) {
			_log.debug("doSaveEvent comp=[{}],evtnm=[{}],notifys=[{}]", comp, evtnm, notifys);
		}
		final BindingKey bkey = getBindingKey(comp, evtnm);
		return _propertyBindingHandler.doSaveEvent(bkey, comp, evt, notifys);
	}

	//for event -> prompt only, no command
	private void doLoadEvent(Component comp, Event evt) {
		if (_log.isDebugEnabled()) {
			_log.debug("doLoadEvent comp=[{}],evtnm=[{}]", comp, evt.getName());
		}
		final BindingKey bkey = getBindingKey(comp, evt.getName());
		_propertyBindingHandler.doLoadEvent(bkey, comp, evt);
	}

	//doCommand -> doValidate
	protected boolean doValidate(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		final Set<Property> validates = new HashSet<Property>();
		String debugInfo = MessageFormat.format("doValidate "
				+ "comp=[{0}],command=[{1}],evt=[{2}],context=[{3}]", comp, command, evt, ctx);
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(debugInfo);
			}
			doPrePhase(Phase.VALIDATE, ctx);

			//we collect properties that need to be validated, than validate one-by-one
			ValidationHelper vHelper = new ValidationHelper(this, new ValidationHelper.InfoProvider() {
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
					return BinderImpl.this.getBindingKey(comp, attr);
				}
			});

			//collect Property of special command for validation in validates
			vHelper.collectSaveBefore(comp, command, evt, validates);
			vHelper.collectSaveAfter(comp, command, evt, validates);

			//do validation (defined by application)
			if (validates.isEmpty()) {
				return true;
			} else {
				if (_log.isDebugEnabled()) {
					_log.debug("doValidate validates=[{}]", validates);
				}
				debugInfo += MessageFormat.format(",validates=[{0}]", validates);
				boolean valid = true;

				//ZK-878 Exception if binding a form with errorMessage
				//To handle wrong value exception when getting a component value.
				for (Property p : validates) {
					if (p instanceof WrongValuePropertyImpl) {
						for (WrongValueException wve : ((WrongValuePropertyImpl) p).getWrongValueExceptions()) {
							//refer to UiEngineImpl#handleError()
							Component wvc = wve.getComponent();
							if (wvc != null) {
								wve = ((ComponentCtrl) wvc).onWrongValue(wve);
								if (wve != null) {
									Component c = wve.getComponent();
									if (c == null)
										c = wvc;
									Clients.wrongValue(c, wve.getMessage());
								}
							}
						}
						valid = false;
					}
				}

				Map<String, Property[]> properties = _propertyBindingHandler.toCollectedProperties(validates);
				valid &= vHelper.validateSaveBefore(comp, command, properties, valid, notifys);
				valid &= vHelper.validateSaveAfter(comp, command, properties, valid, notifys);

				return valid;
			}
		} catch (Exception e) {
			_log.error(debugInfo, e);
			throw UiException.Aide.wrap(e, e.getMessage());
		} finally {
			doPostPhase(Phase.VALIDATE, ctx);
		}
	}

	protected ParamCall createParamCall(BindContext ctx) {
		final ParamCall call = new ParamCall();
		call.setBinder(this);
		call.setBindContext(ctx);
		final Component comp = ctx.getComponent();
		if (comp != null) {
			call.setComponent(comp);
		}
		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			call.setExecution(exec);
		}

		return call;
	}

	protected void doExecute(Component comp, String command, Map<String, Object> commandArgs, BindContext ctx,
			Set<Property> notifys) {
		String debugInfo = MessageFormat.format("doExecute "
				+ "comp=[{0}],command=[{1}],notifys=[{2}]", comp, command, notifys);
		try {
			Matcher matcher = CALL_OTHER_VM_COMMAND_PATTERN.matcher(command);
			if (matcher.find()) {
				String vmId = matcher.group(1);
				Map<String, Binder> vmIdBinderMap = (Map<String, Binder>) comp.getDesktop().getAttribute(BinderCtrl.VIEWMODELID_BINDER_MAP_KEY);
				Binder targetBinder = vmIdBinderMap.get(vmId);
				if (targetBinder != null) {
					((BinderImpl) targetBinder).doExecute(comp, command.replace("$" + vmId + ".", ""), commandArgs, ctx, notifys);
					return;
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug("before " + debugInfo);
			}
			doPrePhase(Phase.EXECUTE, ctx);

			final Object viewModel = getViewModelInView();
			Class<?> viewModelClass = BindUtils.getViewModelClass(viewModel);

			Method method = getCommandMethod(viewModelClass, command, _commandMethodInfoProvider,
					_commandMethodCache, commandArgs != null ? commandArgs.values().size() : 0, false);

			if (method != null) {

				BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
				if (collector != null) {
					collector.addInfo(new CommandInfo(CommandInfo.EXECUTE, comp, null, null, command, commandArgs,
							method.toString()));
				}

				ParamCall parCall = createParamCall(ctx);
				if (commandArgs != null) {
					parCall.setBindingArgs(commandArgs);
				}
				handleNotifyChange(ctx, viewModel, method, parCall, notifys);
			} else if (_notifyCommands == null || !_notifyCommands.containsKey(command)) {

				// F80-ZK-2951, ignore starting with ':' and '/'
				if (!(command.startsWith(":") || command.startsWith("/")))
					throw new UiException(
							MiscUtil.formatLocationMessage("cannot find any method that is annotated for the command "
									+ command + " with @Command in " + viewModel, comp));
			}
			if (_log.isDebugEnabled()) {
				_log.debug("after doExecute notifys=[{}]", notifys);
			}
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
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

	private Method getCommandMethod(Class<?> clz, String command, CommandMethodInfoProvider cmdInfo,
			Map<Class<?>, Map<String, CachedItem<Method>>> cache, int commandParamCount, boolean isGlobal) {
		Map<String, CachedItem<Method>> methods;
		synchronized (cache) {
			methods = cache.get(clz);
			if (methods == null) {
				methods = new HashMap<String, CachedItem<Method>>();
				cache.put(clz, methods);
			}
		}
		Method matchedMethodWithoutAnno = null;
		CachedItem<Method> method = null;
		synchronized (methods) {
			method = methods.get(command);
			boolean inited = false;
			if (method != null) { //quick check and return
				return method.value;
			} else if (methods.get(COMMAND_METHOD_MAP_INIT) != null) {
				//map is already initialized
				inited = true;
			}
			//scan
			for (Method m : clz.getMethods()) {
				if (m.isBridge()) continue;
				String mName = m.getName();
				if (!isGlobal && mName.equals(command) && m.getParameterTypes().length == commandParamCount)
					matchedMethodWithoutAnno = m;
				if (inited) continue; //already scanned @Default and @Command
				if (cmdInfo.isDefaultMethod(m)) {
					if (methods.get(COMMAND_METHOD_DEFAULT) != null) {
						throw new UiException("there are more than one " + cmdInfo.getDefaultAnnotationName()
								+ " method in " + clz + ", " + methods.get(COMMAND_METHOD_DEFAULT).value + " and " + m);
					}
					methods.put(COMMAND_METHOD_DEFAULT, new CachedItem<Method>(m));
				}
				String[] vals = cmdInfo.getCommandName(m);
				if (vals == null)
					continue;
				if (vals.length == 0) {
					vals = new String[] {mName}; //command name from method.
				}
				for (String val : vals) {
					val = val.trim();
					if (methods.get(val) != null) {
						throw new UiException("there are more than one " + cmdInfo.getAnnotationName() + " method "
								+ val + " in " + clz + ", " + methods.get(val).value + " and " + m);
					}
					methods.put(val, new CachedItem<Method>(m));
				}
			}

			if (!inited) {
				//ZK-3133 for matchMedia methods cache
				if (_matchMediaValues != null) {
					for (Map.Entry<String, Method> entry : _matchMediaValues.entrySet()) {
						methods.put(entry.getKey(), new CachedItem<Method>(entry.getValue()));
					}
				}
				methods.put(COMMAND_METHOD_MAP_INIT, NULL_METHOD); //mark this map has been initialized.
			}

			// ZK-4552
			method = methods.get(command);
			if (method == null) {
				if (matchedMethodWithoutAnno != null) {
					method = new CachedItem<Method>(matchedMethodWithoutAnno);
					methods.put(command, method);
				} else {
					method = methods.get(COMMAND_METHOD_DEFAULT); //get default
					if (method != null)
						methods.put(command, method);
				}
			}
		}
		return method == null ? null : method.value;
	}

	//doCommand -> doSaveBefore
	protected void doSaveBefore(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		String debugInfo = MessageFormat.format("doSaveBefore "
				+ "comp=[{0}],command=[{1}],evt=[{2}],notifys=[{3}]", comp, command, evt, notifys);
		if (_log.isDebugEnabled()) {
			_log.debug(debugInfo);
		}
		try {
			doPrePhase(Phase.SAVE_BEFORE, ctx);
			_propertyBindingHandler.doSaveBefore(comp, command, evt, notifys);
			_formBindingHandler.doSaveBefore(comp, command, evt, notifys);
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.SAVE_BEFORE, ctx);
		}
	}

	protected void doSaveAfter(Component comp, String command, Event evt, BindContext ctx, Set<Property> notifys) {
		String debugInfo = MessageFormat.format("doSaveAfter "
				+ "comp=[{0}],command=[{1}],evt=[{2}],notifys=[{3}]", comp, command, evt, notifys);
		if (_log.isDebugEnabled()) {
			_log.debug(debugInfo);
		}
		try {
			doPrePhase(Phase.SAVE_AFTER, ctx);
			_propertyBindingHandler.doSaveAfter(comp, command, evt, notifys);
			_formBindingHandler.doSaveAfter(comp, command, evt, notifys);
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.SAVE_AFTER, ctx);
		}

	}

	protected void doLoadBefore(Component comp, String command, BindContext ctx) {
		String debugInfo = MessageFormat.format("doLoadBefore comp=[{0}],command=[{1}]", comp, command);
		if (_log.isDebugEnabled()) {
			_log.debug(debugInfo);
		}
		try {
			doPrePhase(Phase.LOAD_BEFORE, ctx);
			_propertyBindingHandler.doLoadBefore(comp, command);
			_formBindingHandler.doLoadBefore(comp, command);
			_childrenBindingHandler.doLoadBefore(comp, command);
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.LOAD_BEFORE, ctx);
		}
	}

	protected void doLoadAfter(Component comp, String command, BindContext ctx) {
		String debugInfo = MessageFormat.format("doLoadAfter comp=[{0}],command=[{1}]", comp, command);
		if (_log.isDebugEnabled()) {
			_log.debug(debugInfo);
		}
		try {
			doPrePhase(Phase.LOAD_AFTER, ctx);
			_propertyBindingHandler.doLoadAfter(comp, command);
			_formBindingHandler.doLoadAfter(comp, command);
			_childrenBindingHandler.doLoadAfter(comp, command);
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex);
		} finally {
			doPostPhase(Phase.LOAD_AFTER, ctx);
		}

	}

	public void removeBindings(Set<Component> comps) {
		for (Component comp : comps) {
			removeBindings0(comp);
		}
		//remove tracking batchly
		TrackerImpl tracker = (TrackerImpl) getTracker();
		tracker.removeTrackings(comps);
	}

	/**
	 * Remove all bindings that associated with the specified component.
	 * @param comp the component
	 */
	public void removeBindings(Component comp) {
		removeBindings0(comp);
		//remove tracking
		TrackerImpl tracker = (TrackerImpl) getTracker();
		tracker.removeTrackings(comp);
	}
	
	private void removeBindings(Collection<Binding> removed, Component comp) {
		//F80 - store subtree's binder annotation count
		if (!_bindings.containsKey(comp) && comp instanceof ComponentCtrl)
			((ComponentCtrl) comp).disableBindingAnnotation();
		_formBindingHandler.removeBindings(removed);
		_propertyBindingHandler.removeBindings(removed);
		_childrenBindingHandler.removeBindings(removed);
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
		
		_formBindingHandler.removeBindings(bkey, removed);
		_propertyBindingHandler.removeBindings(bkey, removed);
		_childrenBindingHandler.removeBindings(bkey, removed);
		if (_validationMessages != null) {
			_validationMessages.clearMessages(comp, key);
		}
		_hasValidators.remove(bkey);
		
		removeTemplateResolver(comp, key);
		
		if (_refBindingHandler != null) {
			_refBindingHandler.removeReferenceBinding(comp, key);
		}
		
		//F80 - store subtree's binder annotation count
		removeBindings(removed, comp);
	}

	private void removeBindings0(Component comp) {
		checkInit();
		if (_rootComp == comp) {
			//the binder component was detached, unregister queue
			unsubscribeQueue(_quename, _quescope, _queueListener);
			_rootComp.removeAttribute(ACTIVATOR);
		}
		if (_validationMessages != null) {
			_validationMessages.clearMessages(comp);
		}

		final Map<String, List<Binding>> attrMap = _bindings.remove(comp);
		if (attrMap != null) {
			final Set<Binding> removed = new HashSet<Binding>();
			for (Entry<String, List<Binding>> entry : attrMap.entrySet()) {
				final String key = entry.getKey();
				removeBindings(comp, key);
				removed.addAll(entry.getValue());
			}
			if (!removed.isEmpty()) {
				removeBindings(removed, comp);
			}
		}

		removeFormAssociatedSaveBinding(comp);
		removeForm(comp);

		removeTemplateResolver(comp);
		if (_refBindingHandler != null) {
			_refBindingHandler.removeReferenceBinding(comp);
		}

		BinderUtil.unmarkHandling(comp);

		// if it is a nested binder, we have to put a mark for it and re-bind it
		// when it is re-attached again.
		if (comp.hasAttribute(BindComposer.BINDER_ID))
			comp.setAttribute(REMOVE_BINDINGS, Boolean.TRUE);
	}

	public List<Binding> getLoadPromptBindings(Component comp, String attr) {
		checkInit();
		final List<Binding> bindings = new ArrayList<Binding>();
		final BindingKey bkey = getBindingKey(comp, attr);
		final List<LoadPropertyBinding> loadBindings = _propertyBindingHandler.getLoadPromptBindings(bkey);
		if (loadBindings != null && loadBindings.size() > 0) {
			bindings.addAll(loadBindings);
		}
		if (bindings.size() == 0) { //optimize, they are exclusive
			List<LoadChildrenBinding> childrenLoadBindings = _childrenBindingHandler.getLoadPromptBindings(bkey);
			if (childrenLoadBindings != null && childrenLoadBindings.size() > 0) {
				bindings.addAll(childrenLoadBindings);
			}
		}
		return bindings;
	}

	private void addBinding(Component comp, String attr, Binding binding) {
		//ZK-2289: Futher optimize zkbind memory consumption.
		Map<String, List<Binding>> attrMap = _bindings.get(comp);
		List<Binding> bindings = attrMap == null ? null : attrMap.get(attr);
		bindings = AllocUtil.inst.addList(bindings, binding);
		//bug 657, we have to keep the attribute assignment order.
		attrMap = AllocUtil.inst.putLinkedHashMap(attrMap, attr, bindings);
		_bindings.put(comp, attrMap);

		//F80 - store subtree's binder annotation count
		if (comp instanceof ComponentCtrl)
			((ComponentCtrl) comp).enableBindingAnnotation();

		//associate component with this binder, which means, one component can only bind by one binder
		BinderUtil.markHandling(comp, this);
	}

	public void setTemplate(Component comp, String attr, String templateExpr, Map<String, Object> templateArgs) {
		Map<String, TemplateResolver> resolvers = _templateResolvers.get(comp);
		if (resolvers == null) {
			resolvers = new HashMap<String, TemplateResolver>();
			_templateResolvers.put(comp, resolvers);
		}
		resolvers.put(attr, newTemplateResolverImpl(this, comp, attr, templateExpr, templateArgs));
	}

	//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
	@SuppressWarnings("unchecked")
	private TemplateResolver newTemplateResolverImpl(BinderImpl binderImpl, Component comp, String attr,
			String templateExpr, Map<String, Object> templateArgs) {
		String clznm = Library.getProperty("org.zkoss.bind.TemplateResolver.class",
				TemplateResolverImpl.class.getName());
		try {
			Class<TemplateResolver> clz = (Class<TemplateResolver>) Classes.forNameByThread(clznm);
			Constructor<TemplateResolver> c = clz.getDeclaredConstructor(Binder.class, Component.class, String.class,
					String.class, Map.class);
			TemplateResolver resolver = c.newInstance(binderImpl, comp, attr, templateExpr, templateArgs);
			return resolver;
		} catch (Exception e) {
			throw UiException.Aide.wrap(e, "Can't initialize template resolver ");
		}
	}

	public TemplateResolver getTemplateResolver(Component comp, String attr) {
		Map<String, TemplateResolver> resolvers = _templateResolvers.get(comp);
		return resolvers == null ? null : resolvers.get(attr);
	}

	private void removeTemplateResolver(Component comp, String attr) {
		Map<String, TemplateResolver> resolvers = _templateResolvers.get(comp);
		if (resolvers != null) {
			resolvers.remove(attr);
		}
	}

	private void removeTemplateResolver(Component comp) {
		_templateResolvers.remove(comp);
	}

	@SuppressWarnings("unchecked")
	public Tracker getTracker() {
		if (_tracker == null) {
			String clznm = Library.getProperty("org.zkoss.bind.Tracker.class");
			if (clznm != null) {
				Class<Tracker> clz;
				try {
					clz = (Class<Tracker>) Classes.forNameByThread(clznm);
					_tracker = clz.newInstance();
				} catch (Exception e) {
					throw UiException.Aide.wrap(e, "Can't initialize tracker");
				}
			} else
				_tracker = new TrackerImpl();
		}
		return _tracker;
	}

	/**
	 * Internal Use only. init and load the component
	 */
	public void loadComponent(Component comp, boolean loadinit) {
		loadComponent0(comp, loadinit);
		if (comp == getView() && _notifyCommands != null) {
			// init notifyCommands here
			initNotifyCommands(comp);
		}
	}

	private void initNotifyCommands(Component comp) {
		for (Map.Entry<String, NotifyCommand> me : _notifyCommands.entrySet()) {
			addPropertyLoadBindings4Command(comp, me.getValue().onChange(), me.getKey());
		}
	}

	// since 8.0.0
	private Map<String, Object> _dynamicAttrs = new HashMap<String, Object>(5) {
		public Object put(final String key, Object value) {
			Object oldValue = super.put(key, null); // yes, we put "null" to save the memory
			BinderImpl.this.postCommand(key, Collections.singletonMap("", value));
			return oldValue;
		}
	};

	/**
	 * Internal use only.
	 * @since 8.0.0
	 */
	public Map<String, Object> getDynamicAttrs() {
		return _dynamicAttrs;
	}

	/**
	 * Internal use only.
	 * @since 8.0.0
	 */
	public void setDynamicAttrs(String command, Object value) {
		_dynamicAttrs.put(command, value);
	}

	private void addPropertyLoadBindings4Command(Component comp, String loadExpr, String command) {
		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		final String attr = "dynamicAttrs['" + command + "']";
		String vmname = (String) comp.getAttribute(BindComposer.VM_ID);
		if (vmname != null)
			loadExpr = loadExpr.replaceAll("_vm_", vmname);

		final String loadAttr = "attributes['" + BinderCtrl.BINDER + "']." + attr;
		LoadPropertyBinding binding = newLoadPropertyBinding(comp, attr, loadAttr, null, loadExpr, ConditionType.PROMPT,
				null, null, null, null);
		addBinding(comp, attr, binding);

		//if no command , always add to prompt binding, a prompt binding will be load when , 
		//1.load a component property binding
		//2.property change (TODO, DENNIS, ISSUE, I think loading of property change is triggered by tracker in doPropertyChange, not by prompt-binding 
		final BindingKey bkey = getBindingKey(comp, attr);
		_propertyBindingHandler.addLoadPromptBinding(comp, bkey, binding);

		if (collector != null) {
			collector.addInfo(new AddBindingInfo(AddBindingInfo.PROP_LOAD, comp, "", binding.getPropertyString(),
					binding.getFieldName(), null, null));
		}

		_propertyBindingHandler.doLoad(comp, bkey);
	}

	protected void loadComponent0(Component comp, boolean loadinit) {
		loadComponentProperties0(comp, loadinit);

		final Map<String, List<Binding>> compBindings = _bindings.get(comp);
		if (_activating || compBindings == null || !compBindings.keySet().contains(CHILDREN_ATTR)) {
			for (Component kid = comp.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
				loadComponent0(kid, loadinit); //recursive
			}

			// Bug ZK-3046, we handle it in ShadowElementsCtrl.filterOutShadows() when invoked by BindChildRenderer.java
			if (comp instanceof ComponentCtrl) {
				for (ShadowElement se : ((ComponentCtrl) comp).getShadowRoots()) {
					loadComponent0((Component) se, loadinit); //recursive
				}
			}
		}
	}

	private void loadComponentProperties0(Component comp, boolean loadinit) {

		final Map<String, List<Binding>> compBindings = _bindings.get(comp);
		if (compBindings != null) { // if component is not registered in this binder, do nothing.
			for (String key : compBindings.keySet()) {
				final BindingKey bkey = getBindingKey(comp, key);
				if (loadinit) {
					_formBindingHandler.doInit(comp, bkey);
				}
				_formBindingHandler.doLoad(comp, bkey);
			}
			for (String key : compBindings.keySet()) {
				final BindingKey bkey = getBindingKey(comp, key);
				if (loadinit) {
					_propertyBindingHandler.doInit(comp, bkey);
				}
				_propertyBindingHandler.doLoad(comp, bkey);
			}
			for (String key : compBindings.keySet()) {
				final BindingKey bkey = getBindingKey(comp, key);
				if (loadinit) {
					_childrenBindingHandler.doInit(comp, bkey);
				}

				_childrenBindingHandler.doLoad(comp, bkey);
			}
		}
	}

	public void notifyChange(Object base, String attr) {
		checkInit();
		if (_log.isDebugEnabled()) {
			_log.debug("notifyChange base=[{}],attr=[{}]", base, attr);
		}
		getEventQueue().publish(new PropertyChangeEvent(_rootComp, base, attr));
	}

	private void postGlobalCommand(Component comp, CommandBinding commandBinding, String command, Event evt,
			Map<String, Object> args) {
		String debugInfo = MessageFormat.format("postGlobalCommand command=[{0}],args=[{1}]", command, args);
		if (_log.isDebugEnabled()) {
			_log.debug(debugInfo);
		}

		final BindingExecutionInfoCollector collector = getBindingExecutionInfoCollector();
		try {
			if (collector != null) {
				collector.pushStack("POST_GLOBAL_COMMAND");
				collector.addInfo(new CommandInfo(CommandInfo.POST_GLOBAL, comp, evt == null ? null : evt.getName(),
						BindEvaluatorXUtil.getExpressionString(((CommandBindingImpl) commandBinding).getCommand()),
						command, args, null));
			}

			getEventQueue().publish(new GlobalCommandEvent(_rootComp, command, args, evt));
		} catch (Exception ex) {
			throw new RuntimeException(debugInfo, ex); 
		} finally {
			if (collector != null) {
				collector.popStack();
			}
		}
	}

	public void setPhaseListener(PhaseListener listener) {
		addPhaseListener(listener);
	}

	public void addPhaseListener(PhaseListener listener) {
		_phaseListeners.add(listener);
	}

	public PhaseListener getPhaseListener() {
		List<PhaseListener> list = getPhaseListeners();
		if (list != null && !list.isEmpty())
			return list.get(0);
		return null;
	}

	public List<PhaseListener> getPhaseListeners() {
		return _phaseListeners;
	}

	private void subscribeQueue(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, true);
		que.subscribe(listener);
	}

	private void unsubscribeQueue(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, false);
		if (que != null) {
			que.unsubscribe(listener);
		}
	}

	private boolean isSubscribed(String quename, String quescope, EventListener<Event> listener) {
		EventQueue<Event> que = EventQueues.lookup(quename, quescope, false);
		return que == null ? false : que.isSubscribed(listener);
	}

	protected EventQueue<Event> getEventQueue() {
		return EventQueues.lookup(_quename, _quescope, true);
	}

	// create a unique id base on component's uuid and attr
	private BindingKey getBindingKey(Component comp, String attr) {
		return new BindingKey(comp, attr);
	}

	private class PostCommandListener implements EventListener<Event>, Serializable {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public void onEvent(Event event) throws Exception {
			Object[] data = (Object[]) event.getData();
			String command = (String) data[0];
			Map<String, Object> args = (Map<String, Object>) data[1];
			sendCommand(command, args);
		}
	}

	private void removeFormAssociatedSaveBinding(Component comp) {
		_assocFormSaveBindings.remove(comp);
		Map<SaveBinding, Set<SaveBinding>> associated = _reversedAssocFormSaveBindings.remove(comp);
		if (associated != null) {
			Set<Entry<SaveBinding, Set<SaveBinding>>> entries = associated.entrySet();
			for (Entry<SaveBinding, Set<SaveBinding>> entry : entries) {
				entry.getValue().remove(entry.getKey());
			}
		}
	}

	public void addFormAssociatedSaveBinding(Component associatedComp, String formId, SaveBinding saveBinding,
			String fieldName) {
		checkInit();
		//find the form component by form id and a associated/nested component
		Component formComp = lookupAssociatedFormComponent(formId, associatedComp);
		if (formComp == null) {
			throw new UiException("cannot find any form " + formId + " with " + associatedComp);
		}
		Binder saveCompBinder = saveBinding.getBinder();
		boolean isSameBinder = this.equals(saveCompBinder);
		Set<SaveBinding> bindings = null;
		Set<SaveBinding> originalBindings = _assocFormSaveBindings.get(formComp);
		if (!isSameBinder) {
			bindings = ((BinderImpl) saveCompBinder)._assocFormSaveBindings.get(formComp);
			if (bindings == null) {
				bindings = new LinkedHashSet<SaveBinding>();
				((BinderImpl) saveCompBinder)._assocFormSaveBindings.put(formComp, bindings);
			}
			if (originalBindings != null)
				bindings.addAll(originalBindings);
		} else if (bindings == null) {
			bindings = originalBindings != null ? originalBindings : new LinkedHashSet<SaveBinding>(); //keep the order
		}
		_assocFormSaveBindings.put(formComp, bindings);
		bindings.add(saveBinding);

		//keep the reverse association , so we can remove it if the associated component is detached (and the form component is not).
		Map<SaveBinding, Set<SaveBinding>> reverseMap = null;
		Map<SaveBinding, Set<SaveBinding>> originalReverseMap = _reversedAssocFormSaveBindings.get(associatedComp);
		if (!isSameBinder) {
			reverseMap = ((BinderImpl) saveCompBinder)._reversedAssocFormSaveBindings.get(associatedComp);
			if (reverseMap == null) {
				reverseMap = new HashMap<SaveBinding, Set<SaveBinding>>();
				((BinderImpl) saveCompBinder)._reversedAssocFormSaveBindings.put(associatedComp, reverseMap);
			}
			if (originalReverseMap != null)
				reverseMap.get(saveBinding).addAll(originalReverseMap.get(saveBinding));
		} else if (reverseMap == null) {
			reverseMap = originalReverseMap != null ? originalReverseMap : new HashMap<SaveBinding, Set<SaveBinding>>();
		}
		_reversedAssocFormSaveBindings.put(associatedComp, reverseMap);
		reverseMap.put(saveBinding, bindings);

		//ZK-1017 Property of a form is not correct when validation
		//ZK-1005 ZK 6.0.1 validation fails on nested bean
		((SavePropertyBindingImpl) saveBinding).setFormFieldInfo(formComp, formId, fieldName);
	}

	private Component lookupAssociatedFormComponent(String formId, Component associatedComp) {
		String fid = null;
		Component p = associatedComp;
		while (p != null) {
			fid = (String) p.getAttribute(FORM_ID); //check in default component scope
			if (fid != null && fid.equals(formId)) {
				break;
			}
			p = p.getParent();
		}

		return p;
	}

	public Set<SaveBinding> getFormAssociatedSaveBindings(Component comp) {
		checkInit();
		Set<SaveBinding> bindings = _assocFormSaveBindings.get(comp);
		if (bindings == null) {
			return Collections.emptySet();
		}
		return new LinkedHashSet<SaveBinding>(bindings); //keep the order
	}

	//utility to simply hold a value which might be null
	private static class CachedItem<T> {
		final T value;

		public CachedItem(T value) {
			this.value = value;
		}
	}

	public boolean hasValidator(Component comp, String attr) {
		BindingKey bkey = getBindingKey(comp, attr);
		return _hasValidators.contains(bkey);
	}

	public Component getView() {
		checkInit();
		return _rootComp;
	}

	public ValidationMessages getValidationMessages() {
		return _validationMessages;
	}

	public void setValidationMessages(ValidationMessages messages) {
		_validationMessages = messages;
	}

	/**
	 * did activate when the session is activating
	 */
	private void didActivate() {
		_activating = true;
		try {
			_log.debug("didActivate : [{}]", BinderImpl.this);
			//re-tie value to tracker.
			loadComponent(_rootComp, false);
		} finally {
			_activating = false;
		}
	}

	/**
	 * object that store in root component to help activating.
	 */
	private class Activator implements ComponentActivationListener, Serializable {
		private static final long serialVersionUID = 1L;

		public void didActivate(Component comp) {
			if (_rootComp.equals(comp)) {
				//zk 1442, don't do multiple subscribed if didActivate is called every request (e.x. jboss5)
				initQueue();
				if (_deferredActivator == null) {
					//defer activation to execution only for the first didActivate when failover
					comp.getDesktop().addListener(_deferredActivator = new DeferredActivator());
				}
			}
		}

		public void willPassivate(Component comp) {
			//zk 1442, do nothing
		}
	}

	/**
	 * object that store in desktop listener to help activating.
	 * it do the activation when first execution come into
	 */
	private class DeferredActivator implements ExecutionInit, Serializable {
		private static final long serialVersionUID = 1L;

		public void init(Execution exec, Execution parent) throws Exception {
			Desktop desktop = exec.getDesktop();
			desktop.removeListener(_deferredActivator);
			BinderImpl.this.didActivate();
		}
	}

	public BindingExecutionInfoCollector getBindingExecutionInfoCollector() {
		DebuggerFactory factory = DebuggerFactory.getInstance();
		if (factory == null) return null;
		// ZK-5048: setViewModelClass for MVVM DebuggerFactory to log via SLF4J
		BindingExecutionInfoCollector collector = factory.getExecutionInfoCollector();
		if (collector instanceof DefaultExecutionInfoCollector) {
			Class<?> vmClass = BindUtils.getViewModelClass(getViewModelInView());
			((DefaultExecutionInfoCollector) collector).setViewModelClass(vmClass);
		}
		return collector;
	}

	public BindingAnnotationInfoChecker getBindingAnnotationInfoChecker() {
		DebuggerFactory factory = DebuggerFactory.getInstance();
		if (factory == null) return null;
		// ZK-5048: setViewModelClass for MVVM DebuggerFactory to log via SLF4J
		BindingAnnotationInfoChecker checker = factory.getAnnotationInfoChecker();
		if (checker instanceof DefaultAnnotationInfoChecker) {
			Class<?> vmClass = BindUtils.getViewModelClass(getViewModelInView());
			((DefaultAnnotationInfoChecker) checker).setViewModelClass(vmClass);
		}
		return checker;
	}

	public String getQueueName() {
		return _quename;
	}

	public String getQueueScope() {
		return _quescope;
	}

	public Map<String, Method> getMatchMediaValue() {
		if (_matchMediaValues == null) {
			_matchMediaValues = initMatchMediaValues(getViewModel());
		}
		return Collections.unmodifiableMap(_matchMediaValues);
	}

	private Map<Object, Set<String>> initSaveFormMap() {
		if (_saveFormFields == null) {
			_saveFormFields = new HashMap<Object, Set<String>>(4);
		}
		return _saveFormFields;
	}

	public void addSaveFormFieldName(Form form, String fieldName) {
		Set<String> fields = initSaveFormMap().get(form);
		if (fields == null) {
			fields = new HashSet<String>(16);
			_saveFormFields.put(form, fields);
		}
		fields.add(fieldName);
	}

	public void addSaveFormFieldName(Form form, Set<String> fieldNames) {
		Set<String> fields = initSaveFormMap().get(form);
		if (fields == null) {
			fields = new HashSet<String>(16);
			_saveFormFields.put(form, fields);
		}
		fields.addAll(fieldNames);

	}

	@SuppressWarnings("unchecked")
	public Set<String> removeSaveFormFieldNames(Form self) {
		Set<String> result = initSaveFormMap().remove(self);
		if (result == null)
			return Collections.EMPTY_SET;
		return result;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getSaveFormFieldNames(Form form) {
		Set<String> result = initSaveFormMap().get(form);
		if (result == null)
			return Collections.EMPTY_SET;
		return result;
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
	}

	/**
	 * Internal use only.
	 * Check and init queue
	 */
	public void initQueue() {
		//subscribe queue
		if (!isSubscribed(_quename, _quescope, _queueListener))
			subscribeQueue(_quename, _quescope, _queueListener);
	}

	/**
	 * Internal use only.
	 * Check and init Activator
	 */
	public void initActivator() {
		if (_rootComp != null && !_rootComp.hasAttribute(ACTIVATOR))
			_rootComp.setAttribute(ACTIVATOR, new Activator()); //keep only one instance in root comp
	}
}
