/* Generator.java

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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */

@ApplicationScoped
public class Generator {
	private final java.util.Random rand = new java.util.Random();
	
	private int maxNumber = 100;
	
	
	java.util.Random getRandom(){
		return rand;
	}
	
	
	@Produces @Random
	public int next(){
		return getRandom().nextInt(maxNumber);
	}
	
	@Produces @MaxNumber
	public int getMaxNumber(){
		return maxNumber;
	}
	
	
}
