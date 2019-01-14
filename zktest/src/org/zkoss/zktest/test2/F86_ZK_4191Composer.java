/* F86_ZK_4191Composer.java

		Purpose:
                
		Description:
                
		History:
				Mon Jan 14 16:24:49 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.DOMExceptionEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Camera;

public class F86_ZK_4191Composer extends SelectorComposer {
	@Wire
	private Camera camera;

	@Listen("onClick = #button")
	public void requestCamera() {
		camera.requestCamera();
	}

	@Listen("onCameraUnavailable = #camera")
	public void onCameraUnavailable(DOMExceptionEvent event) {
		Clients.log("error name: " + event.getErrorName());
		Clients.log("error message: " + event.getErrorMessage());
	}
}
