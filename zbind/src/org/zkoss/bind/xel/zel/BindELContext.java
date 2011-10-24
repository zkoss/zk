/* BindELContext.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 4:52:27 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.DependsOn;
import org.zkoss.bind.NotifyChange;
import org.zkoss.bind.Property;
import org.zkoss.bind.impl.BindContextUtil;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.LoadFormBindingImpl;
import org.zkoss.bind.impl.LoadPropertyBindingImpl;
import org.zkoss.bind.impl.PropertyImpl;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.Binding;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.zel.XelELContext;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.VariableMapper;
import org.zkoss.zel.impl.parser.AstBracketSuffix;
import org.zkoss.zel.impl.parser.AstDotSuffix;
import org.zkoss.zel.impl.parser.AstValue;
import org.zkoss.zel.impl.parser.Node;
import org.zkoss.zk.ui.Component;

/**
 * ELContext for Binding.
 * @author henrichen
 *
 */
public class BindELContext extends XelELContext {
	public BindELContext(XelContext xelc) {
		super(xelc);
	}
	
	protected ELResolver newELResolver(XelContext xelc) {
		return new BindELResolver(xelc);
	}

	//can be used to pass info to ELResolver
	public VariableMapper getVariableMapper() {
		return super.getVariableMapper();
	}

	public Binding getBinding() {
		return (Binding) getXelContext().getAttribute(BinderImpl.BINDING); //see BindEvaluatorXImpl#newXelContext()
	}
	
	public boolean ignoreTracker(){
		return getBinding()==null ||  Boolean.TRUE.equals(getXelContext().getAttribute(BinderImpl.IGNORE_TRACKER)); //see BindEvaluatorXImpl#newXelContext()
	}
	
	public Object getAttribute(String name) {
		return getXelContext().getAttribute(name); //see BindEvaluatorXImpl#newXelContext()
	}
	
	public Object setAttribute(String name, Object value) {
		return getXelContext().setAttribute(name, value);
	}
	
	//check method annotation and collect NotifyChange annotation
	public static Set<Property> getNotifys(Method m, Object base, String prop, Object value) {
		//TODO, Dennis, do we really need to pass value here?
		final Set<Property> notifys = new LinkedHashSet<Property>();
		final NotifyChange annt = m == null ? 
			null : m.getAnnotation(NotifyChange.class);
		if (annt != null) {
			//if has annotation, use annotated value or prop (if no value in annotation)
			String[] notifies = annt.value();
			if (notifies.length > 0) {
				for(String notify : notifies) {
					final Property propx = new PropertyImpl(base, notify, value);
					notifys.add(propx);
				}
			} else if (prop != null) {
				//prpoerty is null in doExecute case
				notifys.add(new PropertyImpl(base, prop, value));
			}
		}
		return notifys;
	}
	
	public static void addNotifys(Method m, Object base, String prop, Object value, BindContext ctx) {
		final Set<Property> props = getNotifys(m, base, prop, value);
		addNotifys(props, ctx);
	}
	
	//utility method to add notifys to BindContext
	private static void addNotifys(Set<Property> props, BindContext ctx) {
		if (ctx == null) {
			return;
		}
		Set<Property> notifys = (Set<Property>) ctx.getAttribute(BinderImpl.NOTIFYS);
		if (notifys == null) {
			notifys = new LinkedHashSet<Property>();
			ctx.setAttribute(BinderImpl.NOTIFYS, notifys);
		}
		notifys.addAll(props);
	}
	
	//utility method to add validates to BindContext
	private static void addValidates(Set<Property> props, BindContext ctx) {
		if (ctx == null) {
			return;
		}
		Set<Property> validates = (Set<Property>) ctx.getAttribute(BinderImpl.VALIDATES);
		if (validates == null) {
			validates = new LinkedHashSet<Property>();
			ctx.setAttribute(BinderImpl.VALIDATES, validates);
		}
		validates.addAll(props);
	}

	/*package*/ static String toNodeString(Node next, StringBuffer path) {
		if (next instanceof AstBracketSuffix) {
			final String bracketString = toNodeString(next.jjtGetChild(0), new StringBuffer()); //recursive
			path.append("[").append(bracketString).append("]");
		} else if (next instanceof AstValue) {
    		for(int j = 0, len = next.jjtGetNumChildren(); j < len; ++j) {
    			final Node kid = next.jjtGetChild(j);
    			toNodeString(kid, path); //recursive
    		}
		} else if (next instanceof AstDotSuffix) {
			path.append(".").append(next.getImage());
		} else {
			path.append(next.getImage());
		}
		return path.toString();
	}

	public static boolean isBracket(String script) {
		return script.startsWith("[") && script.endsWith("]");
	}
	
	public static String appendFields(String prefix, String field) {
		return prefix + (isBracket(field) ? "" : '.') + field; 
	}
	
	//check method annotation and collect NotifyChange annotation
	public static void addDependsOnTrackings(Method m, String basepath, List<String> srcpath, Binding binding, BindContext ctx) {
		final DependsOn annt = m.getAnnotation(DependsOn.class);
		if (annt != null) {
			String[] props = annt.value();
			if (props.length > 0) {
				if (binding instanceof LoadPropertyBindingImpl) {
					((LoadPropertyBindingImpl)binding).addDependsOnTrackings(m, basepath, srcpath, props);
				} else if (binding instanceof LoadFormBindingImpl) {
					((LoadFormBindingImpl)binding).addDependsOnTrackings(m, basepath, srcpath, props);
				}
			}
		}
	}
	
	public static String pathToString(List<String> path) {
		final StringBuffer sb = new StringBuffer();
		for(String prop : path) {
			sb.append(prop);
		}
		return sb.toString();
	}
	
	//prepare the dependsOn nodes
	public static void addDependsOnTracking(Method m, String basepath, List<String> srcpath, String prop, Binding binding) {
		final Component comp = binding.getComponent();
		final Binder binder = binding.getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final String path = BindELContext.appendFields(basepath, prop);
		
		//parse depends on series
		BindContext ctxparse = BindContextUtil.newBindContext(binder, binding, false, null, comp, null);
		ctxparse.setAttribute(BinderImpl.SRCPATH, srcpath);
		ExpressionX expr = eval.parseExpressionX(ctxparse, path, Object.class); //prepare the tracking and association
		
		//bean association
		BindContext ctx = BindContextUtil.newBindContext(binder, binding, false, null, comp, null);
		eval.getValue(ctx, comp, expr); //will call tieValue() and recursive back via BindELResolver
	}
}
