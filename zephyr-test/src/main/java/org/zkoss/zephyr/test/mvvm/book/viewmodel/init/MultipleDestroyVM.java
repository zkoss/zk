/* MultipleDestroyVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 16:01:30 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.mvvm.book.viewmodel.init;

import org.zkoss.bind.annotation.Destroy;

/**
 * @author rudyhuang
 */
public class MultipleDestroyVM {
	@Destroy
	public void destroy1() {
		System.out.println("MultipleDestroyVM.destroy1 was called");
	}

	@Destroy
	public void destroy2() {
		System.out.println("MultipleDestroyVM.destroy2 was called");
	}
}
