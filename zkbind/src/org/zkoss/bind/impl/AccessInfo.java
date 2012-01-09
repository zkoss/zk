/* AccessInfo.java

	Purpose:
		
	Description:
		
	History:
		Jul 28, 2011 5:17:48 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.xel.ExpressionX;

/**
 * Represent an load/save binding expression 
 * <p>e.g.
 * "load = property (before|after) command"
 * "save = property (before|after) command"
 * </p>
 * @author henrichen
 * @since 6.0.0
 */
public class AccessInfo implements Serializable{
	
	private static final long serialVersionUID = 1463169907348730644L;
	
	final String commandName; //command name
	final ExpressionX property; //property expression
	final ConditionType type; //the condition type, prompt, before command or after command
	
	public AccessInfo(ExpressionX property,ConditionType type, String command) {
		this.property = property;
		this.type = type;
		this.commandName = command;
	}
	public String getCommandName() {
		return this.commandName;
	}
	public ConditionType getConditionType() {
		return this.type;
	}
	public ExpressionX getProperty() {
		return this.property;
	}


	public static AccessInfo create(Binding binding, String accessExpr, Class<?> expectedType, ConditionType type, String command,boolean ignoreTracker) {
		final Binder binder = binding.getBinder();
		if(type != ConditionType.PROMPT && command==null){
			throw new IllegalArgumentException("condition type is "+type+", but command is null");
		}

		final BindEvaluatorX eval = binder.getEvaluatorX();
		final BindContext ctx = (type != ConditionType.PROMPT) ? null : 
			BindContextUtil.newBindContext(binder, binding, false, null, binding.getComponent(), null); 
		if(ctx!=null && ignoreTracker){
			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);
		}
		final ExpressionX prop = eval.parseExpressionX(ctx, accessExpr, expectedType);
		return new AccessInfo(prop, type, command);
	}
	
}
