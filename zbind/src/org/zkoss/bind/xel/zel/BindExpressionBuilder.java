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

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.LoadFormBindingImpl;
import org.zkoss.bind.impl.LogUtil;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.tracker.impl.TrackerImpl;
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
 *
 */
public class BindExpressionBuilder extends ExpressionBuilder {
	
	private static final LogUtil log = new LogUtil(BindExpressionBuilder.class.getName());
			
	private final BindELContext _ctx;
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
	
	//to tracing load property dependency or form field(both save and load)
	//path example, [vm,.p1,.firstName] or [fx.firstName]
	private void addTracking(List<String> series) {
		final Binding binding = _ctx.getBinding();
		final boolean dotracker = !_ctx.ignoreTracker();
		if (binding != null && series != null && !series.isEmpty()) {
			final Iterator<String> it = series.iterator();
			final String prop = (String) it.next();
			final Binder binder = binding.getBinder();
			final TrackerImpl tracker = (TrackerImpl) binder.getTracker();
			
			final BindContext bctx = (BindContext) _ctx.getAttribute(BinderImpl.BINDCTX);
			final List<String> srcpath = bctx != null ? getSrcList(bctx) : null;
			final String[] srcprops = srcpath != null ? properties(srcpath) : null;
			//check if a PropertyBinding inside a Form
			final Component comp = binding.getComponent();
			final Object base = comp.getAttribute(prop, true);
			if (base instanceof Form) {
				final Form formBean = (Form) base;
				final String fieldName = fieldName(it);
				if (fieldName != null) {
					if (binding instanceof SavePropertyBinding) {
						log.debug("add save-filed %s to form %s", fieldName,formBean);
						formBean.addSaveFieldName(fieldName);
						tracker.addFormSaveBindingTracking(comp, prop,(SavePropertyBinding)binding);
					} else if (binding instanceof LoadPropertyBinding) {
						log.debug("add load-filed %s to form %s", fieldName,formBean);
						formBean.addLoadFieldName(fieldName);
					}
					//initialize Tracker per the series (in special Form way)
					if(dotracker){
						tracker.addTracking(comp, new String[] {prop, fieldName}, srcprops, binding);
					}
				} else {
					if(dotracker){
						tracker.addTracking(comp, new String[] {prop}, srcprops, binding);
					}
				}
			
			} else {
				//initialize Tracker per the series
				String[] props = properties(series);
				if(dotracker){
					tracker.addTracking(binding.getComponent(), props, srcprops, binding);
				}
				
				if (binding instanceof LoadFormBindingImpl) {
					((LoadFormBindingImpl)binding).setSeriesLength(props.length);
				}
			}
		}
	}

	private void visitNode(Node node) {
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
