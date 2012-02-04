/* BindELResolver.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 4:31:51 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
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
		_resolver.add(super.getELResolver());
	}
	protected ELResolver getELResolver() {
		return _resolver;
	}
	//ELResolver//
	public Object getValue(ELContext ctx, Object base, Object property)
	throws PropertyNotFoundException, ELException {
		Object value = super.getValue(ctx, base, property);
		if (value instanceof ReferenceBinding) {
			value = ((ReferenceBinding)value).getValue((BindELContext)((EvaluationContext)ctx).getELContext());
		}
		tieValue(ctx, base, property, value);
		return value;
	}
	
	public void setValue(ELContext ctx, Object base, Object property, Object value)
	throws PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (base instanceof ReferenceBinding) {
			base = ((ReferenceBinding)base).getValue((BindELContext)((EvaluationContext)ctx).getELContext());
		}
		super.setValue(ctx, base, property, value);
		tieValue(ctx, base, property, value);
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> getPathList(BindELContext ctx){
		return (List<String>)ctx.getContext(Path.class);//get path, see #PathResolver
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
	private void tieValue(ELContext elCtx, Object base, Object propName, Object value) {
		final BindELContext ctx = (BindELContext)((EvaluationContext)elCtx).getELContext();
		if(ctx.ignoreTracker()) return; 
		final Binding binding = ctx.getBinding();
		//only there is a binding that needs tie tracking to value
		if (binding != null) {
        	final int nums = ((Integer) ctx.getContext(Integer.class)).intValue(); //get numOfKids, see #PathResolver
        	final List<String> path = getPathList(ctx);
        	
        	String script = null;
			if (base instanceof Form) {
				if (nums > 0) { //still in resolving the form field
					return;
				} else { //done resolving the form field
					script = FormELResolver.fieldName(path);
				}
			} else {
				script = propertyName(path.listIterator(path.size()).previous());
			}
			final Binder binder = binding.getBinder();
			final BindContext bctx = (BindContext) ctx.getAttribute(BinderImpl.BINDCTX);
			final Component ctxcomp = bctx != null ? bctx.getComponent() : binding.getComponent();
			((BinderCtrl)binder).getTracker().tieValue(ctxcomp, base, script, propName, value);
			
			if (base != null) {
				if (nums == 0 && binding instanceof SaveBinding) { //a done save operation, form or not form
					saveEqualBeans(elCtx, base, (String) propName, value);
				}
				if (!(base instanceof Form)) { //no @DependsOn and @NotifyChange in Form
					final Method m = (Method) ctx.getContext(Method.class);
					//parse @DependsOn and add into dependency tracking
					final boolean prompt = bctx != null && bctx.getCommandName() == null; 
					if (prompt && binding instanceof LoadBinding && m != null) {
						//FormBinding shall not check @DependsOn() for dependent nodes
						if (!(binding instanceof LoadFormBindingImpl) || ((LoadFormBindingImpl)binding).getSeriesLength() <= path.size()) {
							BindELContext.addDependsOnTrackings(m, basePath(path), path, binding, bctx);
						}
					}
					
					//parse @NotifyChange and collect Property to publish PropertyChangeEvent
					if (nums == 0 && binding instanceof SaveBinding) { //a done save operation
						//collect Property for @NotifyChange, kept in BindContext
						//see BinderImpl$CommandEventListener#onEvent()
						BindELContext.addNotifys(m, base, (String) propName, value, bctx);
					}
				}
			}
		}
	}

	//get the path before the last dot, to be a basepath 
	//ex, base path of 'vm.person.address.fullstr' will become 'vm.person.address'
	//so, a depends-on(city) on fullstr in address will add a depends to 'vm.person.address'.'city'
	private String basePath(List<String> path) {
    	final StringBuffer sb = new StringBuffer();
		for(String prop : path.subList(0, path.size()-1)) { //remove the last one
			sb.append(prop);
		}
    	return sb.charAt(0) == '.' ? sb.substring(1) : sb.toString();
	}
	
	private String propertyName(String script) {
    	return script.charAt(0) == '.' ? script.substring(1) : script;
	}
	
}
