/* BindExpressionBuilder.java

	Purpose:
		
	Description:
		
	History:
		Aug 15, 2011 11:04:37 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormExt;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.LoadFormBindingImpl;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.sys.tracker.Tracker;

import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.impl.lang.ExpressionBuilder;
import org.zkoss.zel.impl.parser.AstIdentifier;
import org.zkoss.zel.impl.parser.AstValue;
import org.zkoss.zel.impl.parser.Node;
import org.zkoss.zk.ui.Component;

/**
 * Handle value dot series script for Binding.
 * @author henrichen
 * @since 6.0.0
 */
public class BindExpressionBuilder extends ExpressionBuilder {
	
	private static final Logger _log = LoggerFactory.getLogger(BindExpressionBuilder.class);
	private static final String _isVisitedKey = BindExpressionBuilder.class+"_isVisted";
	protected final BindELContext _ctx;
    public BindExpressionBuilder(String expression, ELContext ctx) throws ELException {
		super(expression, ctx);
		_ctx = (BindELContext) ctx;
	}

    //20110815, allow handle value dot series script (e.g. x.y.z ? a.b.c : d.e.f)
    //will call back for each token node.
	public void visit(Node node) throws ELException {
    	super.visit(node);
    	visitNode(node);
    }
	
	
	@SuppressWarnings("unchecked")
	private static List<String> getSrcList(BindContext ctx){
		return (List<String>)ctx.getAttribute(BinderImpl.SRCPATH);
	}
	private static Component getDependsOnComponent(BindContext ctx) {
		return (Component)ctx.getAttribute(BinderImpl.DEPENDS_ON_COMP);
	}
	
	//to tracing load property dependency or form field(both save and load)
	//path example, [vm,.p1,.firstName] or [fx.firstName]
	protected void addTracking(List<String> series) {
		final Binding binding = _ctx.getBinding();
		final boolean dotracker = !_ctx.ignoreTracker();
		
		if (binding != null && series != null && !series.isEmpty()) {
			
			//to prevent unnecessary from-save in nested expression in from binding and cause save error
			//e.g @bind(fx.hash[fx.key]) or @bind(vm.hash[fx.key]) 
			
			final Boolean isVisted = (Boolean)_ctx.getAttribute(_isVisitedKey);
			if(!Boolean.TRUE.equals(isVisted)){
				_ctx.setAttribute(_isVisitedKey, Boolean.TRUE);
			}
			
			final Iterator<String> it = series.iterator();
			final String prop = (String) it.next();
			final Binder binder = binding.getBinder();
			final Tracker tracker = ((BinderCtrl)binder).getTracker();
			
			final BindContext bctx = (BindContext) _ctx.getAttribute(BinderImpl.BINDCTX);
			final List<String> srcpath = bctx != null ? getSrcList(bctx) : null;
			final Component dependsOnComp =  bctx != null ? getDependsOnComponent(bctx) : null;
			final String[] srcprops = srcpath != null ? properties(srcpath) : null;
			//check if a PropertyBinding inside a Form
			final Component comp = bctx != null ? bctx.getComponent() : binding.getComponent();
			final Object base = comp.getAttribute(prop, true);
			if (base instanceof Form) {
				final Form formBean = (Form) base;
				final String fieldName = fieldName(it);
				
				if (fieldName != null) {
					if (binding instanceof SavePropertyBinding && !Boolean.TRUE.equals(isVisted)) {
						if(_log.isDebugEnabled()){
							_log.debug("add save-field '%s' to form '%s'", fieldName,formBean);
						}
						if(formBean instanceof FormExt ){
							((FormExt)formBean).addSaveFieldName(fieldName);
						}
						((BinderCtrl)binder).addFormAssociatedSaveBinding(comp, prop, (SavePropertyBinding)binding, fieldName);
					} else if (binding instanceof LoadPropertyBinding 
							|| binding instanceof LoadChildrenBinding || binding instanceof ReferenceBinding) {
						if(_log.isDebugEnabled()){
							_log.debug("add load-field '%s' to form '%s'", fieldName,formBean);
						}
						if(formBean instanceof FormExt){
							((FormExt)formBean).addLoadFieldName(fieldName);
						}
					}
					//initialize Tracker per the series (in special Form way)
					if(dotracker){
						if (srcprops == null) {
							tracker.addTracking(comp, new String[] {prop, fieldName}, binding);
						} else {
							tracker.addDependsOn(comp, srcprops, binding, dependsOnComp, new String[] {prop, fieldName});
						}
					}
				} else {
					if(dotracker){
						if (srcprops == null) {
							tracker.addTracking(comp, new String[] {prop}, binding);
						} else {
							tracker.addDependsOn(comp, srcprops, binding, dependsOnComp, new String[] {prop});
						}
					}
				}
			} else {
				//initialize Tracker per the series
				String[] props = properties(series);
				if(dotracker){
					if (srcprops == null) {
						tracker.addTracking(comp, props, binding);
					} else {
						tracker.addDependsOn(comp, srcprops, binding, dependsOnComp, props);
					}
				}
				
				if (binding instanceof LoadFormBindingImpl) {
					((LoadFormBindingImpl)binding).setSeriesLength(props.length);
				}
			}
		}
	}

	protected void visitNode(Node node) {
		if(_ctx.getBinding()==null) return; //no need to build tracker, we are not in binding expression
		
		final List<String> path = new ArrayList<String>();
		//find the path from AST value node or AST identifier node
    	if (node instanceof AstValue) {
    		for(int j = 0, len = node.jjtGetNumChildren(); j < len; ++j) {
    			final Node kid = node.jjtGetChild(j);
    			path.add(BindELContext.toNodeString(kid, new StringBuffer()));
    		}
    		addTracking(path);
    	} else if (node instanceof AstIdentifier) {
    		if (!(node.jjtGetParent() instanceof AstValue)) { //one variable series
    			path.add(BindELContext.toNodeString(node, new StringBuffer()));
    			addTracking(path);
    		}
    	}
	}
	
	//remove prefix '.' for properties
	private String[] properties(List<String> series) {
		final String[] props = new String[series.size()];
		int j = 0;
		for (String prop : series) {
			if (prop.charAt(0) == '.') {
				prop = prop.substring(1);
			}
			props[j++] = prop;
		}
		return props;
	}
	
	//append rest field into form field name
	private String fieldName(Iterator<String> it) {
		final StringBuffer sb = new StringBuffer();
		while(it.hasNext()) {
			sb.append(it.next());
		}
		return sb.length() == 0 ? null : sb.charAt(0) == '.' ? sb.substring(1) : sb.toString();
	}
}
