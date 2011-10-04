/**
 * 
 */
package org.zkoss.zk.ui.select.impl;


/**
 * The model of pseudo class definition
 * @author simonpai
 */
public interface PseudoClassDef {
	
	/**
	 * Return true if the component qualifies this pseudo class.
	 * @param ctx 
	 * @param parameters
	 * @return
	 */
	public boolean accept(ComponentMatchCtx ctx, String ... parameters);
	
}
