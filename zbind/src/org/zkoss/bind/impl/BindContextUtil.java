/* BindContextUtil.java

	Purpose:
		
	Description:
		
	History:
		2011/10/19 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * to help handling BindContext, internal use only,
 * 
 * @author dennis
 * 
 */
public class BindContextUtil {
	
	
	public static BindContext newBindContext(Binder binder, Binding binding, boolean save, String command, Component comp, Event event) {
		BindContext ctx = new BindContextImpl(binder,binding,save,command,comp,event);
		if(binding!=null){
			setBindingArgs(binder,comp,ctx,binding);
		}
		return ctx;
	}
	
	public static void setCommandArgs(Binder binder, Component comp, BindContext ctx, Map<String,Object> args){
		ctx.setAttribute(BindContextImpl.COMMAND_ARGS, args);
	}
	private static void setBindingArgs(Binder binder, Component comp, BindContext ctx, Binding binding){
		ctx.setAttribute(BindContextImpl.BINDING_ARGS, BindEvaluatorXUtil.evalArgs(binder.getEvaluatorX(), comp, binding.getArgs()));
	}
	public static void setConverterArgs(Binder binder, Component comp, BindContext ctx, PropertyBinding binding){
		ctx.setAttribute(BindContextImpl.CONVERTER_ARGS, BindEvaluatorXUtil.evalArgs(binder.getEvaluatorX(), comp, binding.getConverterArgs()));
	}
	public static void setValidatorArgs(Binder binder, Component comp, BindContext ctx, SavePropertyBinding binding){
		ctx.setAttribute(BindContextImpl.VALIDATOR_ARGS, BindEvaluatorXUtil.evalArgs(binder.getEvaluatorX(), comp, binding.getValidatorArgs()));
	}
	public static void setValidatorArgs(Binder binder, Component comp, BindContext ctx, SaveFormBinding binding){
		ctx.setAttribute(BindContextImpl.VALIDATOR_ARGS, BindEvaluatorXUtil.evalArgs(binder.getEvaluatorX(), comp, binding.getValidatorArgs()));
	}
	

//	public static void setIgnoreTracker(BindContext ctx,boolean b) {
//		if(b){
//			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);
//		}else{
//			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, null);
//		}
//	}
}
