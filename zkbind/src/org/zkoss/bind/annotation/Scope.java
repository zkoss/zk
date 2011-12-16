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
 * the implicit scopes of zk for {@link ScopeParam}
 * @author dennis
 * @see ScopeParam
 */
public enum Scope {
	ALL("*"),
	REQUEST("requestScope"),
	COMPONENT("componentScope"),
	SPACE("spaceScope"),
	PAGE("pageScope"),
	DESKTOP("desktopScope"),
	SESSION("sessionScope"),
	APPLICATION("applicationScope");
	
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
	
	private static final List<Scope> _sequence;
	static{
		List<Scope> s = new ArrayList<Scope>();
		s.add(REQUEST);
		s.add(COMPONENT);
		s.add(SPACE);
		s.add(PAGE);
		s.add(DESKTOP);
		s.add(SESSION);
		s.add(APPLICATION);
		_sequence = Collections.unmodifiableList(s);
	}
	
	public static List<Scope> getScopeSequence(){
		return _sequence;
	}
}
