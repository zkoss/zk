/* XelELResolver.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 14:53:14     2011, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.xel.zel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.zel.ArrayELResolver;
import org.zkoss.zel.BeanELResolver;
import org.zkoss.zel.BeanNameELResolver;
import org.zkoss.zel.BeanNameResolver;
import org.zkoss.zel.CompositeELResolver;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.ListELResolver;
import org.zkoss.zel.MapELResolver;
import org.zkoss.zel.MethodNotFoundException;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zel.ResourceBundleELResolver;
import org.zkoss.zel.StaticFieldELResolver;
import org.zkoss.zel.impl.stream.StreamELResolverImpl;

/**
 * An XEL implementation of ZEL ELResolver.
 *
 * @since 6.0.0
 */
public class XelELResolver extends ELResolver {
	private static final CompositeELResolver DEFAULT;
	private final static Map<String, Object> localBeans = new HashMap<String, Object>();
	
	static {
		DEFAULT = new CompositeELResolver();
		//add resolver in order to support EL 3.0
		DEFAULT.add(new BeanNameELResolver(
                new StandardBeanNameResolver(localBeans))); // for semicolon expression
		DEFAULT.add(new StreamELResolverImpl()); // for stream operations
		DEFAULT.add(new StaticFieldELResolver()); //for calling static method
		DEFAULT.add(new MapELResolver());
		DEFAULT.add(new ResourceBundleELResolver());
		DEFAULT.add(new ListELResolver());
		DEFAULT.add(new ArrayELResolver());
		DEFAULT.add(new BeanELResolver());
	}
	protected final XelContext _ctx;

	/** Constructor.
	 */
	public XelELResolver(XelContext ctx) {
		_ctx = ctx;
	}
	protected ELResolver getELResolver() {
		return DEFAULT;
	}
	protected XelContext getXelContext() {
		return _ctx;
	}

	//ELResolver//
	public Object getValue(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();

		final Object o = getELResolver().getValue(ctx, base, property);
		return ctx.isPropertyResolved() ? o : resolve(ctx, base, property); 
	}
	protected Object resolve(ELContext ctx, Object base, Object property) {
		VariableResolver resolver = _ctx.getVariableResolver();
		if (resolver != null) {
			if (resolver instanceof VariableResolverX) {
				final Object o = ((VariableResolverX)resolver)
					.resolveVariable(_ctx, base, property);
				//in order to call static method, we can't set property as resolved 
				if (o != null) ctx.setPropertyResolved(true);
				return o;
			} else if (base == null && property != null) {
				final Object o = resolver.resolveVariable(property.toString());
				//in order to call static method, we can't set property as resolved
				if (o != null) ctx.setPropertyResolved(true);
				return o;
			}
		}
		return null;
	}
	public Class<?> getType(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();
		final Class<?> type = getELResolver().getType(ctx, base, property);
		if (ctx.isPropertyResolved()) {
			return type;
		}
		final Object o = resolve(ctx, base, property); //might change isPropertyResolved
		return o != null ? o.getClass() : null;
	}

	public void setValue(ELContext ctx, Object base, Object property,
	Object value)
	throws PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (ctx == null) {
			throw new IllegalArgumentException();
		}

		if (base == null) {
			//some situation occurs here, 
			//we should pass them to the following resolver rather than throw exception
		}

		getELResolver().setValue(ctx, base, property, value);
	}

	public boolean isReadOnly(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();

		if (base == null) {
			ctx.setPropertyResolved(true);
			return true;
		}

		return getELResolver().isReadOnly(ctx, base, property);
	}

	public Iterator<java.beans.FeatureDescriptor> getFeatureDescriptors(ELContext ctx, Object base) {
		return getELResolver().getFeatureDescriptors(ctx, base);
	}

	public Class getCommonPropertyType(ELContext ctx, Object base) {
		return base == null ?
			String.class: getELResolver().getCommonPropertyType(ctx, base);
	}
	
	public Object invoke(ELContext ctx, Object base, Object method, Class[] paramTypes, Object[] params)
	throws MethodNotFoundException {
		if (ctx == null)
			throw new IllegalArgumentException();

		if (base == null) {
			ctx.setPropertyResolved(true);
			throw new MethodNotFoundException();
		}
		return getELResolver().invoke(ctx, base, method, paramTypes, params);
	}

	/**
	 * It's a class copied from StandardELContext
	 * @author Chunfu
	 *
	 */
	public static class StandardBeanNameResolver extends BeanNameResolver {

        private final Map<String,Object> beans;

        public StandardBeanNameResolver(Map<String,Object> beans) {
            this.beans = beans;
        }

        
        public boolean isNameResolved(String beanName) {
            return beans.containsKey(beanName);
        }

        
        public Object getBean(String beanName) {
            return beans.get(beanName);
        }

        
        public void setBeanValue(String beanName, Object value)
                throws PropertyNotWritableException {
            beans.put(beanName, value);
        }

        
        public boolean isReadOnly(String beanName) {
            return false;
        }

        
        public boolean canCreateBean(String beanName) {
            return true;
        }
    }
}
