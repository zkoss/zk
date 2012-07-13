/* TestBean.java

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

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
@Named
@Dependent
public class Hello implements Serializable{
	private static final long serialVersionUID = 2251108109482968801L;
	@Inject
	Greeting greeting;
	
	
	public String sayHello(String yourName){
		return greeting.greet(yourName);
	}
	
	
	@PreDestroy
	public void dispose(){
		System.out.println(">>>>>>>>>> Hello disposed");
	}
	
}
