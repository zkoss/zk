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

import javax.el.ELContext;
import javax.el.ELResolver;

import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;

/**
 * Generic CDI EL named managed bean resolver. 
 * @author henrichen
 *
 */
public class DelegatingVariableResolver implements VariableResolverX {
	private transient boolean _resolving; //prevent recursive
	private ELResolver _cdiResolver;
	public DelegatingVariableResolver() {
		_cdiResolver = CDIUtil.getBeanManager().getELResolver();
	}
	public Object resolveVariable(String name) throws XelException {
		throw new XelException("Need ZK 5.0+ ...");
	}

	public Object resolveVariable(XelContext ctx, Object base, Object name)
	throws XelException {
		if (!_resolving) { //recursive back, return null.
			final boolean old = _resolving;
			_resolving = true;
			try {
				final ELContext elctx = new CDIELContext(ctx, _cdiResolver);
				return _cdiResolver.getValue(elctx, base, name); //might cause recursive
			} finally {
				_resolving = old;
			}
		}
		return null;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_cdiResolver == null) ? 0 : _cdiResolver.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelegatingVariableResolver other = (DelegatingVariableResolver) obj;
		if (_cdiResolver == null) {
			if (other._cdiResolver != null)
				return false;
		} else if (!_cdiResolver.equals(other._cdiResolver))
			return false;
		return true;
	}

}
