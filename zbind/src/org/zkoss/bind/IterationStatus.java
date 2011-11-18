/* IterationStatus.java

	Purpose:
		
	Description:
		
	History:
		2011/11/14 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

/**
 *  Represents the runtime information of each iteration caused by binding to a collection 
 * @author dennis
 *
 */
public interface IterationStatus {
//TODO
//	public int getBegin();
//	public int getEnd();
//	public IterationStatus getPrevious();
	/**
	 * get current index of the iteration
	 * @return current index of the iteration
	 */
	public int getIndex();
	

	
}
