/* Scope.java

	Purpose:
		
	Description:
		
	History:
		2011/12/16 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;


/**
 * The implicit scopes of zk for {@link ScopeParam}. <br/>
 * The {@linkplain #AUTO} scope means searching the value from components to page, desktop one bye one automatically...until find a value 
 * {@linkplain #SESSION}, {@linkplain #APPLICATION} and by the order. <br/>
 * Other scope only represents itself.
 * @author dennis
 * @see ScopeParam
 * @since 6.0.0
 */
public enum Scope {
	/**
	 * Search the value from components to page, desktop one bye one...until find a value 
	 */
	AUTO("auto"),
	/**
	 * Search the value from the implicit componentScope
	 */
	COMPONENT("componentScope"),
	/**
	 * Search the value from the implicit spaceScope
	 */
	SPACE("spaceScope"),
	/**
	 * Search the value from the implicit pageScope
	 */
	PAGE("pageScope"),
	/**
	 * Search the value from the implicit desktopScope
	 */
	DESKTOP("desktopScope"),
	/**
	 * Search the value from the implicit sessionScope
	 */
	SESSION("sessionScope"),
	/**
	 * Search the value from the implicit applicationScope
	 */
	APPLICATION("applicationScope");
	
	
	private String _name;
	
	private Scope(String name){
		this._name = name;
	}
	
	/**
	 * the zk implicit scope name
	 */
	public String getName(){
		return _name;
	}
}
