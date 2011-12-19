/* Scope.java

	Purpose:
		
	Description:
		
	History:
		2011/12/16 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The implicit scopes of zk for {@link ScopeParam}.
 * The {@linkplain #DEFAULT} includes {@linkplain #REQUEST}, {@linkplain #DESKTOP},{@linkplain #SESSION},{@linkplain #APPLICATION} and by the order.
 * The {@linkplain #ALL} includes {@linkplain #REQUEST}, {@linkplain #COMPONENT}, {@linkplain #SPACE}, {@linkplain #PAGE}, {@linkplain #DESKTOP}, 
 * {@linkplain #SESSION}, {@linkplain #APPLICATION} and by the order.
 * Other scope only represents itself.
 * @author dennis
 * @see ScopeParam
 */
public enum Scope {
	/**
	 * special scope, includes {@linkplain #REQUEST}, {@linkplain #DESKTOP},{@linkplain #SESSION},{@linkplain #APPLICATION} and by the order.
	 */
	DEFAULT("default"),
	/**
	 * special scope, includes  {@linkplain #REQUEST}, {@linkplain #COMPONENT}, {@linkplain #SPACE}, {@linkplain #PAGE}, {@linkplain #DESKTOP}, 
	 * {@linkplain #SESSION}, {@linkplain #APPLICATION} and by the order
	 */
	ALL("all"),
	/**
	 * The implicit requestScope
	 */
	REQUEST("requestScope"),
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
	
	private static final List<Scope> _all;
	static{
		List<Scope> s = new ArrayList<Scope>();
		s.add(REQUEST);
		s.add(COMPONENT);
		s.add(SPACE);
		s.add(PAGE);
		s.add(DESKTOP);
		s.add(SESSION);
		s.add(APPLICATION);
		_all = Collections.unmodifiableList(s);
	}
	
	private static final List<Scope> _default;
	static{
		List<Scope> s = new ArrayList<Scope>();
		s.add(REQUEST);
		s.add(DESKTOP);
		s.add(SESSION);
		s.add(APPLICATION);
		_default = Collections.unmodifiableList(s);
	}
	
	public static List<Scope> getAllScopes(){
		return _all;
	}
	public static List<Scope> getDefaultScopes(){
		return _default;
	}
}
