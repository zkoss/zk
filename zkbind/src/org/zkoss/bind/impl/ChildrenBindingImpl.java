/* ChildrenBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.ChildrenBinding;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * A base implementation of {@link ChildrenBinding}.
 * @author dennis
 * @since 6.0.0
 */
public abstract class ChildrenBindingImpl extends BindingImpl implements ChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	protected final AccessInfo _accessInfo;

	/**
	 * @param binder
	 * @param comp
	 * @param accessExpr the binding expression , to access the bean
	 * @param conditionType the condition type
	 * @param command the command, if the conditionType is not prompt, then command must not null
	 * @param bindingArgs
	 */
	protected ChildrenBindingImpl(Binder binder, Component comp, String accessExpr, 
			ConditionType conditionType, String command, Map<String, Object> bindingArgs) {
		super(binder,comp, bindingArgs);
		final Class<Object> returnType = Object.class;
		this._accessInfo = AccessInfo.create(this, accessExpr, returnType, conditionType, command, ignoreTracker());
	}
	
	//should this binding set the ignore tracker attribute when evaluate the expression.
	protected boolean ignoreTracker(){
		return false;
	}
	
	public String getCommandName() {
		return this._accessInfo.getCommandName();
	}
	
	public String getPropertyString() {
		return getPureExpressionString(this._accessInfo.getProperty());
	}
	
	public ConditionType getConditionType() {
		return this._accessInfo.getConditionType();
	}
	
	/*package*/ ExpressionX getProperty() {
		return this._accessInfo.getProperty();
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
		.append(",component:").append(getComponent())
		.append(",access:").append(getProperty().getExpressionString())
		.append(",condition:").append(getConditionType())
		.append(",command:").append(getCommandName()).toString();
	}
}
