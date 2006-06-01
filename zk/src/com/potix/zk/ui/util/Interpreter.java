/* Interpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:51:22     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

/**
 * The interpter used to interpret the zscript codes.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Interpreter {
	/** Returns {@link Namespace} belonging to this interpreter.
	 */
	public Namespace getNamespace();

	/** Sets a variable that both the interpreter and EL can see it.
	 * The variable is defined in the scope of this page.
	 * In other words, it is visible to all components in this page, unless
	 * it is override by {@link Component#setVariable}.
	 */
	public void setVariable(String name, Object val);
	/** Returns the value of a variable defined in the BSH interpreter.
	 * The variable is defined in the scope of this page.
	 * In other words, it is visible to all components in this page, unless
	 * it is override by {@link Component#setVariable}.
	 */
	public Object getVariable(String name);
	/** Unsets a variable.
	 * @see #setVariable
	 */
	public void unsetVariable(String name);

	/** Adds a name resolver that will be used to resolve a variable
	 * by {@link #getVariable}.
	 *
	 * <p>Note: the variables resolved by the specified resolver are
	 * accessible to both zscript and EL expressions.
	 *
	 * @return wether the resolver is added successfully.
	 * Note: if the resolver was added before, it won't be added again
	 * and this method returns false.
	 */
	public boolean addVariableResolver(VariableResolver resolver);
	/** Removes a name resolve that was added by {@link #addVariableResolver}.
	 *
	 * @return false if resolved is not added before.
	 */
	public boolean removeVariableResolver(VariableResolver resolver);

	/** Evaluates the script against the specified namespace.
	 *
	 * @param ns the namespace, or null to use {@link #getNamespace}.
	 */
	public void eval(String script, Namespace ns);
}
