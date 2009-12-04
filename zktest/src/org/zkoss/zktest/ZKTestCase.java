/* ZKTestCase.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;
import com.unitedinternet.portal.selenium.utils.logging.HtmlResultFormatter;
import com.unitedinternet.portal.selenium.utils.logging.LoggingCommandProcessor;
import com.unitedinternet.portal.selenium.utils.logging.LoggingDefaultSelenium;
import com.unitedinternet.portal.selenium.utils.logging.LoggingResultsFormatter;
import com.unitedinternet.portal.selenium.utils.logging.LoggingSelenium;
import com.unitedinternet.portal.selenium.utils.logging.LoggingUtils;
/**
 * A skeleton of ZK Selenium test, which implements all of the methods of {@Selenium}
 * interface.
 * 
 * @author sam
 *
 */
public class ZKTestCase extends SeleneseTestCase implements Selenium {
	protected static final ThreadLocal<Selenium> _selenium = new ThreadLocal<Selenium>();
	
	/**
	 * The prefix is depended on what the ID generator is.
	 */
	private static String PREFIX = "zk_comp_";
	private HashMap<String, BrowserWrapper> _map;
	
	// implicit variable
	protected String target;
	protected String url;
	protected List<Selenium> browsers;

	/**
	 * Launches the browser with a new Selenium session
	 */
	protected void start(Selenium selenium) {
		selenium.start();
		selenium.open(url);
		_selenium.set(selenium);
	}
	
	/**
	 * Returns the current browser.
	 */
	public static final Selenium getCurrent() {
		return _selenium.get();
	}
	
	/**
	 *  Logging
	 */
	protected static final String RESULT_FILE_ENCODING = "UTF-8";
	protected static final String SCREENSHOT_PATH = "screenshots";
	protected static final String RESULTS_BASE_PATH = "Log";
	protected String screenshotsResultsPath;

	public ZKTestCase() {
		try {
			_map = ConfigHelper.getServerWrapperMap();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void setUp() {
		if (target == null)
			throw new NullPointerException("target cannot be null!");
		browsers = getBrowsers(target);
		url = getUrl(target);
	}

	protected final static String uuid(int number) {
		return PREFIX + number;
	}
	
	protected List<Selenium> getBrowsers(String target) {
		
		List<Selenium> browsers = _map.get(target).getBrowsers();
		if(browsers == null || browsers.size() == 0)
			throw new NullPointerException("Browsers Null exception : please check config.properties");
		return browsers;
	}
	
	protected List<LoggingSelenium> getLoggingBrowsers(String target, BufferedWriter loggingWriter){
		if(target == null || loggingWriter == null)
			throw new NullPointerException();
		
		List<String> browserTypes = _map.get(target).getBrowserTypes();
		
		if(browserTypes == null || browserTypes.size() == 0)
			throw new NullPointerException("Browsers Null exception : please check config.properties");
		
		List<LoggingSelenium> browsers = new LinkedList<LoggingSelenium>();
		for(String browserType : browserTypes){
			browsers.add( createLoggingSelenium(browserType, target, loggingWriter) );
		}
		return browsers;
	}
	
	private LoggingSelenium createLoggingSelenium(String browserType, String target, BufferedWriter loggingWriter){
		
		LoggingSelenium browser = new LoggingDefaultSelenium(createLoggingProcessor(browserType, loggingWriter));
		browser.setSpeed(ConfigHelper.getDelaySpeed());
		
		return browser;
	}
	
	private LoggingCommandProcessor createLoggingProcessor(String browserType, BufferedWriter loggingWriter){
		screenshotsResultsPath = new File(RESULTS_BASE_PATH + File.separator + SCREENSHOT_PATH).getAbsolutePath();
		if (!new File(screenshotsResultsPath).exists()) {
			 new File(screenshotsResultsPath).mkdirs();
		}
		
		LoggingResultsFormatter htmlFormatter = new HtmlResultFormatter(loggingWriter, RESULT_FILE_ENCODING);
		htmlFormatter.setScreenShotBaseUri(SCREENSHOT_PATH+"/");
		htmlFormatter.setAutomaticScreenshotPath(screenshotsResultsPath);
		LoggingCommandProcessor myProcessor = 
			new LoggingCommandProcessor(new HttpCommandProcessor(ConfigHelper.getHost(),
																 ConfigHelper.getPort(), 
																 browserType,
																 ConfigHelper.getOpenUrl()), htmlFormatter);
		
		return myProcessor;
	}
	

	protected String getUrl(String target) {
		return _map.get(target).getUrl();
	}
	
	
	protected String getCaptureScreenshotPath(){
		return screenshotsResultsPath + File.separator + LoggingUtils.timeStampForFileName() + ".png";
	}
	
	protected BufferedWriter createLoggerWriter(String target){
		String resultsPath = new File(RESULTS_BASE_PATH).getAbsolutePath();
		String resultHtmlFileName = resultsPath + File.separator + target + "_TestResult.html";
		if (!new File(resultHtmlFileName).exists()) {
			 new File(resultHtmlFileName).mkdirs();
		}
		BufferedWriter loggingWriter = LoggingUtils.createWriter(resultHtmlFileName,
																  RESULT_FILE_ENCODING, true);
		return loggingWriter;
	}

	@Override
	public void addLocationStrategy(String strategyName,
			String functionDefinition) {
		getCurrent().addLocationStrategy(strategyName, functionDefinition);
	}

	@Override
	public void addScript(String scriptContent, String scriptTagId) {
		getCurrent().addScript(scriptContent, scriptTagId);
	}

	@Override
	public void addSelection(String locator, String optionLocator) {
		getCurrent().addSelection(locator, optionLocator);
	}

	@Override
	public void allowNativeXpath(String allow) {
		getCurrent().allowNativeXpath(allow);
	}

	@Override
	public void altKeyDown() {
		getCurrent().altKeyDown();
	}

	@Override
	public void altKeyUp() {
		getCurrent().altKeyUp();
	}

	@Override
	public void answerOnNextPrompt(String answer) {
		getCurrent().answerOnNextPrompt(answer);
	}

	@Override
	public void assignId(String locator, String identifier) {
		getCurrent().assignId(locator, identifier);
	}

	@Override
	public void attachFile(String fieldLocator, String fileLocator) {
		getCurrent().attachFile(fieldLocator, fileLocator);		
	}

	@Override
	public void captureEntirePageScreenshot(String filename, String kwargs) {
		getCurrent().captureEntirePageScreenshot(filename, kwargs);
	}

	@Override
	public String captureEntirePageScreenshotToString(String kwargs) {
		return getCurrent().captureEntirePageScreenshotToString(kwargs);
	}

	@Override
	public void captureScreenshot(String filename) {
		getCurrent().captureScreenshot(filename);
	}

	@Override
	public String captureScreenshotToString() {
		return getCurrent().captureScreenshotToString();
	}

	@Override
	public void check(String locator) {
		getCurrent().check(locator);
	}

	@Override
	public void chooseCancelOnNextConfirmation() {
		getCurrent().chooseCancelOnNextConfirmation();
	}

	@Override
	public void chooseOkOnNextConfirmation() {
		getCurrent().chooseOkOnNextConfirmation();
	}

	@Override
	public void click(String locator) {
		getCurrent().click(locator);
	}

	@Override
	public void clickAt(String locator, String coordString) {
		getCurrent().clickAt(locator, coordString);
	}

	@Override
	public void close() {
		getCurrent().close();
	}

	@Override
	public void contextMenu(String locator) {
		getCurrent().contextMenu(locator);
	}

	@Override
	public void contextMenuAt(String locator, String coordString) {
		getCurrent().contextMenuAt(locator, coordString);
	}

	@Override
	public void controlKeyDown() {
		getCurrent().controlKeyDown();
	}

	@Override
	public void controlKeyUp() {
		getCurrent().controlKeyUp();
	}

	@Override
	public void createCookie(String nameValuePair, String optionsString) {
		getCurrent().createCookie(nameValuePair, optionsString);
	}

	@Override
	public void deleteAllVisibleCookies() {
		getCurrent().deleteAllVisibleCookies();
	}

	@Override
	public void deleteCookie(String name, String optionsString) {
		getCurrent().deleteCookie(name, optionsString);
	}

	@Override
	public void doubleClick(String locator) {
		getCurrent().doubleClick(locator);
	}

	@Override
	public void doubleClickAt(String locator, String coordString) {
		getCurrent().doubleClickAt(locator, coordString);
	}

	@Override
	public void dragAndDrop(String locator, String movementsString) {
		getCurrent().dragAndDrop(locator, movementsString);
	}
	
	/**
	 * Drags and drops the specific element from its specific area to another area.
	 * <p>For example,<br/>
	 * 		draggdropTo("z-xxx", "10,20", "20,20")
	 * <p>The result of the "z-xxx" is moved 10px right.
	 * @param locatorOfObjectToBeDragged the draggable UUID
	 * @param from the "x,y" value is related to the draggable element, which is dragged from.
	 * @param to the "x,y" value is related to the draggable element, which is dropped to.
	 */
	public void dragdropTo(String locatorOfObjectToBeDragged, String from, String to) {
		Selenium browser = getCurrent();
		browser.mouseDownAt(locatorOfObjectToBeDragged, from);
		browser.mouseMoveAt(locatorOfObjectToBeDragged, to);
		browser.mouseUpAt(locatorOfObjectToBeDragged, to);
	}
	
	@Override
	public void dragAndDropToObject(String locatorOfObjectToBeDragged,
			String locatorOfDragDestinationObject) {
		getCurrent().dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
	}

	@Override
	public void dragdrop(String locator, String movementsString) {
		getCurrent().dragdrop(locator, movementsString);
	}

	@Override
	public void fireEvent(String locator, String eventName) {
		getCurrent().fireEvent(locator, eventName);
	}

	@Override
	public void focus(String locator) {
		getCurrent().focus(locator);
	}

	@Override
	public String getAlert() {
		return getCurrent().getAlert();
	}

	@Override
	public String[] getAllButtons() {
		return getCurrent().getAllButtons();
	}

	@Override
	public String[] getAllFields() {
		return getCurrent().getAllFields();
	}

	@Override
	public String[] getAllLinks() {
		return getCurrent().getAllLinks();
	}

	@Override
	public String[] getAllWindowIds() {
		return getCurrent().getAllWindowIds();
	}

	@Override
	public String[] getAllWindowNames() {
		return getCurrent().getAllWindowNames();
	}

	@Override
	public String[] getAllWindowTitles() {
		return getCurrent().getAllWindowTitles();
	}

	@Override
	public String getAttribute(String attributeLocator) {
		return getCurrent().getAttribute(attributeLocator);
	}

	@Override
	public String[] getAttributeFromAllWindows(String attributeName) {
		return getCurrent().getAttributeFromAllWindows(attributeName);
	}

	@Override
	public String getBodyText() {
		return getCurrent().getBodyText();
	}

	@Override
	public String getConfirmation() {
		return getCurrent().getConfirmation();
	}

	@Override
	public String getCookie() {
		return getCurrent().getCookie();
	}

	@Override
	public String getCookieByName(String name) {
		return getCurrent().getCookieByName(name);
	}

	@Override
	public Number getCursorPosition(String locator) {
		return getCurrent().getCursorPosition(locator);
	}

	@Override
	public Number getElementHeight(String locator) {
		return getCurrent().getElementHeight(locator);
	}

	@Override
	public Number getElementIndex(String locator) {
		return getCurrent().getElementIndex(locator);
	}

	@Override
	public Number getElementPositionLeft(String locator) {
		return getCurrent().getElementPositionLeft(locator);
	}

	@Override
	public Number getElementPositionTop(String locator) {
		return getCurrent().getElementPositionTop(locator);
	}

	@Override
	public Number getElementWidth(String locator) {
		return getCurrent().getElementWidth(locator);
	}

	@Override
	public String getEval(String script) {
		return getCurrent().getEval(script);
	}

	@Override
	public String getExpression(String expression) {
		return getCurrent().getExpression(expression);
	}

	@Override
	public String getHtmlSource() {
		return getCurrent().getHtmlSource();
	}

	@Override
	public String getLocation() {
		return getCurrent().getLocation();
	}

	@Override
	public Number getMouseSpeed() {
		return getCurrent().getMouseSpeed();
	}

	@Override
	public String getPrompt() {
		return getCurrent().getPrompt();
	}

	@Override
	public String[] getSelectOptions(String selectLocator) {
		return getCurrent().getSelectOptions(selectLocator);
	}

	@Override
	public String getSelectedId(String selectLocator) {
		return getCurrent().getSelectedId(selectLocator);
	}

	@Override
	public String[] getSelectedIds(String selectLocator) {
		return getCurrent().getSelectedIds(selectLocator);
	}

	@Override
	public String getSelectedIndex(String selectLocator) {
		return getCurrent().getSelectedIndex(selectLocator);
	}

	@Override
	public String[] getSelectedIndexes(String selectLocator) {
		return getCurrent().getSelectedIndexes(selectLocator);
	}

	@Override
	public String getSelectedLabel(String selectLocator) {
		return getCurrent().getSelectedLabel(selectLocator);
	}

	@Override
	public String[] getSelectedLabels(String selectLocator) {
		return getCurrent().getSelectedLabels(selectLocator);
	}

	@Override
	public String getSelectedValue(String selectLocator) {
		return getCurrent().getSelectedValue(selectLocator);
	}

	@Override
	public String[] getSelectedValues(String selectLocator) {
		return getCurrent().getSelectedValues(selectLocator);
	}

	@Override
	public String getSpeed() {
		return getCurrent().getSpeed();
	}

	@Override
	public String getTable(String tableCellAddress) {
		return getCurrent().getTable(tableCellAddress);
	}

	@Override
	public String getText(String locator) {
		return getCurrent().getText(locator);
	}

	@Override
	public String getTitle() {
		return getCurrent().getTitle();
	}

	@Override
	public String getValue(String locator) {
		return getCurrent().getValue(locator);
	}

	@Override
	public boolean getWhetherThisFrameMatchFrameExpression(
			String currentFrameString, String target) {
		return getCurrent().getWhetherThisFrameMatchFrameExpression(currentFrameString, target);
	}

	@Override
	public boolean getWhetherThisWindowMatchWindowExpression(
			String currentWindowString, String target) {
		return getCurrent().getWhetherThisWindowMatchWindowExpression(currentWindowString, target);
	}

	@Override
	public Number getXpathCount(String xpath) {
		return getCurrent().getXpathCount(xpath);
	}

	@Override
	public void goBack() {
		getCurrent().goBack();
	}

	@Override
	public void highlight(String locator) {
		getCurrent().highlight(locator);
	}

	@Override
	public void ignoreAttributesWithoutValue(String ignore) {
		getCurrent().ignoreAttributesWithoutValue(ignore);
	}

	@Override
	public boolean isAlertPresent() {
		return getCurrent().isAlertPresent();
	}

	@Override
	public boolean isChecked(String locator) {
		return getCurrent().isChecked(locator);
	}

	@Override
	public boolean isConfirmationPresent() {
		return getCurrent().isConfirmationPresent();
	}

	@Override
	public boolean isCookiePresent(String name) {
		return getCurrent().isCookiePresent(name);
	}

	@Override
	public boolean isEditable(String locator) {
		return getCurrent().isEditable(locator);
	}

	@Override
	public boolean isElementPresent(String locator) {
		return getCurrent().isElementPresent(locator);
	}

	@Override
	public boolean isOrdered(String locator1, String locator2) {
		return getCurrent().isOrdered(locator1, locator2);
	}

	@Override
	public boolean isPromptPresent() {
		return getCurrent().isPromptPresent();
	}

	@Override
	public boolean isSomethingSelected(String selectLocator) {
		return getCurrent().isSomethingSelected(selectLocator);
	}

	@Override
	public boolean isTextPresent(String pattern) {
		return getCurrent().isTextPresent(pattern);
	}

	@Override
	public boolean isVisible(String locator) {
		return getCurrent().isVisible(locator);
	}

	@Override
	public void keyDown(String locator, String keySequence) {
		getCurrent().keyDown(locator, keySequence);
	}

	@Override
	public void keyDownNative(String keycode) {
		getCurrent().keyDownNative(keycode);
	}

	@Override
	public void keyPress(String locator, String keySequence) {
		getCurrent().keyPress(locator, keySequence);
	}

	@Override
	public void keyPressNative(String keycode) {
		getCurrent().keyPressNative(keycode);
	}

	@Override
	public void keyUp(String locator, String keySequence) {
		getCurrent().keyUp(locator, keySequence);
	}

	@Override
	public void keyUpNative(String keycode) {
		getCurrent().keyUpNative(keycode);
	}

	@Override
	public void metaKeyDown() {
		getCurrent().metaKeyDown();
	}

	@Override
	public void metaKeyUp() {
		getCurrent().metaKeyUp();
	}

	@Override
	public void mouseDown(String locator) {
		getCurrent().mouseDown(locator);
	}

	@Override
	public void mouseDownAt(String locator, String coordString) {
		getCurrent().mouseDownAt(locator, coordString);
	}

	@Override
	public void mouseDownRight(String locator) {
		getCurrent().mouseDownRight(locator);
	}

	@Override
	public void mouseDownRightAt(String locator, String coordString) {
		getCurrent().mouseDownRightAt(locator, coordString);		
	}

	@Override
	public void mouseMove(String locator) {
		getCurrent().mouseMove(locator);
	}

	@Override
	public void mouseMoveAt(String locator, String coordString) {
		getCurrent().mouseMoveAt(locator, coordString);
	}

	@Override
	public void mouseOut(String locator) {
		getCurrent().mouseOut(locator);
	}

	@Override
	public void mouseOver(String locator) {
		getCurrent().mouseOver(locator);
	}

	@Override
	public void mouseUp(String locator) {
		getCurrent().mouseUp(locator);
	}

	@Override
	public void mouseUpAt(String locator, String coordString) {
		getCurrent().mouseUpAt(locator, coordString);
	}

	@Override
	public void mouseUpRight(String locator) {
		getCurrent().mouseUpRight(locator);
	}

	@Override
	public void mouseUpRightAt(String locator, String coordString) {
		getCurrent().mouseUpRightAt(locator, coordString);
	}

	@Override
	public void open(String url) {
		getCurrent().open(url);
	}

	@Override
	public void openWindow(String url, String windowID) {
		getCurrent().openWindow(url, windowID);
	}

	@Override
	public void refresh() {
		getCurrent().refresh();
	}

	@Override
	public void removeAllSelections(String locator) {
		getCurrent().removeAllSelections(locator);
	}

	@Override
	public void removeScript(String scriptTagId) {
		getCurrent().removeScript(scriptTagId);
	}

	@Override
	public void removeSelection(String locator, String optionLocator) {
		getCurrent().removeSelection(locator, optionLocator);
	}

	@Override
	public String retrieveLastRemoteControlLogs() {
		return getCurrent().retrieveLastRemoteControlLogs();
	}

	@Override
	public void rollup(String rollupName, String kwargs) {
		getCurrent().rollup(rollupName, kwargs);
	}

	@Override
	public void runScript(String script) {
		getCurrent().runScript(script);
	}

	@Override
	public void select(String selectLocator, String optionLocator) {
		getCurrent().select(selectLocator, optionLocator);
	}

	@Override
	public void selectFrame(String locator) {
		getCurrent().selectFrame(locator);
	}

	@Override
	public void selectWindow(String windowID) {
		getCurrent().selectWindow(windowID);
	}

	@Override
	public void setBrowserLogLevel(String logLevel) {
		getCurrent().setBrowserLogLevel(logLevel);
	}

	@Override
	public void setContext(String context) {
		getCurrent().setContext(context);
	}

	@Override
	public void setCursorPosition(String locator, String position) {
		getCurrent().setCursorPosition(locator, position);
	}

	@Override
	public void setExtensionJs(String extensionJs) {
		getCurrent().setExtensionJs(extensionJs);
	}

	@Override
	public void setMouseSpeed(String pixels) {
		getCurrent().setMouseSpeed(pixels);
	}

	@Override
	public void setSpeed(String value) {
		getCurrent().setSpeed(value);
	}

	@Override
	public void setTimeout(String timeout) {
		getCurrent().setTimeout(timeout);
	}

	@Override
	public void shiftKeyDown() {
		getCurrent().shiftKeyDown();
	}

	@Override
	public void shiftKeyUp() {
		getCurrent().shiftKeyUp();
	}

	@Override
	public void showContextualBanner() {
		getCurrent().showContextualBanner();
	}

	@Override
	public void showContextualBanner(String className, String methodName) {
		getCurrent().showContextualBanner(className, methodName);
	}

	@Override
	public void shutDownSeleniumServer() {
		getCurrent().shutDownSeleniumServer();
	}

	@Override
	public void start() {
		getCurrent().start();
	}

	@Override
	public void start(String optionsString) {
		getCurrent().start(optionsString);
	}

	@Override
	public void start(Object optionsObject) {
		getCurrent().start(optionsObject);
	}

	@Override
	public void submit(String formLocator) {
		getCurrent().submit(formLocator);
	}

	@Override
	public void type(String locator, String value) {
		getCurrent().type(locator, value);		
	}

	@Override
	public void typeKeys(String locator, String value) {
		getCurrent().typeKeys(locator, value);
	}

	@Override
	public void uncheck(String locator) {
		getCurrent().uncheck(locator);
	}

	@Override
	public void useXpathLibrary(String libraryName) {
		getCurrent().useXpathLibrary(libraryName);		
	}

	@Override
	public void waitForCondition(String script, String timeout) {
		getCurrent().waitForCondition(script, timeout);		
	}

	@Override
	public void waitForFrameToLoad(String frameAddress, String timeout) {
		getCurrent().waitForFrameToLoad(frameAddress, timeout);
	}

	@Override
	public void waitForPageToLoad(String timeout) {
		getCurrent().waitForPageToLoad(timeout);
	}

	@Override
	public void waitForPopUp(String windowID, String timeout) {
		getCurrent().waitForPopUp(windowID, timeout);
	}

	@Override
	public void windowFocus() {
		getCurrent().windowFocus();
	}

	@Override
	public void windowMaximize() {
		getCurrent().windowMaximize();
	}

	@Override
	public void stop() {
		Selenium selenium = getCurrent();
		selenium.close();
		selenium.stop();
		_selenium.remove();	
	}

}
