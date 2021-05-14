/* FizzBuzzVM.java

	Purpose:
		
	Description:
		
	History:
		Fri May 07 14:34:56 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.shadow;

import java.util.stream.IntStream;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class FizzBuzzVM {
	private int[] numbers;

	public int[] getNumbers() {
		return numbers;
	}

	@Command
	@NotifyChange("numbers")
	public void useSmall() {
		numbers = IntStream.rangeClosed(1, 20).toArray();
	}

	@Command
	@NotifyChange("numbers")
	public void useLarge() {
		numbers = IntStream.rangeClosed(50, 100).toArray();
	}
}
