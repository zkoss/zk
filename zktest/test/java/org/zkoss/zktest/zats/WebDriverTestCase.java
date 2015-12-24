/* WebDriverTestCase.java

	Purpose:
		
	Description:
		
	History:
		2:01 PM 12/17/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetSocketAddress;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zktest.zats.ztl.ClientWidget;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;
import org.zkoss.zktest.zats.ztl.ZK;

/**
 * A base class to test using WebDriver.
 * <p>Currently support HtmlUnit only</p>
 * @author jumperchen
 */
public abstract class WebDriverTestCase {

	private static final Logger log = LoggerFactory.getLogger(WebDriverTestCase.class);
	private static Server server;
	private static int port;
	private static final String PACKAGE = WebDriverTestCase.class.getPackage().getName();
	private static ThreadLocal<WebDriver> _local = new ThreadLocal<WebDriver>();
	protected WebDriver driver;
	protected static int getPort() {
		return port;
	}
	protected static String getContextPath() {
		return "/zktest";
	}
	protected static String getHost() {
		return "127.0.0.1";
	}
	protected static String getAddress() {
		return "http://" + getHost() + ":" + getPort() + getContextPath();
	}

	protected WebDriver getWebDriver() {
		if (driver == null) {
			driver = new ZKWebDriver(true);
		}
		return driver;
	}

	public static class ZKWebDriver extends HtmlUnitDriver {
		public ZKWebDriver() {
		}

		public ZKWebDriver(boolean enableJavascript) {
			super(enableJavascript);
		}

		public ZKWebDriver(BrowserVersion version, boolean enableJavascript) {
			super(version, enableJavascript);
		}

		public ZKWebDriver(BrowserVersion version) {
			super(version);
		}

		public ZKWebDriver(Capabilities capabilities) {
			super(capabilities);
		}

		public ZKWebDriver(Capabilities desiredCapabilities,
				Capabilities requiredCapabilities) {
			super(desiredCapabilities, requiredCapabilities);
		}

		public Page lastPage() {
			return super.lastPage();
		}

	}
	public WebDriver connect() {
		return connect("");
	}
	protected String getTestURL(String file) {
		String simple = this.getClass().getSimpleName();
		String name = this.getClass().getName().replace(PACKAGE, "").replace(".","/").replace(simple, "");

		return name + file;
	}
	protected String getFileLocation() {
		String className = this.getClass().getName().replace(PACKAGE, "").replace(".","/");
		int lastTest = className.lastIndexOf("Test");
		return className.substring(0, lastTest) + ".zul";
	}
	public WebDriver connect(String location) {
		ZKWebDriver webDriver = (ZKWebDriver)getWebDriver();
		if (location == null || location.length() <= 0) {
			location = getFileLocation();
			webDriver.get(getAddress() + location);
			int errCode = webDriver.lastPage().getWebResponse().getStatusCode();
			if (errCode == 404) {
				webDriver.get(getAddress() + location.replace("_", "-"));
				errCode = webDriver.lastPage().getWebResponse().getStatusCode();
				if (errCode == 404) {
					webDriver.get(getAddress() + location.replace("-", "_"));
					errCode = webDriver.lastPage().getWebResponse().getStatusCode();
				}
				if (errCode == 404) {
					fail("Error Code: " + errCode + ", from URL[" + webDriver.lastPage().getUrl() + "]");
				}
			}
		} else {
			webDriver.get(getAddress() + location);
			int errCode = webDriver.lastPage().getWebResponse().getStatusCode();
			if (errCode != 200) {
				fail("Error Code: " + errCode + ", from URL[" + webDriver.lastPage().getUrl() + "]");
			}
		}
		_local.set(webDriver);
		return webDriver;
	}

	protected int getTimeout() {
		return 4000;
	}


	@After
	public void stop() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}
	@BeforeClass
	public static void init() throws Exception {
		server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		server.setHandler(context);
		server.start();

		for (Connector c : server.getConnectors()) {
			if (c instanceof NetworkConnector) {
				if (((NetworkConnector)c).getLocalPort() > 0) {
					port = ((NetworkConnector)c).getLocalPort();
					break;
				}
			}
		}
	}

	@AfterClass
	public static void end() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

	/**
	 * Waits for Ajax response. (excluding animation check)
	 * <p>By default the timeout time is specified in config.properties
	 * @see #waitResponse(int)
	 */
	protected void waitResponse() {
		waitResponse(getTimeout());
	}

	/**
	 * Waits for Ajax response.
	 * <p>By default the timeout time is specified in config.properties
	 * @param includingAnimation if true, it will include animation check.
	 * @see #waitResponse(int, boolean)
	 */
	protected void waitResponse(boolean includingAnimation) {
		waitResponse(getTimeout(), includingAnimation);
	}

	/**
	 * Returns the wait response speed.
	 * <p>Default: 300ms</p>
	 * @return
	 */
	protected int getSpeed() {
		return 300;
	}

	/**
	 * Waits for Ajax response according to the timeout attribute.
	 * @param timeout
	 * @param includingAnimation if true, it will include animation check.
	 *
	 */
	protected void waitResponse(int timeout, boolean includingAnimation) {
		long s = System.currentTimeMillis();
		int i = 0;
		int ms = getSpeed();

		String scripts = includingAnimation ? "!!zAu.processing() || !!jq.timers.length"
				: "!!zAu.processing()";

		sleep(ms/2); // take a break first.

		while (i < 2) { // make sure the command is triggered.
			while(Boolean.valueOf(this.getEval(scripts))) {
				if (System.currentTimeMillis() - s > timeout) {
					assertTrue("Test case timeout!", false);
					break;
				}
				i = 0;//reset
				sleep(ms);
			}
			i++;
			sleep(includingAnimation ? ms*2 : ms);
		}
	}
	public static String getEval(String script) {
		WebDriver driver = _local.get();
		return String.valueOf(((JavascriptExecutor) driver).executeScript("return ("+ script+")"));
	}
	public static void eval(String script) {
		WebDriver driver = _local.get();
		((JavascriptExecutor) driver).executeScript("("+ script+")");
	}

	/**
	 * Causes the currently executing thread to sleep for the specified number
	 * of milliseconds, subject to the precision and accuracy of system timers
	 * and schedulers. The thread does not lose ownership of any monitors.
	 * @param millis the length of time to sleep in milliseconds.
	 */
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Waits for Ajax response according to the timeout attribute.(excluding animation check)
	 * @param timeout the time. (millisecond).
	 * @see #waitResponse(int, boolean)
	 */
	protected void waitResponse(int timeout) {
		waitResponse(timeout, false);
	}

	/**
	 * Returns the Widget object of the UUID.
	 * @param uuid the element ID.
	 */
	protected Widget widget(String uuid) {
		return new Widget(uuid);
	}

	/**
	 * Returns the Widget object of the given element.
	 * @param element the element.
	 */
	protected Widget widget(Element element) {
		return new Widget(element);
	}

	/**
	 * Returns the Widget object from the JQuery object.
	 * @param jQuery the JQuery object.
	 */
	protected Widget widget(JQuery jQuery) {
		return new Widget(jQuery);
	}

	/**
	 * Returns the Jquery object of the selector
	 * <p> Default: without "#" sign
	 * @param selector the selector
	 */
	protected JQuery jq(String selector) {
		return new JQuery(selector);
	}

	/**
	 * Returns the Jquery object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected JQuery jq(ClientWidget el) {
		if (el instanceof JQuery)
			return (JQuery) el;
		return new JQuery(el);
	}

	/**
	 * Returns the ZK object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected ZK zk(ClientWidget el) {
		if (el instanceof ZK)
			return (ZK) el;
		return new ZK(el);
	}

	/**
	 * Returns the ZK object of the selector
	 * @param selector the selector of the element
	 */
	protected ZK zk(String selector) {
		return new ZK(selector);
	}
	/**
	 * Returns the int value from the given string number.
	 * @param number the string number, if null or empty, 0 is assumed.
	 */
	public static int parseInt(String number) {
		if (number != null) {
			number = number.replaceAll("[^-0-9\\.]", "");
			int decimal = number.indexOf('.');
			if (decimal > 0)
				number = number.substring(0, decimal);

			if(!"".equals(number.trim())){
				return Integer.parseInt(number);
			}else{
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Execute all handlers and behaviors attached to the matched elements for the given event type
	 * @param widget
	 * @param event
	 */
	protected void trigger(ClientWidget widget, String event) {
		((JavascriptExecutor) driver).executeScript(jq(widget).toLocator() + ".trigger('"+ event + "')");
	}

	/**
	 * Waits for an element to be visible.
	 * @param locator the element where it waits for.
	 * @param timeoutInSeconds the timeout in seconds.
	 */
	protected void waitFor(ClientWidget locator, int timeoutInSeconds) {
		WebDriverWait wait = new WebDriverWait(getWebDriver(),
				timeoutInSeconds);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public static WebElement toElement(ClientWidget locator) {
		WebDriver webDriver = _local.get();
		if (locator instanceof Widget)
			return (WebElement) ((JavascriptExecutor)webDriver).executeScript("return (" + locator + ").$n();");
		else if (locator instanceof JQuery)
			return (WebElement) ((JavascriptExecutor)webDriver).executeScript("return (" + locator + ")[0];");
		return (WebElement) ((JavascriptExecutor)webDriver).executeScript("return (" + locator + ");");
	}

	/**
	 * Trims the multiline string into one line string.
	 */
	public static String trim(String text) {
		return text.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2").replace("\n", "").replace("\r", "");
	}
	/**
	 * Returns the text of zk.log from client side.
	 */
	protected String getZKLog() {
		return getWebDriver().findElement(By.id("zk_log")).getText();
	}

	/**
	 * Closes the zk.log console and removes it.
	 */
	protected void closeZKLog() {
		jq("#zk_logbox").remove();
	}

	// browser operation

	/**
	 * Sets the focus state to the given locator.
	 * @param locator
	 */
	protected void focus(ClientWidget locator) {
		eval(jq(locator).toLocator()+ ".focus()");
	}

	/**
	 * Sets the blur state to the given locator.
	 * @param locator
	 */
	protected void blur(ClientWidget locator) {
		eval(jq(locator).toLocator()+ ".blur()");
	}

	/**
	 * Clicks upon the given locator.
	 * @param locator
	 */
	protected void click(ClientWidget locator) {
		toElement(locator).click();
	}

	/**
	 * Checks the given locator. It's the same as {@link #click(ClientWidget)} internally.
	 * @param locator
	 */
	protected void check(ClientWidget locator) {
		click(locator);
	}

	/**
	 * Types the text into the given locator.
	 * <p>By default, it will simulate a real user behavior to focus the input elemnt
	 * from the given locator,
	 * and then replace the old text with the new text and then blur the input element.</p>
	 * @param locator
	 * @param text
	 */
	protected void type(ClientWidget locator, String text) {
		focus(locator);
		WebElement webElement = toElement(locator);
		webElement.clear();
		webElement.sendKeys(text);
		blur(locator);
	}

	/**
	 * Use this method to simulate typing into an element, which may set its value.
	 * @param keysToSend character sequence to send to the element
	 */
	protected void sendKeys(ClientWidget locator, CharSequence keysToSend) {
		getWebDriver().findElement(locator).sendKeys(keysToSend);
	}

	/**
	 * Selects an comboitem from the given combobox.
	 * @param combobox
	 * @param index
	 */
	protected Widget selectComboitem(Widget combobox, int index) {
		click(combobox.$n("btn"));
		waitResponse();
		Element element = jq(combobox.$n("pp")).find(".z-comboitem").get(index);
		click(element);
		return widget(element);
	}
}
