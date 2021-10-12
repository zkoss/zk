/* F86_ZK_4092Composer_2.java

        Purpose:
                
        Description:
                
        History:
                Fri Oct 26 11:52:45 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Barcodescanner;
import org.zkoss.zul.Vlayout;

public class F86_ZK_4092Composer_2 extends SelectorComposer<Vlayout> {
	@Wire
	private Barcodescanner barcodescanner;

	@Listen("onClick = #invalidate")
	public void invalidate() {
		barcodescanner.invalidate();
	}

	@Listen("onClick = #setConstraintsString")
	public void setConstraintsString() {
		barcodescanner.setConstraintsString("{\"video\": {\"aspectRatio\": 1}}");
	}

	@Listen("onClick = #getConstraints")
	public void getConstraints() {
		Map constraints = barcodescanner.getConstraints();
		Clients.log(constraints == null ? "null" : constraints.toString());
	}

	@Listen("onClick = #setConstraints")
	public void setConstraints() {
		HashMap constraints = new HashMap();
		HashMap videoConstraints = new HashMap();
		videoConstraints.put("aspectRatio", 16.0 / 9);
		constraints.put("video", videoConstraints);
		barcodescanner.setConstraints(constraints);
	}

	@Listen("onClick = #facingModeEnvironment")
	public void facingModeEnvironment() {
		barcodescanner.setConstraintsString("{\"video\": {\"facingMode\": {\"exact\": \"environment\"}}}");
	}

	@Listen("onClick = #facingModeUser")
	public void facingModeUser() {
		HashMap constraints = new HashMap();
		HashMap videoConstraints = new HashMap();
		HashMap facingModeConstraints = new HashMap();
		facingModeConstraints.put("exact", "user");
		videoConstraints.put("facingMode", facingModeConstraints);
		constraints.put("video", videoConstraints);
		barcodescanner.setConstraints(constraints);
	}
}
