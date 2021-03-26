package org.zkoss.bind.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.BindingParams;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.CookieParam;
import org.zkoss.bind.annotation.Default;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.bind.annotation.Scope;
import org.zkoss.bind.annotation.ScopeParam;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.bind.init.ViewModelAnnotationResolvers;
import org.zkoss.bind.paranamer.AdaptiveParanamer;
import org.zkoss.bind.paranamer.CachingParanamer;
import org.zkoss.bind.paranamer.Paranamer;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.json.JSONAware;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.OperationException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Parser;
import org.zkoss.zk.ui.select.Selectors;

/**
 * To help invoke a method with {@link BindingParam} etc.. features.
 * @author dennis
 * @since 6.0.0
 */
public class ParamCall {

	private static final Logger _log = LoggerFactory.getLogger(ParamCall.class);
	private static final Paranamer _PARANAMER = new CachingParanamer(new AdaptiveParanamer());

	protected Map<Class<? extends Annotation>, ParamResolver<Annotation>> _paramResolvers;
	private List<Type> _types; //to map class type directly, regardless the annotation
	private boolean _mappingType; //to enable the map class type without annotation, it is for compatible to rc2, only support BindeContext and Binder
	private ContextObjects _contextObjects;

	private static final String COOKIE_CACHE = "$PARAM_COOKIES$";
	public static final String BINDING_PARAM_CALL_TYPE = "org.zkoss.bind.BindingParamCall.type";

	private Component _root = null;
	private Component _component = null;
	private Execution _execution = null;
	private Binder _binder = null;
	private BindContext _bindContext = null;

	public ParamCall() {
		this(true);
	}

	public ParamCall(boolean mappingType) {
		_paramResolvers = new HashMap<Class<? extends Annotation>, ParamResolver<Annotation>>();
		_contextObjects = new ContextObjects();
		_types = new ArrayList<Type>();
		_mappingType = mappingType;
		_paramResolvers.put(ContextParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Object val = _contextObjects.get(((ContextParam) anno).value());
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});
	}

	public void setBindContext(BindContext ctx) {
		_bindContext = ctx;
		_types.add(new Type(ctx.getClass(), _bindContext));
	}

	public BindContext getBindContext() {
		return _bindContext;
	}

	public void setBinder(Binder binder) {
		_binder = binder;
		_types.add(new Type(binder.getClass(), _binder));
		_root = binder.getView();
	}

	public Binder getBinder() {
		return _binder;
	}

	public void setBindingArgs(final Map<String, Object> bindingArgs) {
		this._bindingArgs = bindingArgs;
		_paramResolvers.put(BindingParam.class, new ParamResolver<Annotation>() {
			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Object val = bindingArgs.get(getAnnotatedParameterName(BindingParam.class, ((BindingParam) anno).value(), parameterName));
				return resolveParameter0(val, returnType);
			}
		});
		_paramResolvers.put(BindingParams.class, new ParamResolver<Annotation>() {
			@Override
			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				try {
					Object bean = Classes.newInstance(returnType, null);
					BeanInfo beanInfo = Introspector.getBeanInfo(returnType);
					for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
						String propertyName = pd.getName();
						Object propertyValue = bindingArgs.get(propertyName);
						if (propertyValue != null || bindingArgs.containsKey(propertyName)) {
							pd.getWriteMethod().invoke(
								bean, Classes.coerce(pd.getPropertyType(), propertyValue)
							);
						}
					}
					return bean;
				} catch (Exception e) {
					_log.error("", e);
					throw UiException.Aide.wrap(e);
				}
			}
		});
	}

	private String getAnnotatedParameterName(Class<? extends Annotation> annoClass,
	                                         String annoValue,
	                                         Supplier<String> parameterName) {
		if (!Strings.isEmpty(annoValue))
			return annoValue;

		String value = parameterName.get();
		if (!Strings.isEmpty(value))
			return value;

		throw new UiException("The parameter name can't be inferred. Please specify @" + annoClass.getSimpleName() + "(value) instead.");
	}

	public void call(Object base, Method method) {
		Method originalMethod = ViewModelAnnotationResolvers.getOriginalMethod(base, method);
		Class<?>[] paramTypes = originalMethod.getParameterTypes();
		java.lang.annotation.Annotation[][] parmAnnos = originalMethod.getParameterAnnotations();
		Object[] params = new Object[paramTypes.length];

		try {
			for (int i = 0; i < paramTypes.length; i++) {
				params[i] = resolveParameter(parmAnnos[i], paramTypes[i], originalMethod, i);
			}

			method.setAccessible(true); // Bug ZK-2428
			method.invoke(base, params);
		} catch (InvocationTargetException invokEx) {
			//Ian YT Tsai (2012.06.20), while InvocationTargetException,
			//using original exception is much meaningful.
			Throwable c = invokEx.getCause();
			if (c == null)
				c = invokEx;
			if (c instanceof OperationException) { //ZK-4255: Expectable exceptions
				_log.debug("", c);
			} else {
				_log.error("", c);
			}
			throw UiException.Aide.wrap(c);
		} catch (Exception e) {
			_log.error("", e);
			throw UiException.Aide.wrap(e);
		}
	}

	private Object resolveParameter(Annotation[] parmAnnos, Class<?> paramType, Method method, int index) {
		Object val = null;
		boolean hitResolver = false;
		Default defAnno = null;
		for (Annotation anno : parmAnnos) {
			Class<?> annotype = anno.annotationType();

			if (defAnno == null && annotype.equals(Default.class)) {
				defAnno = (Default) anno;
				continue;
			}
			ParamResolver<Annotation> resolver = _paramResolvers.get(annotype);
			if (resolver == null)
				continue;
			hitResolver = true;
			val = resolver.resolveParameter(anno, paramType, () -> {
				String[] parameterNames = _PARANAMER.lookupParameterNames(method, false);
				return index < parameterNames.length ? parameterNames[index] : "";
			});
			if (val != null) {
				break;
			}
			//don't break until get a value
		}
		if (val == null && defAnno != null) {
			val = Classes.coerce(paramType, defAnno.value());
		}

		//to compatible to rc2, do we have to?
		if (_mappingType && val == null && !hitResolver && _types != null) {
			for (Type type : _types) {
				if (type != null && paramType.isAssignableFrom(type.clz)) {
					val = type.value;
					break;
				}
			}
		}
		if (val == null)
			val = resolvePositionalParameter(paramType, index);
		return val;
	}

	private Map<String, Object> _bindingArgs;

	private Object resolvePositionalParameter(Class<?> returnType, int index) {
		Object val = null;
		if (_bindingArgs != null) {
			int argIndex = 0;
			for (Map.Entry<String, Object> entry : _bindingArgs.entrySet()) {
				// skip using positional if the param key is not auto-generated
				if (argIndex == index) {
					if (entry.getKey().startsWith(Parser.SIMPLIFIED_COMMAND_PARAM_PREFIX))
						val = entry.getValue();
					break;
				}
				argIndex++;
			}
			return resolveParameter0(val, returnType);
		}
		return null;
	}

	private Object resolveParameter0(Object val, Class<?> returnType) {
		if (val != null && returnType.isAssignableFrom(val.getClass())) { //escape
			return val;
		} else if (Component.class.isAssignableFrom(returnType) && val instanceof String) {
			return _root.getDesktop().getComponentByUuidIfAny((String) val);
		} else if (val instanceof JSONAware) {
			BindContext bindContext = getBindContext();
			Binder binder = getBinder();
			@SuppressWarnings("rawtypes")
			Converter converter = binder.getConverter("jsonBindingParam");
			if (converter != null) {
				try {
					bindContext.setAttribute(BINDING_PARAM_CALL_TYPE, returnType);
					@SuppressWarnings("unchecked")
					Object result = converter.coerceToBean(val, binder.getView(), bindContext);
					return result;
				} catch (Exception ex) {
					return Classes.coerce(returnType, val);
				} finally {
					bindContext.setAttribute(BINDING_PARAM_CALL_TYPE, null);
				}
			} else
				return Classes.coerce(returnType, val);
		} else {
			return val == null ? null : Classes.coerce(returnType, val);
		}
	}

	//utility to hold implicit class and runtime value
	private static class Type {
		final Class<?> clz;
		final Object value;

		public Type(Class<?> clz, Object value) {
			this.clz = clz;
			this.value = value;
		}
	}

	public interface ParamResolver<T> {
		/**
		 * @deprecated since 9.5.0
		 * @see #resolveParameter(Object, Class, Supplier)
		 */
		@Deprecated
		default Object resolveParameter(T anno, Class<?> returnType) {
			return null;
		}
		/**
		 * @since 9.5.0
		 */
		default Object resolveParameter(T anno, Class<?> returnType, Supplier<String> parameterName) {
			return resolveParameter(anno, returnType);
		}
	}

	public void setComponent(Component comp) {
		_component = comp;
		//scope param
		_paramResolvers.put(ScopeParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				final String name = getAnnotatedParameterName(ScopeParam.class, ((ScopeParam) anno).value(), parameterName);
				final Scope[] ss = ((ScopeParam) anno).scopes();

				Object val = null;

				for (Scope s : ss) {
					switch (s) {
					case AUTO:
						if (ss.length == 1) {
							if (_execution != null) val = _execution.getAttribute(name);
							if (val == null) val = _component.getAttribute(name, true);
						} else {
							throw new UiException("don't use " + s + " with other scopes " + Arrays.toString(ss));
						}
					}
				}
				if (val == null) {
					for (Scope scope : ss) {
						final String scopeName = scope.getName();
						Object scopeObj = Components.getImplicit(_component, scopeName);
						if (scopeObj instanceof Map) {
							val = ((Map<?, ?>) scopeObj).get(name);
							if (val != null)
								break;
						} else if (scopeObj != null) {
							_log.error("the scope of " + scopeName + " is not a Map, is " + scopeObj);
						}
					}
				}

				//zk-1469, 
				if (val instanceof ReferenceBinding) {
					val = resolveReferenceBinding(name, (ReferenceBinding) val, returnType);
				}
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});

		//component
		_paramResolvers.put(SelectorParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				final String selector = getAnnotatedParameterName(SelectorParam.class, ((SelectorParam) anno).value(), parameterName);
				final List<Component> result = Selectors.find(_root, selector);
				Object val;
				if (!Collection.class.isAssignableFrom(returnType)) {
					val = result.size() > 0 ? Classes.coerce(returnType, result.get(0)) : null;
				} else {
					val = Classes.coerce(returnType, result);
				}
				return val;
			}
		});
	}

	private Object resolveReferenceBinding(String name, ReferenceBinding rbinding, Class<?> returnType) {
		BindEvaluatorX evalx = rbinding.getBinder().getEvaluatorX();
		//resolve by name or by rbinding.propertyString directly?
		Object val = BindEvaluatorXUtil.eval(evalx, rbinding.getComponent(), name, returnType, null);
		//following is quick but not safe because of the null arg
		//		val = ((ReferenceBinding)val).getValue(null);

		return val;
	}

	public void setExecution(Execution exec) {
		_execution = exec;
		//http param
		_paramResolvers.put(QueryParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Object val = _execution.getParameter(getAnnotatedParameterName(QueryParam.class, ((QueryParam) anno).value(), parameterName));
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});
		_paramResolvers.put(HeaderParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Object val = _execution.getHeader(getAnnotatedParameterName(HeaderParam.class, ((HeaderParam) anno).value(), parameterName));
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});
		_paramResolvers.put(CookieParam.class, new ParamResolver<Annotation>() {

			@SuppressWarnings("unchecked")
			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Map<String, Object> m = (Map<String, Object>) _execution.getAttribute(COOKIE_CACHE);
				if (m == null) {
					final Object req = _execution.getNativeRequest();
					m = new HashMap<String, Object>();
					_execution.setAttribute(COOKIE_CACHE, m);

					if (req instanceof HttpServletRequest) {
						final Cookie[] cks = ((HttpServletRequest) req).getCookies();
						if (cks != null) {
							for (Cookie ck : cks) {
								m.put(ck.getName().toLowerCase(java.util.Locale.ENGLISH), ck.getValue());
							}
						}
					} else /* if(req instanceof PortletRequest)*/ {
						//no cookie in protlet 1.0
					}
				}
				Object val = m == null ? null
						: m.get(getAnnotatedParameterName(CookieParam.class, ((CookieParam) anno).value(), parameterName).toLowerCase(java.util.Locale.ENGLISH));
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});

		//execution
		_paramResolvers.put(ExecutionParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Object val = _execution.getAttribute(getAnnotatedParameterName(ExecutionParam.class, ((ExecutionParam) anno).value(), parameterName));
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});

		_paramResolvers.put(ExecutionArgParam.class, new ParamResolver<Annotation>() {

			public Object resolveParameter(Annotation anno, Class<?> returnType, Supplier<String> parameterName) {
				Object val = _execution.getArg().get(getAnnotatedParameterName(ExecutionArgParam.class, ((ExecutionArgParam) anno).value(), parameterName));
				return val == null ? null : Classes.coerce(returnType, val);
			}
		});
	}

	class ContextObjects {
		public Object get(ContextType type) {
			switch (type) {
			//bind contexts
			case BIND_CONTEXT:
				return _bindContext;
			case BINDER:
				return _binder;
			case COMMAND_NAME:
				return _bindContext == null ? null : _bindContext.getCommandName();
			case TRIGGER_EVENT:
				return _bindContext == null ? null : _bindContext.getTriggerEvent();
			//zk execution contexts
			case EXECUTION:
				return _execution;
			case COMPONENT:
				return _component;
			case SPACE_OWNER:
				return _component == null ? null : _component.getSpaceOwner();
			case VIEW:
				return _binder == null ? null : _binder.getView();
			case PAGE:
				return _component == null ? null : _component.getPage();
			case DESKTOP:
				return _component == null ? null : _component.getDesktop();
			case SESSION:
				return _component == null ? null : Components.getImplicit(_component, "session");
			case APPLICATION:
				return _component == null ? null : Components.getImplicit(_component, "application");
			}
			return null;
		}
	}
}
