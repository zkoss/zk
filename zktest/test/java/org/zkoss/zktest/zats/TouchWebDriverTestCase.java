/* TouchWebDriverTestCase.java

	Purpose:

	Description:

	History:
		1:30 PM 2021/12/30, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * A base class to test using a Touch Simulator to run a remote Docker WebDriver.
 * @author jumperchen
 */
public abstract class TouchWebDriverTestCase extends DockerWebDriverTestCase {

	@Override
	protected final ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	protected PointerInput FINGER = new PointerInput(
			PointerInput.Kind.MOUSE, "finger");

	public void swipe(Point start, Point end) {
		swipe(start, end, 0);
	}
	public void swipe(Point start, Point end, int duration) {
		Sequence swipe = new Sequence(FINGER, 1)
				.addAction(FINGER.createPointerMove(ofMillis(0), viewport(), start.getX(), start.getY()))
				.addAction(FINGER.createPointerDown(LEFT.asArg()))
				.addAction(FINGER.createPointerMove(ofMillis(duration), viewport(), end.getX(), end.getY()))
				.addAction(FINGER.createPointerUp(LEFT.asArg()));
		((RemoteWebDriver) driver).perform(Collections.singleton(swipe));
	}

	public void swipe(WebElement start, WebElement end) {
		swipe(start, end);
	}
	public void swipe(WebElement start, WebElement end, int duration) {
		swipe(start.getLocation(), end.getLocation(), duration);
	}
	public void tap(Point point) {
		tap(point, 0);
	}

	public void tap(Point point, int duration) {
		Sequence tap = new Sequence(FINGER, 1)
				.addAction(FINGER.createPointerMove(ofMillis(0), viewport(), point.getX(), point.getY()))
				.addAction(FINGER.createPointerDown(LEFT.asArg()))
				.addAction(new Pause(FINGER, ofMillis(duration)))
				.addAction(FINGER.createPointerUp(LEFT.asArg()));
		((RemoteWebDriver) driver).perform(Collections.singleton(tap));
	}

	private void tap0(WebElement target, int duration) {
		Dimension size = target.getSize();
		Point location = target.getLocation();
		tap(new Point(location.getX() + (size.getWidth() / 2), location.getY() + (size.getHeight() / 2)));
	}

	public void tap(WebElement start, int duration) {
		tap0(start, duration);
	}

	public void tap(WebElement start) {
		tap0(start, 0);
	}

	public void scrollTo(int startX, int startY, int endX, int endY) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence scrollAction = new Sequence(finger, 0);
		scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
		scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), endX, endY));
		// repeat last action as if to make the finger stay in place for 500 ms
		scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), endX, endY));
		scrollAction.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		((RemoteWebDriver) driver).perform(Collections.singleton(scrollAction));
	}

	public void scroll(WebElement target, int startY, int endY, int delta) {
		Rectangle rect = target.getRect();
		int length = Math.abs(startY - endY);
		int times = (int) Math.ceil(length / Math.max(1, rect.getHeight()));
		if (startY >= endY) { // move up
			while (times-- > 0) {
				scrollTo(rect.getX() + delta, rect.getY() + delta,
						rect.getX() + delta, rect.getHeight() - delta);
			}
		} else { // move down
			while (times-- > 0) {
				scrollTo(rect.getX() + delta, rect.getHeight() - delta,
						rect.getX() + delta, rect.getY() + delta);
			}
		}
	}
	public void scroll(WebElement target, int startY, int endY) {
		scroll(target, startY, endY, 20);
	}

}