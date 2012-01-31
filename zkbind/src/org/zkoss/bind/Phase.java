/* Phase.java

	Purpose:
		
	Description:
		
	History:
		2011/10/25 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

/**
 * The Phase of PhaseListener when doing binding.
 * There are currently seven phases that executed in that order: <br/>
 * Command :  <br/>
 * <ul>
 * <li>{@link #COMMAND}: do a command</li>
 * <li>{@link #VALIDATE}: do validate before a command or zk event</li>
 * <li>{@link #SAVE_BEFORE}: do save bindings before executing a command</li>
 * <li>{@link #LOAD_BEFORE}: do load bindings before executing a command</li>
 * <li>{@link #EXECUTE}: execute a command</li>
 * <li>{@link #SAVE_AFTER}: do save bindings after executing a command</li>
 * <li>{@link #LOAD_AFTER}: do load bindings after executing a command</li>
 * <li>{@link #SAVE_BINDING}: do a save binding</li>
 * <li>{@link #LOAD_BINDING}: do a load binding</li>
 * </ul>
 *  <br/>
 * Global Command :  <br/>
 * <ul>
 * <li>{@link #GLOBAL_COMMAND}: do a global command</li>
 * <li>{@link #EXECUTE}: execute a command</li>
 * </ul>
 * 
 * @author dennis
 * @since 6.0.0
 */
public enum Phase {
	/**
	 * command phase.
	 */
	COMMAND, 
	/**
	 * global command phase.
	 */
	GLOBAL_COMMAND,
	/**
	 * validate phase.
	 */
	VALIDATE, 
	/**
	 * save before command phase.
	 */
	SAVE_BEFORE, 
	/**
	 * load before command phase.
	 */		
	LOAD_BEFORE, 
	/**
	 * command execution phase.
	 */
	EXECUTE, 
	/**
	 * save after command phase.
	 */
	SAVE_AFTER, 
	/**
	 * load after command phase.
	 */
	LOAD_AFTER,
	/**
	 * save binding phase.
	 */
	SAVE_BINDING,
	/**
	 * load binding phase.
	 */
	LOAD_BINDING
}
