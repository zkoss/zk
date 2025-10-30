/** B02735Test.java.

	Purpose:
		
	Description:
		
	History:
		3:04:15 PM May 4, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jumperchen
 *
 */
public class B02735Test {
	@Test
	public void testEnumInProxy() {
		
		Pojo2 pojo = new Pojo2();
		pojo.setState(TaskState.NEW);
		Pojo2 proxy = ProxyHelper.createProxyIfAny(pojo);

		//workaround call the setter with the original value first
		//proxy.setState(pojo.getState());
		
		Assertions.assertEquals(TaskState.NEW, proxy.getState());
	}

	public static enum TaskState {
		NEW, ON_HOLD, IN_PROGRESS, DONE; 
	}
	
	public static class Pojo2 {
		private TaskState state;
		public TaskState getState() {
			return state;
		}
		public void setState(TaskState state) {
			this.state = state;
		}
	}
}
