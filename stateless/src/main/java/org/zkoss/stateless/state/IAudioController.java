/* IAudio.java

	Purpose:

	Description:

	History:
		Mon Nov 23 15:36:02 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.state;

import java.util.Objects;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.UiAgentCtrl;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.util.Oid;
import org.zkoss.stateless.zpr.IAudio;
import org.zkoss.stateless.action.data.StateChangeData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Audio;

/**
 * An {@link IAudio} controller to control play, stop, pause, and some status with
 * the given audio instance.
 *
 * <p>Note: this class is not thread-safe, so when it's used in multi-threading
 * environment, the developer should handle the threading issue.</p>
 *
 * @author katherine
 */
public class IAudioController {
	private IAudio _owner;
	final private Locator _locator;
	public static final int STOP = Audio.STOP;
	public static final int PLAY = Audio.PLAY;
	public static final int PAUSE = Audio.PAUSE;
	public static final int END = Audio.END;
	private int _currentState = STOP;

	private IAudioController(IAudio owner) {
		Objects.requireNonNull(owner);
		IAudio.Builder builder = new IAudio.Builder().from(owner);

		if (Strings.isEmpty(owner.getId())) {
			builder.setId(Oid.generate(owner));
		} else {
			builder.setId(owner.getId());
		}

		// avoid to use setActions() or setAction() to override the original action handlers.
		_owner = builder.addActions(ActionHandler.of(this::doStateChange)).build();
		_locator = Locator.of(_owner);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Action(type = Events.ON_STATE_CHANGE)
	public void doStateChange(StateChangeData data) {
		_currentState = data.getState();
	}

	/** Plays the audio at the client.
	 */
	public void play() {
		if (_currentState != PLAY) {
			UiAgentCtrl.response("ctrl", new AuInvoke(_locator.toComponent(), "play"));
		}
	}

	/** Stops the audio at the client.
	 */
	public void stop() {
		if (_currentState != STOP) {
			UiAgentCtrl.response("ctrl", new AuInvoke(_locator.toComponent(), "stop"));
		}
	}

	/** Pauses the audio at the client.
	 */
	public void pause() {
		if (_currentState != PAUSE) {
			UiAgentCtrl.response("ctrl", new AuInvoke(_locator.toComponent(), "pause"));
		}
	}

	/**
	 * Returns the controller instance with the given {@link IAudio audio}
	 * @param owner The controller to control with
	 */
	public static IAudioController of(IAudio owner) {
		return new IAudioController(owner);
	}

	/**
	 * Returns the immutable audio instance that the controller to build with.
	 */
	public IAudio build() {
		return _owner;
	}

	/**
	 * Return whether the audio is playing.
	 *
	 * @return true if audio is playing;
	 */
	public boolean isPlaying() {
		return _currentState == PLAY;
	}

	/**
	 * Return whether the audio is paused.
	 *
	 * @return true if audio is paused;
	 */
	public boolean isPaused() {
		return _currentState == PAUSE;
	}

	/**
	 * Return whether the audio is stopped.
	 * While the audio is ended, also means it is stopped.
	 *
	 * @return true if audio is stopped or ended;
	 */
	public boolean isStopped() {
		return _currentState == STOP || _currentState == END;
	}

	/**
	 * Return whether the audio is ended.
	 *
	 * @return true if audio is ended;
	 */
	public boolean isEnded() {
		return _currentState == END;
	}
}
