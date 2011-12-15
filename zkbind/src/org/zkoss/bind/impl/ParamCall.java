package org.zkoss.bind.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Default;
import org.zkoss.bind.annotation.Param;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
/**
 * to help invoke a method with {@link Param} etc.. features
 * @author dennis
 *
 */
public class ParamCall {

	private Map<Class<? extends Annotation>, ParamResolver<Annotation>> _paramResolvers;
	private List<Type> _types;//to map class type directly, regardless the annotation
	private boolean _mappingType;//to enable the map class type
	private Map<ContextType,Object> _contextObjects = new HashMap<ContextType,Object>();
	
	public ParamCall(){
		this(true);
	}
	public ParamCall(boolean mappingType){
		_paramResolvers = new HashMap<Class<? extends Annotation>, ParamResolver<Annotation>>();
		_types = new ArrayList<Type>();
		_mappingType = mappingType;
		_paramResolvers.put(ContextParam.class, new ParamResolver<Annotation>() {
			@Override
			public Object resolveParameter(Annotation anno) {
				return _contextObjects.get(((ContextParam) anno).value());
			}
		});
	}
	
	public void setBindContext(BindContext ctx){
		_types.add(new Type(ctx.getClass(),ctx));
		_contextObjects.put(ContextType.BIND_CONTEXT, ctx);
	}
	public void setBinder(Binder binder){
		_types.add(new Type(binder.getClass(),binder));
		_contextObjects.put(ContextType.BINDER, binder);
	}
	
	public void setBindingArgs(final Map<String, Object> bindingArgs){
		_paramResolvers.put(Param.class, new ParamResolver<Annotation>() {
			@Override
			public Object resolveParameter(Annotation anno) {
				return bindingArgs.get(((Param) anno).value());
			}
		});
	}
	
	
	public void call(Object base, Method method) {
		Class<?>[] paramTypes = method.getParameterTypes();
		java.lang.annotation.Annotation[][] parmAnnos = method.getParameterAnnotations();
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			params[i] = resolveParameter(parmAnnos[i],paramTypes[i]);
		}

		try {
			method.invoke(base, params);
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}
	
	private Object resolveParameter(java.lang.annotation.Annotation[] parmAnnos, Class<?> paramType){
		Object val = null;
		boolean hitResolver = false;
		Default defAnno = null;
		for(Annotation anno:parmAnnos){
			Class<?> annotype = anno.annotationType();
			
			if(defAnno==null && annotype.equals(Default.class)){
				defAnno = (Default)anno;
				continue;
			}
			ParamResolver<Annotation> resolver = _paramResolvers.get(annotype);
			if(resolver==null) continue;
			hitResolver = true;
			val = resolver.resolveParameter(anno);
			if(val!=null) {
				val = Classes.coerce(paramType, val);
				break;
			}
			//don't break until get a value
		}
		if(val == null && defAnno != null){
			val = Classes.coerce(paramType, defAnno.value());
		}
		
		//to compatible to rc2, do we have to?
		if(_mappingType && val==null && !hitResolver && _types!=null){
			for (Type type : _types) {
				if (type != null && paramType.isAssignableFrom(type.clz)) {
					val = type.value;
					break;
				}
			}
		}
		return val;
	}
	
	//utility to hold implicit class and runtime value
	private static class Type{
		final Class<?> clz;
		final Object value;
		public Type(Class<?> clz,Object value){
			this.clz = clz;
			this.value = value;
		}
	}
	
	private interface ParamResolver<T> {
		public Object resolveParameter(T anno);
	}

	public void setComponent(final Component comp) {
		// TODO implementation
	}
	public void setExecution(final Execution exec) {
		// TODO implementation
		
	}
}
