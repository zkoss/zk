/* PhaseListener.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 6:49:34 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

/**
 * Phase call back listener that allow user to intervene the command execution life cycle. 
 * There are currently seven phases that executed in that order:
 * <ul>
 * <li>{@link COFIRM}: do confirm before the command</li>
 * <li>{@link SAVE_BEFORE}: do save bindings before executing the command</li>
 * <li>{@link LOAD_BEFORE}: do load bindings before executing the command</li>
 * <li>{@link EXECUTE}: execute the command</li>
 * <li>{@link SAVE_AFTER}: do save bindings after executing the command</li>
 * <li>{@link LOAD_AFTER}: do load bindings after executing the command</li>
 * </ul>
 * @author henrichen
 *
 */
public interface PhaseListener {

	/**
	 * validate phase.
	 */
	public static final int VALIDATE = 1;	
	/**
	 * save before command phase.
	 */
	public static final int SAVE_BEFORE = 2;
	/**
	 * load before command phase.
	 */
	public static final int LOAD_BEFORE = 3;
	/**
	 * command execution phase.
	 */
	public static final int EXECUTE = 4;
	/**
	 * save after command phase.
	 */
	public static final int SAVE_AFTER = 5;
	/**
	 * load after command phase.
	 */
	public static final int LOAD_AFTER = 6;
	/**
	 * command begin/end phase.
	 */
	public static final int COMMAND = 7;
	

	
	/**
	 * Callbacks before each command phase.
	 * @param phase the phase id
	 * @param ctx the associated {@link BindContext}
	 */
	public void prePhase(int phase, BindContext ctx);
	
	/**
	 * Callbacks after each command phase. 
	 * @param phase the phase id
	 * @param ctx the associated {@link BindContext}
	 */
	public void postPhase(int phase, BindContext ctx);
}
