/** ForEachVM.java.

	Purpose:
		
	Description:
		
	History:
		11:41:14 AM Nov 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.clientbind.test.zuti;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jumperchen
 */
public class ChooseVM {
	private int smallNumber = 5;
	private int largeNumber = 20;
	private String direction = "topleft";

	public int getSmallNumber() {
		return smallNumber;
	}

	public void setSmallNumber(int smallNumber) {
		this.smallNumber = smallNumber;
	}

	public int getLargeNumber() {
		return largeNumber;
	}

	public void setLargeNumber(int largeNumber) {
		this.largeNumber = largeNumber;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Command
	@NotifyChange({ "smallNumber", "largeNumber" })
	public void updateNumber(@BindingParam("small") int small,
			@BindingParam("large") int large) {
		this.smallNumber = small;
		this.largeNumber = large;
	}

	@Command
	@NotifyChange("direction")
	public void updateDirection(@BindingParam("direction") String direction) {
		this.direction = direction;
	}

}
