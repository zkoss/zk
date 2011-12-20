/* Scope.java

	Purpose:
		
	Description:
		
	History:
		2011/12/16 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;


/**
 * The implicit scopes of zk for {@link ScopeParam}.
 * The {@linkplain #AUTO} will search the value from components to page, desktop one bye one...until find a value 
 * {@linkplain #SESSION}, {@linkplain #APPLICATION} and by the order.
 * Other scope only represents itself.
 * @author dennis
 * @see ScopeParam
 */
public enum Scope {
	/**
	 * will search the value from components to page, desktop one bye one...until find a value 
	 */
	AUTO("auto"),
	/**
	 * The implicit componentScope
	 */
	COMPONENT("componentScope"),
	/**
	 * The implicit spaceScope
	 */
	SPACE("spaceScope"),
	/**
	 * The implicit pageScope
	 */
	PAGE("pageScope"),
	/**
	 * The implicit desktopScope
	 */
	DESKTOP("desktopScope"),
	/**
	 * The implicit sessionScope
	 */
	SESSION("sessionScope"),
	/**
	 * The implicit applicationScope
	 */
	APPLICATION("applicationScope");
	
	
	/**
	 * 
	 */
	String name;
	
	private Scope(String name){
		this.name = name;
	}
	
	/**
	 * the zk implicit scope name
	 */
	public String getName(){
		return name;
	}
}
