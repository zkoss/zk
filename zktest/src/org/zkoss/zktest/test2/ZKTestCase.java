/* ZKTestCase.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

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
import com.unitedinternet.portal.selenium.utils.logging.LoggingAssert;
import com.unitedinternet.portal.selenium.utils.logging.LoggingCommandProcessor;
import com.unitedinternet.portal.selenium.utils.logging.LoggingDefaultSelenium;
import com.unitedinternet.portal.selenium.utils.logging.LoggingResultsFormatter;
import com.unitedinternet.portal.selenium.utils.logging.LoggingSelenium;
import com.unitedinternet.portal.selenium.utils.logging.LoggingUtils;
/**
 * 
 * @author sam
 *
 */
public class ZKTestCase extends SeleneseTestCase {
	/**
	 * The prefix is depended on what the ID generator is.
	 */
	private static String PREFIX = "zk_comp_";
	private HashMap<String, BrowserWrapper> _map;
	
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

}
