/* B30_1568393Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 17, 2009 10:36:54 AM , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;
import com.unitedinternet.portal.selenium.utils.logging.LoggingSelenium;

/**
 * @author sam
 *
 */
public class B30_1568393Test extends ZKTestCase{
	private String _target="B30-1568393.zul";
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
		String comp1=uuid(5);
		for(Selenium browser : _browsers){
			try{
					
				browser.start();
				browser.open(_url);
					
				int x = browser.getElementPositionLeft(comp1).intValue();
				int y = browser.getElementPositionTop(comp1).intValue();
					
				for(int i=0; i < 6 ; i++){
					browser.mouseDown(comp1);
					browser.mouseUp(comp1);
				}
					
				int x2 = browser.getElementPositionLeft(comp1).intValue();
				int y2 = browser.getElementPositionTop(comp1).intValue();
				
				//after several  mouse click, the comp should not move it position
				assertEquals(x, x2);
				assertEquals(y, y2);
					
				}finally{
					browser.stop();
				}
		}
		
	}
	
	
}
