/**
 * 
 */
package org.zkoss.zk.ui.select.impl;


/**
 * The model of pseudo class definition
 * @since 6.0.0
 * @author simonpai
 */
public interface PseudoClassDef {
	
	/**
	 * Return true if the component qualifies this pseudo class.
	 * @param ctx 
	 * @param parameters
	 */
	public boolean accept(ComponentMatchCtx ctx, String ... parameters);
	
}
