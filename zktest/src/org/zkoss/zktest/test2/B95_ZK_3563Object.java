/* B95_ZK_3563Object.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 27 12:46:50 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;

/**
 * @author jameschu
 */
public class B95_ZK_3563Object implements Serializable {
	private int counter1;
	private int counter2;
	private int counter3;

	public int getCounter1() {
		return counter1++;
	}

	public int getCounter2() {
		return counter2++;
	}

	public int getCounter3() {
		return counter3++;
	}
}
