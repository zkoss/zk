/* Greeting.java

	Purpose:
		
	Description:
		
	History:
		Jul 12, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
//@RequestScoped
public class Greeting implements Serializable{
	private static final long serialVersionUID = -5058722721630286971L;

	public String greet(String name){
		return "Hello, "+name+". bean: "+this.getClass()+System.identityHashCode(this);
	}
	@PostConstruct
	public void init(){
		System.out.println(">>>>>>>>>> @PostConstruct:"+this);
	}
	@PreDestroy
	public void dispose(){
		System.out.println(">>>>>>>>>> @PreDestroy:"+this);
	}
}
