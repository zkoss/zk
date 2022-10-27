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
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.ui.BuildContext;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.zpr.IAnyGroup;
import org.zkoss.stateless.zpr.IAudio;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.state.IAudioController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

public class AudioComposer implements StatelessComposer<IDiv> {
	IAudioController iAudioController;
	@Override
	public IDiv build(BuildContext<IDiv> ctx) {
		IDiv<IAnyGroup> owner = ctx.getOwner();

		IAudio audio = IAudio.of("audio.wav").withControls(true);
		iAudioController = IAudioController.of(audio);
		return owner.withChildren(
				iAudioController.build(),
				IButton.of("start").withAction(this::play),
				IButton.of("pause").withAction(this::pause),
				IButton.of("stop").withAction(this::stop));
	}

	@Action(type = Events.ON_CLICK)
	public void play() {
		iAudioController.play();
		log();
	}

	@Action(type = Events.ON_CLICK)
	public void pause() {
		iAudioController.pause();
		log();
	}

	@Action(type = Events.ON_CLICK)
	public void stop() {
		iAudioController.stop();
		log();
	}

	private void log() {
		Clients.log("isPlaying: " + iAudioController.isPlaying() +
				", isPaused: " + iAudioController.isPaused() +
				", isStopped: " + iAudioController.isStopped());
	}
}