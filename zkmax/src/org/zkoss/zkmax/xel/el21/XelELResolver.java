/* XelELResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 19:39:41     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el21;

import java.util.Iterator;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ResourceBundleELResolver;

import org.zkoss.xel.VariableResolver;

/**
 * A simple implementation of {@link ELResolver}.
 *
 * @since 3.0.0
 */
public final class XelELResolver extends ELResolver {
	private static final ELResolver DEFAULT = new CompositeELResolver();

	static {
		((CompositeELResolver)DEFAULT).add(new MapELResolver());
		((CompositeELResolver)DEFAULT).add(new ResourceBundleELResolver());
		((CompositeELResolver)DEFAULT).add(new ListELResolver());
		((CompositeELResolver)DEFAULT).add(new ArrayELResolver());
		((CompositeELResolver)DEFAULT).add(new BeanELResolver());
	}

	private final VariableResolver _resolver;

	/** Constructor.
	 */
	public XelELResolver(VariableResolver resolver) {
		_resolver = resolver;
	}

	//ELResolver//
	public Object getValue(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();

		if (base == null) {
			ctx.setPropertyResolved(true);
			if (property != null)
				return _resolver.resolveVariable(property.toString());
		}

		return ctx.isPropertyResolved() ? null: DEFAULT.getValue(ctx, base, property);
	}
	public Class getType(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();

		if (base == null) {
			ctx.setPropertyResolved(true);
			if (property != null) {
				Object obj = _resolver.resolveVariable(property.toString());
				return obj != null ? obj.getClass() : null;
			}
		}

		return ctx.isPropertyResolved() ? null: DEFAULT.getType(ctx, base, property);
	}

	public void setValue(ELContext ctx, Object base, Object property,
	Object value)
	throws PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();

		if (base == null) {
			ctx.setPropertyResolved(true);
			throw new PropertyNotWritableException();
		}

		if (!ctx.isPropertyResolved())
			DEFAULT.setValue(ctx, base, property, value);
	}

	public boolean isReadOnly(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		if (ctx == null)
			throw new IllegalArgumentException();

		if (base == null) {
			ctx.setPropertyResolved(true);
			return true;
		}

		return DEFAULT.isReadOnly(ctx, base, property);
	}

	public Iterator getFeatureDescriptors(ELContext ctx, Object base) {
		return DEFAULT.getFeatureDescriptors(ctx, base);
	}

	public Class getCommonPropertyType(ELContext ctx, Object base) {
		return base == null ?
			String.class: DEFAULT.getCommonPropertyType(ctx, base);
	}
}
