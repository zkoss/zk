/* ITrackTest.java

	Purpose:

	Description:

	History:
		4:09 PM 2022/3/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Audio;
import org.zkoss.zul.Track;

/**
 * Test for {@link ITrack}
 * @author jumperchen
 */
public class ITrackTest extends StatelessTestBase {
	@Test
	public void withAudio() {
		// check Richlet API case
		assertEquals(richlet(() -> IAudio.of("/media/guitar.wav").withChildren(
				ITrack.ofKind(ITrack.Kind.CAPTIONS).withSrc("music_lyric.vtt").withSrclang("en").withAsDefault(true),
				ITrack.ofKind(ITrack.Kind.SUBTITLES).withSrc("music_lyric_fr.vtt").withSrclang("fr"),
				ITrack.ofKind(ITrack.Kind.SUBTITLES).withSrc("music_lyric_de.vtt").withSrclang("de")
		)), zul(ITrackTest::newAudio));

		// check Stateless file case
		assertEquals(composer(ITrackTest::newAudio), zul(ITrackTest::newAudio));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Audio("/media/guitar2.wav"), (IAudio iAudio) -> {
					return iAudio.withSrc("/media/guitar.wav").withChildren(
							ITrack.ofKind(ITrack.Kind.CAPTIONS).withSrc("music_lyric.vtt").withSrclang("en").withAsDefault(true),
							ITrack.ofKind(ITrack.Kind.SUBTITLES).withSrc("music_lyric_fr.vtt").withSrclang("fr"),
							ITrack.ofKind(ITrack.Kind.SUBTITLES).withSrc("music_lyric_de.vtt").withSrclang("de")
					);
				}),
				zul(ITrackTest::newAudio));
	}

	private static Audio newAudio() {
		Audio audio = new Audio("/media/guitar.wav");
		Track en = new Track();
		en.setDefault(true);
		en.setKind("captions");
		en.setSrc("music_lyric.vtt");
		en.setSrclang("en");
		audio.appendChild(en);
		Track fr = new Track();
		fr.setKind("subtitles");
		fr.setSrc("music_lyric_fr.vtt");
		fr.setSrclang("fr");
		audio.appendChild(fr);
		Track de = new Track();
		de.setKind("subtitles");
		de.setSrc("music_lyric_de.vtt");
		de.setSrclang("de");
		audio.appendChild(de);
		return audio;
	}
}
