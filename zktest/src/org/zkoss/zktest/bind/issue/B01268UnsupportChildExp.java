/* B01268UnsupportChildExp.java

	Purpose:
		
	Description:
		
	History:
		Aug 27, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class B01268UnsupportChildExp {
	List<String> strings;
	public B01268UnsupportChildExp () {
		strings = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			strings.add("row " + i);
		}
	}
	
	public List<String> getStrings() {
		return strings;
	}
}
