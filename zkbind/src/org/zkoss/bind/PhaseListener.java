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
 * 
 * <br/>
 * <b>Since 6.5.2</b> - You could set phase listener by setting library-property <code>org.zkoss.bind.PhaseListener.class</code>
 * for example:<pre>{@code
<library-property>
	<name>org.zkoss.bind.PhaseListener.class</name>
	<value>foo.BarListener</value>
</library-property>
 * }</pre>
 * Note: the listener instance is shared between all binders, it is not thread-safe, your implementation has to care the concurrent access issue. 
 * @author henrichen
 * @author dennischen
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
