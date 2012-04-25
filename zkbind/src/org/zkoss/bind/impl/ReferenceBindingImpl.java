/* ReferenceImpl.java

	Purpose:
		
	Description:
		
	History:
		Jan 12, 2012 9:11:03 AM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link ReferenceBinding}.
 * @author henrichen
 * @since 6.0.0
 */
public class ReferenceBindingImpl extends BindingImpl implements ReferenceBinding {
	private static final long serialVersionUID = 20120204122151L;
	private final static Object NULL_VALUE = new Object();
	private final ExpressionX _exprX;
	private transient Object _cacheValue; //null means invalid

	public ReferenceBindingImpl(Binder binder, String expression, Component comp) {
		super(binder, comp, null);
		final BindContext ctx = newBindContext();
		_exprX = binder.getEvaluatorX().parseExpressionX(ctx, expression, Object.class);
	}

	@Override
	public Object getValue(BindELContext ctx) {
		load(null);
		return _cacheValue == NULL_VALUE ? null : _cacheValue;
	}
	
	private BindContext newBindContext() {
		return BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
	}

	@Override
	public void load(BindContext ctx) {
		if (_cacheValue == null) {
			final BindContext bctx = newBindContext();
			final Object val = getBinder().getEvaluatorX().getValue(bctx, getComponent(), _exprX);
			_cacheValue = val == null ? NULL_VALUE : val;
		}
	}

	@Override
	public String getPropertyString() {
		return getPureExpressionString(_exprX);
	}
	
	/*package*/ ExpressionX getProperty(){
		return _exprX;
	}

	@Override
	public void invalidateCache() {
		_cacheValue = null;
	}

	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
				.append(",component:").append(getComponent()).toString();
	}
}
