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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.HistoryPopState;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.bind.impl.AbstractAnnotatedMethodInvoker;
import org.zkoss.bind.impl.AnnotationUtil;
import org.zkoss.bind.impl.BindEvaluatorXUtil;
import org.zkoss.bind.impl.MiscUtil;
import org.zkoss.bind.impl.ValidationMessagesImpl;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.DebuggerFactory;
import org.zkoss.bind.tracker.impl.BindUiLifeCycle;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.util.CacheMap;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Callback;
import org.zkoss.zk.ui.util.ComponentActivationListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.ConventionWires;

/**
 * Base composer to apply ZK Bind.
 *
 * @author henrichen
 * @since 6.0.0
 */
@SuppressWarnings("rawtypes")
public class BindComposer<T extends Component>
		implements Composer<T>, ComposerExt<T>, Serializable, AuService, ComponentActivationListener {

	private static final long serialVersionUID = 1463169907348730644L;

	private static final Logger _log = LoggerFactory.getLogger(BindComposer.class);

	public static final String VM_ID = "$VM_ID$";
	public static final String BINDER_ID = "$BINDER_ID$";

	private Object _viewModel;
	private AnnotateBinder _binder;

	private final Map<String, Converter> _converters;
	private final Map<String, Validator> _validators;
	private final BindEvaluatorX evalx;

	protected static final String ID_ANNO = "id";
	protected static final String INIT_ANNO = "init";

	protected static final String VALUE_ANNO_ATTR = "value";

	protected static final String VIEW_MODEL_ATTR = "viewModel";
	protected static final String BINDER_ATTR = "binder";
	protected static final String VALIDATION_MESSAGES_ATTR = "validationMessages";

	protected static final String QUEUE_NAME_ANNO_ATTR = "queueName";
	protected static final String QUEUE_SCOPE_ANNO_ATTR = "queueScope";

	private static final Map<Class<?>, List<Method>> _afterComposeMethodCache = new CacheMap<Class<?>, List<Method>>(
			600, CacheMap.DEFAULT_LIFETIME);
	private static final Map<Class<?>, List<Method>> _historyPopStateMethodCache = new CacheMap<Class<?>, List<Method>>(
			600, CacheMap.DEFAULT_LIFETIME);

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
			//do view model proxy
			Object vm = this._binder.createViewModelProxyIfEnabled(_viewModel);
			this._binder.setViewModel(vm);
			_viewModel = vm;
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
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) throws Exception {
		return compInfo;
	}

	public void doBeforeComposeChildren(final Component comp) throws Exception {
		//ZK-3831
		if (comp.getPage() == null) {
			final Map<?, ?> currentArg = Executions.getCurrent().getArg();
			((ComponentCtrl) comp).addCallback(ComponentCtrl.AFTER_PAGE_ATTACHED, new Callback() {
				public void call(Object data) {
					try {
						Executions.getCurrent().pushArg(currentArg);
						doBeforeComposeChildren(comp);
					} catch (Exception e) {
						throw UiException.Aide.wrap(e);
					} finally {
						Executions.getCurrent().popArg();
					}
				}
			});
			return;
		}
		//init viewmodel first
		_viewModel = initViewModel(evalx, comp);
		_binder = initBinder(evalx, comp);

		//do view model proxy
		if (!this.equals(_viewModel)) {
			Object vmProxy = _binder.createViewModelProxyIfEnabled(_viewModel);
			if (vmProxy != null) {
				_viewModel = vmProxy;
				comp.setAttribute((String) comp.getAttribute(VM_ID), vmProxy);
			}
		}

		ValidationMessages vmsgs = initValidationMessages(evalx, comp, _binder);

		//wire before call init
		Selectors.wireVariables(comp, _viewModel, Selectors.newVariableResolvers(BindUtils.getViewModelClass(_viewModel), null));
		if (vmsgs != null) {
			_binder.setValidationMessages(vmsgs);
		}

		try {
			BinderKeeper keeper = BinderKeeper.getInstance(comp);
			keeper.book(_binder, comp);

			_binder.init(comp, _viewModel, getViewModelInitArgs(evalx, comp));
		} catch (Exception x) {
			throw MiscUtil.mergeExceptionInfo(x, comp);
		}

		//to apply composer-name
		ConventionWires.wireController(comp, this);
	}

	//--Composer--//
	public void doAfterCompose(final T comp) throws Exception {
		//ZK-3831
		if (comp.getPage() == null) {
			final Map<?, ?> currentArg = Executions.getCurrent().getArg();
			((ComponentCtrl) comp).addCallback(ComponentCtrl.AFTER_PAGE_ATTACHED, new Callback() {
				public void call(Object data) {
					try {
						Executions.getCurrent().pushArg(currentArg);
						doAfterCompose(comp);
					} catch (Exception e) {
						throw UiException.Aide.wrap(e);
					} finally {
						Executions.getCurrent().popArg();
					}
				}
			});
			return;
		}
		_binder.initAnnotatedBindings();

		// trigger ViewModel's @AfterCompose method.
		new AbstractAnnotatedMethodInvoker<AfterCompose>(AfterCompose.class, _afterComposeMethodCache) {
			protected boolean shouldLookupSuperclass(AfterCompose annotation) {
				return annotation.superclass();
			}
		}.invokeMethod(_binder, getViewModelInitArgs(evalx, comp));

		// call loadComponent
		BinderKeeper keeper = BinderKeeper.getInstance(comp);
		if (keeper.isRootBinder(_binder)) {
			keeper.loadComponentForAllBinders();
		}

		comp.setAttribute(BinderCtrl.ON_BIND_PROPERITIES_READY, true);
		comp.setAuService(this);

		// ZK-3711 Listen to HistoryPopStateEvent if @HistoryPopState exists.
		final AbstractAnnotatedMethodInvoker<HistoryPopState> historyPopStateInvoker =
		new AbstractAnnotatedMethodInvoker<HistoryPopState>(HistoryPopState.class, _historyPopStateMethodCache) {
			protected boolean shouldLookupSuperclass(HistoryPopState annotation) {
				return false;
			}
		};
		if (historyPopStateInvoker.hasAnnotatedMethod(_binder)) {
			Page page = comp.getPage();
			if (page != null) {
				page.addEventListener(Events.ON_HISTORY_POP_STATE, new SerializableEventListener<HistoryPopStateEvent>() {
					// ZK-4061: Prevent from duplicated handling because of multiple root components
					private HistoryPopStateEvent _handling = null;
					public void onEvent(HistoryPopStateEvent event) throws Exception {
						if (event != _handling) {
							_handling = event;
							historyPopStateInvoker.invokeMethod(getBinder(), null, event, true);
						}
					}
				});
			}
		}

		Collection<Callback> callbacks = ((ComponentCtrl) comp).getCallback(BinderCtrl.ON_BIND_PROPERITIES_READY);
		for (Callback callback : new ArrayList<Callback>(callbacks)) {
			callback.call(_binder);
			((ComponentCtrl) comp).removeCallback(BinderCtrl.ON_BIND_PROPERITIES_READY, callback);
		}
	}

	private Map<String, Object> getViewModelInitArgs(BindEvaluatorX evalx, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Collection<Annotation> anncol = compCtrl.getAnnotations(VIEW_MODEL_ATTR, INIT_ANNO);
		if (anncol.size() == 0)
			return null;
		final Annotation ann = anncol.iterator().next();

		final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
		Map<String, String[]> args = null;

		for (final Iterator<Entry<String, String[]>> it = attrs.entrySet().iterator(); it.hasNext();) {
			final Entry<String, String[]> entry = it.next();
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
		return args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(), args);
	}

	private Object initViewModel(BindEvaluatorX evalx, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(VIEW_MODEL_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(VIEW_MODEL_ATTR, INIT_ANNO);
		String vmname = null;
		Object vm = null;

		BindingAnnotationInfoChecker checker = getBindingAnnotationInfoChecker();
		if (checker != null) {
			checker.checkViewModel(comp);
		}

		if (idanno == null && initanno == null) {
			return _viewModel;
		} else if (idanno == null) {
			throw new IllegalSyntaxException(
					MiscUtil.formatLocationMessage("you have to use @id to assign the name of view model", comp));
		} else if (initanno == null) {
			throw new IllegalSyntaxException(
					MiscUtil.formatLocationMessage("you have to use @init to assign the view model", comp));
		}

		vmname = BindEvaluatorXUtil.eval(evalx, comp,
				AnnotationUtil.testString(idanno.getAttributeValues(VALUE_ANNO_ATTR), idanno), String.class);
		vm = BindEvaluatorXUtil.eval(evalx, comp,
				AnnotationUtil.testString(initanno.getAttributeValues(VALUE_ANNO_ATTR), initanno), Object.class);

		if (Strings.isEmpty(vmname)) {
			throw new UiException(MiscUtil.formatLocationMessage("name of view model is empty", idanno));
		}

		try {
			if (vm instanceof String) {
				Page page = comp.getPage();
				if (page == null) {
					throw new UiException(MiscUtil.formatLocationMessage(
							"can't find Page to resolve a view model class :'" + vm + "'", initanno));
				} else {
					vm = comp.getPage().resolveClass((String) vm);
				}
			}
			if (vm instanceof Class<?>) {
				vm = ((Class<?>) vm).newInstance();
			}
		} catch (Exception e) {
			throw MiscUtil.mergeExceptionInfo(e, initanno);
		}
		if (vm == null) {
			throw new UiException(MiscUtil.formatLocationMessage("view model of '" + vmname + "' is null", initanno));
		} else if (vm.getClass().isPrimitive()) {
			throw new UiException(MiscUtil
					.formatLocationMessage("view model '" + vmname + "' is a primitive type, is " + vm, initanno));
		}
		comp.setAttribute(vmname, vm);
		comp.setAttribute(VM_ID, vmname);

		Desktop desktop = comp.getDesktop();
		Map<Object, Component> relationMap = (Map<Object, Component>) desktop.getAttribute(BinderCtrl.VIEWMODEL_COMPONENT_MAP_KEY);
		if (relationMap == null) {
			relationMap = new HashMap<>();
			desktop.setAttribute(BinderCtrl.VIEWMODEL_COMPONENT_MAP_KEY, relationMap);
		}
		relationMap.put(vm, comp);
		return vm;
	}

	private AnnotateBinder initBinder(BindEvaluatorX evalx, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(BINDER_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(BINDER_ATTR, INIT_ANNO);
		Object binder = null;
		String bname = null;

		BindingAnnotationInfoChecker checker = getBindingAnnotationInfoChecker();
		if (checker != null) {
			checker.checkBinder(comp);
		}

		if (idanno != null) {
			bname = BindEvaluatorXUtil.eval(evalx, comp,
					AnnotationUtil.testString(idanno.getAttributeValues(VALUE_ANNO_ATTR), idanno), String.class);
			if (Strings.isEmpty(bname)) {
				throw new UiException(MiscUtil.formatLocationMessage("name of binder is empty", idanno));
			}
		} else {
			bname = BINDER_ATTR;
		}

		if (initanno != null) {
			binder = AnnotationUtil.testString(initanno.getAttributeValues(VALUE_ANNO_ATTR), initanno);
			String name = AnnotationUtil.testString(initanno.getAttributeValues(QUEUE_NAME_ANNO_ATTR), initanno);
			String scope = AnnotationUtil.testString(initanno.getAttributeValues(QUEUE_SCOPE_ANNO_ATTR), initanno);
			//if no binder, create default binder with custom queue name and scope
			String expr;
			if (name != null) {
				name = BindEvaluatorXUtil.eval(evalx, comp, expr = name, String.class);
				if (Strings.isBlank(name)) {
					throw new UiException(MiscUtil
							.formatLocationMessage("evaluated queue name is empty, expression is " + expr, initanno));
				}
			}
			if (scope != null) {
				scope = BindEvaluatorXUtil.eval(evalx, comp, expr = scope, String.class);
				if (Strings.isBlank(scope)) {
					throw new UiException(MiscUtil
							.formatLocationMessage("evaluated queue scope is empty, expression is " + expr, initanno));
				}
			}
			if (binder != null) {

				binder = BindEvaluatorXUtil.eval(evalx, comp, (String) binder, Object.class);
				try {
					if (binder instanceof String) {
						binder = comp.getPage().resolveClass((String) binder);
					}
					if (binder instanceof Class<?>) {
						binder = ((Class<?>) binder).getDeclaredConstructor(String.class, String.class)
								.newInstance(name, scope);
					}
				} catch (Exception e) {
					throw UiException.Aide.wrap(e, e.getMessage());
				}
				if (!(binder instanceof AnnotateBinder)) {
					throw new UiException(
							MiscUtil.formatLocationMessage("evaluated binder is not a binder is " + binder, initanno));
				}

			} else {
				binder = newAnnotateBinder(name, scope); //ZK-2288
			}
		} else {
			binder = newAnnotateBinder(null, null); //ZK-2288
		}

		//put to attribute, so binder could be referred by the name
		comp.setAttribute(bname, binder);
		comp.setAttribute(BINDER_ID, bname);

		return (AnnotateBinder) binder;
	}

	//ZK-2288: A way to specify a customized default AnnotateBinder.
	private AnnotateBinder newAnnotateBinder(String name, String scope) {
		String clznm = Library.getProperty("org.zkoss.bind.AnnotateBinder.class");
		if (clznm != null) {
			try {
				return (AnnotateBinder) Classes.newInstanceByThread(clznm, new Class[] { String.class, String.class },
						new String[] { name, scope });
			} catch (Exception e) {
				throw UiException.Aide.wrap(e, "Can't initialize binder");
			}
		} else {
			return new AnnotateBinder(name, scope);
		}
	}

	private ValidationMessages initValidationMessages(BindEvaluatorX evalx, Component comp, Binder binder) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation idanno = compCtrl.getAnnotation(VALIDATION_MESSAGES_ATTR, ID_ANNO);
		final Annotation initanno = compCtrl.getAnnotation(VALIDATION_MESSAGES_ATTR, INIT_ANNO);
		Object vmessages = null;
		String vname = null;

		BindingAnnotationInfoChecker checker = getBindingAnnotationInfoChecker();
		if (checker != null) {
			checker.checkValidationMessages(comp);
		}

		if (idanno != null) {
			vname = BindEvaluatorXUtil.eval(evalx, comp,
					AnnotationUtil.testString(idanno.getAttributeValues(VALUE_ANNO_ATTR), idanno), String.class);
			if (Strings.isEmpty(vname)) {
				throw new UiException(MiscUtil.formatLocationMessage("name of ValidationMessages is empty", idanno));
			}
		} else {
			return null; //validation messages is default null
		}

		if (initanno != null) {
			vmessages = BindEvaluatorXUtil.eval(evalx, comp,
					AnnotationUtil.testString(initanno.getAttributeValues(VALUE_ANNO_ATTR), initanno), Object.class);
			try {
				if (vmessages instanceof String) {
					vmessages = comp.getPage().resolveClass((String) vmessages);
				}
				if (vmessages instanceof Class<?>) {
					vmessages = ((Class<?>) vmessages).newInstance();
				}
			} catch (Exception e) {
				throw UiException.Aide.wrap(e, MiscUtil.formatLocationMessage(e.getMessage(), initanno));
			}
			if (!(vmessages instanceof ValidationMessages)) {
				throw new UiException(MiscUtil.formatLocationMessage(
						"evaluated validationMessages is not a ValidationMessages is " + vmessages, initanno));
			}
		} else {
			vmessages = new ValidationMessagesImpl();
		}

		//put to attribute, so binder could be referred by the name
		comp.setAttribute(vname, vmessages);

		return (ValidationMessages) vmessages;
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

	// Bug fixed for B70-ZK-2843
	public void didActivate(Component comp) {
		Selectors.rewireVariablesOnActivate(comp, this.getViewModel(),
				Selectors.newVariableResolvers(BindUtils.getViewModelClass(_viewModel), null));
	}

	public void willPassivate(Component comp) {

	}

	/**
	 * <p>A parsing scope context for storing Binders, and handle there loadComponent
	 * invocation properly.</p>
	 * <p>
	 * <p>if component trees with bindings are totally separated( none of
	 * each contains another), then for each separated tree, there's only one keeper.</p>
	 *
	 * @author Ian Y.T Tsai(zanyking)
	 */
	private static class BinderKeeper {
		private static final String KEY_BINDER_KEEPER = "$BinderKeeper$";

		/**
		 * get a Binder Keeper or create it by demand.
		 *
		 * @param comp
		 * @return
		 */
		static BinderKeeper getInstance(Component comp) {
			BinderKeeper keeper = (BinderKeeper) comp.getAttribute(KEY_BINDER_KEEPER, true);
			if (keeper == null) {
				comp.setAttribute(KEY_BINDER_KEEPER, keeper = new BinderKeeper(comp));
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
			comp.addEventListener("onRootBinderHostDone", new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					//suicide first...
					_host.removeEventListener("onRootBinderHostDone", this);
					BinderKeeper keeper = (BinderKeeper) _host.getAttribute(KEY_BINDER_KEEPER);
					if (keeper == null) {
						// suppose to be null...
					} else {
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

		public boolean isRootBinder(Binder binder) {
			return _queue.getFirst().binder == binder;
		}

		public void loadComponentForAllBinders() {
			_host.removeAttribute(KEY_BINDER_KEEPER);
			for (Loader loader : _queue) {
				loader.load();
			}
		}

		/**
		 * for Binder to load Component.
		 *
		 * @author Ian Y.T Tsai(zanyking)
		 */
		private static class Loader {
			Binder binder;
			Component comp;

			public Loader(Binder binder, Component comp) {
				super();
				this.binder = binder;
				this.comp = comp;
			}

			public void load() {
				//ZK-1699, mark the comp and it's children are handling, to prevent load twice in include.src case 
				BindUiLifeCycle.markLifeCycleHandling(comp);

				//load data
				binder.loadComponent(comp, true); //load all bindings
			}
		} //end of class...
	} //end of class...

	private BindingAnnotationInfoChecker getBindingAnnotationInfoChecker() {
		DebuggerFactory factory = DebuggerFactory.getInstance();
		return factory == null ? null : factory.getAnnotationInfoChecker();
	}

	public boolean service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.startsWith("onBindCommand$") || cmd.startsWith("onBindGlobalCommand$") || cmd.startsWith("onBindCommandUpload$")) {
			final Map<String, Object> data = request.getData();
			String vcmd = data.get("cmd").toString();

			final ToServerCommand ccmd = BindUtils.getViewModelClass(_viewModel).getAnnotation(ToServerCommand.class);
			List<String> asList = new ArrayList<String>();
			if (ccmd != null) {
				asList.addAll(Arrays.asList(ccmd.value()));
			}
			//ZK-3133
			Map<String, Method> mmv = _binder.getMatchMediaValue();
			if (!mmv.isEmpty()) {
				asList.addAll(mmv.keySet());
			}
			if (asList != null) {
				if (asList.contains("*") || asList.contains(vcmd)) {
					if (cmd.startsWith("onBindCommand$")) {
						_binder.postCommand(vcmd, (Map<String, Object>) data.get("args"));
					} else if (cmd.startsWith("onBindGlobalCommand$")) {
						BindUtils.postGlobalCommand(_binder.getQueueName(), _binder.getQueueScope(), vcmd,
								(Map<String, Object>) data.get("args"));
					} else if (cmd.startsWith("onBindCommandUpload$")) { // ZK-4472
						_binder.postCommand(vcmd, Collections.singletonMap(BinderCtrl.CLIENT_UPLOAD_INFO, UploadEvent.getLatestUploadEvent(vcmd, request.getComponent(), request)));
					}
				}
			}
			return true;
		}
		return false;
	}

} //end of class...
