/* F86_ZK_4092Composer.java

        Purpose:
                
        Description:
                
        History:
                Wed Oct 24 14:37:34 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Camera;
import org.zkoss.zul.Vlayout;

public class F86_ZK_4092Composer_1 extends SelectorComposer<Vlayout> {
	@Wire
	private Camera camera;

	@Listen("onClick = #requestCamera")
	public void requestCamera() {
		camera.requestCamera();
	}

	@Listen("onClick = #setConstraintsString")
	public void setConstraintsString() {
		camera.setConstraintsString("{\"video\": {\"aspectRatio\": 1}}");
	}

	@Listen("onClick = #getConstraints")
	public void getConstraints() {
		Map constraints = camera.getConstraints();
		Clients.log(constraints == null ? "null" : constraints.toString());
	}

	@Listen("onClick = #setConstraints")
	public void setConstraints() {
		HashMap constraints = new HashMap();
		HashMap videoConstraints = new HashMap();
		videoConstraints.put("aspectRatio", 16.0 / 9);
		constraints.put("video", videoConstraints);
		camera.setConstraints(constraints);
	}

	@Listen("onClick = #facingModeEnvironment")
	public void facingModeEnvironment() {
		camera.setConstraintsString("{\"video\": {\"facingMode\": {\"exact\": \"environment\"}}}");
	}

	@Listen("onClick = #facingModeUser")
	public void facingModeUser() {
		HashMap constraints = new HashMap();
		HashMap videoConstraints = new HashMap();
		HashMap facingModeConstraints = new HashMap();
		facingModeConstraints.put("exact", "user");
		videoConstraints.put("facingMode", facingModeConstraints);
		constraints.put("video", videoConstraints);
		camera.setConstraints(constraints);
	}
}