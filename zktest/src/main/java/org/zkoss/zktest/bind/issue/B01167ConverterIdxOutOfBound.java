/* B01167ConverterIdxOutOfBound.java

	Purpose:
		
	Description:
		
	History:
		Jun 14, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class B01167ConverterIdxOutOfBound {
	private String text= "hello";
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void someMethod(String... vargs){
		
	}
	
}
