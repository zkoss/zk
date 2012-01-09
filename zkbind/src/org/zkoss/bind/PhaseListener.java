/* PhaseListener.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 6:49:34 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

/**
 * A call back listener that allow user to intervene the execution life cycle. 
 * @author henrichen
 * @since 6.0.0
 */
public interface PhaseListener {
	
	/**
	 * Callbacks before each phase.
	 * @param phase the phase id
	 * @param ctx the associated {@link BindContext}
	 */
	public void prePhase(Phase phase, BindContext ctx);
	
	/**
	 * Callbacks after each phase. 
	 * @param phase the phase id
	 * @param ctx the associated {@link BindContext}
	 */
	public void postPhase(Phase phase, BindContext ctx);
}
