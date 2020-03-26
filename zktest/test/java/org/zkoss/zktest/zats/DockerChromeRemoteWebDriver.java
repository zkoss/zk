/* DockerChromeRemoteWebDriver.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 11:11:20 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.remote.RemoteTouchScreen;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * A RemoteWebDriver.
 * It's a workaround that Chrome on Linux won't accept argument {@code --lang},
 * only environment variable {@code $LANGUAGE} is accepted.
 *
 * @see <a href="https://github.com/elgalu/docker-selenium/issues/159">Support the google-chrome --lang argument</a>
 * @see <a href="https://stackoverflow.com/q/60445154">Chrome80 & WDIO : Chrome option --lang not working on ubuntu</a>
 * @see <a href="https://crbug.com/1010288">Chrome is not supporting --lang parameter with language that contains a '-' while using headless mode</a>
 * @author rudyhuang
 */
public class DockerChromeRemoteWebDriver extends RemoteWebDriver implements HasTouchScreen {
	public DockerChromeRemoteWebDriver(URL remoteAddress, Capabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	public DockerChromeRemoteWebDriver(Capabilities capabilities) {
		this(toURL("http://sechrome.test:4444/wd/hub"), capabilities);
	}

	private static URL toURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public TouchScreen getTouch() {
		return new RemoteTouchScreen(getExecuteMethod());
	}
}
