/* BindELContext.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 4:52:27 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Property;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Immutable;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyChangeDisabled;
import org.zkoss.bind.impl.AllocUtil;
import org.zkoss.bind.impl.BindContextUtil;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.LoadChildrenBindingImpl;
import org.zkoss.bind.impl.LoadFormBindingImpl;
import org.zkoss.bind.impl.LoadPropertyBindingImpl;
import org.zkoss.bind.impl.PropertyImpl;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.Binding;
import org.zkoss.lang.Primitives;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.zel.XelELContext;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.impl.parser.AstBracketSuffix;
import org.zkoss.zel.impl.parser.AstDotSuffix;
import org.zkoss.zel.impl.parser.AstValue;
import org.zkoss.zel.impl.parser.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * ELContext for Binding.
 * @author henrichen
 * @since 6.0.0
 */
public class BindELContext extends XelELContext {
	public BindELContext(XelContext xelc) {
		super(xelc);
	}
	
	protected ELResolver newELResolver(XelContext xelc) {
		return new BindELResolver(xelc);
	}

	public Binding getBinding() {
		return (Binding) getXelContext().getAttribute(BinderImpl.BINDING); //see BindEvaluatorXImpl#newXelContext()
	}
	
	public BindContext getBindContext() {
		return (BindContext) getXelContext().getAttribute(BinderImpl.BINDCTX); //see BindEvaluatorXImpl#newXelContext()
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
	
	/**
	 * Removes the attribute, if any.
	 * @since 8.0.0
	 */
	public Object removeAttribute(String name) {
		return getXelContext().removeAttribute(name);
	}
	
	private static final String TMPBASE = "$TMPBASE$";
	public static Property prepareProperty(Object base, String prop, Object value, BindContext ctx) {
		if (ctx != null && prop.indexOf('[') >= 0) { //handle properties that containing [] indirect reference
			final Binder binder = ctx.getBinder();
			final Component comp = ctx.getComponent();
			Object old = null;
			try {
				old = comp.setAttribute(TMPBASE, base);
				final BindEvaluatorX eval = binder.getEvaluatorX();
				final BindContext bctx = BindContextUtil.newBindContext(binder, null, false, null, comp, null);
				final String expression = TMPBASE + (prop.startsWith("[") ? prop : ("."+prop));
				final ExpressionX exprX = eval.parseExpressionX(bctx, expression, Object.class);
				final String propTrim = prop.trim();
				final ValueReference valref = eval.getValueReference(bctx, comp, exprX);
				if(valref==null){
					throw new UiException("value reference not found by expression ["+exprX.getExpressionString()+"]");
				}
				base = valref.getBase();
				prop = propTrim.endsWith("]") ? 
						"[" + valref.getProperty() + "]" :
						""+valref.getProperty();
			} finally {
				comp.setAttribute(TMPBASE, old);
			}
		}
		return new PropertyImpl(base, prop, value);
	}
	
	//check method annotation and collect NotifyChange annotation
	public static Set<Property> getNotifys(Method m, Object base, String prop, Object value, BindContext ctx) {
		//TODO, Dennis, do we really need to pass value here?
		final Set<Property> notifys = new LinkedHashSet<Property>();
		if(m==null) return notifys;
		
		final NotifyChange annt = m.getAnnotation(NotifyChange.class);
		//ZK-687 @NotifyChange should be doing automatically. 
		final NotifyChangeDisabled anntdis = m.getAnnotation(NotifyChangeDisabled.class);
		if (annt != null && anntdis != null) {
			throw new UiException("don't use "+NotifyChange.class+" with "+NotifyChangeDisabled.class+", choose only one");
		}
		
		if (annt != null) {
			//if has annotation, use annotated value or prop (if no value in annotation)
			String[] notifies = annt.value();
			if (notifies.length > 0) {
				for(String notify : notifies) {
					final Property propx = prepareProperty(base, notify, value, ctx);
					notifys.add(propx);
				}
			} else if (prop != null) { //property is null in doExecute case
				notifys.add(new PropertyImpl(base, prop, value));
			}
		}else if(anntdis == null && prop != null){
			notifys.add(new PropertyImpl(base, prop, value));
		}
		return notifys;
	}
	
	public static void addNotifys(Object base, String prop, Object value, BindContext ctx) {
		final Set<Property> properties = new HashSet<Property>(3);
		properties.add(new PropertyImpl(base, prop, value));
		addNotifys(properties, ctx);
	}
	
	public static void addNotifys(Method m, Object base, String prop, Object value, BindContext ctx) {
		final Set<Property> props = getNotifys(m, base, prop, value, ctx);
		addNotifys(props, ctx);
	}
	
	//utility method to add notifys to BindContext
	public static void addNotifys(Set<Property> props, BindContext ctx) {
		if (ctx == null) {
			return;
		}
		Set<Property> notifys = getNotifys(ctx);
		if (notifys == null) {
			notifys = new LinkedHashSet<Property>();
			ctx.setAttribute(BinderImpl.NOTIFYS, notifys);
		}
		notifys.addAll(props);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Property> getNotifys(BindContext ctx){
		return (Set<Property>)ctx.getAttribute(BinderImpl.NOTIFYS);
	}
	
	@SuppressWarnings("unchecked")
	private static Set<Property> getValidates(BindContext ctx){
		return (Set<Property>)ctx.getAttribute(BinderImpl.VALIDATES);
	}	
	
	//utility method to add validates to BindContext
	@SuppressWarnings("unused")
	private static void addValidates(Set<Property> props, BindContext ctx) {
		if (ctx == null) {
			return;
		}
		Set<Property> validates = getValidates(ctx);
		if (validates == null) {
			validates = new LinkedHashSet<Property>();
			ctx.setAttribute(BinderImpl.VALIDATES, validates);
		}
		validates.addAll(props);
	}

	public static String toNodeString(Node next, StringBuilder path) {
		if (next instanceof AstBracketSuffix) {
			final String bracketString = toNodeString(next.jjtGetChild(0), new StringBuilder()); //recursive
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

	public static String toNodeString(Node next, StringBuffer path) {
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
					((LoadPropertyBindingImpl)binding).addDependsOnTrackings(srcpath, basepath, props);
				} else if (binding instanceof LoadFormBindingImpl) {
					((LoadFormBindingImpl)binding).addDependsOnTrackings(srcpath, basepath, props);
				} else if (binding instanceof LoadChildrenBindingImpl) {
					((LoadChildrenBindingImpl)binding).addDependsOnTrackings(srcpath, basepath, props);
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
	
	/**
	 * Prepare the dependsOn nodes
	 * @param srcBinding associated binding of the source dependent field; e.g. <label id="full" value="@bind(vm.fullname)"/>
	 * @param srcPath the source dependent field name series in list. e.g. "vm", "fullname" for "vm.fullname". 
	 * @param dependsOnBasepath the base path for the depends-on field; e.g. the "vm" of the "vm.firstname"
	 * @param dependsOnProp the property name of the depends-on field; e.g. the "firstname" of the "vm.firstname"
	 */
	public static void addDependsOnTracking(Binding srcBinding, List<String> srcPath, String dependsOnBasepath, String dependsOnProp) {
		final String dependsOnPath = BindELContext.appendFields(dependsOnBasepath, dependsOnProp);
		final Component srcComp = srcBinding.getComponent();
		addDependsOnTracking(srcBinding, srcPath, srcComp, dependsOnPath, srcComp); //source component shared as DependsOnComp 
	}
	
	/**
	 * Prepare the dependsOn nodes
	 * @param srcBinding the binding with the source dependent field; e.g. <label id="full" value="@load(vm.fullname)"/>
	 * @param srcPath the source dependent field name series in list; e.g. ["vm", "fullname"] for "vm.fullname".
	 * @param srcComp the source component associated with the binding; e.g. <label>
	 * @param dependsOnPath the depends-on property name series; e.g. "vm.firstname" 
	 * @param dependsOnComp the depends-on component associated with the depends-on property name series binding; e.g. "vm.firstname"
	 */
	public static void addDependsOnTracking(Binding srcBinding, List<String> srcPath, Component srcComp, String dependsOnPath, Component dependsOnComp) {
		final Binder binder = srcBinding.getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		
		//parse depends on series
		BindContext ctxparse = BindContextUtil.newBindContext(binder, srcBinding, false, null, srcComp, null);
		ctxparse.setAttribute(BinderImpl.SRCPATH, srcPath);
		ctxparse.setAttribute(BinderImpl.DEPENDS_ON_COMP, dependsOnComp);
		ExpressionX expr = eval.parseExpressionX(ctxparse, dependsOnPath, Object.class); //prepare the tracking and association
		
		//bean association
		BindContext ctx = BindContextUtil.newBindContext(binder, srcBinding, false, null, srcComp, null);
		eval.getValue(ctx, srcComp, expr); //will call tieValue() and recursive back via BindELResolver
	}

	/** Returns whether the specified Object is an immutable object */
	public static boolean isImmutable(Object value) {
		//null is deemed as primitive
		if (value == null) {
			return true;
		}
		final Class<? extends Object> cls = value.getClass();
		return cls.isPrimitive() //value is primitive 
			|| Primitives.toPrimitive(cls) != null //or a wrapper
			|| value instanceof String //or a String
			|| isAnnotatedImmutable(cls); //or an Immutable
	}
	
	public static String getModelName(Component comp) {
		return (String) AllocUtil.inst.processScript(BinderImpl.MODEL + comp.getUuid());
	}
	
	public static void addModel(Component comp, Object model) {
		final String name = getModelName(comp);
		comp.setAttribute(name, model);
	}
	
	public static Object removeModel(Component comp) {
		final String name = getModelName(comp);
		return comp.removeAttribute(name);
	}
	private static boolean isAnnotatedImmutable(Class<? extends Object> cls){
		return cls.getAnnotation(Immutable.class)!=null; 
	}
}
