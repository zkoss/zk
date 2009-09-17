/* B30_1455584Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;
/**
 * 
 * @author sam
 *
 */
public class B30_1455584Test extends ZKTestCase{

	private String _target="B30-1455584.zul";
	private List<Selenium> _browsers;
	private String _url;
	
	
	@Override
	@Before
	public void setUp(){
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
	}

	@Test(expected=AssertionError.class)
	public void test1(){
		String testComp= uuid(4);
		for(Selenium browser : _browsers){
				browser.start();
				try{	
					browser.open(_url);
					String strClickBefor = browser.getText(testComp);
						
					browser.focus(testComp);
					browser.click(testComp);
						
					String strClickAfter = browser.getText(testComp);
					assertNotEquals(strClickBefor, strClickAfter);
						
					browser.close();
				}finally{
					browser.stop();
				}
		}
	}
	
}
