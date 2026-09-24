/* F110_ZK_6084_RangesliderTest.java

		Purpose:

		Description:

		History:
				Tue Aug 25 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 2.5.2: pressing a slider track moves no handle and notifies nothing; the nearest
 * handle jumps to the pointer on release.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_RangesliderTest extends WebDriverTestCase {
	private static final String RS_TRACK = "$rs .z-rangeslider-track";
	private static final String MS_TRACK = "$ms .z-multislider-track";
	private static final String RS_BUTTONS = "$rs .z-sliderbuttons-button";
	/** Offsets from the track centre; both are bare track, clear of the handles and marks. */
	private static final int PRESS_X = 45, RELEASE_DX = 25;
	/** A whole log of exactly one change, moving the end handle to the release position. */
	private static final String ONE_CHANGE_ON_RELEASE = "\\[10,7[2-9]\\]";

	private String changeLog(String id) {
		return jq("$" + id).text();
	}

	private String handleLefts() {
		return jq(RS_BUTTONS).first().positionLeft() + "," + jq(RS_BUTTONS).last().positionLeft();
	}

	private void pressTrack(String track) {
		getActions().moveToElement(toElement(jq(track)), PRESS_X, 0)
				.clickAndHold().pause(Duration.ofMillis(300)).perform();
	}

	private void moveAndRelease(String id) {
		getActions().moveByOffset(RELEASE_DX, 0).release().perform();
		waitResponse();
		assertTrue(changeLog(id).matches(ONE_CHANGE_ON_RELEASE),
				"change log was: " + changeLog(id));
	}

	@Test
	public void rangesliderInertOnPressThenJumpsOnceOnRelease() {
		connect();
		waitResponse();
		String handles = handleLefts();

		pressTrack(RS_TRACK);
		assertEquals("", changeLog("rslog"), "nothing may reach the server on the down-event");
		assertEquals(handles, handleLefts(), "no handle may move on the down-event");

		moveAndRelease("rslog");
	}

	@Test
	public void multisliderInertOnPressThenJumpsOnceOnRelease() {
		connect();
		waitResponse();

		pressTrack(MS_TRACK);
		assertEquals("", changeLog("mslog"), "nothing may reach the server on the down-event");

		moveAndRelease("mslog");
	}

	@Test
	public void releaseOutsideDoesNotChange() {
		connect();
		waitResponse();

		getActions().moveToElement(toElement(jq(RS_TRACK)), PRESS_X, 0)
				.clickAndHold().moveByOffset(0, 200).release().perform();
		waitResponse();

		assertEquals("", changeLog("rslog"), "releasing away from the component aborts the gesture");
	}
}
