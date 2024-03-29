/* AbstractAnnotatedMethodInvoker

	Purpose:
		
	Description:
		
	History:
		Jun 27, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.bind.impl;

import static org.zkoss.bind.sys.BinderCtrl.VM;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Property;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.init.ViewModelAnnotationResolvers;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;

/**
 * 
 * To help calling a ViewModel annotated method with binding arguments. 
 * 
 * @see Init
 * @see AfterCompose
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public abstract class AbstractAnnotatedMethodInvoker<T extends Annotation> {

	private static final Logger _log = LoggerFactory.getLogger(AbstractAnnotatedMethodInvoker.class);

	private final Map<Class<?>, List<Method>> annoMethodCache;

	private Class<T> annoClass;

	public AbstractAnnotatedMethodInvoker(Class<T> annoClass, Map<Class<?>, List<Method>> annoMethodCache) {
		this.annoClass = annoClass;
		this.annoMethodCache = annoMethodCache;
	}

	public boolean hasAnnotatedMethod(Binder binder) {
		Component rootComp = binder.getView();
		Object viewModel = rootComp.getAttribute(VM);

		final Class<?> vmClz = BindUtils.getViewModelClass(viewModel);
		List<Method> methods = getAnnotateMethods(binder, annoClass, vmClz);
		return (!methods.isEmpty());
	}

	public void invokeMethod(Binder binder, Map<String, Object> bindingArgs) {
		invokeMethod(binder, bindingArgs, null, false);
	}

	public void invokeMethod(Binder binder, Map<String, Object> bindingArgs, Event triggeringEvent, boolean notifyChange) {

		Component rootComp = binder.getView();
		Object viewModel = rootComp.getAttribute(VM);

		final Class<?> vmClz = BindUtils.getViewModelClass(viewModel);
		List<Method> methods = getAnnotateMethods(binder, annoClass, vmClz);
		if (methods.size() == 0)
			return; //no annotated method

		if (bindingArgs != null) {
			bindingArgs = BindEvaluatorXUtil.evalArgs(binder.getEvaluatorX(), rootComp, bindingArgs);
		}

		Set<Property> changes = Collections.emptySet();
		if (notifyChange) {
			changes = new HashSet<Property>();
		}
		for (Method m : methods) { //TODO: why paramCall need to be prepared each time?
			final BindContext ctx = BindContextUtil.newBindContext(binder, null, false, null, rootComp, triggeringEvent);

			try {
				ParamCall parCall = binder instanceof BinderImpl ? ((BinderImpl) binder).createParamCall(ctx)
						: createParamCall(ctx, binder);
				if (bindingArgs != null) {
					parCall.setBindingArgs(bindingArgs);
				}

				if (notifyChange) {
					handleNotifyChange(binder, ctx, viewModel, m, parCall, changes);
				} else {
					parCall.call(viewModel, m);
				}
			} catch (Exception e) {
				synchronized (annoMethodCache) { //remove it for the hot deploy case if getting any error
					annoMethodCache.remove(vmClz);
				}
				throw UiException.Aide.wrap(e, e.getMessage());
			}
		}
		if (!changes.isEmpty()) {
			fireNotifyChanges(binder, changes);
		}
	}

	protected void handleNotifyChange(Binder binder, BindContext ctx, Object viewModel, Method m, ParamCall parCall, Set<Property> changes) {
		if (binder instanceof BinderImpl)
			((BinderImpl) binder).handleNotifyChange(ctx, viewModel, m, parCall, changes);
	}

	protected void fireNotifyChanges(Binder binder, Set<Property> changes) {
		if (binder instanceof BinderImpl)
			((BinderImpl) binder).fireNotifyChanges(changes);
	}

	private List<Method> getAnnotateMethods(Binder binder, Class<T> annotationClass, Class<?> vmClass) {
		List<Method> methods = null;
		synchronized (annoMethodCache) {
			//have to synchronized cache, because it calls expunge when get.
			methods = annoMethodCache.get(vmClass);
			if (methods != null)
				return methods;

			methods = new ArrayList<Method>(); //if still null in synchronized, scan it

			Class<?> curr = vmClass;

			String sign = null;
			Set<String> signs = new HashSet<String>();

			while (curr != null && !curr.equals(Object.class)) {
				Method currm = null;
				//Annotation should supports to annotate on Type
				T annotation = ViewModelAnnotationResolvers.getAnnotation(curr, annotationClass);
				//Allow only one annotated method in a class.
				for (Method m : curr.getDeclaredMethods()) {
					final T i = ViewModelAnnotationResolvers.getAnnotation(m, annotationClass);
					if (i == null)
						continue;
					if (annotation != null) {
						throw new UiException(
								"more than one [@" + annotationClass.getSimpleName() + "] in the class " + curr);
					}
					annotation = i;
					currm = m;
					//don't break, we need to check all annotated methods, we allow only one per class.
				}

				if (currm != null) {
					//check if overrode the same annotated method
					sign = MiscUtil.toSimpleMethodSignature(currm);
					if (signs.contains(sign)) {
						_log.warn("more than one {} method that has same signature '{}' "
								+ "in the hierarchy of '{}', the method in extended class will be call "
								+ "more than once ", annotationClass.getSimpleName(), sign, vmClass);
					} else {
						signs.add(sign);
					}

					if (annotation.annotationType() == Destroy.class) {
						//super last
						methods.add(currm);
					} else {
						//super first
						methods.add(0, currm);
					}
				}
				//check if we should take care super's annotated methods also.
				curr = (annotation != null && shouldLookupSuperclass(annotation)) ? curr.getSuperclass() : null;
			}
			methods = Collections.unmodifiableList(methods);
			annoMethodCache.put(vmClass, methods);
		}
		return methods;
	}

	/**
	 * Verify if the super classes need to be traced.  
	 * @param annotation
	 * @return true, if handler should trace super class, false otherwise. 
	 */
	protected abstract boolean shouldLookupSuperclass(T annotation);

	private static ParamCall createParamCall(BindContext ctx, Binder binder) {
		final ParamCall call = new ParamCall();
		call.setBinder(binder);
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
}
