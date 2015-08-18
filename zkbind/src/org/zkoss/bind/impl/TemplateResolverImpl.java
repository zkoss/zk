/* TemplateResolver.java

	Purpose:
		
	Description:
		
	History:
		2012/1/4 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Template;

/**
 * The resolver resolves template and handles template changes
 * @author dennis
 * @since 6.0.0
 */
@SuppressWarnings("deprecation")
public class TemplateResolverImpl implements TemplateResolver, /*Binding,*/ Serializable{//implement binding for tieValue
	private static final long serialVersionUID = 1L;
	private final String _templateExpr;
	private final Map<String, Object> _templateArgs;
	private final Binder _binder;
	private final String _attr;
	private final Component _comp;
	private final ExpressionX _expression;
	
	public TemplateResolverImpl(Binder binder, Component comp, String attr, String templateExpr, Map<String, Object> templateArgs) {
		_binder = binder;
		_comp = comp;
		_templateExpr = templateExpr;
		_templateArgs = templateArgs;
		_attr = attr;
		//don't need to pass this, since add tracker was do by loadbinding
		final BindContext ctx = BindContextUtil.newBindContext(binder, null, false, null, _comp, null);
		_expression = binder.getEvaluatorX().parseExpressionX(ctx, templateExpr, Object.class);
	}
	
	public Binder getBinder(){
		return _binder;
	}
	
	public Component getComponent(){
		return _comp;
	}
	
	public String getExpression(){
		return _templateExpr;
	}
	

	public Map<String, Object> getTemplateArgs() {
		return _templateArgs;
	}

	public String getAttr() {
		return _attr;
	}

	private Template lookupTemplate(Component comp, String name) {
		if(comp==null) return null;
		Template template = comp.getTemplate(name);
		return template==null?lookupTemplate(comp.getParent(),name):template;
	}
	protected Object evaluateTemplate(Component eachComp,final Object eachData, final int index, final int size){
		return evaluateTemplate(eachComp, eachData, index, size, null);
	}
	protected Object evaluateTemplate(Component eachComp,final Object eachData, final int index, final int size, final String subType){
		Object oldEach = null;
		Object oldStatus = null;
		try {
			//TODO set subtype to evaluation context, so can use it as a condition.
			
			//prepare each and eachStatus
			oldEach = eachComp.setAttribute(EACH_VAR, eachData);
			oldStatus = eachComp.setAttribute(EACH_STATUS_VAR, new AbstractForEachStatus(){
				private static final long serialVersionUID = 1L;
				
				public int getIndex() {
					return index;
				}
				
				public Object getCurrent(){
					return eachData;
				}
				
				public Integer getEnd(){
					if(size<0){
						throw new UiException("end attribute is not supported");// the tree case
					}
					return size;
				}
			});
			
			final BindEvaluatorX eval = _binder.getEvaluatorX();
			final BindContext ctx = BindContextUtil.newBindContext(_binder, null, false, null, eachComp, null);
			final Object value = eval.getValue(ctx, eachComp, _expression);
			return value;
		} finally {
			//Bug ZK-2789: do not use setAttribute when actually trying to removeAttribute
			if (oldEach != null) {
				eachComp.setAttribute(EACH_VAR, oldEach);
			} else {
				eachComp.removeAttribute(EACH_VAR);
			}
			if (oldStatus != null) {
				eachComp.setAttribute(EACH_STATUS_VAR, oldStatus);
			} else {
				eachComp.removeAttribute(EACH_STATUS_VAR);
			}
		}
	}
	
	public Template resolveTemplate(Component eachComp, final Object eachData, final int index, final int size) {
		return resolveTemplate(eachComp, eachData, index, size, null);
	}
	
	public Template resolveTemplate(Component eachComp, final Object eachData, final int index, final int size, final String subType) {
			final Object value = evaluateTemplate(eachComp,eachData,index,size,subType);
			if(value instanceof Template){
				return (Template) value;
			}else if(value instanceof String){
				//lookup from each, to allow put template in rows
				Template template;
				//B70-ZK-2555: for backward compatibility				
				if (subType == null) {
					template = lookupTemplate(eachComp, (String)value);
				} else {
					template = lookupTemplate(eachComp, (String)value + ":" + subType);
					if (template == null) { //if not found, try lookup for value only
						template = lookupTemplate(eachComp, (String)value);
					}
				}
				if (template == null && ((String)value).indexOf('.') > 0) { //might be a class path
					try {
						template = (Template) _comp.getPage().resolveClass(((String)value)).newInstance();
					} catch (Exception e) {
						//ignore;
					}
				}
				return template;
			}else{
				throw new UiException("unknow template type "+value);
			}
	}
	
    //ZK-739: Allow dynamic template for collection binding.	
	//Tracking template expression to trigger load binding of the template component
	@Deprecated
	public void addTemplateTracking(final Component eachComp) {
		List<Binding> bindings = ((BinderCtrl)_binder).getLoadPromptBindings(_comp,_attr);
		final Binding binding = bindings.size()>0?bindings.get(bindings.size()-1):null;
		if(binding!=null){
			//following is for making tracker traces _teamplateExpr with binding
			final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, null, eachComp, null);
			final BindEvaluatorX eval = _binder.getEvaluatorX();
			final ExpressionX exprX = eval.parseExpressionX(ctx, _templateExpr, Object.class); 
			eval.getValue(ctx, eachComp, exprX);
		}
	}
	
    //ZK-739: Allow dynamic template for collection binding.	
	//Tracking template expression to trigger load binding of the template component
	public void addTemplateTracking(final Component eachComp,final Object eachData, final int index, final int size) {
		Binding binding = getTemplateBinding(eachComp,eachData, index, size);
		if(binding!=null){
			//following is for making tracker traces _teamplateExpr with binding
			final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, null, eachComp, null);
			final BindEvaluatorX eval = _binder.getEvaluatorX();
			final ExpressionX exprX = eval.parseExpressionX(ctx, _templateExpr, Object.class); 
			eval.getValue(ctx, eachComp, exprX);
		}
	}
	
	//Gets the binding that will be tacker when template change.
	protected Binding getTemplateBinding(Component eachComp, Object eachData, int index, int size) {
		List<Binding> bindings = ((BinderCtrl)_binder).getLoadPromptBindings(_comp,_attr);
		final Binding loadbinding = bindings.size()>0?bindings.get(bindings.size()-1):null;
		return loadbinding;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(_templateExpr).append("]").append(super.toString());
		return sb.toString();
	}
}
