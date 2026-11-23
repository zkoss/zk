package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jameschu
 */
public class B90_ZK_4227VM {
	private boolean token = true;
	public boolean getBtnVisible() {
		return token;
	}
	@Command
	@NotifyChange("btnVisible")
	public void changeBtnVisible() {
		token = !token;
	}
}
