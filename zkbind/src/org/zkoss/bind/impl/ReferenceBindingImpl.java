/* ReferenceImpl.java

	Purpose:
		
	Description:
		
	History:
		Jan 12, 2012 9:11:03 AM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Property;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.LoadInfo;
import org.zkoss.bind.sys.debugger.impl.SaveInfo;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
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
	private final String _attr;
	
	public ReferenceBindingImpl(Binder binder, Component comp, String attr,String expression) {
		super(binder, comp, null);
		final BindContext ctx = newBindContext();
		_exprX = binder.getEvaluatorX().parseExpressionX(ctx, expression, Object.class);
		_attr = attr;
	}

	@Override
	public Object getValue(BindELContext ctx) {
		load(null);
		return _cacheValue == NULL_VALUE ? null : _cacheValue;
	}
	
	@Override
	public void setValue(BindELContext ctx, Object val) {
		invalidateCache();
		final BindContext bctx = newBindContext();
		getBinder().getEvaluatorX().setValue(bctx, getComponent(), _exprX, val);
		
		
		final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		if(collector!=null){
			collector.addInfo(new SaveInfo(getComponent(), "save-reference", "", "self."+_attr, getPropertyString(), val, null, ""));
		}
		
		//copy notifies back
		final Set<Property> notifies = BindELContext.getNotifys(bctx);
		if(notifies!=null){
			BindELContext.addNotifys(notifies, ctx.getBindContext());
		}
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
			
			final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
			if(collector!=null){
				collector.addInfo(new LoadInfo(getComponent(), "load-reference", "", getPropertyString(), "self."+_attr, _cacheValue, null, ""));
			}
		}
	}

	@Override
	public String getPropertyString() {
		return BindEvaluatorXUtil.getExpressionString(_exprX);
	}

	@Override
	public void invalidateCache() {
		_cacheValue = null;
	}

	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
				.append(",component:").append(getComponent()).toString();
	}

	
	/*package*/ ValueReference getValueReference() {
		final BindContext bctx = newBindContext();
		return getBinder().getEvaluatorX().getValueReference(bctx, getComponent(), _exprX);
	}
}
