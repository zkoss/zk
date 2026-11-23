/** ManipulateInsertionVM.java.

	Purpose:
		
	Description:
		
	History:
		4:54:11 PM Nov 20, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jumperchen
 */
public class ManipulateInsertionVM {
	private boolean rootVisible = true, visible = true;

	public void setRootVisible(boolean visible) {
		this.rootVisible = visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isVisible() {
		return visible;
	}
	public boolean isRootVisible() {
		return rootVisible;
	}
	
	@NotifyChange("visible")
	@Command
	public void changeVisible() {
		visible = !visible;
	}
}
