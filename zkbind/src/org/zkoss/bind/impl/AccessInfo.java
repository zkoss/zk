/* AccessInfo.java

	Purpose:
		
	Description:
		
	History:
		Jul 28, 2011 5:17:48 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.Binding;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Represent an load/save binding expression 
 * <p>e.g.
 * "load = property (before|after) command"
 * "save = property (before|after) command"
 * </p>
 * @author henrichen
 *
 */
public class AccessInfo {
	final String commandName; //command name
	final ExpressionX property; //property expression
	final boolean after; //before command or after command
	
	public AccessInfo(ExpressionX property, boolean after, String command) {
		this.property = property;
		this.after = after;
		this.commandName = command;
	}
	public String getCommandName() {
		return this.commandName;
	}
	public boolean isAfter() {
		return this.after;
	}
	public ExpressionX getProperty() {
		return this.property;
	}

	//tokenize
	private static final Pattern ACCESS_PATTERN = Pattern.compile("\\s+|[^\\s\"']+|\"[^\"]*\"|'[^']*'|'[^']*");
	public static AccessInfo create(Binding binding, String accessScript, Class<?> expectedType, boolean ignoreTracker) {
		final Binder binder = binding.getBinder();
		final Matcher matcher = ACCESS_PATTERN.matcher(accessScript);
		final StringBuffer property = new StringBuffer();
		final StringBuffer command = new StringBuffer();
		Boolean after = null;
		while (matcher.find()) {
			final String token = matcher.group();
			if (after != null) {
				command.append(token);
			} else {
				if ("after".equalsIgnoreCase(token)) {
					after = Boolean.TRUE;
				} else if ("before".equalsIgnoreCase(token)) {
					after = Boolean.FALSE;
				} else {
					property.append(token);
				}
			}
		}
		if (property.length() <= 0) {
			throw new IllegalArgumentException(accessScript);
		}
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final String script = command.toString().trim();
		//parse the script to a expression, in default implementation it will add ${} to the script, 
		final ExpressionX comm = script.length() > 0 ? eval.parseExpressionX(null, script, String.class) : null;
		//in current spec. we only allow literal string in command.
		final String commandName = comm == null ? null : (String) eval.getValue((BindContext)null, (Component)null, comm);
		if (comm != null && !isLiteralString(script, commandName)) {
			throw new IllegalArgumentException("command must be a literal text rather than an expression: " + comm.getExpressionString());
		}
		//only prompt loading shall track dependency
		//(dependency only be tracing when has a binding presented and not ignoreTracker, see TrackerImpl)
		final BindContext ctx = commandName != null ? null : 
			BindContextUtil.newBindContext(binder, binding, false, null, null, null); 
		if(ctx!=null && ignoreTracker){
			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);
		}
		final ExpressionX prop = eval.parseExpressionX(ctx, property.toString().trim(), expectedType);
		final boolean af = after != null ? after.booleanValue() : false;
		return new AccessInfo(prop, af, commandName);
	}
	
	private static boolean isLiteralString(String script, String evaled) {
		return script.equals("'"+evaled+"'") || script.equals("\""+evaled+"\"");
	}
}
