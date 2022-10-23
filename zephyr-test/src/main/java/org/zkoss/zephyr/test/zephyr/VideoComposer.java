/* VideoComposer.java

	Purpose:

	Description:

	History:
		Tue Nov 23 15:55:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.zephyr;

/**
 * @author katherine
 */
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.ui.BuildContext;
import org.zkoss.zephyr.ui.StatelessComposer;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyrex.state.IVideoController;
import org.zkoss.zephyrex.zpr.IVideo;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

public class VideoComposer implements StatelessComposer<IDiv> {
	IVideoController iVideoController;
	IVideo video = IVideo.of("video.mp4").withControls(true).withId("video");
	@Override
	public IDiv build(BuildContext<IDiv> ctx) {
		IDiv<IAnyGroup> owner = ctx.getOwner();

		iVideoController = IVideoController.of(video);
		return owner.withChildren(
				iVideoController.build(),
				IButton.of("start").withAction(this::play),
				IButton.of("pause").withAction(this::pause),
				IButton.of("stop").withAction(this::stop));
	}

	@Action(type = Events.ON_CLICK)
	public void play() {
		iVideoController.play();
		log();
	}

	@Action(type = Events.ON_CLICK)
	public void pause() {
		iVideoController.pause();
		log();
	}

	@Action(type = Events.ON_CLICK)
	public void stop() {
		iVideoController.stop();
		log();
	}

	private void log() {
		Clients.log("isPlaying: " + iVideoController.isPlaying() +
				", isPaused: " + iVideoController.isPaused() +
				", isStopped: " + iVideoController.isStopped());
	}
}