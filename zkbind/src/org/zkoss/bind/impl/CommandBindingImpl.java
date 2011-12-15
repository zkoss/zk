/* CommandBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 8, 2011 3:28:41 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.CommandBinding;
import org.zkoss.lang.Classes;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of CommandBinding.
 * @author henrichen
 *
 */
public class CommandBindingImpl extends BindingImpl implements CommandBinding {
	private final String _evtnm;
	private final ExpressionX _command;
	public CommandBindingImpl(Binder binder, Component comp, String evtnm, String cmdScript, Map<String, Object> args) {
		super(binder, comp, args);
		_evtnm = evtnm;
		final BindEvaluatorX eval = binder.getEvaluatorX();
		_command = eval.parseExpressionX(null, cmdScript, String.class);
	}
	public String getEventName() {
		return _evtnm;
	}
	public ExpressionX getCommand() {
		return _command;
	}
	public String getCommandString() {
		return getPureExpressionString(_command);
	}
	
	//TODO, DENNIS, Nobody call this
	public void execute(BindContext ctx) {
		final Object base = getBinder().getViewModel();
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		final String methodName = (String) eval.getValue(ctx, comp, getCommand());
		try {
			final Method method = Classes.getMethodInPublic(base.getClass(), methodName, new Class[] {Map.class});
			method.invoke(getBinder().getViewModel(), ctx.getAttributes());
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
		.append(",component:").append(getComponent())
		.append(",evtnm:").append(_evtnm)
		.append(",command:").append(getCommandString()).toString();
	}
}
