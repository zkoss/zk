/* CameraComposer.java

	Purpose:

	Description:

	History:
		Tue Nov 23 15:55:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.composer;

/**
 * @author katherine
 */
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.ui.BuildContext;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.sul.IAnyGroup;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.statelessex.state.ICameraController;
import org.zkoss.statelessex.sul.ICamera;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

public class CameraComposer implements StatelessComposer<IDiv> {
	ICameraController iCameraController;
	ICamera Camera = ICamera.ofId("camera");
	@Override
	public IDiv build(BuildContext<IDiv> ctx) {
		IDiv<IAnyGroup> owner = ctx.getOwner();

		iCameraController = ICameraController.of(Camera);
		return owner.withChildren(
				iCameraController.build(),
				IButton.of("start").withAction(this::play),
				IButton.of("pause").withAction(this::pause),
				IButton.of("stop").withAction(this::stop));
	}

	@Action(type = Events.ON_CLICK)
	public void play() {
		iCameraController.requestCamera();
		iCameraController.start();
		log();
	}

	@Action(type = Events.ON_CLICK)
	public void pause() {
		iCameraController.pause();
		log();
	}

	@Action(type = Events.ON_CLICK)
	public void stop() {
		iCameraController.stop();
		iCameraController.stopStreaming();
		log();
	}

	private void log() {
		Clients.log("isPlaying: " + iCameraController.isRecording() +
				", isPaused: " + iCameraController.isPaused() +
				", isStopped: " + iCameraController.isStopped());
	}
}