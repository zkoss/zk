/* B30_1562358Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 17, 2009 9:58:02 AM , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * @author sam
 *
 */
public class B30_1562358Test extends ZKTestCase{
	private String _target="B30-1562358.zul";
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
		String cmp1 = uuid(6);
	
		for(Selenium browser : _browsers){
				try{
					browser.start();
					browser.open(_url);
					
					int height = browser.getElementHeight(cmp1).intValue();				
					//comp should have 1 line
					assertTrue(height < 35);
					
					browser.close();
					
				}finally{
					browser.stop();
				}
		}	
	}
	
	@Override
	@After
	public void tearDown(){
	}
}
