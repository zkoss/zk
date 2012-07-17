/* DelegatingVariableResolver.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 10:56:14 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.cdi;

import java.util.Set;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;

/**
 * Generic CDI EL named managed bean resolver.
 * <p>Applicable to CDI version 1.0 or later</p>
 * @author henrichen
 *
 */
public class DelegatingVariableResolver implements VariableResolverX {

	private VariableResolverX fVariableResolverX;
	public DelegatingVariableResolver() {
		fVariableResolverX = new DelegatingVariableResolverEL();
		// DelegatingVariableResolverManager
		// DelegatingVariableResolverEL
	}
	
	public Object resolveVariable(String name) throws XelException {
		return resolveVariable(null, null, name);
	}

	public Object resolveVariable(XelContext ctx, Object base, Object name)throws XelException {
		return fVariableResolverX.resolveVariable(ctx, base, name);
	}

	public int hashCode() {
		return fVariableResolverX.hashCode();
	}
	
	public boolean equals(Object o) {
		return fVariableResolverX.equals(o);
	}
}


/**
 * 
 * @author Ian Y.T Tsai(zanyking)
 *
 */
class DelegatingVariableResolverManager implements VariableResolverX {
	
	private static final String CREATIONAL_CONTEXT = DelegatingVariableResolver.class.getName();
	private BeanManager _beanMgr;
	public DelegatingVariableResolverManager() {
		_beanMgr = CDIUtil.getBeanManager();
		System.out.println(">>>>>>> DelegatingVariableResolverManager()");
	}
	public Object resolveVariable(String name) throws XelException {
		return resolveVariable(null, null, name);
	}

	@SuppressWarnings("rawtypes")
	public Object resolveVariable(XelContext ctx, Object base, Object name)
	throws XelException {
		if(base!=null || !(name instanceof String)) return null;
		
		final Set<Bean<?>> beans = _beanMgr.getBeans((String)name);
		if(beans == null || beans.size()==0) return null;

		
		//I(Dennis) think we don't need to check(if there are more than one bean has same name), CDI should check this when startup
		//However, Since it returns a Set, so I just check it.
		//Note, I do some test, the alternative bean will not be returned by the getBeans api with the name.
		Bean bean = null;
		for(Bean b:beans){
			if(b.isAlternative()) continue;//(Ian Tsai) alternative is the bean with explicit declaration in bean.xml. they are reserved for Bean 
			if(bean != null){
				throw new XelException("more than one non-alternative bean have same name "+bean+" and "+b+", name "+name);
			}
			bean = b;
		}
		if(bean==null) return null;

		
		CreationalContext context = _beanMgr.createCreationalContext(null);
		
		/*
		 * Ian Tsai & Dennis
		 * 
		 * We are not sure what is the best way to handle the life cycle of CreationalContext.
		 * CreationalContext is the context designed to serve  
		 * if we put it in Desktop, then if the bean is 
		 */
//		CreationalContext context = ctx==null?
//			null:(CreationalContext)ctx.getAttribute(CREATIONAL_CONTEXT);
//		if(context==null){
//			System.out.println(">>>>>create a new CreationalContext");
//			context = _beanMgr.createCreationalContext(null);
//			if(ctx!=null){
//				ctx.setAttribute(CREATIONAL_CONTEXT,context);
//			}else{
//				
//			}
//		}
		System.out.println(">>>>>CreationalContext: "+context);		
		Object value = _beanMgr.getReference(bean, bean.getBeanClass(), context);
		context.release();

		return value;
	}

	public int hashCode() {
		return Objects.hashCode(_beanMgr);
	}
	public boolean equals(Object o) {
		return this == o || (o instanceof DelegatingVariableResolverManager
			&& Objects.equals(_beanMgr, ((DelegatingVariableResolverManager)o)._beanMgr));
	}
}
/**
 * 
 * @author Ian Y.T Tsai(zanyking)
 *
 */
class DelegatingVariableResolverEL implements VariableResolverX {
	private boolean _resolving; //prevent recursive
	private ELResolver _cdiResolver;
	public DelegatingVariableResolverEL() {
		_cdiResolver = CDIUtil.getBeanManager().getELResolver();
	}
	public Object resolveVariable(String name) throws XelException {
		return resolveVariable(null, null, name);
	}

	public Object resolveVariable(XelContext ctx, Object base, Object name)
	throws XelException {
		if (!_resolving) { //recursive back, return null.
			final boolean old = _resolving;
			_resolving = true;
			final CDIELContext elctx = new CDIELContext(ctx, _cdiResolver);
			try {
				return _cdiResolver.getValue(elctx, base, name); //might cause recursive
			} finally {
				_resolving = old;
			}
		}
		return null;
	}

	public int hashCode() {
		return Objects.hashCode(_cdiResolver);
	}
	public boolean equals(Object o) {
		return this == o || (o instanceof DelegatingVariableResolverEL
			&& Objects.equals(_cdiResolver, ((DelegatingVariableResolverEL)o)._cdiResolver));
	}
}