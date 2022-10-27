/* VideoComposer.java

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
import org.zkoss.statelessex.state.IVideoController;
import org.zkoss.statelessex.sul.IVideo;
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