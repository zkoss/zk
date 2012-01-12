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
import org.zkoss.bind.sys.LoadDummyBinding;
import org.zkoss.bind.sys.Reference;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link Reference}.
 * @author henrichen
 * @since 6.0.0
 */
public class ReferenceImpl implements Reference {
	final Binder _binder;
	final Component _comp;
	final ExpressionX _exprX;

	public ReferenceImpl(Binder binder, String expression, Component comp) {
		_binder = binder;
		_comp = comp;
		final BindContext ctx = newBindContext();
		_exprX = binder.getEvaluatorX().parseExpressionX(ctx, expression, Object.class);
	}

	@Override
	public Object getValue(BindELContext ctx) {
		//TODO if binder in ctx and cached _binder is different?
		final BindContext bctx = newBindContext();
		return _binder.getEvaluatorX().getValue(bctx, _comp, _exprX);
	}
	
	private BindContext newBindContext() {
		return BindContextUtil.newBindContext(_binder, new LoadDummyBinding() {
			@Override
			public Binder getBinder() {
				return _binder;
			}
			@Override
			public Component getComponent() {
				return _comp;
			}
		}, false, null, _comp, null);
	}
}
