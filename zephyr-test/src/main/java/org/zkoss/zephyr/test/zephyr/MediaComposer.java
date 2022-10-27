/* VideoComposer.java

	Purpose:

	Description:

	History:
		Mon Nov 22 15:47:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.zephyr;

import static org.zkoss.zkmax.ui.event.Events.ON_SNAPSHOT_UPLOAD;
import static org.zkoss.zkmax.ui.event.Events.ON_VIDEO_UPLOAD;

import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.video.Video;
import org.zkoss.stateless.action.data.FileData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.ui.BuildContext;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.zpr.IAnyGroup;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.IImage;
import org.zkoss.statelessex.state.ICameraController;
import org.zkoss.statelessex.state.IVideoController;
import org.zkoss.statelessex.zpr.ICamera;
import org.zkoss.statelessex.zpr.IVideo;
import org.zkoss.zk.ui.event.Events;

public class MediaComposer implements StatelessComposer<IDiv> {
	IVideoController iVideoController;
	ICameraController iCameraController;
	IImage image = IImage.ofId("image");
	IVideo video = IVideo.of("test.mp4").withControls(true).withId("video");
	Media media = null;
	@Override
	public IDiv build(BuildContext<IDiv> ctx) {
		IDiv<IAnyGroup> owner = ctx.getOwner();

		ICamera camera = ICamera.ofSize("200px", "200px")
				.withActions(ActionHandler.of(this::doSnapshot), ActionHandler.of(this::doVideoUpload));

		iVideoController = IVideoController.of(video);
		iCameraController = ICameraController.of(camera);
		return owner.withChildren(iVideoController.build(),
				iCameraController.build(),
				IButton.of("start recording").withAction(this::startRecording),
				IButton.of("stop recording").withAction(this::stopRecording),
				IButton.of("loading to video").withAction(this::load),
				IButton.of("snapshot").withAction(this::snapshot),
				image);
	}

	@Action(type = Events.ON_CLICK)
	public void startRecording() {
		iCameraController.requestCamera();
		iCameraController.start();
	}

	@Action(type = Events.ON_CLICK)
	public void stopRecording() {
		iCameraController.stop();
	}

	@Action(type = Events.ON_CLICK)
	public void snapshot() {
		iCameraController.snapshot();
	}

	@Action(type = ON_SNAPSHOT_UPLOAD)
	public void doSnapshot(FileData data) {
		UiAgent.getCurrent().replaceWith(Locator.ofId("image"), image.withContent((AImage) data.getMedia()));
	}

	@Action(type = ON_VIDEO_UPLOAD)
	public void doVideoUpload(FileData data) {
		media = data.getMedia();
	}

	@Action(type = Events.ON_CLICK)
	public void load() {
		iVideoController.build().withContent((Video) media);
	}
}
