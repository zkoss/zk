/* BindELResolver.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 4:31:51 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.lang.reflect.Method;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormExt;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.LoadFormBindingImpl;
import org.zkoss.bind.impl.Path;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.tracker.impl.TrackerImpl;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.zel.XelELResolver;
import org.zkoss.zel.CompositeELResolver;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zel.impl.lang.EvaluationContext;
import org.zkoss.zk.ui.Component;

/**
 * ELResolver for Binding; handle Form bean.
 * @author henrichen
 * @since 6.0.0
 */
public class BindELResolver extends XelELResolver {
	private final CompositeELResolver _resolver;
	public BindELResolver(XelContext ctx) {
		super(ctx);
		_resolver = new CompositeELResolver();
		_resolver.add(new PathELResolver()); //must be the first
		_resolver.add(new FormELResolver());
		_resolver.add(new ListModelELResolver());
		_resolver.add(new TreeModelELResolver());
		_resolver.add(new ValidationMessagesELResolver());
		_resolver.add(super.getELResolver());
	}
	protected ELResolver getELResolver() {
		return _resolver;
	}
	//ELResolver//
	public Object getValue(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		Object value = super.getValue(ctx, base, property);
		//ZK-950: The expression reference doesn't update while change the instant of the reference
		final ReferenceBinding rbinding = value instanceof ReferenceBinding ? (ReferenceBinding)value : null;
		if (rbinding != null) {
			value = rbinding.getValue((BindELContext) ((EvaluationContext)ctx).getELContext());
		} 
		//If value evaluated to a ReferenceBinding, always tie the ReferenceBinding itself as the 
		//evaluated bean, @see TrackerImpl#getLoadBindings0() and TrackerImpl#getAllTrackerNodesByBeanNodes()
		tieValue(ctx, base, property, rbinding != null ? rbinding : value, false);
		return value;
	}
	
	public void setValue(ELContext ctx, Object base, Object property, Object value)
	throws PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (base instanceof ReferenceBinding) {
			base = ((ReferenceBinding)base).getValue((BindELContext)((EvaluationContext)ctx).getELContext());
		}
		super.setValue(ctx, base, property, value);
		tieValue(ctx, base, property, value, true);
	}
	
	private static Path getPathList(BindELContext ctx){
		return (Path)ctx.getContext(Path.class);//get path, see #PathResolver
	}

	//save value into equal beans
	private void saveEqualBeans(ELContext elCtx, Object base, String prop, Object value) {
		final BindELContext ctx = (BindELContext)((EvaluationContext)elCtx).getELContext();
		final BindContext bctx = (BindContext) ctx.getAttribute(BinderImpl.BINDCTX);

		if (bctx.getAttribute(BinderImpl.SAVE_BASE) != null) { //recursive back, return
			return; 
		}
		ctx.setAttribute(BinderImpl.SAVE_BASE, Boolean.TRUE);
		try {
			final Binder binder = bctx.getBinder();
			final TrackerImpl tracker = (TrackerImpl) ((BinderCtrl)binder).getTracker();
			final Set<Object> beans = tracker.getEqualBeans(base);
			beans.remove(base);
			for (Object candidate : beans) {
				super.setValue(elCtx, candidate, prop, value); //might recursive back
			}
		} finally {
			ctx.setAttribute(BinderImpl.SAVE_BASE, null);
		}
	}

	//update dependency and notify changed
	private void tieValue(ELContext elCtx, Object base, Object propName, Object value, boolean allownotify) {
		final BindELContext ctx = (BindELContext)((EvaluationContext)elCtx).getELContext();
		if(ctx.ignoreTracker()) return; 
		final Binding binding = ctx.getBinding();
		//only there is a binding that needs tie tracking to value
		if (binding != null) {
        	final int nums = ((Integer) ctx.getContext(Integer.class)).intValue(); //get numOfKids, see #PathResolver
        	final Path path = getPathList(ctx);
        	
        	String script = null;
			if (base instanceof Form) {
				if (nums > 0) { //still in resolving the form field
					return;
				} else { //done resolving the form field
					script = path.getTrackFieldName();
				}
			} else {
				script = path.getTrackProperty();
			}
			final Binder binder = binding.getBinder();
			final BindContext bctx = (BindContext) ctx.getAttribute(BinderImpl.BINDCTX);
			final Component ctxcomp = bctx != null ? bctx.getComponent() : binding.getComponent();
			((BinderCtrl)binder).getTracker().tieValue(ctxcomp, base, script, propName, value);
			
			if (base != null) {
				if (binding instanceof SaveBinding) {
					if (nums == 0) { //a done save operation, form or not form
						//handle equal beans
						saveEqualBeans(elCtx, base, (String) propName, value);
						
						//ZK-913 Value is reload after validation fail, 
						//only when notify is allowed.
						//parse @NotifyChange and collect Property to publish PropertyChangeEvent
						if (allownotify) { 
							//ZK-905 Save into a Form should fire NotifyChange
							if (base instanceof Form) {
								//collect notify property, kept in BindContext
								BindELContext.addNotifys(base, (String) propName, value, bctx);
								if (base instanceof FormExt)
									BindELContext.addNotifys(((FormExt)base).getStatus(), "*", null, bctx);
							} else {
								final Method m = (Method) ctx.getContext(Method.class);
								//collect Property for @NotifyChange, kept in BindContext
								//see BinderImpl$CommandEventListener#onEvent()
								BindELContext.addNotifys(m, base, (String) propName, value, bctx);
							}
						}
					}
				} else if (!(base instanceof Form) && binding instanceof LoadBinding) { //no @DependsOn in Form bean
					//parse @DependsOn and add into dependency tracking
					final Method m = (Method) ctx.getContext(Method.class);
					if (m != null) {
						final boolean prompt = bctx != null && bctx.getCommandName() == null; 
						if (prompt) {
							//FormBinding shall not check @DependsOn() for dependent nodes
							if (!(binding instanceof LoadFormBindingImpl) || ((LoadFormBindingImpl)binding).getSeriesLength() <= path.size()) {
								BindELContext.addDependsOnTrackings(m, path.getTrackBasePath(), path.getTrackFieldsList(), binding, bctx);
							}
						}
					}
				}
			}
		}
	}
}
