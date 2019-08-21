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

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zktest.zats.ztl.ClientWidget;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;
import org.zkoss.zktest.zats.ztl.ZK;

/**
 * A base class to test using WebDriver.
 * <p>Currently support Chromium headless</p>
 * @author jumperchen
 */
public abstract class WebDriverTestCase {

	private static final Logger log = LoggerFactory.getLogger(WebDriverTestCase.class);
	private static final String PACKAGE = WebDriverTestCase.class.getPackage().getName();
	private static final ThreadLocal<Server> server = new ThreadLocal<>();
	private static final ThreadLocal<Integer> port = new ThreadLocal<>();
	private static final ThreadLocal<WebDriver> _local = new ThreadLocal<WebDriver>();
	private static final String JS_DROP_FILES = "var c=arguments,b=c[0],k=c[1];c=c[2];for(var d=b.ownerDocument||document,l=0;;){var e=b.getBoundingClientRect(),g=e.left+(k||e.width/2),h=e.top+(c||e.height/2),f=d.elementFromPoint(g,h);if(f&&b.contains(f))break;if(1<++l)throw b=Error('Element not interactable'),b.code=15,b;b.scrollIntoView({behavior:'instant',block:'center',inline:'center'})}var a=d.createElement('INPUT');a.setAttribute('type','file');a.setAttribute('multiple','');a.setAttribute('style','position:fixed;z-index:2147483647;left:0;top:0;');a.onchange=function(b){a.parentElement.removeChild(a);b.stopPropagation();var c={constructor:DataTransfer,effectAllowed:'all',dropEffect:'none',types:['Files'],files:a.files,setData:function(){},getData:function(){},clearData:function(){},setDragImage:function(){}};window.DataTransferItemList&&(c.items=Object.setPrototypeOf(Array.prototype.map.call(a.files,function(a){return{constructor:DataTransferItem,kind:'file',type:a.type,getAsFile:function(){return a},getAsString:function(b){var c=new FileReader;c.onload=function(a){b(a.target.result)};c.readAsText(a)}}}),{constructor:DataTransferItemList,add:function(){},clear:function(){},remove:function(){}}));['dragenter','dragover','drop'].forEach(function(a){var b=d.createEvent('DragEvent');b.initMouseEvent(a,!0,!0,d.defaultView,0,0,0,g,h,!1,!1,!1,!1,0,null);Object.setPrototypeOf(b,null);b.dataTransfer=c;Object.setPrototypeOf(b,DragEvent.prototype);f.dispatchEvent(b)})};d.documentElement.appendChild(a);a.getBoundingClientRect();return a;";

	protected WebDriver driver;
	protected static int getPort() {
		return port.get();
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
			driver = new ChromiumHeadlessDriver(getWebDriverOptions(), true);
		}
		return driver;
	}

	/**
	 * Gets the WebDriver options.
	 * You can add arguments for Chromium to change settings like locale or user-agent string.
	 * A list of available options can be found at <a href="http://chromedriver.chromium.org/capabilities">Capabilities & ChromeOptions</a>.
	 *
	 * @return WebDriver options
	 */
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("window-size=1920,1080");
		options.setExperimentalOption("w3c", false); // Temporary workaround for TouchAction
		return options;
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
		return className.substring(0, lastTest) + getFileExtension();
	}
	protected String getFileExtension() {
		return ".zul";
	}
	public WebDriver connect(String location) {
		WebDriver webDriver = getWebDriver();
		if (location == null || location.length() <= 0) {
			location = getFileLocation();
			String loc = getAddress() + location;
			int errCode = getStatusCode(loc);
			if (errCode == 404) {
				loc = getAddress() + location.replace("_", "-");
				errCode = getStatusCode(loc);
				if (errCode == 404) {
					loc = getAddress() + location.replace("-", "_");
					errCode = getStatusCode(loc);
				}
				if (errCode == 404) {
					fail("Error Code: " + errCode + ", from URL[" + loc + "]");
				}
				webDriver.get(loc);
			}
		} else {
			String loc = getAddress() + location;
			int errCode = getStatusCode(loc);
			if (errCode != 200) {
				fail("Error Code: " + errCode + ", from URL[" + loc + "]");
			}
			webDriver.get(loc);
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
		_local.set(null);
	}

	@BeforeClass
	public static void init() throws Exception {
		Server server = new Server(new InetSocketAddress(getHost(), 0));

		final WebAppContext context = new WebAppContext();
		context.setContextPath(getContextPath());
		context.setBaseResource(Resource.newResource("./src/archive/"));
		context.getSessionHandler().setSessionIdPathParameterName(null);
		server.setHandler(new HandlerList(context, new DefaultHandler()));
		initServer(server);
	}

	protected static void initServer(Server currentServer) throws Exception {
		currentServer.start();
		server.set(currentServer);

		for (Connector c : currentServer.getConnectors()) {
			if (c instanceof NetworkConnector) {
				if (((NetworkConnector)c).getLocalPort() > 0) {
					port.set(((NetworkConnector) c).getLocalPort());
					break;
				}
			}
		}
	}

	@AfterClass
	public static void end() throws Exception {
		Server currentServer = server.get();
		if (currentServer != null) {
			currentServer.stop();
		}
		server.set(null);
		port.set(0);
	}

	/**
	 * Waits for Ajax response. (excluding animation check)
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
	 * <p>Default: 500ms</p>
	 * @return
	 */
	protected int getSpeed() {
		return 500;
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
		return getWebDriver().findElement(By.id("zk_log")).getAttribute("value").trim();
	}

	/**
	 * Returns the text of zk.log from client side.
	 */
	protected boolean isZKLogAvailable() {
		WebElement log = null;
		try {
			log = getWebDriver().findElement(By.id("zk_log"));
		} catch (NoSuchElementException e) {
			//do nothing
		}
		return log != null;
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
	 * Right clicks upon the given locator.
	 * @param locator
	 */
	protected void rightClick(ClientWidget locator) {
		Actions act = getActions();
		act.contextClick(toElement(locator));
		act.perform();
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
	protected void sendKeys(ClientWidget locator, CharSequence... keysToSend) {
		getWebDriver().findElement(locator).sendKeys(keysToSend);
	}

	/**
	 * Selects an comboitem from the given combobox.
	 * @param combobox
	 * @param index
	 */
	protected Widget selectComboitem(Widget combobox, int index) {
		click(combobox.$n("btn"));
		waitResponse(true);
		Element element = jq(combobox.$n("pp")).find(".z-comboitem").get(index);
		click(element);
		waitResponse(true);
		return widget(element);
	}

	/**
	 * Gets the HTTP response status code.
	 *
	 * @param url the URL
	 * @return status code. -1 if the connection has any exception.
	 */
	public static int getStatusCode(String url) {
		HttpURLConnection http = null;
		try {
			URL u = new URL(url);
			http = (HttpURLConnection) u.openConnection();
			return http.getResponseCode();
		} catch (Exception e) {
			return -1;
		} finally {
			if (http != null) http.disconnect();
		}
	}

	/**
	 * Strips ;jsessionid= in URL because it is annoying when comparing URLs.
	 *
	 * @param url the original URL
	 * @return stripped URL
	 */
	protected String stripJsessionid(String url) {
		return url.replaceAll(";jsessionid=[^?]*", "");
	}

	public boolean hasError() {
		return Boolean.valueOf(getEval("!!jq('.z-messagebox-error')[0] || !!jq('.z-errorbox')[0] || jq('.z-error')[0]"));
	}

	/**
	 * Asserts no JavaScript errors occur.
	 * If there is any error on browser console, an {@link Assert#fail(String)} would be raised.
	 */
	protected void assertNoJSError() {
		driver.manage().logs().get(LogType.BROWSER).getAll()
				.stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
				.findFirst()
				.ifPresent(log -> Assert.fail(log.toString()));
	}

	/**
	 * Returns the text content of zk messagebox from client side.
	 */
	protected String getMessageBoxContent() {
		return jq(".z-messagebox").text().replaceAll("\u00A0"," ").trim();
	}

	/**
	 * Returns the browser actions.
	 */
	protected Actions getActions() {
		return new Actions(getWebDriver());
	}

	/**
	 * Simulates drag and drop a file to a Dropupload element.
	 *
	 * @param element Dropupload element
	 * @param file file path
	 * @throws FileNotFoundException file not found
	 */
	protected void dropUploadFile(JQuery element, Path file) throws FileNotFoundException {
		dropUploadFiles(element, Collections.singletonList(file), 0, 0);
	}

	/**
	 * Simulates drag and drop files to a Dropupload element.
	 *
	 * @param element Dropupload element
	 * @param files file paths
	 * @param offsetX Drop offset x relative to the top/left corner of the drop area. Center if 0.
	 * @param offsetY Drop offset y relative to the top/left corner of the drop area. Center if 0.
	 * @throws FileNotFoundException file not found
	 * @see <a href="https://stackoverflow.com/a/38830823">Selenium: Drag and Drop from file system to WebDriver?</a>
	 */
	protected void dropUploadFiles(JQuery element, List<Path> files, int offsetX, int offsetY) throws FileNotFoundException {
		List<String> paths = new ArrayList<>();
		for (Path file : files) {
			if (!Files.isRegularFile(file)) {
				throw new FileNotFoundException(file.toString());
			}
			paths.add(file.toAbsolutePath().toString());
		}

		eval(element + ".show()"); // needed to interact
		String value = String.join("\n", paths);
		WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(JS_DROP_FILES, toElement(element), offsetX, offsetY);
		input.sendKeys(value);
	}

	/**
	 * Press hot key (e.g. Ctrl + X) to trigger a cut action.
	 * It's caller's responsibility to focus/select text before calling this method.
	 */
	protected void cut() {
		String cutKeys = System.getProperty("os.name").startsWith("Mac")
				? Keys.chord(Keys.SHIFT, Keys.DELETE)
				: Keys.chord(Keys.CONTROL, "x");
		getActions().sendKeys(cutKeys).perform();
	}

	/**
	 * Press hot key (e.g. Ctrl + C) to trigger a copy action.
	 * It's caller's responsibility to focus/select text before calling this method.
	 */
	protected void copy() {
		// Workaround for https://bugs.chromium.org/p/chromedriver/issues/detail?id=30
		String copyKeys = System.getProperty("os.name").startsWith("Mac")
				? Keys.chord(Keys.CONTROL, Keys.INSERT)
				: Keys.chord(Keys.CONTROL, "c");
		getActions().sendKeys(copyKeys).perform();
	}

	/**
	 * Press hot key (e.g. Ctrl + V) to trigger a paste action.
	 * It's caller's responsibility to click/focus on a DOM node before calling this method.
	 */
	protected void paste() {
		String pasteKeys = System.getProperty("os.name").startsWith("Mac")
				? Keys.chord(Keys.SHIFT, Keys.INSERT)
				: Keys.chord(Keys.CONTROL, "v");
		getActions().sendKeys(pasteKeys).perform();
	}

	/**
	 * Trigger a select all action.
	 * It's caller's responsibility to click/focus on a DOM node before calling this method.
	 */
	protected void selectAll() {
		eval("document.activeElement.select && document.activeElement.select()");
		sleep(100);
	}

	/**
	 * Moves the text cursor to the specified position in the given input element or textarea.
	 *
	 * @param locator pointing to an input element or textarea
	 * @param position position (starts from 0)
	 */
	protected void setCursorPosition(ClientWidget locator, int position) {
		eval(zk(locator) + String.format(".setSelectionRange(%1$d, %1$d)", position));
		sleep(100);
	}

	/**
	 * Hover.
	 *
	 * @param locator element
	 */
	protected void mouseOver(ClientWidget locator) {
		getActions().moveToElement(toElement(locator)).pause(100).perform();
	}
}
